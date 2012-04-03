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

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.savara.sam.epn.AbstractEPNManager;
import org.savara.sam.epn.EPNContext;
import org.savara.sam.epn.EPNManager;
import org.savara.sam.epn.EventList;
import org.savara.sam.epn.Network;

/**
 * This class provides the JMS implementation of
 * the EPN Manager.
 *
 */
@Singleton
@ConcurrencyManagement(BEAN)
@Startup
@Local(EPNManager.class)
public class JMSEPNManager extends AbstractEPNManager implements javax.jms.MessageListener {
    
    @Resource(mappedName = "java:/JmsXA")
    ConnectionFactory _connectionFactory;
    
    @Resource(mappedName = "java:/queue/EPNServer")
    Destination _epnServerDestination;
    
    public static final String EPN_NETWORK = "EPNNetwork";
    public static final String EPN_DESTINATION_NODE = "EPNDestinationNode";
    public static final String EPN_SOURCE_NODE = "EPNSourceNode";
    public static final String EPN_RETRIES_LEFT = "EPNRetriesLeft";
    
    private Connection _connection=null;
    private Session _session=null;
    private MessageProducer _producer=null;
    
    private EPNContext _context=null;
    
    private static final Logger LOG=Logger.getLogger(JMSEPNManager.class.getName());

    protected EPNContext getContext() {
        return(_context);
    }
    
    public void enqueue(String network, List<?> events) throws Exception {
        
    }

    /**
     * This method dispatches a set of events directly to the named
     * network and node. If the node is not specified, then it will
     * be dispatched to the 'root' node of the network.
     * 
     * @param network The name of the network
     * @param node The optional node name, or root node if not specified
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left
     * @throws Exception Failed to dispatch the events for processing
     */
    protected void dispatch(String network, String node, String source, List<?> events,
                            int retriesLeft) throws Exception {
        
    }

    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("EPNManager("+this+"): Received event batch: "+message);
            }
            
            try {
                EventList<?> events=(EventList<?>)((ObjectMessage)message).getObject();
                
                String network=message.getStringProperty(JMSEPNManager.EPN_NETWORK);
                String node=message.getStringProperty(JMSEPNManager.EPN_DESTINATION_NODE);
                String source=message.getStringProperty(JMSEPNManager.EPN_SOURCE_NODE);
                int retriesLeft=message.getIntProperty(JMSEPNManager.EPN_RETRIES_LEFT);
                
                dispatch(network, node, source, events, retriesLeft);
                
            } catch(Exception e) {
                LOG.severe("Failed to handle events: "+e);
            }
        }
    }

    public void close() throws Exception {
        try {
            _session.close();
            _connection.close();
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "Failed to close JMS", e);
        }
    }
}
