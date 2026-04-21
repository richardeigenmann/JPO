package org.jpo.org.jpo.startup

import org.jpo.datamodel.ProgressTracker
import org.jpo.service.JpoPictureCollection
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Order(1) // lower number = higher priority
class CollectionLoader(private val jpoPictureCollection: JpoPictureCollection) : CommandLineRunner {

    companion object {
        private val LOGGER = Logger.getLogger(CollectionLoader::class.java.name)
    }

    override fun run(vararg args: String) {
        LOGGER.log(Level.INFO, "--- Loading Collection ---")

        val collectionFile = File("/richi/Fotos/tools/RichardsCollection.xml")

        // Kotlin allows passing empty lambdas {} for Runnable/Consumer functional interfaces
        jpoPictureCollection.pictureCollection.fileLoad(
            collectionFile,
            MyProgressTracker(),
            { }          // onStart/Success lambda
        )

        LOGGER.log(Level.INFO, "Done Loading Collection")
    }

    inner class MyProgressTracker : ProgressTracker {
        override fun update(message: String) {
            LOGGER.log(Level.INFO, message)
        }

        override fun done() {
            LOGGER.log(Level.INFO, "Progress Tracker: Done")
        }
    }
}