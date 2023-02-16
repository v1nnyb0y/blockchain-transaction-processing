package com.bknprocessing.app.service.worker

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer

abstract class BaseWorker<T>(
    client: IClient,
    server: IServer,
) : IWorker<T> {

    override fun verifyObject(obj: T) {
        TODO("Not yet implemented")
    }
}
