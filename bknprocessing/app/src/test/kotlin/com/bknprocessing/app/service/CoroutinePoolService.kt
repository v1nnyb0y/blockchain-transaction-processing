package com.bknprocessing.app.service

import com.bknprocessing.app.service.worker.CoroutineWorker
import com.bknprocessing.app.service.wrapper.uppper.TestedCoroutineUpper

class CoroutinePoolService : PoolServiceTest(TestedCoroutineUpper(), CoroutineWorker())
