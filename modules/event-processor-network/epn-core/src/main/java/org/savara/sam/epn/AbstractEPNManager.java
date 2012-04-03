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

    private java.util.Map<String, Network<?>> _networkMap=new java.util.HashMap<String, Network<?>>();
    
    protected abstract EPNContext getContext();
    
    public void register(Network<?> network) throws Exception {
        _networkMap.put(network.getName(), network);
        
        network.init(getContext());
    }

    public void unregister(Network<?> network) throws Exception {
        _networkMap.remove(network.getName());
    }
    
    protected Network<?> getNetwork(String name) {
        return (_networkMap.get(name));
    }

    public void close() throws Exception {
    }

}
