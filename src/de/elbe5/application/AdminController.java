/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.response.AjaxResponse;
import de.elbe5.response.CloseDialogResponse;
import de.elbe5.rights.SystemRights;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.response.IResponse;

public class AdminController extends Controller {

    public static final String TYPE_KEY = "admin";

    private static AdminController instance = null;

    public static void setInstance(AdminController instance) {
        AdminController.instance = instance;
    }

    public static AdminController getInstance() {
        return instance;
    }

    public static void register(AdminController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return TYPE_KEY;
    }

    public IResponse openSystemAdministration(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserAnySystemRight(rdata.getCurrentUser()));
        return showSystemAdministration(rdata);
    }

    public IResponse openPersonAdministration(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserAnySystemRight(rdata.getCurrentUser()));
        return showPersonAdministration(rdata);
    }

    public IResponse openContentAdministration(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserAnySystemRight(rdata.getCurrentUser()));
        return showContentAdministration(rdata);
    }

    public IResponse checkChanges(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        Application.checkIdChanged();
        Application.getConfiguration().checkChanged();
        Application.getContent().checkChanged();
        Application.getUsers().checkChanged();
        return openSystemAdministration(rdata);
    }

    public IResponse runCleanup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        Application.getContent().cleanupFiles();
        return openSystemAdministration(rdata);
    }

    public IResponse createBackup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        if (Application.createBackup()){
            //todo
        }
        else{

        }
        return openSystemAdministration(rdata);
    }

    public IResponse openRestoreBackup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        return showSelectBackup(rdata);
    }

    public IResponse restoreBackup(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        String backupName = rdata.getString("backupName");
        if (Application.restoreBackup(backupName)){
            setSuccess(rdata,"_restartHint");
        }
        else{

        }
        return openSystemAdministration(rdata);
    }

    public IResponse restart(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM));
        if (!Application.restart()){
            //todo
        }
        setSuccess(rdata,"_restartHint");
        return openSystemAdministration(rdata);
    }

    public IResponse openEditConfiguration(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserAnySystemRight(rdata.getCurrentUser()));
        Configuration config = new Configuration();
        config.copyEditableAttributes(Application.getConfiguration());
        rdata.setSessionObject("config", config);
        return showEditConfiguration(rdata);
    }

    public IResponse saveConfiguration(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserAnySystemRight(rdata.getCurrentUser()));
        Configuration config = (Configuration) rdata.getSessionObject("config");
        assert(config!=null);
        config.readSettingsRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showEditConfiguration(rdata);
        }
        Application.getConfiguration().copyEditableAttributesLocked(config);
        Application.getConfiguration().setChangerId(rdata.getCurrentUser().getId());
        Application.getConfiguration().setHasChanged();
        rdata.removeSessionObject( "config");
        setSuccess(rdata, "_configurationSaved");
        return new CloseDialogResponse("/ctrl/admin/openSystemAdministration");
    }

    protected IResponse showSelectBackup(SessionRequestData rdata) {
        return new AjaxResponse("/WEB-INF/_jsp/administration/selectBackup.ajax.jsp");
    }

    protected IResponse showEditConfiguration(SessionRequestData rdata) {
        return new AjaxResponse("/WEB-INF/_jsp/administration/editConfiguration.ajax.jsp");
    }

}
