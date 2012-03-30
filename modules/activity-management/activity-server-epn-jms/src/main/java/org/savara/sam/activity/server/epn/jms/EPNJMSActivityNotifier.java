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
package org.savara.sam.activity.server.epn.jms;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.savara.sam.activity.model.Activity;
import org.savara.sam.activity.server.spi.ActivityNotifier;
import org.savara.sam.epn.EventList;
import org.savara.sam.epn.util.jms.JMSEventDestination;

/**
 * This class provides a bridge between the Activity Server, where
 * activity events are reported, and the Event Processor Network
 * which will be used to provide additional processing and analysis
 * of the activity events.
 *
 */
public class EPNJMSActivityNotifier implements ActivityNotifier {

    private static final Logger LOG=Logger.getLogger(EPNJMSActivityNotifier.class.getName());
    
    @Resource(mappedName = "java:/queue/sam/activities")
    private Destination _destination;
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory _connectionFactory;
    
    private Connection _connection=null; 
    private Session _session=null;
    private MessageProducer _producer=null;
    
    private JMSEventDestination _epnSender=null;
    
    /**
     * This method initializes the Activity Server to EPN bridge.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN (via JMS) bridge");
        }
        
        try {
            _connection = _connectionFactory.createConnection();
            _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            _producer = _session.createProducer(_destination);
            
            _epnSender = new JMSEventDestination(_session, _producer);
            
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "Failed to setup JMS connection/session", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void notify(List<Activity> activities) throws Exception {
        _epnSender.send(null, new EventList<Activity>(activities));
    }

    /**
     * This method closes the Activity Server to EPN bridge.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN (via JMS) bridge");
        }

        try {                       
            _session.close();
            _connection.close();
            
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "Failed to close JMS connection/session", e);
        }
    }
}
