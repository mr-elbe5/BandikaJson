/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.base.data.StringUtil;
import de.elbe5.data.BaseData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.file.FileData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.Right;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.*;

public abstract class ContentData extends BaseData implements Comparable<ContentData> {

    public static final String TYPE_KEY = "content";

    public static final String ACCESS_TYPE_OPEN = "OPEN";
    public static final String ACCESS_TYPE_INHERITS = "INHERIT";
    public static final String ACCESS_TYPE_INDIVIDUAL = "INDIVIDUAL";

    public static final String NAV_TYPE_NONE = "NONE";
    public static final String NAV_TYPE_HEADER = "HEADER";
    public static final String NAV_TYPE_FOOTER = "FOOTER";

    public static final String VIEW_TYPE_SHOW = "SHOW";
    public static final String VIEW_TYPE_SHOWPUBLISHED = "PUBLISHED";
    public static final String VIEW_TYPE_EDIT = "EDIT";

    public static final int ID_ROOT = 1;

    public static void register(){
        DataFactory.addClass(ContentData.TYPE_KEY, ContentData.class);
    }

    public static List<String> childTypes = new ArrayList<>();

    private enum keys{
        name,
        displayName,
        description,
        accessType,
        navType,
        active,
        groupRights,
        children,
        files
    }

    // base data
    private String name = "";
    private String displayName = "";
    private String description = "";
    private String accessType = ACCESS_TYPE_OPEN;
    private String navType = NAV_TYPE_NONE;
    private boolean active = true;
    private Map<Integer, Right> groupRights = new HashMap<>();
    private List<ContentData> children = new ArrayList<>();
    private List<FileData> files = new ArrayList<>();

    protected int parentId = 0;
    protected int parentVersion = 0;
    protected int ranking = 0;

    protected boolean openAccess = true;
    protected String viewType = VIEW_TYPE_SHOW;

    // runtime

    private String path = "";

    // constructors

    protected ContentData() {
    }

    @Override
    public String getTypeKey(){
        return ContentData.TYPE_KEY;
    }

    // copy and editing methods

    public void setCreateValues(ContentData parent, int userId) {
        super.setCreateValues(userId);
        setParentId(parent.getId());
        setParentVersion(parent.getVersion());
        inheritRightsFromParent(parent);
    }

    public void setEditValues(ContentData data) {
        super.setEditValues(data);
        setParentId(data.getParentId());
        setParentVersion(data.getParentVersion());
    }

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof ContentData);
        ContentData data = (ContentData)idata;
        setName(data.getName());
        setDisplayName(data.getDisplayName());
        setDescription(data.getDescription());
        setAccessType(data.getAccessType());
        setNavType(data.getNavType());
        setActive(data.isActive());
        groupRights.clear();
        groupRights.putAll(data.getGroupRights());
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.name.name(), name);
        obj.put(keys.displayName.name(), displayName);
        obj.put(keys.description.name(), description);
        obj.put(keys.accessType.name(), accessType);
        obj.put(keys.navType.name(), navType);
        obj.put(keys.active.name(), active);
        JSONObject jo = new JSONObject();
        for (Integer key : groupRights.keySet()){
            jo.put(Integer.toString(key), groupRights.get(key).name());
        }
        obj.put(keys.groupRights.name(), jo);
        obj.put(keys.children.name(), createJSONArray(children));
        obj.put(keys.files.name(), createJSONArray(files));
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        name = obj.optString(keys.name.name());
        displayName = obj.optString(keys.displayName.name());
        description = obj.optString(keys.description.name());
        accessType = obj.optString(keys.accessType.name());
        navType = obj.optString(keys.navType.name());
        active = obj.optBoolean(keys.active.name());
        groupRights.clear();
        JSONObject jo = obj.optJSONObject(keys.groupRights.name());
        if (jo != null) {
            for (String key : jo.keySet()) {
                groupRights.put(Integer.parseInt(key), Right.valueOf(jo.getString(key)));
            }
        }
        children = getList(obj, keys.children.name(), ContentData.class);
        files = getList(obj, keys.files.name(), FileData.class);
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setDisplayName(rdata.getString("displayName").trim());
        setName(StringUtil.toSafeWebName(getDisplayName()));
        setDescription(rdata.getString("description"));
        if (getName().isEmpty()) {
            rdata.addIncompleteField("name");
        }
        setAccessType(rdata.getString("accessType"));
        setNavType((rdata.getString("navType")));
        setActive(rdata.getBoolean("active"));
    }

    // interface implementation and defaults

    @Override
    public int compareTo(ContentData data) {
        return getDisplayName().compareTo(data.getDisplayName());
    }

    public String getKeywords(){
        return "";
    }

    public String getEditDataJsp() {
        return "/WEB-INF/_jsp/content/editContentData.ajax.jsp";
    }

    public boolean isPublished() {
        return true;
    }

    public boolean hasUnpublishedDraft() {
        return false;
    }

    public String getSearchContent(){
        return "";
    }

    // getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void generatePath(ContentData parent) {
        if (parent == null)
            return;
        setPath(parent.getPath() + "/" + getName());
    }

    public String getUrl(){
        if (getPath().isEmpty())
            return "/home.html";
        return getPath() + ".html";
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getNavType() {
        return navType;
    }

    public void setNavType(String navType) {
        this.navType = navType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<Integer, Right> getGroupRights() {
        return groupRights;
    }

    public boolean isGroupRight(int id, Right right) {
        return groupRights.containsKey(id) && groupRights.get(id) == right;
    }

    public boolean hasAnyGroupRight(int id) {
        return groupRights.containsKey(id);
    }

    public void setGroupRights(Map<Integer, Right> groupRights) {
        this.groupRights = groupRights;
    }

    public void setParentIdAndVersion(ContentData parent) {
        setParentId(parent.getId());
        setParentVersion(parent.getVersion());
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(int parentVersion) {
        this.parentVersion = parentVersion;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void inheritRightsFromParent(ContentData parent) {
        getGroupRights().clear();
        if (parent != null) {
            getGroupRights().putAll(parent.getGroupRights());
        }
    }

    public List<ContentData> getChildren() {
        return children;
    }

    public boolean hasChildren(){
        return getChildren().size()>0;
    }

    public<T extends ContentData> List<T> getChildren(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (ContentData data : getChildren()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public void getAllChildren(List<ContentData> list) {
        if (!hasChildren())
            return;
        for (ContentData data : getChildren()) {
            list.add(data);
            data.getAllChildren(list);
        }
    }

    public<T extends ContentData> void getAllChildren(List<T> list,Class<T> cls) {
        if (!hasChildren())
            return;
        for (ContentData data : getChildren()) {
            try {
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
            catch(NullPointerException | ClassCastException e){
                // ignore
            }
            data.getAllChildren(list, cls);
        }
    }

    public void addChild(ContentData data) {
        children.add(data);
    }

    public void collectChildTypes(List<String> list){
        list.addAll(childTypes);
    }

    public List<FileData> getFiles() {
        return files;
    }

    public boolean hasFiles(){
        return getFiles().size()>0;
    }

    public<T extends FileData> List<T> getFiles(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (FileData data : getFiles()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public void addFile(FileData data) {
        files.add(data);
    }

    public boolean isOpenAccess() {
        return openAccess;
    }

    public void setOpenAccess(boolean openAccess) {
        this.openAccess = openAccess;
    }

    // view

    public void displayContent(PageContext context, SessionRequestData rdata) throws IOException, ServletException {

    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public boolean isEditing(){
        return viewType.equals(VIEW_TYPE_EDIT);
    }

    public void stopEditing(){
        this.viewType=VIEW_TYPE_SHOW;
    }

    public void startEditing(){
        this.viewType=VIEW_TYPE_EDIT;
    }

    public boolean isPublishedView(){
        return viewType.equals(VIEW_TYPE_SHOWPUBLISHED);
    }

    public boolean isStandardView(){
        return viewType.equals(VIEW_TYPE_SHOW);
    }

}
