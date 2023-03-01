package com.bknprocessing.common.restJson

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ServerConfiguration

class RestServer private constructor() : IServer {

    override fun setup(configuration: ServerConfiguration) {}

    override fun sendObj(element: Any, topic: String): Boolean {
        return false
    }

    companion object {
        val INSTANCE = RestServer()
    }
}
