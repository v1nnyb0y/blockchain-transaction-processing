package com.bknprocessing.common

import com.bknprocessing.common.globals.ServerConfiguration

interface IServer {

    fun setup(configuration: ServerConfiguration)

    fun sendObj(obj: Any, to: String): Boolean
}
