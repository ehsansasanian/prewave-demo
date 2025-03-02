package com.prewave.demo.data

import com.prewave.demo.core.Edge
import com.prewave.demo.core.EdgeRepository
import com.prewave.demo.core.Node
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class JooqEdgeRepository(private val dsl: DSLContext) : EdgeRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun addEdge(edge: Edge): Boolean {
        if (edgeExists(edge)) {
            return false
        }

        return try {
            dsl.insertInto(Tables.EDGETable)
                .set(Tables.EDGETable.FROM_ID, edge.fromId)
                .set(Tables.EDGETable.TO_ID, edge.toId)
                .execute() > 0
        } catch (e: Exception) {
            logger.error("Failed to add edge from ${edge.fromId} to ${edge.toId}", e)
            throw e
        }
    }

    override fun deleteEdge(fromId: Int, toId: Int): Boolean {
        return dsl.deleteFrom(Tables.EDGETable)
            .where(Tables.EDGETable.FROM_ID.eq(fromId))
            .and(Tables.EDGETable.TO_ID.eq(toId))
            .execute() > 0
    }

    override fun edgeExists(edge: Edge): Boolean {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(Tables.EDGETable)
                .where(Tables.EDGETable.FROM_ID.eq(edge.fromId))
                .and(Tables.EDGETable.TO_ID.eq(edge.toId))
        )
    }

    override fun hasParent(nodeId: Int): Boolean {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(Tables.EDGETable)
                .where(Tables.EDGETable.TO_ID.eq(nodeId))
        )
    }

    override fun getNodeTreeByNodeId(nodeId: Int, maxDepth: Int): Node? {
        val nodeExists = dsl.fetchExists(
            dsl.selectOne()
                .from(Tables.EDGETable)
                .where(Tables.EDGETable.FROM_ID.eq(nodeId))
                .or(Tables.EDGETable.TO_ID.eq(nodeId))
        )

        if (!nodeExists) {
            val isLeafNode = dsl.fetchExists(
                dsl.selectOne()
                    .from(Tables.EDGETable)
                    .where(Tables.EDGETable.TO_ID.eq(nodeId))
            )

            return if (isLeafNode) Node(nodeId) else null
        }

        // Alternatively, a cursor based query can be used to fetch the data in chunks which has better memory management.
        val recursiveQuery = """
            WITH RECURSIVE subtree AS (
                SELECT from_id as parent_id, to_id as child_id, 1 AS depth
                FROM edge
                WHERE from_id = {0}
                
                UNION ALL
                
                SELECT e.from_id as parent_id, e.to_id as child_id, s.depth + 1
                FROM edge e
                JOIN subtree s ON e.from_id = s.child_id
                WHERE s.depth < {1}
            )
            SELECT parent_id, child_id FROM subtree
        """

        val edgeRows = dsl.resultQuery(recursiveQuery, nodeId, maxDepth).fetch()

        if (edgeRows.isEmpty()) {
            return Node(nodeId)
        }

        val nodeMap = mutableMapOf<Int, MutableList<Int>>()
        edgeRows.forEach { record ->
            val parentId = record.getValue("parent_id", Int::class.java)
            val childId = record.getValue("child_id", Int::class.java)
            nodeMap.getOrPut(parentId) { mutableListOf() }.add(childId)
        }


        // Build node tree
        val nodeCache = mutableMapOf<Int, Node>()

        fun getOrCreateNode(id: Int): Node {
            return nodeCache.getOrPut(id) { Node(id) }
        }

        // Create all nodes
        nodeMap.forEach { (parentId, _) ->
            getOrCreateNode(parentId)
        }

        // Add children to parents
        nodeMap.forEach { (parentId, childIds) ->
            val parentNode = getOrCreateNode(parentId)
            childIds.forEach { childId ->
                parentNode.addChild(getOrCreateNode(childId))
            }
        }

        return nodeCache[nodeId]
    }
}
