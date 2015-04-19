package com.util;

import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * User: YamStranger
 * Date: 4/16/15
 * Time: 12:27 PM
 */
public class JsonUtil {
    private JsonValue jsonValue;

    public JsonUtil(JsonValue jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String string() {
        String result = "";
        if (this.jsonValue != null) {
            JsonValue.ValueType type = this.jsonValue.getValueType();
            switch (type) {
                case STRING:
                    result = ((JsonString) this.jsonValue).getString();
                    break;
                case NUMBER:
                    result = ((JsonNumber) this.jsonValue).toString();
                    break;
                case NULL:
                    result = "";
                    break;
                case TRUE:
                    result = "true";
                    break;
                case FALSE:
                    result = "false";
                    break;
                default:
                    result = this.jsonValue.toString();
                    break;
            }
        }
        return result;
    }
}
