package com.mstoica.dogoapp.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AppCustomInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val requestBuilder: Request.Builder = original.newBuilder()
            .addHeader("x-api-key", NetworkOptions.apiKey)
            .url(original.url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}