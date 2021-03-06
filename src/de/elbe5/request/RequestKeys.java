/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

public abstract class RequestKeys {

    public static final String KEY_REQUESTDATA = "$REQUESTDATA";
    public static final String KEY_URL = "$URL";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_HOST = "$HOST";
    public static final String KEY_JSP = "$JSP";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGETYPE = "$MESSAGETYPE";
    public static final String KEY_CLIPBOARD = "$CLIPBOARD";
    public static final String KEY_LOGIN = "$LOGIN";

    public static final String KEY_CONTENT_CONTEXT = "contentContext";
    public static final String KEY_CONTENT = "contentData";
    public static final String KEY_FILE = "fileData";
    public static final String KEY_GROUP = "groupData";
    public static final String KEY_USER = "userData";
    public static final String KEY_PART = "partData";

    public static final String MESSAGE_TYPE_INFO = "info";
    public static final String MESSAGE_TYPE_SUCCESS = "success";
    public static final String MESSAGE_TYPE_ERROR = "danger";

}


