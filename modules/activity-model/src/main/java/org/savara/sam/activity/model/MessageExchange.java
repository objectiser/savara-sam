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
package org.savara.sam.activity.model;

public class MessageExchange extends ActivityType {

	private String _serviceType=null;
	private String _operation=null;
	private String _fault=null;

	public enum InvocationType {
		Undefined,
		Request,
		Response
	}
	
	private InvocationType _invocationType=InvocationType.Undefined;
	
	public enum Direction {
		Inbound,
		Outbound
	}
	
	private Direction _direction=Direction.Inbound;
	
	// GPB? should we support multipart messages, where on the wire we have the diff parts?
	private String _messageType=null;
	private String _content=null;
	
	private String _correlation=null;
	
	public MessageExchange() {
	}
	
	public MessageExchange(MessageExchange mex) {
	    _serviceType = mex._serviceType;
	    _operation = mex._operation;
	    _fault = mex._fault;
	    _invocationType = mex._invocationType;
	    _direction = mex._direction;
	    _messageType = mex._messageType;
	    _content = mex._content;
	    _correlation = mex._correlation;
	}
	
	public void setServiceType(String serviceType) {
	    _serviceType = serviceType;
	}
	
	public String getServiceType() {
	    return (_serviceType);
	}
	
	public void setOperation(String operation) {
	    _operation = operation;
	}
	
	public String getOperation() {
	    return (_operation);
	}
	
	public void setFault(String fault) {
	    _fault = fault;
	}
	
	public String getFault() {
	    return (_fault);
	}
	
	public void setInvocationType(InvocationType itype) {
	    _invocationType = itype;
	}
	
	public InvocationType getInvocationType() {
	    return (_invocationType);
	}
	
	public void setDirection(Direction direction) {
	    _direction = direction;
	}
	
	public Direction getDirection() {
	    return (_direction);
	}
	
	public void setMessageType(String mtype) {
	    _messageType = mtype;
	}
	
	public String getMessageType() {
	    return (_messageType);
	}
	
	public void setContent(String content) {
	    _content = content;
	}
	
	public String getContent() {
	    return (_content);
	}
	
	public void setCorrelation(String correlation) {
	    _correlation = correlation;
	}
	
	public String getCorrelation() {
	    return (_correlation);
	}
}
