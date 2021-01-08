/*
 BandikaJson CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.file.DiskFile;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;
import de.elbe5.request.*;
import de.elbe5.response.*;
import de.elbe5.rights.ContentRights;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.UserData;
import io.jsonwebtoken.orgjson.io.OrgJsonSerializer;

import javax.servlet.http.HttpServletResponse;

public class FileController extends Controller {

    private static FileController instance = null;

    public static void setInstance(FileController instance) {
        FileController.instance = instance;
    }

    public static FileController getInstance() {
        return instance;
    }

    public static void register(FileController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return FileData.TYPE_KEY;
    }

    public IResponse openCreateFile(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parent = contentContainer().getContent(parentId);
        assert(parent!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), parent));
        FileData data = new FileData();
        data.setCreateValues(parent, rdata.getUserId());
        rdata.setSessionObject(RequestData.KEY_FILE, data);
        return showEditFile();
    }

    public IResponse openEditFile(SessionRequestData rdata) {
        FileData data = contentContainer().getFile(rdata.getId());
        FileData editData = new FileData();
        editData.setEditValues(data);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getParentId()));
        rdata.setSessionObject(RequestData.KEY_FILE,data);
        return showEditFile();
    }

    public IResponse saveFile(SessionRequestData rdata) {
        int fileId = rdata.getId();
        FileData data = rdata.getSessionObject(RequestData.KEY_FILE,FileData.class);
        assert(data != null && data.getId() == fileId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getParentId()));
        data.readRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showEditFile();
        }
        if (data.isNew()) {
            if (!contentContainer().addFile(data, rdata.getUserId())) {
                Log.warn("data could not be added");
                setError(rdata, "_versionError");
                return showEditFile();
            }
        }
        else{
            if (!contentContainer().updateFile(data, rdata.getUserId())){
                Log.warn("data could not be updated");
                setError(rdata, "_versionError");
                return showEditFile();
            }
        }
        setSuccess(rdata,"_fileSaved");
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    public IResponse upload(ApiRequestData rdata) {
        Log.log("uploading image");
        int code=rdata.checkLogin();
        if (code != HttpServletResponse.SC_OK){
            return new StatusResponse(code);
        }
        UserData user = rdata.getCurrentUser();
        if (user == null)
            return new StatusResponse(HttpServletResponse.SC_UNAUTHORIZED);
        int parentId= rdata.getInt("parentId");
        ContentData parent = contentContainer().getContent(parentId);
        if (parent==null)
            parent=contentContainer().getContentRoot();
        FileData fileData = new FileData();
        fileData.setCreateValues(parent, rdata.getUserId());
        fileData.readRequestData(rdata);
        if (!contentContainer().addFile(fileData, rdata.getUserId())){
            return new StatusResponse(HttpServletResponse.SC_BAD_REQUEST);
        }
        FileResponseData data = new FileResponseData();
        data.setId(fileData.getId());
        data.setParentId(parent.getId());
        data.setName(fileData.getFileName());
        String jsonDataString = (new OrgJsonSerializer().serializeObject(data));
        if (!jsonDataString.isEmpty()) {
            return new JsonResponse(jsonDataString);
        }
        return new StatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public IResponse cutFile(SessionRequestData rdata) {
        int fileId = rdata.getId();
        FileData data = contentContainer().getFile(fileId);
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getParentId()));
        rdata.setClipboardData(RequestData.KEY_FILE, data);
        return showContentAdministration(rdata,data.getParentId());
    }

    public IResponse copyFile(SessionRequestData rdata) {
        int fileId = rdata.getId();
        FileData original = contentContainer().getFile(fileId,FileData.class);
        assert(original!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), original.getParentId()));
        ContentData parent = contentContainer().getContent(original.getParentId());
        FileData data = new FileData();
        data.copyEditableAttributes(original);
        data.setCreateValues(parent, rdata.getUserId());
        // marking as copy
        data.setParentId(0);
        data.setParentVersion(0);
        DiskFile originalFile = original.getFile();
        DiskFile newFile = data.getFile();
        boolean success = FileService.copyFile(originalFile,newFile);
        if (original.isImage()){
            originalFile = original.getPreviewFile();
            newFile = data.getPreviewFile();
            success &= FileService.copyFile(originalFile,newFile);
        }
        assert success;
        rdata.setClipboardData(RequestData.KEY_FILE, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse pasteFile(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        int parentVersion = rdata.getInt("parentVersion");
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), parentId));
        ContentData parent=contentContainer().getContent(parentId);
        if (parent == null){
            setError(rdata,"_actionNotExcecuted");
            return showContentAdministration(rdata);
        }
        FileData data=rdata.getClipboardData(RequestData.KEY_FILE,FileData.class);
        if (data==null){
            setError(rdata,"_actionNotExcecuted");
            return showContentAdministration(rdata);
        }
        if (data.getParentId() != 0) {
            //has been cut
            if (!contentContainer().moveFile(data, parentId, parentVersion, rdata.getUserId())) {
                setError(rdata, "_actionNotExcecuted");
                return showContentAdministration(rdata);
            }
        }
        else{
            //has been copied
            data.setParentId(parentId);
            data.setParentVersion(parentVersion);
            if (!contentContainer().addFile(data, rdata.getUserId())) {
                setError(rdata, "_actionNotExcecuted");
                return showContentAdministration(rdata);
            }
        }
        rdata.removeClipboardData(RequestData.KEY_FILE);
        setSuccess(rdata,"_filePasted");
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse deleteFile(SessionRequestData rdata) {
        int contentId = rdata.getId();
        FileData file = contentContainer().getFile(contentId);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(),file.getParentId()));
        contentContainer().removeFile(file);
        rdata.put("contentId", Integer.toString(file.getParentId()));
        setSuccess(rdata,"_fileDeleted");
        return showContentAdministration(rdata,file.getParentId());
    }

    protected IResponse showEditFile() {
        return new AjaxForwardResponse("/WEB-INF/_jsp/file/editFile.ajax.jsp");
    }

    protected IResponse showContentAdministration(SessionRequestData rdata, int contentId) {
        return new ForwardResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
