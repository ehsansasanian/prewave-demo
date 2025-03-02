package com.prewave.demo.core.impl

import com.prewave.demo.core.Edge
import com.prewave.demo.core.EdgeRepository
import com.prewave.demo.core.Node
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TreeServiceImplTest {

    @Mock
    private lateinit var edgeRepository: EdgeRepository
    private lateinit var treeService: TreeServiceImpl

    @BeforeEach
    fun setUp() {
        treeService = TreeServiceImpl(edgeRepository)
    }

    @Test
    fun `addEdge should return true when edge is successfully added`() {
        val edge = Edge(1, 2)
        `when`(edgeRepository.edgeExists(Edge(edge.fromId, edge.toId))).thenReturn(false)
        `when`(edgeRepository.hasParent(edge.toId)).thenReturn(false)
        `when`(edgeRepository.edgeExists(Edge(-1, edge.toId))).thenReturn(false)
        `when`(edgeRepository.addEdge(edge)).thenReturn(true)

        assertTrue(treeService.addEdge(edge))
        verify(edgeRepository).addEdge(edge)
    }

    @Test
    fun `deleteEdge should return true when edge is successfully deleted`() {
        val fromId = 1
        val toId = 2
        `when`(edgeRepository.edgeExists(Edge(fromId, toId))).thenReturn(true)
        `when`(edgeRepository.deleteEdge(fromId, toId)).thenReturn(true)

        assertTrue(treeService.deleteEdge(fromId, toId))
        verify(edgeRepository).deleteEdge(fromId, toId)
    }

    @Test
    fun `getTreeByNodeId should return correct tree structure`() {
        val rootId = 1
        val expectedTree = Node(rootId)
        expectedTree.addChild(Node(2))
        expectedTree.addChild(Node(3))

        `when`(edgeRepository.getNodeTreeByNodeId(rootId)).thenReturn(expectedTree)
        val result = treeService.getTreeByNodeId(rootId)
        
        assertEquals(expectedTree, result)
    }

    @Test
    fun `getTreeByNodeId should return null when no tree exists for node`() {
        val nodeId = 999
        `when`(edgeRepository.getNodeTreeByNodeId(nodeId)).thenReturn(null)
        
        val result = treeService.getTreeByNodeId(nodeId)
        
        assertEquals(null, result)
    }
    
    @Test
    fun `getTreeByNodeId should return complex nested tree structure correctly`() {
        val rootId = 1
        val root = Node(rootId)
        
        val child1 = Node(2)
        val child2 = Node(3)
        
        val grandchild1 = Node(4)
        val grandchild2 = Node(5)
        
        child1.addChild(grandchild1)
        child1.addChild(grandchild2)
        
        root.addChild(child1)
        root.addChild(child2)
        
        `when`(edgeRepository.getNodeTreeByNodeId(rootId)).thenReturn(root)
        
        val result = treeService.getTreeByNodeId(rootId)
        
        assertEquals(root, result)
    }
    
    @Test
    fun `getTreeByNodeId should return single node when node has no children`() {
        val leafId = 5
        val leaf = Node(leafId)
        
        `when`(edgeRepository.getNodeTreeByNodeId(leafId)).thenReturn(leaf)
        
        val result = treeService.getTreeByNodeId(leafId)
        
        assertEquals(leaf, result)
    }

    // TODO: more tests can be implemented to cover all scenarios – skipping it as this is a demo project ...
}
