/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.log.Log;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

public class Strings {

    public static String string(String key, Locale locale) {
        try {
            return ResourceBundle.getBundle("strings", locale).getString(key);
        }
        catch (Exception e){
            Log.warn("string not found: " + key);
            return "...";
        }
    }

    public static String string(String key) {
        return string(key, Application.getDefaultLocale());
    }

    public static String html(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(string(key, locale));
    }

    public static String htmlMultiline(String key, Locale locale) {
        return StringEscapeUtils.escapeHtml4(string(key, locale)).replaceAll("\\\\n", "<br/>");
    }

    public static String js(String key, Locale locale) {
        return StringEscapeUtils.escapeEcmaScript(string(key, locale));
    }

    public static String xml(String key, Locale locale) {
        return StringEscapeUtils.escapeXml11(string(key, locale));
    }

    public static String html(String key) {
        return html(key, Application.getDefaultLocale());
    }
}
