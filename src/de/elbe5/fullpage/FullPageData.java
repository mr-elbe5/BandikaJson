/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.fullpage;

import de.elbe5.application.Application;
import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.content.*;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.templatepage.SectionData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullPageData extends EditableContentData {

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

    protected String cssClass = "paragraph";
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

    public void copyPageAttributes(EditableContentData pdata){
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
        setPublishedContent(StringUtil.format(viewContent,
                getCssClass(),
                getContent()));
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

    final String editContent = """
        <form action="/ctrl/{1}/savePage/{2}" method="post" id="pageform" name="pageform" accept-charset="UTF-8">
            <div class="btn-group btn-group-sm pageEditButtons">
              <button type="submit" class="btn btn-sm btn-success" onclick="updateEditor();">{3}</button>
              <button class="btn btn-sm btn-secondary" onclick="return linkTo('/ctrl/{4}/cancelEditPage/{5}');">{6}</button>
            </div>
            <div class="{7}">
              <div class="ckeditField" id="content" contenteditable="true">{8}</div>
            </div>
            <input type="hidden" name="content" value="{9}" />
        </form>
        """;

    final String viewContent = """
        <div class="{1}">
            {2}
        </div>
    """;

    @Override
    protected void displayEditContent(StringBuilder sb, RequestData rdata)  {
        sb.append(StringUtil.format(editContent,
                getTypeKey(),
                Integer.toString(getId()),
                Strings.html("_savePage", rdata.getLocale()),
                getTypeKey(),
                Integer.toString(getId()),
                Strings.html("_cancel", rdata.getLocale()),
                getCssClass(),
                getContent(),
                getContent()
        ));
        sb.append(StringUtil.format(editScripts,
                Integer.toString(getId()),
                Integer.toString(getId())
        ));
    }


    @Override
    protected void displayDraftContent(StringBuilder sb, RequestData rdata) {
        sb.append(StringUtil.format(viewContent,
                getCssClass(),
                getContent()));
    }

    public ContentViewContext createViewContext(ViewType viewType) {
        return new FullPageContext(this, viewType);
    }

    final String editScripts = """
        <script type="text/javascript">
            $('#content').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ajax/ckeditor/openLinkBrowser?contentId={1}',filebrowserImageBrowseUrl : '/ajax/ckeditor/openImageBrowser?contentId={2}'});
                    
            function updateEditor(){
             if (CKEDITOR) {
                 $('input[name="content"]').val(CKEDITOR.instances['content'].getData());
             }
            }
        </script>
            """;

}
