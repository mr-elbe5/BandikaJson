/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.application.Application;
import de.elbe5.content.ContentData;
import de.elbe5.data.BaseData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public abstract class SectionPartData extends BaseData implements Comparable<SectionPartData> {

    public static final String TYPE_KEY = "sectionpart";

    public static void register(){
        DataFactory.addClass(SectionPartData.TYPE_KEY, SectionPartData.class);
    }

    public static String LAYOUT_TYPE = "Part";

    private enum keys{
        cssClass,
        sectionName,
        position,
        editable
    }

    protected String cssClass = "";
    protected String sectionName = "";
    protected int position = 0;
    protected boolean editable = true;

    public static String jspBasePath = "/WEB-INF/_jsp/_layout";

    // constructors and type

    public SectionPartData() {
    }

    @Override
    public String getTypeKey(){
        return SectionPartData.TYPE_KEY;
    }

    // copy and editing methods

    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof SectionPartData);
        SectionPartData data = (SectionPartData)idata;
        setCssClass(data.getCssClass());
        setSectionName(data.getSectionName());
        setPosition(data.getPosition());
        setEditable(data.isEditable());
    }

    public void setCreateValues(SessionRequestData rdata) {
        String sectionName = rdata.getString("sectionName");
        setSectionName(sectionName);
        setId(Application.getNextId());
        setNew(true);
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.cssClass.name(), cssClass);
        obj.put(keys.sectionName.name(), sectionName);
        obj.put(keys.position.name(), position);
        obj.put(keys.editable.name(), editable);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        cssClass = obj.optString(keys.cssClass.name());
        sectionName = obj.optString(keys.sectionName.name());
        position = obj.optInt(keys.position.name());
        editable = obj.optBoolean(keys.editable.name());
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        // -1 if deleted
        setPosition(rdata.getInt(getPartPositionName(),-1));
    }

    // interface implementation and defaults

    @Override
    public int compareTo(SectionPartData data) {
        return position - data.position;
    }

    // getter and setter

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPartInclude() {
        return getJspPath() + "/show.jsp";
    }

    public String getEditPartInclude() {
        return getJspPath() + "/edit.jsp";
    }

    public String getPartWrapperId() {
        return "part_" + getId();
    }

    public String getPartPositionName() {
        return "partpos_" + getId();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getEditTitle(Locale locale) {
        return "Section Part, ID=" + getId();
    }

    public String getJspPath() {
        return jspBasePath;
    }

}
