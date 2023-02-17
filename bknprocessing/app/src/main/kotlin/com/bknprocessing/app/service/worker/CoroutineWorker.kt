package com.bknprocessing.app.service.worker

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer

class CoroutineWorker<T>(
    client: CoroutineClient = CoroutineClient.INSTANCE,
    server: CoroutineServer = CoroutineServer.INSTANCE,
) : BaseWorker<T>(client, server)
