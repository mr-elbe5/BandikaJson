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
import de.elbe5.data.BaseData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.*;

public class TemplatePartData extends BaseData implements Comparable<TemplatePartData>  {

    public static final String TYPE_KEY = "templatepart";

    public static String LAYOUT_TYPE = "Part";

    public static void register(){
        DataFactory.addClass(TemplatePartData.TYPE_KEY, TemplatePartData.class);
    }

    public static List<String> fieldTypes = new ArrayList<>();

    private enum keys{
        cssClass,
        sectionName,
        position,
        editable,
        layout,
        publishDate,
        publishedContent,
        fields
    }

    protected String cssClass = "";
    protected String sectionName = "";
    protected int position = 0;
    protected boolean editable = true;
    protected String layout="";
    protected LocalDateTime publishDate = null;
    protected String publishedContent = "";

    protected Map<String, PartField> fields = new HashMap<>();

    // constructors and type

    public TemplatePartData() {
    }

    @Override
    public String getTypeKey(){
        return TemplatePartData.TYPE_KEY;
    }

    // copy and editing methods

    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof TemplatePartData);
        TemplatePartData data = (TemplatePartData)idata;
        setCssClass(data.getCssClass());
        setSectionName(data.getSectionName());
        setPosition(data.getPosition());
        setEditable(data.isEditable());
        setLayout(data.getLayout());
        setPublishDate(data.getPublishDate());
        setPublishedContent(data.getPublishedContent());
        fields.clear();
        for (String fieldName : data.getFields().keySet()){
            PartField field = IData.getEditableCopy(data.getFields().get(fieldName));
            if (field != null){
                fields.put(fieldName, field);
            }
        }
    }

    public void setCreateValues(SessionRequestData rdata) {
        setSectionName(rdata.getString("sectionName"));
        setLayout(rdata.getString("layout"));
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
        obj.put(keys.layout.name(), layout);
        obj.put(keys.publishDate.name(), jsonString(publishDate));
        obj.put(keys.publishedContent.name(), publishedContent);
        obj.put(keys.fields.name(), createJSONObjectFromStringMap(fields));
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        cssClass = obj.optString(keys.cssClass.name());
        sectionName = obj.optString(keys.sectionName.name());
        position = obj.optInt(keys.position.name());
        editable = obj.optBoolean(keys.editable.name());
        layout = obj.optString(keys.layout.name());
        publishDate = getLocalDateTime(obj.optString(keys.publishDate.name()));
        publishedContent = keys.publishedContent.name();
        fields = getStringMap(obj, keys.fields.name(), PartField.class);
    }

    // overrides

    public void collectFieldTypes(List<String> list){
        list.addAll(fieldTypes);
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        // -1 if deleted
        setPosition(rdata.getInt(getPartPositionName(),-1));
        for (PartField field : getFields().values()) {
            field.readRequestData(rdata);
        }
    }

    @Override
    public int compareTo(TemplatePartData data) {
        return position - data.position;
    }

    // interface implementation and defaults

    public String getEditTitle(Locale locale) {
        return "Section Part, ID=" + getId();
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

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public boolean hasUnpublishedDraft() {
        return publishDate == null || publishDate.isBefore(getChangeDate());
    }

    public boolean isPublished() {
        return publishDate != null;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(String publishedContent) {
        this.publishedContent = publishedContent;
    }

    public Map<String, PartField> getFields() {
        return fields;
    }

    public PartField getField(String name) {
        return fields.get(name);
    }

    public TextField ensureTextField(String name) {
        PartField field = fields.get(name);
        if (field instanceof TextField)
            return (TextField) field;
        TextField textfield = new TextField();
        textfield.setName(name);
        textfield.setPartId(getId());
        fields.put(name, textfield);
        return textfield;
    }

    public HtmlField ensureHtmlField(String name) {
        PartField field = fields.get(name);
        if (field instanceof HtmlField)
            return (HtmlField) field;
        HtmlField htmlfield = new HtmlField();
        htmlfield.setName(name);
        htmlfield.setPartId(getId());
        fields.put(name, htmlfield);
        return htmlfield;
    }

    public ScriptField ensureScriptField(String name) {
        PartField field = fields.get(name);
        if (field instanceof ScriptField)
            return (ScriptField) field;
        ScriptField scriptField = new ScriptField();
        scriptField.setName(name);
        scriptField.setPartId(getId());
        fields.put(name, scriptField);
        return scriptField;
    }

    // html

    public void appendHtml(StringBuilder sb, TemplateContext context){
        PartTemplate template = TemplateCache.getPartTemplate(layout);
        if (template==null){
            Log.error("part template not found:" + layout);
            return;
        }
        template.processCode(sb, context);
    }

}
