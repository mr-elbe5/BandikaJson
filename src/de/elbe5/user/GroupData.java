/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.content.ContentData;
import de.elbe5.data.BaseData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.*;

public class GroupData extends BaseData {

    public static final String TYPE_KEY = "group";

    public static Constructor<GroupData> defaultConstructor = DataFactory.createDefaultConstructor(GroupData.class);

    public static final int ID_ALL = 0;
    public static final int ID_GLOBAL_ADMINISTRATORS = 1;
    public static final int ID_GLOBAL_APPROVERS = 2;
    public static final int ID_GLOBAL_EDITORS = 3;
    public static final int ID_GLOBAL_READERS = 4;

    public static final int ID_MAX_FINAL = 4;

    public static void register(){
        DataFactory.addClass(GroupData.TYPE_KEY, GroupData.class);
    }

    private enum keys{
        name,
        notes,
        systemRights
    }

    protected String name = null;
    protected String notes = "";
    protected Set<SystemZone> systemRights = new HashSet<>();

    protected Set<Integer> userIds = new HashSet<>();

    // constructors

    public GroupData(){

    }

    @Override
    public String getTypeKey(){
        return GroupData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof GroupData);
        GroupData data = (GroupData) idata;
        setName(data.getName());
        setNotes(data.getNotes());
        userIds.clear();
        userIds.addAll(data.getUserIds());
        systemRights.clear();
        systemRights.addAll(data.getSystemRights());
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.name.name(), name);
        obj.put(keys.notes.name(), notes);
        JSONArray srs = new JSONArray();
        for (SystemZone key : systemRights){
            srs.put(key.name());
        }
        obj.put(keys.systemRights.name(), srs);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        name = obj.optString(keys.name.name());
        notes = obj.optString(keys.notes.name());
        JSONArray srs = obj.optJSONArray(keys.systemRights.name());
        systemRights.clear();
        if (srs != null) {
            for (int i = 0; i < srs.length(); i++) {
                systemRights.add(SystemZone.valueOf(srs.getString(i)));
            }
        }
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setName(rdata.getString("name"));
        setNotes(rdata.getString("notes"));
        getSystemRights().clear();
        for (SystemZone zone : SystemZone.values()) {
            boolean hasRight = rdata.getBoolean("zoneright_" + zone.name());
            if (hasRight)
                addSystemRight(zone);
        }
        setUserIds(rdata.getIntegerSet("userIds"));
        if (getName().isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

    // getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public Set<SystemZone> getSystemRights() {
        return systemRights;
    }

    public void addSystemRight(SystemZone zone) {
        systemRights.add(zone);
    }

    public boolean hasSystemRight(SystemZone zone) {
        return systemRights.contains(zone);
    }

}
