package com.prewave.demo.core

/**
 * Repository interface for managing edges in the supply chain tree.
 */
interface EdgeRepository {
    /**
     * Adds a new edge to the database.
     * @param edge The edge to be added
     * @return true if edge was added successfully, false if it already exists
     */
    fun addEdge(edge: Edge): Boolean

    /**
     * Deletes an existing edge from the database.
     * @param fromId The ID of the source node
     * @param toId The ID of the target node
     * @return true if edge was deleted successfully, false if it doesn't exist
     */
    fun deleteEdge(fromId: Int, toId: Int): Boolean

    /**
     * Checks if an edge exists between the specified nodes.
     * @param fromId The ID of the source node
     * @param toId The ID of the target node
     * @return true if the edge exists, false otherwise
     */
    fun edgeExists(edge: Edge): Boolean

    /**
     * Checks if a node already has a parent (any incoming edge)
     * @param nodeId The ID of the node to check
     * @return true if the node already has a parent, false otherwise
     */
    fun hasParent(nodeId: Int): Boolean

    /**
     * Retrieves a node and its tree structure based on the node ID.
     * Note: This method would typically belong in NodeRepository, but due to 
     * architectural simplifications (no separate node table), it's implemented here.
     * 
     * @param nodeId The ID of the node to retrieve
     * @param maxDepth Maximum depth of the tree to retrieve (to prevent infinite recursion)
     * @return The node with its complete tree structure, or null if node doesn't exist
     */
    fun getNodeTreeByNodeId(nodeId: Int, maxDepth: Int = 100): Node?
}
