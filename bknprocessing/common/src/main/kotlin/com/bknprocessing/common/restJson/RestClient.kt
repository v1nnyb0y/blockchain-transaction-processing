package com.bknprocessing.common.restJson

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration

class RestClient private constructor() : IClient {

    override fun setup(configuration: ClientConfiguration) {}

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return null
    }

    companion object {
        val INSTANCE = RestClient()
    }
}
