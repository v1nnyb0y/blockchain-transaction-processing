package com.bknprocessing.app.service.upper.localupper

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.CoroutineClientConfiguration
import com.bknprocessing.common.globals.CoroutineServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration

open class CoroutineLocalUpper<T>(
    client: () -> CoroutineClient = { CoroutineClient.INSTANCE },
    server: () -> CoroutineServer = { CoroutineServer.INSTANCE },

    getClientConfiguration: (Any) -> ClientConfiguration =
        { networkSize: Any -> CoroutineClientConfiguration(networkSize as Int) },
    getServerClientConfiguration: (Any) -> ServerConfiguration =
        { networkSize: Any -> CoroutineServerConfiguration(networkSize as Int) },
) : LocalUpper<T>(client, getClientConfiguration, server, getServerClientConfiguration)
