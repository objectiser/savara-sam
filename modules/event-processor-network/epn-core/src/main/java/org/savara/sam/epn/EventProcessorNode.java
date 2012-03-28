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

import com.google.common.base.Predicate;

/**
 * This class represents a node in the Event Processor Network.
 *
 * @param <S> The source event type
 * @param <T> The target event type
 */
public class EventProcessorNode<S extends java.io.Serializable,T extends java.io.Serializable> {

    private static Logger LOG=Logger.getLogger(EventProcessorNode.class.getName());
    
    public static final String EPN_NAME="EPNName";
    
    private EventProcessor<S,T> _eventProcessor=null;
    private Predicate<S> _predicate=null;
    private java.util.List<EventDestination> _destinations=new java.util.Vector<EventDestination>();
    
    /**
     * The constructor for the event processor node.
     * 
     * @param ep The event processor associated with the node
     * @param predicate The optional predicate to filter the source events
     * @param destinations The list of destinations to send transformed events
     */
    public EventProcessorNode(EventProcessor<S,T> ep, Predicate<S> predicate,
                java.util.List<EventDestination> destinations) {
        _eventProcessor = ep;
        _predicate = predicate;
        _destinations = destinations;
    }
    
    /**
     * This method initializes the event processor node.
     * 
     * @param destinations The target destinations
     */
    public void init(java.util.List<EventDestination> destinations) {
        _destinations = destinations;
    }
    
    /**
     * This method returns the event processor.
     * 
     * @return The event processor
     */
    public EventProcessor<S,T> getEventProcessor() {
        return (_eventProcessor);
    }
    
    /**
     * This method returns the optional predicate that can be used
     * to filter the source events that should be processed.
     * 
     * @return The optional predicate
     */
    public Predicate<S> getPredicate() {
        return (_predicate);
    }
    
    /**
     * This method processes the supplied list of events against the
     * event processor configured with the node, to determine
     * which transformed events should be forwarded, and which need
     * to be returned to be retried.
     * 
     * @param source The source event processor node that generated the event
     * @param events The list of events to be processed
     * @param retriesLeft The number of remaining retries
     * @return The list of events to retry, or null if no retries necessary
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    public EventList<S> process(String source, EventList<S> events, int retriesLeft)
                            throws Exception {
        EventList<S> retries=null;
        EventList<T> results=new EventList<T>();
        
        for (S event : events) {
            
            if (getPredicate() == null || getPredicate().apply(event)) {
                try {
                    T processed=getEventProcessor().process(source, event, retriesLeft);
                    
                    if (processed != null) {
                        results.add(processed);
                    }
                    
                } catch(Exception e) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Retry event: "+event);
                    }
                    if (retries == null) {
                        retries = new EventList<S>();
                    }
                    retries.add(event);
                }
            }
        }
        
        if (results.size() > 0) {
            forward(results);
        }
        
        return (retries);
    }
    
    /**
     * This method forwards the results to any destinations that have been
     * defined.
     * 
     * @param results The results
     * @throws Exception Failed to forward results
     */
    protected void forward(EventList<T> results) throws Exception {
        
        for (EventDestination dest : _destinations) {
            dest.send(_eventProcessor.getName(), results);
        }
    }
}
