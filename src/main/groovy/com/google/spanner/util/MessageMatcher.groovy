package com.google.spanner.util

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils

@Slf4j
class MessageMatcher {

    boolean matchJsonString(String message, String contentToValidate){
        boolean  foundMatch = false
        if (StringUtils.isNotBlank(message) && StringUtils.isNotBlank(contentToValidate)) {
            def messageObject = stringToMap(message)
            def contentToValidateObject = stringToMap(contentToValidate)
            if (messageObject instanceof Map && contentToValidateObject instanceof Map){
                foundMatch = validate (messageObject, contentToValidateObject)
            } else if (messageObject instanceof List && contentToValidateObject instanceof  List) {
                foundMatch = matchList (messageObject, contentToValidateObject)
            }
        }
        return foundMatch
    }

    private def stringToMap (String json){
        log.info('JSON String : {}', json)
        def map = new JsonSlurper().parseText(json)
        return map
    }

    boolean validate(Map message, Map contentToValidate){
        Set<String> keySetToMatch = contentToValidate.keySet()
        boolean allFound = true
        for(String key : keySetToMatch) {
            if(!message.containsKey(key)){
                return false
            }

            Object valueFromMessage = message.get(key)
            Object valueFromMatcher = contentToValidate.get(key)

            if(valueFromMatcher instanceof Map && valueFromMessage instanceof Map) {
                allFound = allFound & validate(valueFromMessage, valueFromMatcher)
            } else if(valueFromMatcher instanceof List && valueFromMessage instanceof List) {
                allFound = allFound & matchList(valueFromMessage, valueFromMatcher)
            } else {
                if (valueFromMatcher != valueFromMessage) {
                    log.debug("Did not match for ${key} values :" + valueFromMessage + " != " + valueFromMatcher)
                }
                allFound = allFound & (valueFromMessage == valueFromMatcher)
            }

            if (!allFound) {
                return false
            }
        }
        return allFound
    }

    boolean matchList(List valueFromMessage, List valueFromMatcher){
        if (valueFromMessage.isEmpty() && valueFromMatcher.isEmpty()) {
            return true
        }

        if (valueFromMessage.isEmpty() ^ valueFromMatcher.isEmpty()) {
            log.debug(" array is empty in one but in other")
            return false
        }

        boolean found = true
        valueFromMatcher.each { valueToSearch ->
            boolean valueFound = false
            if (valueToSearch instanceof Map) {
                for (Map itemFromMessage : valueFromMessage) {
                    valueFound = validate(itemFromMessage, valueToSearch)
                    if (valueFound) {
                        break
                    }
                }
            } else if (valueToSearch instanceof String) {
                for (String itemFromMessage : valueFromMessage) {
                    valueFound = itemFromMessage == valueToSearch
                    if (valueFound) {
                        break
                    }
                }
            } else {
                for (Map itemFromMessage : valueFromMessage) {
                    valueFound = itemFromMessage == valueToSearch
                    if (valueFound) {
                        break
                    }
                }
            }

            if (!valueFound) {
                log.debug("value not found in array : ${valueToSearch}")
                found = false
                return
            }
        }
        return found
    }
}
