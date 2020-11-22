/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fieldsectionpart;

import de.elbe5.data.DataFactory;
import de.elbe5.request.RequestData;

public class PartHtmlField extends PartField {

    public static final String TYPE_KEY = "parthtmlfield";

    public static void register(){
        DataFactory.addClass(PartHtmlField.TYPE_KEY, PartHtmlField.class);
    }

    // constructors and type

    public PartHtmlField() {
    }

    @Override
    public String getTypeKey(){
        return PartHtmlField.TYPE_KEY;
    }

    // request

    @Override
    public void readRequestData(RequestData rdata) {
        setContent(rdata.getString(getIdentifier()));
    }

}
