package org.jpo.api

import org.jpo.datamodel.GroupInfo
import org.jpo.datamodel.SortableDefaultMutableTreeNode
import org.jpo.org.jpo.model.TreeNodeDTO
import org.jpo.service.JpoPictureCollection
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.swing.tree.TreeNode

@RestController
@RequestMapping("/api/jpo")
class JpoController(private val jpoPictureCollection: JpoPictureCollection) {

    @GetMapping
    fun getAll(): List<TreeNodeDTO> {
        // This is the clean, final API call. The Service handles the complex traversal.
        return getTreeModelAsDTO()
    }

    /**
     * Converts the internal DefaultTreeModel into a serializable List of TreeNodeDTOs.
     * This method performs a depth-first traversal of the tree structure.
     * @return The list of top-level nodes (children of the hidden root).
     */
    fun getTreeModelAsDTO(): List<TreeNodeDTO> {
        val root = jpoPictureCollection.pictureCollection?.treeModel?.root as? SortableDefaultMutableTreeNode
            ?: return emptyList()

        // We skip the root node and return its children to represent the logical top level
        return getChildrenAsDTO(root)
    }

    /**
     * Recursive helper to convert a parent node's children into a list of TreeNodeDTOs.
     */
    private fun getChildrenAsDTO(parentNode: SortableDefaultMutableTreeNode): List<TreeNodeDTO> {
        if (parentNode.childCount == 0) {
            return emptyList()
        }

        // Use Kotlin's collection extension for Enumerations to avoid manual stream logic
        return parentNode.children().toList()
            .filterIsInstance<SortableDefaultMutableTreeNode>()
            .map { mapNodeToDTO(it) }
    }

    /**
     * Maps a single DefaultMutableTreeNode to a TreeNodeDTO.
     * The recursive call to getChildrenAsDTO() ensures depth-first traversal.
     */
    private fun mapNodeToDTO(node: SortableDefaultMutableTreeNode): TreeNodeDTO {
        val userObject = node.userObject
        val label = userObject?.toString() ?: "Unnamed Node"

        val id = if (userObject != null) {
            node.uniqueId.toString()
        } else {
            "temp-hash-${node.hashCode()}"
        }

        // Checks if the user object is a specific Group class
        val isGroup = userObject is GroupInfo

        // RECURSIVE CALL: This is where the depth-first traversal occurs.
        val children = getChildrenAsDTO(node)

        return TreeNodeDTO(id, label, isGroup, children)
    }
}