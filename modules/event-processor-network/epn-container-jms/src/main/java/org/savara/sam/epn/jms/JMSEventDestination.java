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

import org.savara.sam.epn.EventDestination;
import org.savara.sam.epn.EventList;
import org.savara.sam.epn.EventProcessorNode;

/**
 * This class represents a JMS implementation of the event destination
 * for sending a list of events.
 *
 */
public class JMSEventDestination implements EventDestination {
    
    private javax.jms.Session _session=null;
    private javax.jms.MessageProducer _producer=null;

    /**
     * This is the constructor for the JMS event destination.
     * 
     * @param session The session
     * @param producer The producer
     */
    public JMSEventDestination(javax.jms.Session session, javax.jms.MessageProducer producer) {
        _session = session;
        _producer = producer;
    }
    
    /**
     * This method sends the supplied events to a destination.
     * 
     * @param source The source
     * @param events The events
     * @throws Exception Failed to send the events
     */
    public void send(String source, EventList<?> events) throws Exception {
        javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
        mesg.setStringProperty(EventProcessorNode.EPN_NAME, source);
        _producer.send(mesg);
    }
 
    /**
     * This method closes the JMS event destination.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        _producer.close();
    }
}
