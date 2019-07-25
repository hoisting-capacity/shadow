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
class Test1 : Test {
    override suspend fun getResult(): String {
        delay(1000)
        return "com.accumulate.test.Test1 is Ok!!!"
    }

    override fun test(): String = runBlocking {
        val result1 = GlobalScope.async { getResult() }
        val result2 = GlobalScope.async { getResult() }
//        val result1 = getResult()
//        val result2 = getResult()
        return@runBlocking result1.await() + result2.await()
//        return@runBlocking result1 + result2
    }
}
