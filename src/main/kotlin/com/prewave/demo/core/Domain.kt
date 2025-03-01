package com.prewave.demo.core

/*
 * Edge represents a connection between two companies in the supply chain
 * No FK constraints are implemented as not requested in the task
 * There is one supply chain tree –– This can be extended to have multiple trees. For simplicity, the implementation is for a single tree.
 * I initialized the root as: from_id = -1, to_id = <POSITIVE INTEGER>
 */
data class Edge(val fromId: Int, val toId: Int)

/*
 * Node represents a company in the supply chain
 * Each company can have multiple suppliers (children)
 */
class Node(val id: Int) {
    private val children: MutableList<Node> = mutableListOf()

    fun addChild(node: Node) {
        children.add(node)
    }

    fun getChildren(): List<Node> {
        return children.toList()
    }

    fun hasChild(nodeId: Int): Boolean {
        return children.any { it.id == nodeId }
    }
}
