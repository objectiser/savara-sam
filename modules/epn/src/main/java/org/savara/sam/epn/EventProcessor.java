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
public interface EventProcessor<S,T> {

    /**
     * This method processes the supplied event, and optionally
     * returns a transformed representation to be forwarded to
     * other processors. If the event cannot be processed at
     * this time, then an exception should be thrown to initiate
     * a retry. The number of remaining retries is supplied,
     * to enable the processor to take appropriate error
     * reporting action.
     * 
     * @param event The event to process
     * @param retriesLeft The number of retries left
     * @return The optional transformed representation of the event
     * @throws Exception Failed to process event, requesting retry
     */
    public T process(S event, int retriesLeft) throws Exception;
    
}
