package com.bknprocessing.app.service.worker

import com.bknprocessing.app.utils.logger
import com.bknprocessing.app.utils.sendToVerify
import com.bknprocessing.app.utils.startFinishingProcess
import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import org.slf4j.Logger

abstract class BaseWorker<T>(
    client: IClient,
    private val server: IServer,
) : IWorker<T> {

    val log: Logger by logger()

    private enum class TopicsList {
        ObjQueue, StateChange,
    }

    override fun verifyObject(obj: T): Boolean {
        if (server.sendObj(obj!!, TopicsList.ObjQueue.name)) {
            log.sendToVerify()
            return true
        }
        return false
    }

    override fun finishNodes(transCount: Int) {
        server.sendObj(transCount, TopicsList.StateChange.name)
        log.startFinishingProcess()
    }
}
