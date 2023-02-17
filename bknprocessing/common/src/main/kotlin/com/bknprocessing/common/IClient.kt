package com.bknprocessing.common

interface IClient {

    fun setup(configuration: ClientConfiguration)

    fun getObj(from: String): Any?
}
