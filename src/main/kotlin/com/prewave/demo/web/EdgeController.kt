package com.prewave.demo.web

import com.prewave.demo.core.Edge
import com.prewave.demo.core.Node
import com.prewave.demo.core.TreeService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/edges")
class EdgeController(private val treeService: TreeService) {
    @PostMapping
    fun createEdge(@Valid @RequestBody request: EdgeCreateRequest): ResponseEntity<Any> {
        val result = treeService.addEdge(Edge(request.fromId, request.toId))
        if (!result) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Edge created successfully"))
    }

    @DeleteMapping("/{fromId}/{toId}")
    fun deleteEdge(
        @PathVariable @Min(value = 1, message = "From node ID must be greater than 0") fromId: Int,
        @PathVariable @Min(value = 1, message = "To node ID must be greater than 0") toId: Int
    ): ResponseEntity<Any> {
        val result = treeService.deleteEdge(fromId, toId)
        return if (result) {
            ResponseEntity.ok(mapOf("message" to "Edge deleted successfully"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "Edge not found"))
        }
    }

    @GetMapping("/{nodeId}/tree")
    fun getTree(
        @PathVariable @Min(value = 1, message = "Node ID must be greater than 0") nodeId: Int
    ): ResponseEntity<Node> {
        return ResponseEntity.ok(treeService.getTreeByNodeId(nodeId))
    }
}
