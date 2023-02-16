package com.bknprocessing.common.utils // ktlint-disable filename

import org.slf4j.LoggerFactory

fun <R : Any> R.logger() = lazy { LoggerFactory.getLogger(this::class.java.name) }
