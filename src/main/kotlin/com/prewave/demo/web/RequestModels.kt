package com.prewave.demo.web

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class EdgeCreateRequest(
    @field:NotNull(message = "Source node ID cannot be null")
    @field:Min(value = 1, message = "Source node ID must be greater than 0")
    val fromId: Int,
    
    @field:NotNull(message = "Target node ID cannot be null")
    @field:Min(value = 1, message = "Target node ID must be greater than 0")
    val toId: Int
)
