package com.eokoe.sagui.data.net.adapters

import com.eokoe.sagui.data.entities.LatLong
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
class LatLongTypeAdapter : TypeAdapter<LatLong>() {
    override fun write(out: JsonWriter, latLong: LatLong) {
        out.beginArray()
        out.value(latLong.latitude)
        out.value(latLong.longitude)
        out.endArray()
    }

    override fun read(json: JsonReader): LatLong? {
        var latLong: LatLong? = null
        json.beginArray()
        while (json.hasNext()) {
            if (json.peek() === JsonToken.NULL) {
                json.nextNull()
            } else {
                if (latLong == null) {
                    latLong = LatLong(latitude = json.nextDouble())
                } else {
                    latLong.longitude = json.nextDouble()
                    break
                }
            }
        }
        json.endArray()
        return latLong
    }

    companion object {
        val INSTANCE = LatLongTypeAdapter().nullSafe()!!
    }
}