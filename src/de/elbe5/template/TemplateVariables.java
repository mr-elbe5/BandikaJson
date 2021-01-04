package de.elbe5.template;

import de.elbe5.application.Strings;

import java.util.HashMap;
import java.util.Locale;

public class TemplateVariables extends HashMap<String,String>{

    public void addByKey(String key, String resourceKey, Locale locale){
        put(key, Strings.string(resourceKey, locale));
    }
}
