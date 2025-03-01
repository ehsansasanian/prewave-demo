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

    /*
    * Alternatively, a cursor based query can be used to fetch the data in chunks which has better memory management.
    * */
    override fun getTreeByNodeId(nodeId: Int, givenMaxDepth: Int): List<Edge> {
        val maxDepth = maxOf(givenMaxDepth, 1)

        val hasChildren = dsl.fetchExists(
            dsl.selectOne()
                .from(Tables.EDGETable)
                .where(Tables.EDGETable.FROM_ID.eq(nodeId))
        )

        if (!hasChildren) {
            return emptyList()
        }

        // If the tree has a large number of nodes, this can lead to memory overflow too.
        // Maybe then using a cursor based query is better.
        val recursiveQuery = """
            WITH RECURSIVE subtree AS (
                SELECT 1 AS depth, from_id, to_id
                FROM edge
                WHERE from_id = {0}
                
                UNION ALL
                
                -- Recursive case: children of children
                SELECT s.depth + 1, e.from_id, e.to_id
                FROM edge e
                JOIN subtree s ON e.from_id = s.to_id
                WHERE s.depth < {1}
            )
            SELECT from_id, to_id FROM subtree
        """

        val result = dsl.resultQuery(recursiveQuery, nodeId, maxDepth)
            .fetch()
            .map { Edge(it.getValue("from_id", Int::class.java), it.getValue("to_id", Int::class.java)) }

        return result
    }
}
