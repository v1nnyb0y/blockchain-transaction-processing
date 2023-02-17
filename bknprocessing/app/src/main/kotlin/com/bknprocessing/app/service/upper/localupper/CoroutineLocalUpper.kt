package com.bknprocessing.app.service.upper.localupper

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer

class CoroutineLocalUpper<T>(
    private val client: CoroutineClient = CoroutineClient.INSTANCE,
    private val server: CoroutineServer = CoroutineServer.INSTANCE,
) : LocalUpper<T>(client, server)
