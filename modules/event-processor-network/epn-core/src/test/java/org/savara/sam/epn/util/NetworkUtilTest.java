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
package org.savara.sam.epn.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.sam.epn.Destination;
import org.savara.sam.epn.Network;
import org.savara.sam.epn.Node;
import org.savara.sam.epn.testdata.TestEvent;
import org.savara.sam.epn.testdata.TestEventProcessor1;
import org.savara.sam.epn.testdata.TestEventProcessor2;
import org.savara.sam.epn.testdata.TestEventProcessor3;
import org.savara.sam.epn.testdata.TestPredicate1;
import org.savara.sam.epn.testdata.TestPredicate2;
import org.savara.sam.epn.util.NetworkUtil;

public class NetworkUtilTest {

    @Test
    public void testSerializeEPN() {
        Network<TestEvent> epn=new Network<TestEvent>();
        
        epn.setName("Test");
        
        // Event destinations
        Destination ed1=new Destination();
        ed1.setName("N1");
        
        // Node 0
        Node<TestEvent,TestEvent> n0=new Node<TestEvent,TestEvent>();
        n0.setName("Test");
        epn.getNodes().add(n0);
        
        n0.getDestinations().add(ed1);
        n0.setEventProcessor(new TestEventProcessor1<TestEvent,TestEvent>());
        n0.setPredicate(new TestPredicate1<TestEvent>());
        
        // Node 1
        Node<TestEvent,TestEvent> n1=new Node<TestEvent,TestEvent>();
        n1.setName("N1");
        epn.getNodes().add(n1);
        
        TestEventProcessor2<TestEvent,TestEvent> ep2=new TestEventProcessor2<TestEvent,TestEvent>();
        n1.setEventProcessor(ep2);
        TestPredicate2<TestEvent> tp2=new TestPredicate2<TestEvent>();
        tp2.setSomeProperty("TestProperty");
        n1.setPredicate(tp2);
        
        // Node 2
        Node<TestEvent,TestEvent> n2=new Node<TestEvent,TestEvent>();
        n2.setName("N2");
        epn.getNodes().add(n2);
        
        TestEventProcessor3<TestEvent,TestEvent> ep3=new TestEventProcessor3<TestEvent,TestEvent>();
        n2.setEventProcessor(ep3);
        
        try {
            byte[] b=NetworkUtil.serialize(epn);
            
            String str=new String(b);
            
            java.io.InputStream is=NetworkUtilTest.class.getResourceAsStream("/jsondata/TestNetwork1.json");
            
            b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            String expected=new String(b);
            
            if (!str.equals(expected)) {
                fail("Output mismatch");
            }
            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }
    }

    @Test
    public void testDeserializeEPN() {
        try {
            java.io.InputStream is=NetworkUtilTest.class.getResourceAsStream("/jsondata/TestNetwork1.json");
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            Network<?> network=NetworkUtil.deserialize(b);
            
            if (network.getNodes().size() != 3) {
                fail("Number of nodes not 3: "+network.getNodes().size());
            }
            
            Node<?,?> n1=network.getNodes().get(0);
            Node<?,?> n2=network.getNodes().get(1);
            Node<?,?> n3=network.getNodes().get(2);
            
            if (n1.getPredicate() == null) {
                fail("Predicate 1 should not be null");
            }
            
            if (!(n1.getPredicate() instanceof TestPredicate1)) {
                fail("Predicate 1 not correct class");
            }
            
            if (n1.getEventProcessor() == null) {
                fail("Event Processor 1 should not be null");
            }
            
            if (!(n1.getEventProcessor() instanceof TestEventProcessor1)) {
                fail("Event Processor 1 not correct class");
            }
            
            if (n2.getPredicate() == null) {
                fail("Predicate 2 should not be null");
            }
            
            if (!(n2.getPredicate() instanceof TestPredicate2)) {
                fail("Predicate 2 not correct class");
            }
            
            if (n2.getEventProcessor() == null) {
                fail("Event Processor 2 should not be null");
            }
            
            if (!(n2.getEventProcessor() instanceof TestEventProcessor2)) {
                fail("Event Processor 2 not correct class");
            }
            
            if (n3.getPredicate() != null) {
                fail("Predicate 3 should be null");
            }
            
            if (n3.getEventProcessor() == null) {
                fail("Event Processor 3 should not be null");
            }
            
            if (!(n3.getEventProcessor() instanceof TestEventProcessor3)) {
                fail("Event Processor 3 not correct class");
            }
            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }

    }
}
