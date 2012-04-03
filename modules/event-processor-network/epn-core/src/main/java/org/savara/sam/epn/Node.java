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
public class Node<S extends java.io.Serializable,T extends java.io.Serializable> {

    private static Logger LOG=Logger.getLogger(Node.class.getName());
    
    private String _name=null;
    private int _maxRetries=3;
    private long _retryInterval=0;
    private EventProcessor<S,T> _eventProcessor=null;
    private Predicate<S> _predicate=null;
    private java.util.List<Destination> _destinations=new java.util.Vector<Destination>();
    
    private java.util.List<Channel> _channels=new java.util.Vector<Channel>();
    private RetryChannel _retryChannel=null;
    
    /**
     * The default constructor for the event processor node.
     * 
     */
    public Node() {
    }
    
    /**
     * This method returns the node name.
     * 
     * @return The node name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the node name.
     * 
     * @param name The node name
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * This method returns the maximum number of retries
     * for processing an event.
     * 
     * @return The maximum number of events
     */
    public int getMaxRetries() {
        return (_maxRetries);
    }
    
    /**
     * This method sets the maximum number of retries
     * for processing an event.
     * 
     * @param max The maximum number of events
     */
    public void setMaxRetries(int max) {
        _maxRetries = max;
    }
    
    /**
     * This method returns the retry interval. A
     * value of 0 means use the default for the
     * channel.
     * 
     * @return The retry interval, or 0 to use the default for the channel
     */
    public long getRetryInterval() {
        return (_retryInterval);
    }
    
    /**
     * This method sets the retry interval. A
     * value of 0 means use the default value for the
     * channel.
     * 
     * @param interval The retry interval
     */
    public void setRetryInterval(long interval) {
        _retryInterval = interval;
    }
    
    /**
     * This method returns the list of destinations.
     * 
     * @return The destinations
     */
    public java.util.List<Destination> getDestinations() {
        return (_destinations);
    }
    
    /**
     * This method sets the list of destinations.
     * 
     * @param destinations The destinations
     */
    public void setDestinations(java.util.List<Destination> destinations) {
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
     * This method sets the event processor.
     * 
     * @param ep The event processor
     */
    public void setEventProcessor(EventProcessor<S,T> ep) {
        _eventProcessor = ep;
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
     * This method sets the optional predicate that can be used
     * to filter the source events that should be processed.
     * 
     * @param pred The optional predicate
     */
    public void setPredicate(Predicate<S> pred) {
        _predicate = pred;
    }
    
    /**
     * This method initializes the node.
     * 
     * @param context The context
     * @throws Exception Failed to initialize the node
     */
    protected void init(EPNContext context) throws Exception {
        
        // Obtain the channels associated with the specified destinations
        if (_destinations != null) {
            for (Destination d : _destinations) {
                _channels.add(context.getChannel(d));
            }
        }
        
        _retryChannel = context.getRetryChannel(this);
        
        if (getPredicate() != null) {
            getPredicate().init(context);
        }
        
        if (getEventProcessor() == null) {
            throw new Exception("Event Processor has not been configured for node '"+getName()+"'");
        }
        
        getEventProcessor().init(context);
    }
    
    /**
     * This method processes the supplied list of events against the
     * event processor configured with the node, to determine
     * which transformed events should be forwarded, and which need
     * to be returned to be retried.
     * 
     * @param context The context
     * @param source The source event processor node that generated the event
     * @param events The list of events to be processed
     * @param retriesLeft The number of remaining retries
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    protected void process(EPNContext context, String source,
                      EventList<S> events, int retriesLeft) throws Exception {
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
            forward(context, results);
        }
        
        if (retries != null) {
            retry(context, retries, retriesLeft);
        }
    }
    
    /**
     * This method forwards the results to any destinations that have been
     * defined.
     * 
     * @param context The context
     * @param results The results
     * @throws Exception Failed to forward results
     */
    protected void forward(EPNContext context, EventList<T> results) throws Exception {
        
        for (Channel ch : _channels) {
            ch.send(getName(), results);
        }
    }

    /**
     * This method forwards the results to any destinations that have been
     * defined.
     * 
     * @param context The context
     * @param events The events
     * @param retriesLeft The number of retries left
     * @throws Exception Failed to forward results
     */
    protected void retry(EPNContext context, EventList<S> events, int retriesLeft) throws Exception {
        
        if (_retryChannel != null && retriesLeft > 0) {
            _retryChannel.send(events, retriesLeft-1);
        } else {
            context.eventProcessingFailed(events);
        }
    }

    /**
     * This method closes the node.
     * 
     * @param context The container context
     * @throws Exception Failed to close the node
     */
    protected void close(EPNContext context) throws Exception {
        
        for (Channel ch : _channels) {
            ch.close();
        }
        
        if (_retryChannel != null) {
            _retryChannel.close();
        }
        
        if (getPredicate() != null) {
            getPredicate().close(context);
        }
        
        if (getEventProcessor() == null) {
            getEventProcessor().close(context);
        }
    }
    
}
