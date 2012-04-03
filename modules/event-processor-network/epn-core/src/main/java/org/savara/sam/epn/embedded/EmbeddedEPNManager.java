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
package org.savara.sam.epn.embedded;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.savara.sam.epn.AbstractEPNManager;
import org.savara.sam.epn.EPNContext;
import org.savara.sam.epn.EPNManager;
import org.savara.sam.epn.EventList;
import org.savara.sam.epn.Network;

/**
 * This class provides the embedded implementation of
 * the EPN Manager.
 *
 */
public class EmbeddedEPNManager extends AbstractEPNManager {
    
    private static final int MAX_THREADS = 10;

    private ExecutorService _executor=Executors.newFixedThreadPool(MAX_THREADS);
    private EPNContext _context=null;
    
    protected EPNContext getContext() {
        return(_context);
    }
    
    public void enqueue(String network, EventList events) throws Exception {
        
    }

    public void dispatch(String network, String node, List<?> events)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void close() throws Exception {
        // TODO Auto-generated method stub
        
    }

    protected class EPNTask implements Runnable {
        
        private String _network=null;
        private String _node=null;
        private List<?> _events=null;
        
        public EPNTask(String network, String node, List<?> events) {
            _network = network;
            _node = node;
            _events = events;
        }

        public void run() {
            // TODO Auto-generated method stub
            
        }
        
    }
}
