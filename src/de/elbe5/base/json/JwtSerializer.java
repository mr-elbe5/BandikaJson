/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.json;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.orgjson.io.OrgJsonSerializer;

public class JwtSerializer extends OrgJsonSerializer {

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        try {
            return serializeObject(t).getBytes();
        } catch (SerializationException se) {
            throw se;
        } catch (Exception e) {
            String msg = "Unable to serialize object of type " + t.getClass().getName() + " to JSON: " + e.getMessage();
            throw new SerializationException(msg, e);
        }
    }


}
