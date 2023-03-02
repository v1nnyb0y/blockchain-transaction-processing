package com.bknprocessing.node

class WorkerTest : AbstractTest<NodeApplication>(
    clazz = NodeApplication::class.java,
    constructor = { NodeApplication() },
)
