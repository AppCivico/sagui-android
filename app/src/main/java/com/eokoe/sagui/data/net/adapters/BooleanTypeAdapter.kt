package com.eokoe.sagui.data.net.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type

/**
 * @author Pedro Silva
 * @since 04/10/17
 */

class BooleanTypeAdapter : JsonDeserializer<Boolean> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) =
            when (json.asInt) {
                0 -> false
                1 -> true
                else -> false
            }
}
