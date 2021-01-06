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
import de.elbe5.data.JsonData;
import de.elbe5.request.RequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionData implements IData, JsonData {

    public static final String TYPE_KEY = "section";

    public static void register(){
        DataFactory.addClass(SectionData.TYPE_KEY, SectionData.class);
    }

    private enum keys{
        name,
        contentId,
        cssClass,
        parts
    }

    protected String name = "";
    protected int contentId = 0;
    protected String cssClass = "";
    protected List<TemplatePartData> parts = new ArrayList<>();

    // constructors and type

    public SectionData() {
    }

    @Override
    public String getTypeKey(){
        return TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyFixedAttributes(IData idata) {

    }

    @Override
    public void copyEditableAttributes(IData idata){
        assert idata instanceof SectionData;
        SectionData data =(SectionData) idata;
        setName(data.getName());
        setContentId(data.getContentId());
        setCssClass(data.getCssClass());
        parts.clear();
        for (TemplatePartData part : data.getParts()) {
            TemplatePartData p = IData.getEditableCopy(part);
            assert p!=null;
            parts.add(p);
        }
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(typeKey, getTypeKey());
        addJSONAttributes(obj);
        return obj;
    }

    public void addJSONAttributes(JSONObject obj) {
        obj.put(keys.name.name(), name);
        obj.put(keys.contentId.name(), contentId);
        obj.put(keys.cssClass.name(), cssClass);
        obj.put(keys.parts.name(), createJSONArray(parts));
    }

    public void fromJSONObject(JSONObject obj) throws JSONException {
        name = obj.optString(keys.name.name());
        contentId = obj.optInt(keys.contentId.name());
        cssClass = obj.optString(keys.cssClass.name());
        parts = getList(obj, keys.parts.name(), TemplatePartData.class);
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        for (int i=getParts().size()-1;i>=0;i--){
            TemplatePartData part = getParts().get(i);
            part.readRequestData(rdata);
            if (part.getPosition()==-1) {
                getParts().remove(i);
            }
        }
    }

    // getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionId(){
        return "section_"+getName();
    }

    public int getContentId() {
        return contentId;
    }

    public void setPageId(int contentId) {
        this.contentId = contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public List<TemplatePartData> getParts() {
        return parts;
    }

    public void sortParts() {
        Collections.sort(parts);
    }

    public TemplatePartData getPart(int pid) {
        for (TemplatePartData pdata : parts) {
            if (pdata.getId() == pid) {
                return pdata;
            }
        }
        return null;
    }

    public<T extends TemplatePartData> T getPart(int pid, Class<T> cls) {
        try{
            return cls.cast(getPart(pid));
        }
        catch (NullPointerException | ClassCastException e){
            //ignore
        }
        return null;
    }

    public void addPart(TemplatePartData part, int fromPartId, boolean setRanking) {
        boolean found = false;
        if (fromPartId != -1) {
            for (int i = 0; i < parts.size(); i++) {
                TemplatePartData ppd = parts.get(i);
                if (ppd.getId() == fromPartId) {
                    parts.add(i + 1, part);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            parts.add(part);
        }
        if (setRanking) {
            for (int i = 0; i < parts.size(); i++) {
                parts.get(i).setPosition(i + 1);
            }
        }
    }

    public void movePart(int id, int dir) {
        for (int i = 0; i < parts.size(); i++) {
            TemplatePartData ppd = parts.get(i);
            if (ppd.getId() == id) {
                parts.remove(i);
                int idx = i + dir;
                if (idx > parts.size() - 1) {
                    parts.add(ppd);
                } else if (idx < 0) {
                    parts.add(0, ppd);
                } else {
                    parts.add(idx, ppd);
                }
                break;
            }
        }
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).setPosition(i + 1);
        }
    }

    public void deletePart(int id) {
        for (int i = 0; i < parts.size(); i++) {
            TemplatePartData ppd = parts.get(i);
            if (ppd.getId() == id) {
                parts.remove(i);
                return;
            }
        }
    }

}
