package com.bknprocessing.common

import com.bknprocessing.common.globals.ClientConfiguration

interface IClient {

    fun setup(configuration: ClientConfiguration)

    fun getObj(from: String): Any?

    fun getObj(from: String, who: Int): Any?
}
