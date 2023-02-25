package com.bknprocessing.app.service.worker

interface IWorker<T> {

    fun verifyObject(obj: T): Boolean

    fun finishNodes(transCount: Int)
}
