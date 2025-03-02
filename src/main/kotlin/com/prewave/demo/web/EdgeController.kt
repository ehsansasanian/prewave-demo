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

    @GetMapping("/{nodeId}/tree")
    fun getTree(
        @PathVariable @Min(value = 1, message = "Node ID must be greater than 0") nodeId: Int
    ): ResponseEntity<Node> {
        return ResponseEntity.ok(treeService.getTreeByNodeId(nodeId))
    }
}
