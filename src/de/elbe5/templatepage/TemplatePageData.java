/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.page.PageData;
import de.elbe5.page.PageTypes;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplatePageData extends PageData {

    public static final String TYPE_KEY = "templatepage";

    public static void register(){
        DataFactory.addClass(TemplatePageData.TYPE_KEY, TemplatePageData.class);
        PageTypes.typeNames.add(TemplatePageData.TYPE_KEY);
    }

    public static List<String> childTypes = new ArrayList<>();

    public static List<String> partTypes = new ArrayList<>();

    private enum keys{
        layout,
        sections
    }

    public static String PAGE_TYPE = "SectionPage";

    protected String layout = "";

    protected Map<String, SectionData> sections = new HashMap<>();

    // constructors and type

    public TemplatePageData() {
    }

    @Override
    public String getTypeKey(){
        return TemplatePageData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof TemplatePageData);
        TemplatePageData data = (TemplatePageData)idata;
        setLayout(data.getLayout());
    }

    public void copyPageAttributes(PageData pdata){
        super.copyPageAttributes(pdata);
        assert pdata instanceof TemplatePageData;
        TemplatePageData data = (TemplatePageData) pdata;
        sections.clear();
        for (String sectionName : data.sections.keySet()) {
            SectionData section = data.sections.get(sectionName);
            SectionData newSection = IData.getEditableCopy(section);
            if (newSection != null) {
                sections.put(sectionName, newSection);
            }
        }
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.layout.name(), layout);
        obj.put(keys.sections.name(), createJSONObjectFromStringMap(sections));
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        layout = obj.optString(keys.layout.name());
        sections = this.getStringMap(obj, keys.sections.name(), SectionData.class);
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setLayout(rdata.getString("layout"));
        if (getLayout().isEmpty()) {
            rdata.addIncompleteField("layout");
        }
    }

    public void readPageRequestData(RequestData rdata) {
        super.readPageRequestData(rdata);
        for (SectionData section : getSections().values()) {
            section.readRequestData(rdata);
        }
    }

    // overrides

    @Override
    public void collectChildTypes(List<String> list){
        super.collectChildTypes(list);
        list.addAll(childTypes);
    }

    public void collectPartTypes(List<String> list){
        list.addAll(partTypes);
    }

    public void createPublishedContent(RequestData rdata){
        setPublishedContent(getHtml(rdata));
        setPublishDate(Application.getCurrentTime());
    }

    // base data

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Map<String, SectionData> getSections() {
        return sections;
    }

    public SectionData getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public SectionData ensureSection(String sectionName) {
        if (!sections.containsKey(sectionName)) {
            SectionData section = new SectionData();
            section.setPageId(getId());
            section.setName(sectionName);
            sections.put(sectionName, section);
            return section;
        }
        return sections.get(sectionName);
    }

    // part data

    public void sortParts() {
        for (SectionData section : sections.values()) {
            section.sortParts();
        }
    }

    public TemplatePartData getPart(int pid) {
        for (SectionData section : getSections().values()) {
            TemplatePartData part = section.getPart(pid);
            if (part!=null)
                return part;
        }
        return null;
    }

    public void addPart(TemplatePartData part, int fromPartId, boolean setRanking) {
        SectionData section = getSection(part.getSectionName());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSectionName());
            sections.put(part.getSectionName(), section);
        }
        section.addPart(part, fromPartId, setRanking);
    }

    public void movePart(String sectionName, int id, int dir) {
        SectionData section = getSection(sectionName);
        section.movePart(id, dir);
    }

    public void deletePart(int pid) {
        for (SectionData section : getSections().values()) {
            TemplatePartData part = section.getPart(pid);
            if (part!=null) {
                section.deletePart(pid);
                break;
            }
        }
    }

    // view

    @Override
    public String getEditDataJsp() {
        return "/WEB-INF/_jsp/sectionpage/editContentData.ajax.jsp";
    }

    @Override
    protected void displayEditContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        context.include("/WEB-INF/_jsp/sectionpage/editPageContent.inc.jsp");
    }

    @Override
    protected void displayDraftContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        writer.write(getHtml(rdata));
    }

    protected void displayPublishedContent(PageContext context, JspWriter writer, SessionRequestData rdata) throws IOException, ServletException {
        writer.write(getHtml(rdata));
    }

    // html

    public String getHtml(RequestData rdata){
        TemplateContext context = new TemplateContext(rdata, this);
        PageTemplate template = TemplateCache.getPageTemplate(layout);
        if (template==null){
            Log.error("page template not found:" + layout);
            return "";
        }
        String html = template.processTemplate(context);
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());
        doc.outputSettings().indentAmount(2);
        html = "\n" + doc.toString() + "\n";
        //Log.log(html);
        return html;
    }

}
