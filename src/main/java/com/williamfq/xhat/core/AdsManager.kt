
package com.williamfq.xhat.core

class AdsManager {
    fun createAd(targetingOptions: Map<String, Any>, content: String) {
        println("Anuncio creado con opciones de segmentación: \$targetingOptions")
        println("Contenido del anuncio: \$content")
    }
}
