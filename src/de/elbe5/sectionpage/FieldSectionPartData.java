/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.sectionpage;

import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.*;

public class FieldSectionPartData extends SectionPartData {

    public static final String TYPE_KEY = "fieldsectionpart";

    public static void register(){
        DataFactory.addClass(FieldSectionPartData.TYPE_KEY, FieldSectionPartData.class);
    }

    public static List<String> fieldTypes = new ArrayList<>();

    private enum keys{
        layout,
        publishDate,
        publishedContent,
        fields
    }

    protected String layout="";
    protected LocalDateTime publishDate = null;
    protected String publishedContent = "";

    protected Map<String, PartField> fields = new HashMap<>();

    // constructors and type

    public FieldSectionPartData() {
    }

    @Override
    public String getTypeKey(){
        return FieldSectionPartData.TYPE_KEY;
    }

    // copy and editing methods

    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof FieldSectionPartData);
        FieldSectionPartData data = (FieldSectionPartData)idata;
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

    @Override
    public void setCreateValues(SessionRequestData rdata) {
        super.setCreateValues(rdata);
        setSectionName(rdata.getString("sectionName"));
        setLayout(rdata.getString("layout"));
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.layout.name(), layout);
        obj.put(keys.publishDate.name(), jsonString(publishDate));
        obj.put(keys.publishedContent.name(), publishedContent);
        obj.put(keys.fields.name(), createJSONObjectFromStringMap(fields));
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
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
        super.readRequestData(rdata);
        for (PartField field : getFields().values()) {
            field.readRequestData(rdata);
        }
    }

    // interface implementation and defaults

    public String getJspPath() {
        return jspBasePath + "/sectionpage";
    }

    // getter and setter

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTemplateUrl() {
        return "/WEB-INF/_jsp/_layout/"+ layout +".jsp";
    }

    public String getPartInclude() {
        return getTemplateUrl();
    }

    public String getEditPartInclude() {
        return getTemplateUrl();
    }

    public String getEditTitle(Locale lcale) {
        return getLayout() + ", ID=" + getId();
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

    public PartTextField ensureTextField(String name) {
        PartField field = fields.get(name);
        if (field instanceof PartTextField)
            return (PartTextField) field;
        PartTextField textfield = new PartTextField();
        textfield.setName(name);
        textfield.setPartId(getId());
        fields.put(name, textfield);
        return textfield;
    }

    public PartHtmlField ensureHtmlField(String name) {
        PartField field = fields.get(name);
        if (field instanceof PartHtmlField)
            return (PartHtmlField) field;
        PartHtmlField htmlfield = new PartHtmlField();
        htmlfield.setName(name);
        htmlfield.setPartId(getId());
        fields.put(name, htmlfield);
        return htmlfield;
    }

    public PartScriptField ensureScriptField(String name) {
        PartField field = fields.get(name);
        if (field instanceof PartScriptField)
            return (PartScriptField) field;
        PartScriptField scriptField = new PartScriptField();
        scriptField.setName(name);
        scriptField.setPartId(getId());
        fields.put(name, scriptField);
        return scriptField;
    }

}
