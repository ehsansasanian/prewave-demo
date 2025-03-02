package com.prewave.demo.data

import com.prewave.demo.config.TestContainerConfig
import com.prewave.demo.core.Edge
import com.prewave.demo.core.EdgeRepository
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JooqEdgeRepositoryTest : TestContainerConfig() {

    @Autowired
    private lateinit var dsl: DSLContext
    private lateinit var edgeRepository: EdgeRepository

    @BeforeEach
    fun setup() {
        dsl.deleteFrom(Tables.EDGETable).execute()
        edgeRepository = JooqEdgeRepository(dsl)
    }

    @Test
    fun `addEdge should successfully add a new edge`() {
        val edge = Edge(1, 2)

        assertTrue { edgeRepository.addEdge(edge) }
        assertTrue { edgeRepository.edgeExists(edge) }
    }

    @Test
    fun `addEdge should return false when edge already exists`() {
        val edge = Edge(1, 2)
        assertTrue { edgeRepository.addEdge(edge) }
        assertFalse { edgeRepository.addEdge(edge) }
    }

    @Test
    fun `deleteEdge should successfully delete an existing edge`() {
        val edge = Edge(1, 2)
        edgeRepository.addEdge(edge)
        assertTrue { edgeRepository.edgeExists(edge) }

        val result = edgeRepository.deleteEdge(1, 2)
        assertTrue { result }
        assertFalse { edgeRepository.edgeExists(edge) }
    }

    @Test
    fun `deleteEdge should return false when edge does not exist`() {
        assertFalse { edgeRepository.deleteEdge(3, 4) }
    }

    @Test
    fun `getNodeTreeByNodeId should handle complex tree structures`() {
        edgeRepository.addEdge(Edge(1, 2))
        edgeRepository.addEdge(Edge(1, 3))
        edgeRepository.addEdge(Edge(2, 4))
        edgeRepository.addEdge(Edge(2, 5))
        edgeRepository.addEdge(Edge(3, 6))
        edgeRepository.addEdge(Edge(3, 7))
        edgeRepository.addEdge(Edge(4, 8))
        edgeRepository.addEdge(Edge(6, 9))

        val result = edgeRepository.getNodeTreeByNodeId(1)

        // Verify full structure
        assertTrue { result != null }
        assertEquals(1, result?.id)

        val treeMap = result?.toMap()
        val level1 = treeMap?.get("children") as List<*>
        assertEquals(2, level1.size)

        // Count total nodes in the tree (should be 9)
        fun countNodes(node: Map<*, *>): Int {
            val children = node["children"] as List<*>
            return 1 + children.sumOf { countNodes(it as Map<*, *>) }
        }

        assertEquals(9, countNodes(treeMap))
    }

    @Test
    fun `reassignChildrenToGrandparent should handle reassignment with multiple children`() {
        // 1 -> 2 -> 3, 4, 5
        //   -> 6
        edgeRepository.addEdge(Edge(1, 2))
        edgeRepository.addEdge(Edge(1, 6))
        edgeRepository.addEdge(Edge(2, 3))
        edgeRepository.addEdge(Edge(2, 4))
        edgeRepository.addEdge(Edge(2, 5))

        // Reassign all children of 2 to be direct children of 1
        val result = edgeRepository.reassignChildrenToGrandparent(2, 1)

        // Verify result
        assertTrue(result)

        // Original links should be gone
        assertFalse(edgeRepository.edgeExists(Edge(2, 3)))
        assertFalse(edgeRepository.edgeExists(Edge(2, 4)))
        assertFalse(edgeRepository.edgeExists(Edge(2, 5)))

        // New links should exist
        assertTrue(edgeRepository.edgeExists(Edge(1, 3)))
        assertTrue(edgeRepository.edgeExists(Edge(1, 4)))
        assertTrue(edgeRepository.edgeExists(Edge(1, 5)))

        // Unchanged links should remain
        assertTrue(edgeRepository.edgeExists(Edge(1, 2)))
        assertTrue(edgeRepository.edgeExists(Edge(1, 6)))
    }

    // TODO: more tests ...
}