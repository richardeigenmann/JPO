package org.jpo.org.jpo.model

/**
 * Data Transfer Object (DTO) representing a node in the picture collection tree
 * for API serialization.
 * Using a Kotlin Data Class for immutability and concise implementation.
 * The 'children' list allows for recursive representation of the tree structure.
 */
data class TreeNodeDTO(
    val id: String,
    val label: String,
    val isGroup: Boolean,
    val children: List<TreeNodeDTO> = emptyList()
)