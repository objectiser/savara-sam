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

import org.savara.sam.epn.Channel;
import org.savara.sam.epn.Destination;
import org.savara.sam.epn.EventList;

/**
 * This class represents a JMS implementation of the event destination
 * for sending a list of events.
 *
 */
public class JMSChannel implements Channel {
    
    private javax.jms.Session _session=null;
    private javax.jms.MessageProducer _producer=null;
    private Destination _destination=null;

    /**
     * This is the constructor for the JMS channel.
     * 
     * @param session The session
     * @param producer The producer
     * @param destination The node destination
     */
    public JMSChannel(javax.jms.Session session, javax.jms.MessageProducer producer,
                            Destination dest) {
        _session = session;
        _producer = producer;
        _destination = dest;
    }
    
    /**
     * This method sends the supplied events to a channel.
     * 
     * @param source The source node within the network
     * @param events The events
     * @throws Exception Failed to send the events
     */
    public void send(String source, EventList<?> events) throws Exception {
        javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
        mesg.setStringProperty(JMSEPNManager.EPN_NETWORK, _destination.getNetwork());
        mesg.setStringProperty(JMSEPNManager.EPN_DESTINATION_NODE, _destination.getNode());
        mesg.setStringProperty(JMSEPNManager.EPN_SOURCE_NODE, source);
        mesg.setIntProperty(JMSEPNManager.EPN_RETRIES_LEFT, -1);
        _producer.send(mesg);
    }
 
    /**
     * This method closes the JMS channel.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        // Creator is responsible for closing JMS session
    }
}
