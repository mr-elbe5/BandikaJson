/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.*;
import de.elbe5.rights.SystemRights;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;

public class GroupController extends Controller {

    private static GroupController instance = null;

    public static void setInstance(GroupController instance) {
        GroupController.instance = instance;
    }

    public static GroupController getInstance() {
        return instance;
    }

    public static void register(GroupController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return GroupData.TYPE_KEY;
    }

    public IResponse openEditGroup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        int groupId = rdata.getId();
        GroupData data = new GroupData();
        GroupData original = userContainer().getGroup(groupId);
        data.setEditValues(original);
        rdata.setSessionObject(RequestData.KEY_GROUP, data);
        return showEditGroup();
    }

    public IResponse openCreateGroup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        GroupData data = new GroupData();
        data.setNew(true);
        data.setId(Application.getNextId());
        rdata.setSessionObject(RequestData.KEY_GROUP, data);
        return showEditGroup();
    }

    public IResponse saveGroup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        GroupData data = (GroupData) rdata.getSessionObject(RequestData.KEY_GROUP);
        assert(data!=null);
        data.readRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showEditGroup();
        }
        if (data.isNew()){
            userContainer().addGroup(data, rdata.getUserId());
        }
        else{
            if (!userContainer().updateGroup(data, rdata.getUserId())){
                Log.warn("original data not found for update.");
                setError(rdata,"_versionError");
                return showEditGroup();
            }
        }
        rdata.removeSessionObject(RequestData.KEY_GROUP);
        setSuccess(rdata,"_groupSaved");
        return new CloseDialogResponse("/ctrl/admin/openPersonAdministration?groupId=" + data.getId());
    }

    public IResponse deleteGroup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        int id = rdata.getId();
        int version = rdata.getInt("version");
        GroupData data = userContainer().getGroup(id);
        if (data == null || data.getVersion() != version){
            Log.warn("original data not found for update.");
            setError(rdata,"_deleteError");
            return showPersonAdministration();
        }
        userContainer().removeGroup(data);
        setSuccess(rdata,"_groupDeleted");
        return showPersonAdministration();
    }

    // calls

    protected IResponse showPersonAdministration(){
        return new ForwardResponse("/ctrl/admin/openPersonAdministration");
    }

    protected IResponse showEditGroup() {
        return new AjaxResponse("/WEB-INF/_jsp/user/editGroup.ajax.jsp");
    }

}
