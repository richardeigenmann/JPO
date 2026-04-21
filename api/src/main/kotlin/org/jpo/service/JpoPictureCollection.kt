package org.jpo.service

import org.jpo.datamodel.PictureCollection
import org.springframework.stereotype.Service

@Service
class JpoPictureCollection {
    val pictureCollection: PictureCollection = PictureCollection()
}