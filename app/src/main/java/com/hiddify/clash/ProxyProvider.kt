package com.hiddify.clash

import android.net.Uri
import kotlin.random.Random

//
class ProxyProvider: IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("http://") ||url.startsWith("https://")
    }
    override fun decode(_url: String): LinkedHashMap<String,Any> {
        var yml=LinkedHashMap<String,Any>()
        var url = _url
        if(!canDecode(url))
            throw Exception("Can not decode $url")
        val uri = Uri.parse(url)
        val newstr = url.replace("[^A-Za-z0-9_]+".toRegex(), "_")
        var name = "${uri.fragment ?: "provider"}_${newstr}_${Random.nextInt(0, 100000)}"
        yml["type"]= "http"
        yml["url"]= url
        yml["path"]= "$name.yaml"

        var out=LinkedHashMap<String,Any>()
        out[name]=yml
        return out
    }

}