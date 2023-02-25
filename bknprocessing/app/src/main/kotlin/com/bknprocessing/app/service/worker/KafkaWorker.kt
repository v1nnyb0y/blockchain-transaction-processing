package com.bknprocessing.app.service.worker

import com.bknprocessing.common.kafka.KafkaConsumer
import com.bknprocessing.common.kafka.KafkaProducer

class KafkaWorker<T>(
    consumer: KafkaConsumer = KafkaConsumer(),
    producer: KafkaProducer = KafkaProducer.INSTANCE,
) : BaseWorker<T>(consumer, producer)
