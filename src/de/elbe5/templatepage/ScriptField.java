/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import org.json.JSONException;
import org.json.JSONObject;

public class ScriptField extends PartField {

    public static final String TYPE_KEY = "scriptfield";

    public static void register(){
        DataFactory.addClass(ScriptField.TYPE_KEY, ScriptField.class);
    }

    private enum keys{
        code
    }

    // constructors and type

    public ScriptField() {
    }

    @Override
    public String getTypeKey(){
        return ScriptField.TYPE_KEY;
    }

    protected String code = "";

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert idata instanceof ScriptField;
        ScriptField data = (ScriptField) idata;
        setCode(data.getCode());
    }

    // json methods

    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.code.name(), code);
    }

    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        code = obj.optString(keys.code.name());
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        setCode(rdata.getString(getIdentifier()));
    }

    // getter and setter

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
