package com.danielflower.ifp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.nio.charset.StandardCharsets

class HttpProxyCacheTest {


}

private fun URI.toRequest() = Request.Builder().url(this.toURL())
private fun OkHttpClient.call(request: Request.Builder) = this.newCall(request.build()).execute()
private fun String.ascii() = this.toByteArray(StandardCharsets.US_ASCII)
