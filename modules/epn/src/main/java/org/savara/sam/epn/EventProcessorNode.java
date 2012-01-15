/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.sam.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a node in the Event Processor Network.
 *
 * @param <S> The source event type
 * @param <T> The target event type
 */
public class EventProcessorNode<S,T> {

    private Logger LOGGER=Logger.getLogger(EventProcessorNode.class.getName());
    
    private EventProcessor<S,T> _eventProcessor=null;
    private java.util.List<EventDestination<T>> _destinations=null;
    
    /**
     * The constructor for the event processor node.
     * 
     * @param ep The event processor associated with the node
     * @param destinations The list of destinations to send transformed events
     */
    public EventProcessorNode(EventProcessor<S,T> ep,
                java.util.List<EventDestination<T>> destinations) {
        _eventProcessor = ep;
        _destinations = destinations;
    }
    
    /**
     * This method returns the event processor.
     * 
     * @return The event processor
     */
    protected EventProcessor<S,T> getEventProcessor() {
        return (_eventProcessor);
    }
    
    /**
     * This method processes the supplied list of events against the
     * event processor configured with the node, to determine
     * which transformed events should be forwarded, and which need
     * to be returned to be retried.
     * 
     * @param events The list of events to be processed
     * @param retriesLeft The number of remaining retries
     * @return The list of events to retry, or null if no retries required
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    public java.util.List<S> process(java.util.List<S> events, int retriesLeft)
                            throws Exception {
        java.util.List<S> ret=null;
        java.util.List<T> results=new java.util.Vector<T>();
        
        for (S event : events) {
            
            try {
                T processed=getEventProcessor().process(event, retriesLeft);
                
                if (processed != null) {
                    results.add(processed);
                }
                
            } catch(Exception e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Retry event: "+event);
                }
                if (ret == null) {
                    ret = new java.util.Vector<S>();
                }
                ret.add(event);
            }
        }
        
        if (results.size() > 0) {
            forward(results);
        }
        
        return (ret);
    }
    
    /**
     * This method forwards the results to any destinations that have been
     * defined.
     * 
     * @param results The results
     * @throws Exception Failed to forward results
     */
    protected void forward(java.util.List<T> results) throws Exception {
        
        for (EventDestination<T> dest : _destinations) {
            dest.send(results);
        }
    }
}
