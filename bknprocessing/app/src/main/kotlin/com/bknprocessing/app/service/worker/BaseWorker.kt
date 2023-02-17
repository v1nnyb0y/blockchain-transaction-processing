package com.bknprocessing.app.service.worker

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer

abstract class BaseWorker<T>(
    client: IClient,
    private val server: IServer,
) : IWorker<T> {

    private enum class TopicsList {
        ObjQueue,
    }

    override fun verifyObject(obj: T) {
        server.sendObj(obj!!, TopicsList.ObjQueue.name)
    }
}
