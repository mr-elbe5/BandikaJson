/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fullpage;

import de.elbe5.application.Application;
import de.elbe5.content.ContentData;
import de.elbe5.content.ContentViewContext;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.content.PageTypes;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.templatepage.SectionData;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullPageData extends ContentData {

    public static final String TYPE_KEY = "fullpage";

    public static void register(){
        DataFactory.addClass(FullPageData.TYPE_KEY, FullPageData.class);
        PageTypes.typeNames.add(FullPageData.TYPE_KEY);
    }

    public static List<String> childTypes = new ArrayList<>();

    private enum keys{
        cssClass,
        content
    }

    protected String cssClass = "";
    protected String content = "";

    protected Map<String, SectionData> sections = new HashMap<>();

    // constructors and type

    public FullPageData() {
    }

    @Override
    public String getTypeKey(){
        return FullPageData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof FullPageData);
        FullPageData data = (FullPageData)idata;
        setCssClass(data.getCssClass());
    }

    public void copyPageAttributes(ContentData pdata){
        super.copyPageAttributes(pdata);
        assert pdata instanceof FullPageData;
        FullPageData data = (FullPageData) pdata;
        setContent(data.getContent());
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.cssClass.name(), cssClass);
        obj.put(keys.content.name(), content);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        cssClass = obj.optString(keys.cssClass.name());
        content = obj.optString(keys.content.name());

    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setCssClass(rdata.getString("cssClass"));
    }

    public void readPageRequestData(RequestData rdata) {
        super.readPageRequestData(rdata);
        setContent(rdata.getString("content"));
    }

    public void createPublishedContent(RequestData rdata){
        setPublishedContent(getContent());
        setPublishDate(Application.getCurrentTime());
    }

    // base data

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // view

    @Override
    public String getEditDataJsp() {
        return "/WEB-INF/_jsp/fullpage/editContentData.ajax.jsp";
    }

    @Override
    protected void displayEditContent(StringBuilder sb, RequestData rdata) {
        //todo
    }

    @Override
    protected void displayDraftContent(StringBuilder sb, RequestData rdata) {
        //todo
    }

}
