package com.hiddify.clash

import android.net.Uri
import android.util.Base64
import android.util.JsonReader
import org.json.JSONObject
import java.net.URLDecoder
import kotlin.random.Random


class VmessProxy : IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("vmess://")
    }

    override fun decode(url: String): LinkedHashMap<String, Any> {
        if (!canDecode(url))
            throw Exception("Can not decode $url")

        var yml = LinkedHashMap<String, Any>()
        var jsonstr=url.substringAfter("vmess://")
        if (!jsonstr.startsWith("{"))
            jsonstr=Utils.decodeBase64(jsonstr)

        var json = JSONObject(jsonstr)

        yml["name"] = "${json.optString("ps", json.optString("add","new"))}_vmess_${Random.nextInt(0, 100000)}"
        yml["type"] = "vmess"
        yml["server"] = json.optString("add",json.optString("host",""))
        yml["port"] = json.getString("port")
        yml["uuid"] = json.getString("id")
        yml["alterId"] = json.optString("aid", "0")
        yml["cipher"] = json.optString("scy", "auto")
        yml["udp"] = true
        if (json.optString("tls", "") == "tls")
            yml["tls"] = true
        yml["skip-cert-verify"] = true
        var ps = json.optString("host",json.optString("add",""))
        yml["servername"] = json.optString("sni", ps)
        var localType = json.optString("net", "tcp")
        when (localType) {
            "ws" -> {
                yml["network"] = "ws"
                var wsOpts= mutableMapOf(
                    "path" to (json.optString("path","/")),
                )
                if (json.has("host")){
                    wsOpts["headers"]= mutableMapOf(
                        "host" to json["host"]
                    ).toString()
                }
                yml["ws-opts"]=wsOpts

            }
            "h2" -> {
                yml["network"] = "h2"
                yml["h2-opts"] = mutableMapOf(
                    "path" to (json["path"]),
                )
            }
            "tcp" -> {
                if (json["type"]=="http"){
                    yml["network"] = "http"
                    yml["http-opts"] = mutableMapOf(
                        "path" to (json["path"]?.toString()?.split(",")),
                    )
                }
            }
            "grpc" -> {
                yml["network"] = "grpc"
                yml["grpc-opts"] = mutableMapOf(
                    "grpc-service-name" to (json["path"] ?: ""),
                )
            }
            else -> throw Exception("${localType} Not Supported")
        }
        return yml
    }
}