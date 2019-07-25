package com.shadow.verify.test

/**
 * @author accumulate
 * @Description
 * @data 2019-05-16
 */
interface Test {
    fun test() : String
    suspend fun getResult() : String
}
