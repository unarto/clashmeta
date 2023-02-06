package com.hiddify.clash

import android.net.Uri
import kotlin.random.Random

//
class HttpProxy: IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("http://") ||url.startsWith("https://")
    }
    override fun decode(_url: String): LinkedHashMap<String,Any> {
        var yml=LinkedHashMap<String,Any>()
        var url = _url
        if(!canDecode(url))
            throw Exception("Can not decode $url")
        val uri = Uri.parse(url)
        yml["name"] = "${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 100000)}"
        yml["type"]= "http"
        if (uri.userInfo != null) {
            val splt = uri.userInfo!!.split(":").toTypedArray()
            yml["username"] = splt[0]
            yml["password"] = splt[1]
        }
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp"]=true
        yml["skip-cert-verify"]=true
        yml["tls"]=uri.scheme=="https"
        if (Utils.getQueryParamIgnoreCase(uri,"sni")!=null)
            yml["sni"]=Utils.getQueryParamIgnoreCase(uri,"sni")!!

        return yml
    }

}