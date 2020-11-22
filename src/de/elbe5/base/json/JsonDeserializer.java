/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.json;

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Strings;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonDeserializer implements Deserializer {



    @Override
    public Object deserialize(byte[] bytes) throws DeserializationException {

        Assert.notNull(bytes, "JSON byte array cannot be null");

        if (bytes.length == 0) {
            throw new DeserializationException("Invalid JSON: zero length byte array.");
        }

        try {
            String s = new String(bytes, Strings.UTF_8);
            return new JSONObject(s);
        } catch (Exception e) {
            String msg = "Invalid JSON: " + e.getMessage();
            throw new DeserializationException(msg, e);
        }
    }

}
