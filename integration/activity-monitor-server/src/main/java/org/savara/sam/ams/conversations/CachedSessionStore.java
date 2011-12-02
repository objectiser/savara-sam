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
package org.savara.sam.ams.conversations;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.infinispan.AdvancedCache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.savara.common.config.Configuration;
import org.savara.monitor.ConversationId;
import org.savara.monitor.SessionStore;
import org.savara.protocol.ProtocolId;

public class CachedSessionStore implements SessionStore {

	private static final Logger LOG=Logger.getLogger(CachedSessionStore.class.getName());

	private static AdvancedCache<ProtocolConversationKey,Serializable> _cache=null;

	public CachedSessionStore(org.infinispan.manager.CacheContainer cc) {
		
		if (_cache == null) {
			EmbeddedCacheManager manager = new DefaultCacheManager(); //"infinispan-config-file.xml");
	
			org.infinispan.config.Configuration config = manager.getDefaultConfiguration().clone().fluent()
					  .locking()
					    .concurrencyLevel(10000).isolationLevel(IsolationLevel.REPEATABLE_READ)
					    .lockAcquisitionTimeout(12000L).useLockStriping(false).writeSkewCheck(true)
					  .transaction()
					    .lockingMode(LockingMode.PESSIMISTIC)
					    .recovery()
					    .transactionManagerLookup(new GenericTransactionManagerLookup())
					  .build();
			
			String newCacheName = "conversationSessions";
			manager.defineConfiguration(newCacheName, config);
			
			org.infinispan.Cache<ProtocolConversationKey,Serializable> cache=manager.getCache(newCacheName);
			//org.infinispan.Cache<ProtocolConversationKey,Serializable> cache=cc.getCache("conversationSessions");
	
			_cache = cache.getAdvancedCache()
					.withFlags(Flag.FAIL_SILENTLY, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
		}
	}
	
	public Serializable create(ProtocolId pid, ConversationId cid,
							Serializable value) {
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Create session for pid="+pid+" cid="+cid+" value="+value);	
		}

		java.io.Serializable ret=_cache.put(new ProtocolConversationKey(pid, cid), value);
				
		if (ret != null && value != ret) {
			LOG.warning("Created session '"+value+"' is not same as returned '"+ret+"'");
		}
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Created session for pid="+pid+" cid="+cid+" value="+value+" ret="+ret);	
		}

		return (ret);
	}

	public Serializable find(ProtocolId pid, ConversationId cid) {
		ProtocolConversationKey key=new ProtocolConversationKey(pid, cid);
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Finding session for pid="+pid+" cid="+cid);			
		}
		
		java.io.Serializable ret=_cache.get(key);
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Found session for pid="+pid+" cid="+cid+" ret="+ret);			
		}
		
		return (ret);
	}

	public void remove(ProtocolId pid, ConversationId cid) {
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Remove session for pid="+pid+" cid="+cid);			
		}

		Serializable session=_cache.remove(new ProtocolConversationKey(pid, cid));
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Removed session for pid="+pid+" cid="+cid+" session="+session);			
		}
	}

	public void setConfiguration(Configuration config) {
	}

	public void update(ProtocolId pid, ConversationId cid,
					Serializable value) {
		ProtocolConversationKey key=new ProtocolConversationKey(pid, cid);
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Update session for pid="+pid+" cid="+cid+" value="+value);			
		}
			
		if (_cache.lock(key)) {
			_cache.replace(key, value);
		} else {
			LOG.info("GPB: Unable to acquire lock for: "+key);
			throw new RuntimeException("RETRY DUE TO NOT ACQUIRING LOCK");
		}
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Updated session for pid="+pid+" cid="+cid+" value="+value);			
		}
	}
	
	public void close() {
	}

	public static class ProtocolConversationKey implements java.io.Serializable {

		private static final long serialVersionUID = 3223220549261325929L;
		
		private ProtocolId _protocolId=null;
		private ConversationId _conversationId=null;
		
		public ProtocolConversationKey(ProtocolId pid, ConversationId cid) {
			_protocolId = pid;
			_conversationId = cid;
		}
		
		public int hashCode() {
			return (_protocolId.hashCode());
		}
		
		public boolean equals(Object obj) {
			boolean ret=false;
			
			if (obj instanceof ProtocolConversationKey) {
				ProtocolConversationKey pck=(ProtocolConversationKey)obj;
				
				if (pck._protocolId.equals(_protocolId) &&
						pck._conversationId.equals(_conversationId)) {
					ret = true;
				}
			}
			
			return(ret);
		}
		
		public String toString() {
			return("["+_protocolId+"/"+_conversationId+"]");
		}
	}
}
