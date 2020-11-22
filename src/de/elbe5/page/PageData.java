/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.application.Application;
import de.elbe5.content.ContentData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.MasterResponse;
import de.elbe5.rights.ContentRights;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class PageData extends ContentData {

    public static final String TYPE_KEY = "page";

    public static void register(){
        DataFactory.addClass(PageData.TYPE_KEY, PageData.class);
    }

    public static List<String> childTypes = new ArrayList<>();

    private enum keys{
        keywords,
        master,
        publishDate,
        publishedContent
    }

    public static String MASTER_TYPE = "Master";

    private String keywords = "";
    protected String master = MasterResponse.DEFAULT_MASTER;
    protected LocalDateTime publishDate = null;
    protected String publishedContent="";

    // constructors

    protected PageData(){

    }

    @Override
    public String getTypeKey(){
        return PageData.TYPE_KEY;
    }

    // copy and editing methods

    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof PageData);
        PageData data = (PageData)idata;
        setKeywords(data.getKeywords());
        setMaster(data.getMaster());
        setPublishDate(data.getPublishDate());
        setPublishedContent(data.getPublishedContent());
    }

    public void copyPageAttributes(PageData pdata){
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.keywords.name(), keywords);
        obj.put(keys.master.name(), master);
        obj.put(keys.publishDate.name(), jsonString(publishDate));
        obj.put(keys.publishedContent.name(), publishedContent);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        keywords = obj.optString(keys.keywords.name());
        master = obj.optString(keys.master.name());
        publishDate = getLocalDateTime(obj.optString(keys.publishDate.name()));
        publishedContent = obj.optString(keys.publishedContent.name());
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setKeywords(rdata.getString("keywords"));
        setMaster(rdata.getString("master"));
    }

    public void readPageRequestData(RequestData rdata) {
    }

    // interface implementation and defaults

    protected void displayEditContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
    }

    protected void displayDraftContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
    }

    protected void displayPublishedContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        writer.write(publishedContent);
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return getPublishDate() != null;
    }

    @Override
    public void collectChildTypes(List<String> list){
        super.collectChildTypes(list);
        list.addAll(childTypes);
    }

    @Override
    public String getSearchContent(){
        Document doc = Jsoup.parse(getPublishedContent(), "", Parser.htmlParser());
        return doc.text();
    }

    // getter and setter

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate=publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public void reformatPublishedContent() {
        Document doc= Jsoup.parseBodyFragment(getPublishedContent());
        setPublishedContent(doc.body().html());
    }

    // view

    @Override
    public String getEditDataJsp() {
        return "/WEB-INF/_jsp/page/editContentData.ajax.jsp";
    }

    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {
        JspWriter writer = context.getOut();
        switch (getViewType()) {
            case VIEW_TYPE_PUBLISH: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                StringWriter stringWriter = new StringWriter();
                context.pushBody(stringWriter);
                displayDraftContent(context, context.getOut(), rdata);
                setPublishedContent(stringWriter.toString());
                reformatPublishedContent();
                Application.getContent().publishPage(this);
                context.popBody();
                writer.write(getPublishedContent());
                setViewType(ContentData.VIEW_TYPE_SHOW);
                writer.write("</div>");
            }
            break;
            case VIEW_TYPE_EDIT: {
                writer.write("<div id=\"pageContent\" class=\"editArea\">");
                displayEditContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
            case VIEW_TYPE_SHOWPUBLISHED: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                if (isPublished())
                    displayPublishedContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
            default: {
                writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                if (isPublished() && !ContentRights.hasUserEditRight(rdata.getCurrentUser(), getId()))
                    displayPublishedContent(context, context.getOut(), rdata);
                else
                    displayDraftContent(context, context.getOut(), rdata);
                writer.write("</div>");
            }
            break;
        }
    }

}
