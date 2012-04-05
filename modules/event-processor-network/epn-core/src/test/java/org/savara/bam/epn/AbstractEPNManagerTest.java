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
package org.savara.bam.epn;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.epn.testdata.TestEventProcessorA;

public class AbstractEPNManagerTest {

    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String N3 = "N3";
    private static final String TEST_NETWORK = "TestNetwork";

    @Test
    public void testRegisterNetworkIncorrectRootNodeName() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N2);
        
        Node n1=new Node();
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put(N1, n1);
        
        AbstractEPNManager mgr=new AbstractEPNManager() {
            
            public void enqueue(String network,
                    EventList events) throws Exception {
            }
            
            public EPNContext getContext() {
                return null;
            }
        };
        
        try {
            mgr.register(net);
            
            fail("Network registration should fail due to missing or incorrect root node");
        } catch(Exception e) {
        }
    }

    @Test
    public void testRegisterNetworkNodeNoEventProcessor() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        
        Node n1=new Node();
        net.getNodes().put(N1, n1);
        
        AbstractEPNManager mgr=new AbstractEPNManager() {
            
            public void enqueue(String network,
                    EventList events) throws Exception {
            }
            
            public EPNContext getContext() {
                return null;
            }
        };
        
        try {
            mgr.register(net);
            
            fail("Network registration should fail due to node with missing event processor");
        } catch(Exception e) {
        }
    }

    @Test
    public void testNetworkAndNodeLookup() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        
        Node n1=new Node();
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put(N1, n1);
        
        Node n2=new Node();
        n2.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put("N2", n2);
        
        Node n3=new Node();
        n3.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put("N3", n3);
        
        AbstractEPNManager mgr=new AbstractEPNManager() {
            
            public void enqueue(String network,
                    EventList events) throws Exception {
            }
            
            public EPNContext getContext() {
                return null;
            }
        };
        
        try {
            mgr.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        if (mgr.getNetwork(TEST_NETWORK) != net) {
            fail("Failed to find test network");
        }
        
        try {
            if (mgr.getNode(TEST_NETWORK, N1) != n1) {
                fail("Failed to find node n1");
            }
            if (mgr.getNode(TEST_NETWORK, N2) != n2) {
                fail("Failed to find node n2");
            }
            if (mgr.getNode(TEST_NETWORK, N3) != n3) {
                fail("Failed to find node n3");
            }
        } catch(Exception e) {
            fail("Failed to find node");
        }
    }

}
