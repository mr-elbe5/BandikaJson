/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fieldsectionpart;

import de.elbe5.data.IData;
import de.elbe5.data.JsonData;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class PartField implements IData, JsonData {

    public static final String TYPE_KEY = "partfield";

    private enum keys{
        partId,
        name,
        content
    }

    protected int partId = 0;
    protected String name = "";
    protected String content = "";

    // constructors and type

    public PartField() {
    }

    @Override
    public String getTypeKey(){
        return PartField.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyFixedAttributes(IData idata) {
        assert idata instanceof PartField;
        PartField data = (PartField) idata;
        setPartId(data.getPartId());
        setName(data.getName());
    }

    @Override
    public void copyEditableAttributes(IData idata){
        assert idata instanceof PartField;
        PartField data = (PartField) idata;
        setContent(data.getContent());
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(typeKey, getTypeKey());
        addJSONAttributes(obj);
        return obj;
    }

    public void addJSONAttributes(JSONObject obj) {
        obj.put(keys.partId.name(), partId);
        obj.put(keys.name.name(), name);
        obj.put(keys.content.name(), content);
    }

    public void fromJSONObject(JSONObject obj) throws JSONException {
        partId = obj.optInt(keys.partId.name());
        name = obj.optString(keys.name.name());
        content = obj.optString(keys.content.name());
    }

    // getter and setter

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getPartId() {
        return partId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return Integer.toString(partId) + '_' + name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // search

    public void appendSearchText(StringBuilder sb) {
    }

}
