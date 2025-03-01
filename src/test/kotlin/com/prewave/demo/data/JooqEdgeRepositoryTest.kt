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
        assertFalse(edgeRepository.deleteEdge(3, 4))
    }

    @Test
    fun `getTreeByNodeId should return empty list when node has no children`() {
        val result = edgeRepository.getTreeByNodeId(1, 10)
        assertEquals(0, result.size)
    }

    @Test
    fun `getTreeByNodeId should return direct children`() {
        edgeRepository.addEdge(Edge(1, 2))
        edgeRepository.addEdge(Edge(1, 3))
        edgeRepository.addEdge(Edge(2, 4))

        val result = edgeRepository.getTreeByNodeId(1, 1)

        assertEquals(2, result.size)
        assertTrue(result.any { it.fromId == 1 && it.toId == 2 })
        assertTrue(result.any { it.fromId == 1 && it.toId == 3 })
    }

    @Test
    fun `getTreeByNodeId should return full tree`() {
        // Level 1: 1 -> 2, 1 -> 3
        // Level 2: 2 -> 4, 3 -> 5
        // Level 3: 4 -> 6
        edgeRepository.addEdge(Edge(1, 2))
        edgeRepository.addEdge(Edge(1, 3))
        edgeRepository.addEdge(Edge(2, 4))
        edgeRepository.addEdge(Edge(3, 5))
        edgeRepository.addEdge(Edge(4, 6))

        val result = edgeRepository.getTreeByNodeId(1, 10)

        assertEquals(5, result.size)
        // Check all
        assertTrue(result.any { it.fromId == 1 && it.toId == 2 })
        assertTrue(result.any { it.fromId == 1 && it.toId == 3 })
        assertTrue(result.any { it.fromId == 2 && it.toId == 4 })
        assertTrue(result.any { it.fromId == 3 && it.toId == 5 })
        assertTrue(result.any { it.fromId == 4 && it.toId == 6 })
    }

    @Test
    fun `getTreeByNodeId should respect max depth parameter`() {
        // Level 1: 1 -> 2, 1 -> 3
        // Level 2: 2 -> 4, 3 -> 5
        // Level 3: 4 -> 6
        edgeRepository.addEdge(Edge(1, 2))
        edgeRepository.addEdge(Edge(1, 3))
        edgeRepository.addEdge(Edge(2, 4))
        edgeRepository.addEdge(Edge(3, 5))
        edgeRepository.addEdge(Edge(4, 6))

        // Only get the first two levels
        val result = edgeRepository.getTreeByNodeId(1, 2)

        assertEquals(4, result.size)
        // These should be included
        assertTrue(result.any { it.fromId == 1 && it.toId == 2 })
        assertTrue(result.any { it.fromId == 1 && it.toId == 3 })
        assertTrue(result.any { it.fromId == 2 && it.toId == 4 })
        assertTrue(result.any { it.fromId == 3 && it.toId == 5 })
        // This should not be included
        assertFalse(result.any { it.fromId == 4 && it.toId == 6 })
    }
}