package com.shadow.verify.test

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

/**
 * @author accumulate
 * @Description
 * @data 2019-05-16
 */
@Component
class Test2 : Test {
    override suspend fun getResult(): String {
        delay(1000)
        return "com.accumulate.test.Test2 is Ok!!!"
    }

    override fun test() = runBlocking {
        val result = GlobalScope.async { getResult() }
        return@runBlocking result.await()
    }
}
