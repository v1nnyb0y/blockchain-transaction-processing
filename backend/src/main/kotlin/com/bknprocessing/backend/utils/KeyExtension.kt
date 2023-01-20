package com.bknprocessing.backend.utils // ktlint-disable filename

import java.security.Key
import java.util.*

fun Key.encodeToString(): String {
    return Base64.getEncoder().encodeToString(this.encoded)
}
