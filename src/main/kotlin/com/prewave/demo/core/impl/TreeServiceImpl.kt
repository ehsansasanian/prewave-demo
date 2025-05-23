package com.prewave.demo.core.impl

import com.prewave.demo.core.Edge
import com.prewave.demo.core.EdgeRepository
import com.prewave.demo.core.Node
import com.prewave.demo.core.TreeService
import com.prewave.demo.core.exceptions.EdgeNotFoundException
import com.prewave.demo.core.exceptions.EdgeOperationNotAllowedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TreeServiceImpl(private val edgeRepository: EdgeRepository) : TreeService {

    private companion object {
        const val ROOT_FROM_ID = -1
    }

    @Transactional
    override fun addEdge(edge: Edge): Boolean {
        validateEdgeForAddition(edge)
        return edgeRepository.addEdge(edge)
    }

    /*
    * I was not sure how the delete should function.
    * 1. Should it create a new tree by detaching the subtree?
    * 2. Or should it only remove the edge and assign the children of 'to_id' to 'from_id'?
    * I proceeded wit the latter.
    * */
    @Transactional
    override fun deleteEdge(fromId: Int, toId: Int): Boolean {
        // Since I allowed only one tree in the system, the root can not be deleted.
        // Otherwise, the system will end up with multiple trees.
        if (ROOT_FROM_ID == fromId) {
            throw EdgeOperationNotAllowedException("Root node can not be deleted.")
        }

        if (!edgeRepository.edgeExists(Edge(fromId, toId))) {
            throw EdgeNotFoundException("Edge from $fromId to $toId does not exist")
        }

        val reassign = edgeRepository.reassignChildrenToGrandparent(toId, fromId)

        if (!reassign) {
            // more sophisticated error handling can be done here
            throw EdgeOperationNotAllowedException("Edge could not be deleted.")
        }

        val deleteEdge = edgeRepository.deleteEdge(fromId, toId)

        if (!deleteEdge) {
            // more sophisticated error handling can be done here
            throw EdgeOperationNotAllowedException("Edge could not be deleted.")
        }

        return true
    }

    override fun getTreeByNodeId(rootId: Int): Node? {
        return edgeRepository.getNodeTreeByNodeId(rootId)
    }

    private fun validateEdgeForAddition(edge: Edge) {
        if (edge.fromId <= 0 || edge.toId <= 0) {
            throw EdgeOperationNotAllowedException("Node IDs must be greater than 0.")
        }

        // No self loops -> from_id should not be equal to to_id
        if (edge.fromId == edge.toId) {
            throw EdgeOperationNotAllowedException("Self-loops are not allowed.")
        }

        // No Duplicate edges –> if Edge already exists
        if (edgeRepository.edgeExists(edge)) {
            throw EdgeOperationNotAllowedException("Edge from ${edge.fromId} to ${edge.toId} already exists.")
        }

        /* Cyclic graph prevention – Considering there is one tree in the system */

        // Each node should have only one parent node ––> the node should only appear once in the to_id column
        if (edgeRepository.hasParent(edge.toId)) {
            throw EdgeOperationNotAllowedException("Node ${edge.toId} already has a parent.")
        }

        // Target node can not be root node
        if (edgeRepository.edgeExists(Edge(ROOT_FROM_ID, edge.toId))) {
            throw EdgeOperationNotAllowedException("Adding this edge would create a cycle.")
        }
    }
}
