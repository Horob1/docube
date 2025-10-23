package com.horob1.common_service.util

import java.security.SecureRandom
import kotlin.math.pow

object OtpGenerator {
    private val random = SecureRandom()

    fun generateOtp(length: Int = 6): String {
        val min = 10.0.pow((length - 1).toDouble()).toInt()
        val max = 10.0.pow(length.toDouble()).toInt() - 1
        val number = random.nextInt(max - min + 1) + min
        return number.toString()
    }
}