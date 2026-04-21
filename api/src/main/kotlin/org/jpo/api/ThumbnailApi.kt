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
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import kotlin.streams.toList

@RestController
@RequestMapping("/api/jpo/thumbnail")
@Tag(name = "Retrieve a thumbnail image", description = "Retrieve a thumbnail image for the supplied node id")
class ThumbnailApi(private val jpoPictureCollection: JpoPictureCollection) {

    companion object {
        private val LOGGER = Logger.getLogger(ThumbnailApi::class.java.name)
        private val BROKEN_THUMBNAIL_PICTURE: ByteArray by lazy {
            imageIconToByteArray(getResource("broken_thumbnail.gif"))
        }

        private fun getResource(resource: String): ImageIcon? {
            val resourceURL = ThumbnailApi::class.java.classLoader.getResource(resource)
            return if (resourceURL == null) {
                LOGGER.log(Level.SEVERE, "Classloader could not find the file: {0}", resource)
                null
            } else {
                ImageIcon(resourceURL)
            }
        }

        private fun imageIconToByteArray(icon: ImageIcon?): ByteArray {
            val image = icon?.image ?: return ByteArray(0)

            val width = image.getWidth(null)
            val height = image.getHeight(null)

            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val g2 = bufferedImage.createGraphics()
            g2.drawImage(image, 0, 0, null)
            g2.dispose()

            return try {
                ByteArrayOutputStream().use { baos ->
                    ImageIO.write(bufferedImage, "gif", baos)
                    baos.toByteArray()
                }
            } catch (e: IOException) {
                LOGGER.log(Level.SEVERE, "Could not convert ImageIcon to byte array.", e)
                ByteArray(0)
            }
        }
    }

    @GetMapping("/{id}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE])
    fun getImage(@PathVariable id: String): ResponseEntity<ByteArray> {
        LOGGER.log(Level.INFO, "Trying to get Thumbnail for node {0}", id)

        val nodeId = id.substringBeforeLast(".").toIntOrNull() ?: return ResponseEntity.notFound().build()

        val rootNode = jpoPictureCollection.pictureCollection?.rootNode
        val desiredNode = SortableDefaultMutableTreeNode.getNodeById(rootNode, nodeId)
            ?: return ResponseEntity.notFound().build()

        return try {
            val userObject = desiredNode.userObject ?: throw IOException("Could not find node $id")

            val imageBytes = when (userObject) {
                is PictureInfo -> {
                    JpoCache.getThumbnailImageBytes(
                        userObject.imageFile,
                        userObject.rotation,
                        Dimension(CacheSettings.getThumbnailSize(), CacheSettings.getThumbnailSize())
                    )
                }
                else -> {
                    val childPictureNodes = desiredNode.childPictureNodesDFS.limit(6).toList()
                    JpoCache.getGroupThumbnailImageBytes(childPictureNodes)
                }
            }

            ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes.bytes)

        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Hit an IOException on node: {0} - {1}", arrayOf(desiredNode, e.message))
            ResponseEntity.ok()
                .contentType(MediaType.IMAGE_GIF)
                .body(BROKEN_THUMBNAIL_PICTURE)
        }
    }
}