/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.application.Application;
import de.elbe5.request.RequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public abstract class BaseData implements IData, JsonData {

    public static final String TYPE_KEY = "base";

    private enum keys{
        id,
        version,
        creationDate,
        changeDate,
        creatorId,
        changerId
    }

    private int id = 0;
    private int version = 1;
    private boolean isNew = false;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime changeDate = LocalDateTime.now();
    private int creatorId = 0;
    private int changerId = 0;

    // constructors

    public BaseData(){

    }

    @Override
    public String getTypeKey(){
        return BaseData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyFixedAttributes(IData idata){
        assert idata instanceof BaseData;
        BaseData data = (BaseData) idata;
        setId(data.getId());
        setCreationDate(data.getCreationDate());
        setCreatorId(data.getCreatorId());
    }

    @Override
    public void copyEditableAttributes(IData idata){
        assert idata instanceof BaseData;
        BaseData data = (BaseData) idata;
        setVersion(data.getVersion());
        setChangeDate(data.getChangeDate());
        setChangerId(data.getChangerId());
    }

    public void setCreateValues(int userId) {
        setNew(true);
        setId(Application.getNextId());
        setVersion(1);
        setCreatorId(userId);
        setCreationDate(Application.getCurrentTime());
        setChangerId(userId);
        setChangeDate(getCreationDate());
    }

    public void setEditValues(BaseData data) {
        copyFixedAttributes(data);
        copyEditableAttributes(data);
    }

    public boolean isEqualByIdAndVersion(BaseData data){
        return data!=null && getId()==data.getId() && getVersion()==data.getVersion();
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(typeKey, getTypeKey());
        addJSONAttributes(obj);
        return obj;
    }

    public void addJSONAttributes(JSONObject obj) {
        obj.put(keys.id.name(), id);
        obj.put(keys.version.name(), version);
        obj.put(keys.changeDate.name(), jsonString(changeDate));
        obj.put(keys.creationDate.name(), jsonString(creationDate));
        obj.put(keys.creatorId.name(), creatorId);
        obj.put(keys.changerId.name(), changerId);
    }

    public void fromJSONObject(JSONObject obj) throws JSONException {
        id = obj.optInt(keys.id.name());
        version = obj.optInt(keys.version.name());
        changeDate = getLocalDateTime(obj.getString(keys.changeDate.name()));
        creationDate = getLocalDateTime(obj.getString(keys.creationDate.name()));
        creatorId = obj.getInt(keys.creatorId.name());
        changerId = obj.getInt(keys.changerId.name());
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){

    }

    // overrideables

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass()) && (id == ((BaseData)obj).getId());
    }

    // getter and setter

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void increaseVersion(){
        version++;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
        if (creationDate == null)
            creationDate = changeDate;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getChangerId() {
        return changerId;
    }

    public void setChangerId(int changerId) {
        this.changerId = changerId;
    }

}
