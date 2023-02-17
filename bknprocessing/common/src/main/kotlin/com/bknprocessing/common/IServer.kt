package com.bknprocessing.common

interface IServer {

    fun setup(configuration: ServerConfiguration)

    fun sendObj(obj: Any, to: String): Boolean
}
