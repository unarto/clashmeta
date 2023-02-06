package com.hiddify.clash

interface IProxy {
    fun canDecode(url: String): Boolean
    fun decode(url: String): LinkedHashMap<String,Any>
}