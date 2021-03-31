/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.application.Application;
import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.base.log.Log;
import de.elbe5.content.*;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateCache;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.util.*;

public class TemplatePageData extends EditableContentData {

    public static final String TYPE_KEY = "templatepage";

    public static void register(){
        DataFactory.addClass(TemplatePageData.TYPE_KEY, TemplatePageData.class);
        PageTypes.typeNames.add(TemplatePageData.TYPE_KEY);
    }

    public static List<String> childTypes = new ArrayList<>();

    public static List<String> partTypes = new ArrayList<>();

    private enum keys{
        template,
        sections
    }

    protected String template = "";

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
        setTemplate(data.getTemplate());
    }

    public void copyPageAttributes(EditableContentData pdata){
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
        obj.put(keys.template.name(), template);
        obj.put(keys.sections.name(), createJSONObjectFromStringMap(sections));
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        template = obj.optString(keys.template.name());
        sections.clear();
        if (obj.has(keys.sections.name())) {
            JSONObject mapObject = obj.optJSONObject(keys.sections.name());
            if (mapObject == null){
                return;
            }
            for (String okey : mapObject.keySet()) {
                JSONObject jo = mapObject.optJSONObject(okey);
                if (jo == null){
                    continue;
                }
                SectionData section = new SectionData();
                section.fromJSONObject(jo);
                sections.put(okey, section);
            }
        }
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setTemplate(rdata.getString("template"));
        if (getTemplate().isEmpty()) {
            rdata.addIncompleteField("template");
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
        rdata.setViewContext(new TemplatePageContext(this, ViewType.show));
        StringBuilder sb = new StringBuilder();
        appendContent(sb, rdata);
        String html = sb.toString();
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());
        doc.outputSettings().indentAmount(2);
        html = "\n" + doc.toString() + "\n";
        setPublishedContent(html);
        setPublishDate(Application.getCurrentTime());
    }

    // base data

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

    public void addPart(TemplatePartData part, int fromPartId) {
        SectionData section = getSection(part.getSectionName());
        if (section == null) {
            section = new SectionData();
            section.setPageId(getId());
            section.setName(part.getSectionName());
            sections.put(part.getSectionName(), section);
        }
        section.addPart(part, fromPartId);
    }

    // view

    @Override
    public String getEditDataJsp() {
        return "/WEB-INF/_jsp/templatepage/editContentData.ajax.jsp";
    }

    final String editContentStart = """
            <form action="/ctrl/{1}/savePage/{2}" method="post" id="pageform" name="pageform" accept-charset="UTF-8">
                    <div class="btn-group btn-group-sm pageEditButtons">
                        <button type="submit" class="btn btn-sm btn-success" onclick="updateEditors();">{3}</button>
                        <button class="btn btn-sm btn-secondary" onclick="return linkTo('/ctrl/{4}/cancelEditPage/{5}');">{6}</button>
                    </div>
            """;

    final String editContentEnd = """
                </form>
            """;

    @Override
    protected void displayEditContent(StringBuilder sb, RequestData rdata)  {
        sb.append(StringUtil.format(editContentStart,
                getTypeKey(),
                Integer.toString(getId()),
                Strings.html("_savePage", rdata.getLocale()),
                getTypeKey(),
                Integer.toString(getId()),
                Strings.html("_cancel", rdata.getLocale())
        ));
        appendContent(sb, rdata);
        sb.append(editContentEnd);
        sb.append(StringUtil.format(editScripts,
                Strings.js("_confirmDelete",rdata.getLocale()),
                getTypeKey(),
                Integer.toString(getId())

        ));
    }

    @Override
    protected void displayDraftContent(StringBuilder sb, RequestData rdata)  {
        appendContent(sb, rdata);
    }

    // html

    public ContentViewContext createViewContext(ViewType viewType) {
        return new TemplatePageContext(this, viewType);
    }

    public void appendContent(StringBuilder sb, RequestData rdata){
        PageTemplate template = TemplateCache.getPageTemplate(this.template);
        if (template==null){
            Log.error("page template not found:" + this.template);
            return;
        }
        template.processTemplate(sb, rdata);
    }

    final String editScripts = """
            <script type="text/javascript">
                    function confirmDelete() {
                        return confirm('{1}');
                    }
                    function updatePartButtonsVisible(){
                        $(".section").each(function () {
                            let $this = $(this);
                            let $buttonDiv = $this.find('div.addPartButtons');
                            let partCount = $this.find('div.partWrapper').length;
                            if (partCount === 0){
                                $buttonDiv.show();
                            }
                            else{
                                $buttonDiv.hide();
                            }
                        });
                    }
                    function movePart(id,direction){
                        let $partWrapper=$('#part_'+id);
                        if (direction===1){
                            let $nextPart=$partWrapper.next();
                            if (!$nextPart || $nextPart.length===0){
                                return false;
                            }
                            $partWrapper.detach();
                            $nextPart.after($partWrapper);
                        }
                        else{
                            let $prevPart=$partWrapper.prev();
                            if (!$prevPart || $prevPart.length===0){
                                return false;
                            }
                            $partWrapper.detach();
                            $prevPart.before($partWrapper);
                        }
                        updatePartPositions();
                        return false;
                    }
                    function deletePart(id){
                        let $partWrapper=$('#part_'+id);
                        $partWrapper.remove();
                        updatePartPositions();
                        updatePartButtonsVisible();
                        return false;
                    }
                    function addPart(fromId, section, type, layout){
                        let data = {
                            fromPartId: fromId,
                            sectionName: section,
                            partType: type,
                            layout: layout
                        };
                        $.ajax({
                            url: '/ajax/{2}/addPart/' + {3},
                            type: 'POST',
                            data: data,
                            dataType: 'html'
                        }).success(function (html, textStatus) {
                            if (fromId === -1) {
                                let $section=$('#pageform').find('#section_'+section);
                                $section.append(html);
                            }
                            else{
                                let $fromPartWrapper = $('#part_' + fromId);
                                if ($fromPartWrapper) {
                                    $fromPartWrapper.after(html);
                                }
                            }
                            updatePartPositions();
                            updatePartButtonsVisible();
                        });
                        return false;
                    }
                    function updateEditors(){
                        if (CKEDITOR) {
                            $(".ckeditField").each(function () {
                                let id = $(this).attr('id');
                                $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
                            });
                        }
                    }
                    function updatePartEditors($part){
                        if (CKEDITOR) {
                            $(".ckeditField",$part).each(function () {
                                let id = $(this).attr('id');
                                $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
                            });
                        }
                    }
                    function updatePartPositions(){
                        let $sections=$('#pageform').find('.section');
                        $sections.each(function(){
                            updateSectionPartPositions($(this));
                        });
                    }
                    function updateSectionPartPositions($section){
                        let $inputs = $section.find('input.partPos');
                        $inputs.each(function (index) {
                            $(this).attr('value', index);
                        });
                    }
                    updatePartPositions();
                    updatePartButtonsVisible();
                </script>
                        
            """;

}
