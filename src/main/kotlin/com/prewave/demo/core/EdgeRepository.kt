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
    fun edgeExists(fromId: Int, toId: Int): Boolean

    /**
     * Checks if a node already has a parent (any incoming edge)
     * @param nodeId The ID of the node to check
     * @return true if the node already has a parent, false otherwise
     */
    fun hasParent(nodeId: Int): Boolean

    fun getTreeByNodeId(nodeId: Int, givenMaxDepth: Int = 100): List<Edge>
}
