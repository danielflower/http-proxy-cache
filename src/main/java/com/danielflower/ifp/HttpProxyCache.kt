package com.danielflower.ifp

import io.muserver.MuHandler
import io.muserver.MuRequest
import io.muserver.handlers.ResourceHandlerBuilder
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString


/**
 * An HTTP proxy that caches selected files.
 */
class HttpProxyCache private constructor(private val cache: Path, private val work: Path, private val translator: TargetTranslator) {

    companion object {
        private val log = LoggerFactory.getLogger(HttpProxyCache::class.java)!!

        @JvmStatic
        fun create(config: HttpProxyCacheConfig): HttpProxyCache {

            val path = config.workDir ?: throw IllegalStateException("Please specify a workDir which is a directory the proxy can use to cache files")
            if (!Files.isDirectory(path)) throw IllegalStateException("The work directory ${path.absolutePathString()} does not exist.")
            val cache = path.resolve("cache")
            Files.createDirectories(cache);
            val work = path.resolve("work")
            Files.createDirectories(work);

            val translator = config.targetTranslator ?: throw IllegalStateException("No targetTraslator was configed")

            return HttpProxyCache(cache, work, translator)
        }

    }

    fun createHandler() : MuHandler {
        val fileHandler = ResourceHandlerBuilder.fileHandler(cache).build()
        return MuHandler { request, response ->
            // serve from the cache if it is there
            if (fileHandler.handle(request, response)) return@MuHandler true
            val target = translator.uriFor(request) ?: return@MuHandler false



            true
        }

    }

}

/**
 * Config for the proxy.
 */
class HttpProxyCacheConfig {


    /**
     * A directory used to store files for the cache
     */
    var workDir: Path? = null

    /**
     * A translator that specifies where to download files from
     */
    var targetTranslator: TargetTranslator? = null

}

/**
 * Gets a target URI to download from based on a request
 */
interface TargetTranslator {

    /**
     * Returns the target that should be used for the given request.
     *
     * @return the URI to download the actual target from, or null if this handle should not handle this request
     * @throws jakarta.ws.rs.WebApplicationException if the request should be rejected
     */
    fun uriFor(request: MuRequest) : URI?
}
