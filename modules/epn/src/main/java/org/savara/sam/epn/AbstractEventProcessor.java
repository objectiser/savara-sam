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

/**
 * This interface defines an event processor responsible
 * for processing events, and where appropriate, forwarding
 * results to other awaiting event processors.
 *
 */
public abstract class AbstractEventProcessor<S,T> implements EventProcessor<S,T> {
    
    private String _name=null;
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return (_name);
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method initializes the event processor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
    }
    
    /**
     * This method closes the event processor.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }

}
