package org.jpo.api

import io.swagger.v3.oas.annotations.tags.Tag
import org.jpo.datamodel.*
import org.jpo.service.JpoPictureCollection
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

@RestController
@RequestMapping("/api/jpo/full")
@Tag(name = "Retrieve a full image", description = "Retrieve a full size image for the supplied node id")
class FullImageApi(private val jpoPictureCollection: JpoPictureCollection) {

    companion object {
        private val LOGGER = Logger.getLogger(FullImageApi::class.java.name)
    }

    @GetMapping("/{id}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImage(@PathVariable id: String): ResponseEntity<ByteArray> {
        LOGGER.log(Level.INFO, "Trying to get Full Image for node {0}", id)

        val nodeId = id.substringBeforeLast(".").toIntOrNull() ?: return ResponseEntity.notFound().build()

        val rootNode = jpoPictureCollection.pictureCollection?.rootNode
        val desiredNode = SortableDefaultMutableTreeNode.getNodeById(rootNode, nodeId)
            ?: return ResponseEntity.notFound().build()

        return try {
            val userObject = desiredNode.userObject ?: throw IOException("Could not find node $id")

            if (userObject is PictureInfo) {
                val imageBytes = JpoCache.getHighresImageBytes(userObject.sha256, userObject.imageFile)
                ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes.bytes)
            } else {
                ResponseEntity.badRequest().build()
            }

        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Hit an IOException on node: {0} - {1}", arrayOf(desiredNode, e.message))
            ResponseEntity.notFound().build()
        }
    }
}
