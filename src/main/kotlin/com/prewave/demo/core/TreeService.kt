package com.prewave.demo.core

/**
 * Service interface for managing the supply chain tree structure.
 */
interface TreeService {
    /**
     * Adds a new edge to the tree.
     * @param edge The edge to be added
     * @return true if the edge was successfully added, false if it already exists
     * @throws IllegalArgumentException if the edge would create a cycle
     */
    fun addEdge(edge: Edge): Boolean
    
    /**
     * Deletes an existing edge from the tree.
     * @param fromId The ID of the source node
     * @param toId The ID of the target node
     * @return true if the edge was successfully deleted, false if it doesn't exist
     */
    fun deleteEdge(fromId: Int, toId: Int): Boolean
    
    /**
     * Retrieves the entire tree structure with the specified node as the root.
     * @param rootId The ID of the root node
     * @return The root Node with all its descendants
     * @throws IllegalArgumentException if the node doesn't exist
     */
    fun getTreeByNodeId(rootId: Int): Node?
}
