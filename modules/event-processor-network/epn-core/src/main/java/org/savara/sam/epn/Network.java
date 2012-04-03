/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
 * This class represents an Event Processor Network.
 *
 */
public class Network<S extends java.io.Serializable> {

    private String _name=null;
    private String _rootNodeName=null;
    private java.util.List<Node<?,?>> _nodes=new java.util.Vector<Node<?,?>>();
    
    private Node<S,?> _root=null;
    
    /**
     * The default constructor.
     */
    public Network() {
    }
    
    /**
     * This method returns the name of the network. This can be used
     * to locate the network by name.
     * 
     * @return The name of the network
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the network.
     * 
     * @param name The name of the network
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * This method returns the root node name.
     * 
     * @return The root node name
     */
    public String getRootNodeName() {
        return (_rootNodeName);
    }
    
    /**
     * This method sets the root node name.
     * 
     * @param rootNodeName The root node name
     */
    public void setRootNodeName(String rootNodeName) {
        _rootNodeName = rootNodeName;
    }
    
    /**
     * This method returns the event processor nodes.
     * 
     * @return The event processor nodes
     */
    public java.util.List<Node<?,?>> getNodes() {
        return (_nodes);
    }
    
    /**
     * This method sets the event processor nodes.
     * 
     * void nodes The event processor nodes
     */
    public void setNodes(java.util.List<Node<?,?>> nodes) {
        _nodes = nodes;
    }

    /**
     * This method initializes the network.
     * 
     * @param context The container context
     * @throws Exception Failed to initialize the network
     */
    @SuppressWarnings("unchecked")
    protected void init(EPNContext context) throws Exception {
        for (Node<?,?> node : _nodes) {
            node.init(context);
            
            if (node.getName().equals(getRootNodeName())) {
                _root = (Node<S,?>)node;                
            }
        }
        
        if (_root == null) {
            throw new Exception("Network does not contain a root node of name '"+getRootNodeName()+"'");
        }
    }
    
    /**
     * This method processes the supplied list of events against the root
     * event processor node associated with the network.
     * 
     * @param context The context
     * @param events The list of events to be processed
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    protected void process(EPNContext context, EventList<S> events) throws Exception {
 
        if (_root != null) {
            _root.process(context, null, events, _root.getMaxRetries());
        }
    }

    /**
     * This method closes the network.
     * 
     * @param context The container context
     * @throws Exception Failed to close the network
     */
    protected void close(EPNContext context) throws Exception {
        for (Node<?,?> node : _nodes) {
            node.close(context);
        }
    }
    
}
