package com.bknprocessing.app.service

import com.bknprocessing.app.service.worker.KafkaWorker
import com.bknprocessing.app.service.wrapper.uppper.TestedKafkaUpper

class KafkaPoolServiceTest : PoolServiceTest(TestedKafkaUpper(), KafkaWorker())
