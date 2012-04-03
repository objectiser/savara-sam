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


public abstract class AbstractEPNManager implements EPNManager {

    private java.util.Map<String, Network> _networkMap=new java.util.HashMap<String, Network>();
    
    protected abstract EPNContext getContext();
    
    public void register(Network network) throws Exception {
        _networkMap.put(network.getName(), network);
        
        network.init(getContext());
    }

    public void unregister(String networkName) throws Exception {
        _networkMap.remove(networkName);
    }
    
    protected Network getNetwork(String name) {
        return (_networkMap.get(name));
    }

    /**
     * This method dispatches a set of events directly to the named
     * network and node. If the node is not specified, then it will
     * be dispatched to the 'root' node of the network.
     * 
     * @param networkName The name of the network
     * @param nodeName The optional node name, or root node if not specified
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left
     * @throws Exception Failed to dispatch the events for processing
     */
    protected void dispatch(String networkName, String nodeName, String source, EventList events,
                            int retriesLeft) throws Exception {
        Network net=getNetwork(networkName);
        
        if (net == null) {
            throw new Exception("No network '"+networkName+"' was found");
        }
        
        Node node=net.getNodes().get(nodeName);
        
        if (node == null) {
            throw new Exception("No node '"+nodeName+"' was found in network '"+networkName+"'");
        }
        
        node.process(getContext(), source, events, retriesLeft);
    }

    public void close() throws Exception {
    }

}
