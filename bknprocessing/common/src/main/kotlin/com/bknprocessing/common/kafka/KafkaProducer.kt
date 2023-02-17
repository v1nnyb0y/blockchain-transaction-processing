package com.bknprocessing.common.kafka

import com.bknprocessing.common.IServer
import com.bknprocessing.common.ServerConfiguration

class KafkaProducer private constructor() : IServer {

    override fun setup(configuration: ServerConfiguration) {
        TODO("Not yet implemented")
    }

    override fun sendObj(obj: Any, to: String): Boolean {
        TODO("Not yet implemented")
    }
}
