/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Application;
import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import de.elbe5.data.DataContainer;
import de.elbe5.data.DefaultUserContainer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class UserContainer extends DataContainer {

    private enum keys{
        users,
        groups
    }

    protected List<UserData> users = new ArrayList<>();
    protected List<GroupData> groups = new ArrayList<>();

    private final Map<Integer, UserData> userMap = new HashMap<>();
    private final Map<String, UserData> userLoginMap = new HashMap<>();
    private final Map<Integer, GroupData> groupMap = new HashMap<>();

    // constructors and initializers

    public UserContainer(){
    }

    public boolean initialize(){
        Log.log("initializing users");
        if (!ApplicationPath.getUsersFile().exists()) {
            UserContainer dc = new DefaultUserContainer();
            dc.save();
        }
        String json = ApplicationPath.getUsersFile().readAsText();
        try{
            JSONObject obj = new JSONObject(json);
            fromJSONObject(obj);
            changed = false;
            return true;
        }
        catch (JSONException e){
            return false;
        }
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        super.addJSONAttributes(obj);
        obj.put(keys.users.name(), createJSONArray(users));
        obj.put(keys.groups.name(), createJSONArray(groups));
        return obj;
    }

    public void fromJSONObject(JSONObject obj) {
        try {
            groups = getList(obj, keys.groups.name(), GroupData.class);
            mapGroups();
            users = getList(obj, keys.users.name(), UserData.class);
            mapUsers();
            super.getJSONAttributes(obj);
        }
        catch (Exception e){
            Log.error("unable to read data", e);
        }
    }

    private  void mapGroups() {
        groupMap.clear();
        for (GroupData group : groups){
            groupMap.put(group.getId(), group);
        }
        Log.log("groups mapped to ids");
    }

    private  void mapUsers() {
        userMap.clear();
        for (UserData user : users){
            userMap.put(user.getId(), user);
            userLoginMap.put(user.getLogin(), user);
            for (Integer gid : user.getGroupIds()){
                GroupData group = getGroup(gid);
                if (group != null){
                    group.getUserIds().add(user.getId());
                }
            }
        }
        Log.log("users mapped to ids");
    }

    // getter and setter

    // groups

    public List<GroupData> getGroups() {
        return groups;
    }

    public GroupData getGroup(int id) {
        return groupMap.get(id);
    }

    // group changes

    public boolean addGroup(GroupData data, int userId){
        try {
            dataLock.lock();
            data.setNew(false);
            groups.add(data);
            groupMap.put(data.getId(), data);
            data.setChangerId(userId);
            data.setChangeDate(Application.getCurrentTime());
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;
    }

    public boolean updateGroup(GroupData data, int userId){
        boolean success = false;
        try {
            dataLock.lock();
            GroupData original = getGroup(data.getId());
            if (original != null && original.getVersion() == data.getVersion()){
                removeGroupUsers(original);
                original.copyEditableAttributes(data);
                addGroupUsers(original);
                original.setChangerId(userId);
                original.setChangeDate(Application.getCurrentTime());
                original.increaseVersion();
                setHasChanged();
                success = true;
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    private void removeGroupUsers(GroupData data){
        for (int userId : data.getUserIds()){
            UserData user = getUser(userId);
            if (user!=null) {
                user.getGroupIds().remove(data.getId());
            }
            else{
                Log.warn("removing group from user: user not found: " + userId);
            }
        }
    }

    private void addGroupUsers(GroupData data){
        for (int userId : data.getUserIds()){
            UserData user = getUser(userId);
            if (user!=null) {
                user.getGroupIds().add(data.getId());
            }
            else{
                Log.warn("adding group to user: user not found: " + userId);
            }
        }
    }

    public boolean removeGroup(GroupData data){
        try {
            dataLock.lock();
            removeGroupUsers(data);
            groups.remove(data);
            groupMap.remove(data.getId());
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;
    }

    // users

    public List<UserData> getUsers() {
        return users;
    }

    public UserData getUser(String login, String pwd) {
        UserData data = userLoginMap.get(login);
        if (data == null){
            return null;
        }
        String passwordHash = UserSecurity.encryptPassword(pwd, Application.getSalt());
        if (data.getPasswordHash().equals(passwordHash)) {
            return data;
        }
        return null;
    }

    public UserData getUser(int id) {
        return userMap.get(id);
    }

    public UserData getUser(String login) {
        return userLoginMap.get(login);
    }

    // user changes

    public boolean addUser(UserData data, int userId){
        try {
            dataLock.lock();
            data.setNew(false);
            users.add(data);
            userMap.put(data.getId(), data);
            userLoginMap.put(data.getLogin(), data);
            setUserToGroups(data);
            data.setChangerId(userId);
            data.setChangeDate(Application.getCurrentTime());
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;

    }

    public boolean updateUser(UserData data, int userId){
        boolean success = false;
        try {
            dataLock.lock();
            UserData original = getUser(data.getId());
            if (original != null && original.getVersion() == data.getVersion()) {
                removeUserFromGroups(original);
                original.copyEditableAttributes(data);
                setUserToGroups(original);
                original.increaseVersion();
                original.setChangerId(userId);
                original.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
        } finally {
            dataLock.unlock();
        }
        return success;

    }

    public boolean updateUserPassword(UserData data, String newPassword){
        boolean success;
        try {
            dataLock.lock();
            data.setPassword(newPassword);
            data.setChangerId(data.getId());
            data.setChangeDate(Application.getCurrentTime());
            data.increaseVersion();
            setHasChanged();
            success = true;
        } finally {
            dataLock.unlock();
        }
        return success;

    }

    private void removeUserFromGroups(UserData data){
        for (int groupId : data.getGroupIds()){
            GroupData group = getGroup(groupId);
            if (group!=null) {
                group.getUserIds().remove(data.getId());
            }
            else{
                Log.warn("removing user from group: group not found: " + groupId);
            }
        }
    }

    private boolean setUserToGroups(UserData data){
        for (int groupId : data.getGroupIds()){
            GroupData group = getGroup(groupId);
            if (group!=null) {
                group.getUserIds().add(data.getId());
            }
            else{
                Log.warn("adding user to group: group not found: " + groupId);
            }
        }
        return true;

    }

    public boolean removeUser(UserData data){
        try {
            dataLock.lock();
            removeUserFromGroups(data);
            users.remove(data);
            userMap.remove(data.getId());
            userLoginMap.remove(data.getLogin());
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;
    }

    //persistance

    protected boolean save(){
        JSONObject obj = toJSONObject();
        String jsonString = obj.toString(4);
        return ApplicationPath.getUsersFile().writeToDisk(jsonString);
    }

}
