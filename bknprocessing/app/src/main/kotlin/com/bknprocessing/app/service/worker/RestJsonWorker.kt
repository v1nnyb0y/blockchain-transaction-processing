package com.bknprocessing.app.service.worker

import com.bknprocessing.common.rest.RestClient
import com.bknprocessing.common.rest.RestServer

class RestJsonWorker<T>(
    client: RestClient = RestClient.INSTANCE,
    server: RestServer = RestServer.INSTANCE,
) : BaseWorker<T>(client, server)
