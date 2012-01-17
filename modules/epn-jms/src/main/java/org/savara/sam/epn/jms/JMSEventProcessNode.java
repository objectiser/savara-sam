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
package org.savara.sam.epn.jms;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.savara.sam.epn.EventDestination;
import org.savara.sam.epn.EventList;
import org.savara.sam.epn.EventProcessorNode;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents the JMS based Event Process Node
 * wrapper.
 *
 */
public class JMSEventProcessNode<S extends java.io.Serializable,T extends java.io.Serializable>
                                                    implements MessageListener {

    private static final String EPN_RETRY_COUNT = "EPNRetryCount";

    private static final Logger LOG=Logger.getLogger(JMSEventProcessNode.class.getName());
    
    private static final int MAX_RETRY = 6;
    
    private EventProcessorNode<S,T> _node=null;
    private Session _session=null;
    private Destination _source=null;
    private javax.jms.MessageProducer _sourceProducer=null;
    private java.util.List<EventDestination> _destinations=null;
    
    /**
     * This is the constructor for the JMS implementation of the Event
     * Processor Node.
     * 
     * @param node The Event Processor Node
     */
    public JMSEventProcessNode(EventProcessorNode<S,T> node) {
        _node = node;
    }
    
    /**
     * This method initializes the JMS event processor node.
     * 
     * @param session The JMS session
     * @param source The source JMS destination
     * @param destinations The list of target JMS destinations
     */
    public void init(Session session, Destination source, Destination... destinations) {
        
        _session = session;
        
        try {
            if (source != null) {
                _source = source;
                _sourceProducer = session.createProducer(source);
            }
            
            for (Destination d : destinations) {
                _destinations.add(new JMSEventDestination(session, session.createProducer(d)));
            }
            
            _node.init(_destinations);
            
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "Failed to setup JMS connection/session", e);
        }
    }
    
    /**
     * This method returns the JMS session.
     * 
     * @return The JMS session
     */
    protected javax.jms.Session getSession() {
        return(_session);
    }
    
    /**
     * This method closes the JMS event processor node.
     */
    public void close() throws Exception {
        ((JMSEventDestination)_source).close();
        
        for (EventDestination ed : _destinations) {
            ((JMSEventDestination)ed).close();
        }
    }
    
    /**
     * This method receives the JMS message.
     * 
     * @param message The message
     */
    @SuppressWarnings("unchecked")
    public void onMessage(Message message) {
        
        if (message instanceof ObjectMessage) {
            try {
                String sourceAQName=message.getStringProperty(EventProcessorNode.EPN_NAME);
                int retriesLeft=MAX_RETRY-getRetryCount(message);
             
                EventList<S> events=(EventList<S>)((ObjectMessage)message).getObject();
                
                EventList<S> retries=_node.process(sourceAQName, events, retriesLeft);
                
                if (retries != null) {
                    // Send retry request with only those activities that failed
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Sending retry list with: "+retries);
                    }

                    javax.jms.ObjectMessage om=getSession().createObjectMessage(retries);
                    java.util.Enumeration<?> iter=message.getPropertyNames();
                    while (iter.hasMoreElements()) {
                        String name=(String)iter.nextElement();
                        if (!name.startsWith("JMSX")) {
                            om.setObjectProperty(name, message.getObjectProperty(name));
                        }
                    }
                    
                    retry(om);
                }
            } catch(Exception e) {
                LOG.severe("Failed to process message: "+message);
                
                // TODO: Need to rollback the transaction
                
            }
        }
    }
    
    protected int getRetryCount(javax.jms.Message message) throws Exception {
        int ret=0;
        
        if (message.propertyExists(EPN_RETRY_COUNT)) {
            ret = message.getIntProperty(EPN_RETRY_COUNT);
        } else {
            ret = 0;
        }

        return(ret);
    }
    
    protected boolean retry(javax.jms.ObjectMessage message) throws Exception {
        boolean ret=false;
        
        if (message.propertyExists(EPN_RETRY_COUNT)) {
            int retryCount=message.getIntProperty(EPN_RETRY_COUNT);

            if (retryCount < MAX_RETRY) {
                message.setIntProperty(EPN_RETRY_COUNT, retryCount+1);
                ret = true;
            } else {
                LOG.severe("Max retries ("+MAX_RETRY+") reached for message="+
                                message+" contents="+message.getObject());
            }
        } else {
            message.setIntProperty(EPN_RETRY_COUNT, 1);
            ret = true;
        }
        
        if (ret) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Retrying message: "+message);
            }
            
            _sourceProducer.send(message);
        }
        
        return(ret);
    }
}
