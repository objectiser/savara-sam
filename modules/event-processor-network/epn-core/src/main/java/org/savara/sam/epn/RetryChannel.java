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
 * This interface provides the channel through which a
 * set of events will be retried.
 *
 */
public interface RetryChannel {

    /**
     * This method sends the supplied events, for
     * retry against the same node, indicating how
     * many remaining retry attempts are left. This
     * value will have been decremented by the node,
     * before calling this method, and therefore the
     * number should be carried (as is) with the set
     * of events, and re-presented to the node.
     * 
     * @param events The events
     * @param retriesRemaining The number of retries remaining
     * @throws Exception Failed to send the events
     */
    public void send(EventList<?> events, int retriesRemaining) throws Exception;

    /**
     * This method closes the channel.
     * 
     * @throws Exception Failed to close the channel
     */
    public void close() throws Exception;
    
}
