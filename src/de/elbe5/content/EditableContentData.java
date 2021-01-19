/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.rights.ContentRights;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.time.LocalDateTime;

public abstract class EditableContentData extends ContentData {

    public static void register(){
        DataFactory.addClass(EditableContentData.TYPE_KEY, EditableContentData.class);
    }

    private enum keys{
        publishDate,
        publishedContent
    }

    // base data
    private LocalDateTime publishDate = null;
    private String publishedContent="";

    // constructors

    protected EditableContentData() {
    }

    @Override
    public String getTypeKey(){
        return EditableContentData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof EditableContentData);
        EditableContentData data = (EditableContentData)idata;
        setPublishDate(data.getPublishDate());
        setPublishedContent(data.getPublishedContent());
    }

    public void copyPageAttributes(EditableContentData data){
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.publishDate.name(), jsonString(publishDate));
        obj.put(keys.publishedContent.name(), publishedContent);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        publishDate = getLocalDateTime(obj.optString(keys.publishDate.name()));
        publishedContent = obj.optString(keys.publishedContent.name());
    }

    // request

    public void readPageRequestData(RequestData rdata) {
    }

    // interface implementation and defaults

    protected void displayEditContent(StringBuilder sb, RequestData rdata)  {
    }

    protected void displayDraftContent(StringBuilder sb, RequestData rdata)  {
    }

    protected void displayPublishedContent(StringBuilder sb, RequestData rdata)  {
        sb.append(publishedContent);
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return getPublishDate() != null;
    }

    public String getSearchContent(){
        Document doc = Jsoup.parse(getPublishedContent(), "", Parser.htmlParser());
        return doc.text();
    }

    // getter and setter

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

    public void createPublishedContent(RequestData rdata){
    }

    // view

    public ContentViewContext createViewContext(ViewType viewType){
        return new ContentViewContext(this, viewType);
    }

    public void displayContent(PageContext context, RequestData rdata) throws ServletException, IOException {
        context.getOut().write(getContent(rdata));
    }

    public String getContent(RequestData rdata) {
        StringBuilder sb = new StringBuilder();
        switch (rdata.getViewContext().getViewType()) {
            case edit -> {
                sb.append("<div id=\"pageContent\" class=\"editArea\">");
                displayEditContent(sb, rdata);
                sb.append("</div>");
            }
            case showPublished -> {
                sb.append("<div id=\"pageContent\" class=\"viewArea\">");
                if (isPublished())
                    displayPublishedContent(sb, rdata);
                sb.append("</div>");
            }
            case showDraft -> {
                sb.append("<div id=\"pageContent\" class=\"viewArea\">");
                if (ContentRights.hasUserEditRight(rdata.getCurrentUser(), getId()))
                    displayDraftContent(sb, rdata);
                sb.append("</div>");
            }
            case show -> {
                sb.append("<div id=\"pageContent\" class=\"viewArea\">");
                if (ContentRights.hasUserEditRight(rdata.getCurrentUser(), getId())) {
                    //Log.log("display draft");
                    displayDraftContent(sb, rdata);
                }
                else if (isPublished()){
                    //Log.log("display published");
                    displayPublishedContent(sb, rdata);
                }
                sb.append("</div>");
            }
        }
        String html = sb.toString();
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());
        doc.outputSettings().indentAmount(2);
        html = "\n" + doc.toString() + "\n";
        //Log.log(html);
        return html;
    }

}
