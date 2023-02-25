package com.bknprocessing.node

class WorkerTest : AbstractTest<Worker>(
    clazz = Worker::class.java,
    constructor = { Worker() },
)
