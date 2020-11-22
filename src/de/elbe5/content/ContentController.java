/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.*;
import de.elbe5.rights.ContentRights;
import de.elbe5.rights.Right;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.user.GroupData;

import java.util.*;

public class ContentController extends Controller {

    private static ContentController instance = null;

    public static void setInstance(ContentController instance) {
        ContentController.instance = instance;
    }

    public static ContentController getInstance() {
        return instance;
    }

    public static void register(ContentController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return ContentData.TYPE_KEY;
    }

    public IResponse show(SessionRequestData rdata) {
        //Log.log("show");
        int contentId = rdata.getId();
        ContentData data = contentContainer().getContent(contentId);
        assert(data!=null);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(), data.getId()));
        return new ContentResponse(data);
    }

    public IResponse openCreateContentData(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        ContentData parentData = contentContainer().getContent(parentId);
        assert(parentData!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), parentId));
        String type = rdata.getString("type");
        ContentData data = DataFactory.createObject(type, ContentData.class);
        assert(data!=null);
        data.setCreateValues(parentData, rdata.getUserId());
        rdata.setSessionObject(RequestData.KEY_CONTENT, data);
        return showEditContent(data);
    }

    public IResponse openEditContentData(SessionRequestData rdata) {
        int contentId = rdata.getId();
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), contentId));
        ContentData original = contentContainer().getContent(contentId);
        ContentData data = IData.getEditableCopy(original);
        assert data != null;
        data.setEditValues(original);
        rdata.setCurrentSessionContent(data);
        return showEditContent(data);
    }

    public IResponse saveContentData(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getSessionObject(RequestData.KEY_CONTENT,ContentData.class);
        assert(data != null && data.getId() == contentId);
        if (rdata.hasFormErrors()) {
            return showEditContent(data);
        }
        if (data.isNew()){
            checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getParentId()));
        }
        else{
            checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data));
        }
        data.readRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showEditContent(data);
        }
        if (data.isNew()){
            if (!contentContainer().addContent(data, rdata.getUserId())){
                Log.warn("data could not be added");
                setError(rdata, "_versionError");
                return showEditContent(data);
            }
            data.setNew(false);
        }
        else{
            if (!contentContainer().updateContent(data, rdata.getUserId())){
                Log.warn("original data not found for update");
                setError(rdata, "_versionError");
                return showEditContent(data);
            }
        }
        rdata.removeCurrentSessionContent();
        setSuccess(rdata,"_contentSaved");
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    // request >> controller?

    public IResponse openEditRights(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = contentContainer().getContent(contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        rdata.setCurrentSessionContent(data);
        return showEditRights(data);
    }

    public IResponse saveRights(SessionRequestData rdata) {
        int contentId = rdata.getId();
        int version = rdata.getInt("version");
        ContentData data = contentContainer().getContent(contentId);
        if (data == null || data.getVersion() != version){
            Log.warn("original data not found for update.");
            setError(rdata,"_saveError");
            return showEditRights(data);
        }
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data));
        Map<Integer,Right> rights = new HashMap<>();
        readRightsRequestData(rights, rdata);
        if (!contentContainer().updateContentRights(data, rights, rdata.getUserId())){
            Log.warn("content rights could not be updated");
            setError(rdata,"_saveError");
            return showEditRights(data);
        }
        rdata.removeCurrentSessionContent();
        setSuccess(rdata,"_rightsSaved");
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + data.getId());
    }

    protected void readRightsRequestData(Map<Integer,Right> map, SessionRequestData rdata) {
        List<GroupData> groups = Application.getUsers().getGroups();
        for (GroupData group : groups) {
            if (group.getId() <= GroupData.ID_MAX_FINAL)
                continue;
            String value = rdata.getString("groupright_" + group.getId());
            if (!value.isEmpty())
                map.put(group.getId(), Right.valueOf(value));
        }
    }

    public IResponse cutContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        if (contentId==ContentData.ID_ROOT){
            return showContentAdministration(rdata,ContentData.ID_ROOT);
        }
        ContentData data = contentContainer().getContent(contentId);
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data));
        rdata.setClipboardData(RequestData.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse copyContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        if (contentId==ContentData.ID_ROOT){
            return showContentAdministration(rdata,ContentData.ID_ROOT);
        }
        ContentData srcData = contentContainer().getContent(contentId);
        assert(srcData!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), srcData));
        ContentData data = DataFactory.createObject(srcData.getTypeKey(), ContentData.class);
        assert(data!=null);
        ContentData parent = contentContainer().getContent(srcData.getParentId());
        data.copyEditableAttributes(srcData);
        data.setCreateValues(parent, rdata.getUserId());
        //marking as copy
        data.setParentId(0);
        data.setParentVersion(0);
        rdata.setClipboardData(RequestData.KEY_CONTENT, data);
        return showContentAdministration(rdata,data.getId());
    }

    public IResponse pasteContent(SessionRequestData rdata) {
        int parentId = rdata.getInt("parentId");
        int parentVersion = rdata.getInt("parentVersion");
        ContentData data=rdata.getClipboardData(RequestData.KEY_CONTENT,ContentData.class);
        if (data==null){
            setError(rdata,"_actionNotExcecuted");
            return showContentAdministration(rdata);
        }
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), parentId));
        List<Integer> parentIds=contentContainer().collectParentIds(data.getId());
        if (parentIds.contains(data.getId())){
            setError(rdata,"_actionNotExcecuted");
            return showContentAdministration(rdata);
        }
        if (data.getParentId() != 0) {
            //has been cut
            if (!contentContainer().moveContent(data, parentId, parentVersion, rdata.getUserId())) {
                setError(rdata, "_actionNotExcecuted");
                return showContentAdministration(rdata);
            }
        }
        else{
            // has been copied
            data.setParentId(parentId);
            data.setParentVersion(parentVersion);
            if (!contentContainer().addContent(data, rdata.getUserId())) {
                setError(rdata, "_actionNotExcecuted");
                return showContentAdministration(rdata);
            }
        }
        rdata.removeClipboardData(RequestData.KEY_CONTENT);
        setSuccess(rdata,"_contentPasted");
        return showContentAdministration(rdata,data.getId());
    }

    //backend
    public IResponse deleteContent(SessionRequestData rdata) {
        int contentId = rdata.getId();
        if (contentId == ContentData.ID_ROOT) {
            setError(rdata,"_notDeletable");
            return showContentAdministration(rdata);
        }
        ContentData data=contentContainer().getContent(contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data)) ;
        ContentData parent = contentContainer().getContent(data.getParentId());
        parent.getChildren().remove(data);
        contentContainer().removeContent(data);
        rdata.put("contentId", Integer.toString(parent.getId()));
        setSuccess(rdata,"_contentDeleted");
        return showContentAdministration(rdata,parent.getId());
    }

    //backend
    public IResponse openSortChildPages(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = contentContainer().getContent(contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data));
        rdata.setCurrentSessionContent(data);
        return showSortChildContents();
    }

    //backend
    public IResponse saveChildPageRanking(SessionRequestData rdata) {
        int contentId = rdata.getId();
        ContentData data = rdata.getCurrentSessionContent();
        assert(data != null && data.getId() == contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data));
        Map<Integer,Integer> rankMap = new HashMap<>();
        for (ContentData child : data.getChildren()){
            int ranking=rdata.getInt("select"+child.getId(),-1);
            rankMap.put(child.getId(), ranking);
        }
        if (!contentContainer().updateChildRanking(data, rankMap, rdata.getUserId())){
            Log.warn("sorting did not succeed");
            return showSortChildContents();
        }
        rdata.removeCurrentSessionContent();
        setSuccess(rdata,"_newRankingSaved");
        return new CloseDialogResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

    public IResponse clearClipboard(SessionRequestData rdata) {
        rdata.removeClipboardData(RequestData.KEY_CONTENT);
        rdata.removeClipboardData(RequestData.KEY_FILE);
        return showContentAdministration(rdata);
    }

    protected IResponse showEditContent(ContentData contentData) {
        return new AjaxResponse(contentData.getEditDataJsp());
    }

    protected IResponse showEditRights(ContentData contentData) {
        return new AjaxResponse("/WEB-INF/_jsp/content/editGroupRights.ajax.jsp");
    }

    protected IResponse showSortChildContents() {
        return new AjaxResponse("/WEB-INF/_jsp/content/sortChildContents.ajax.jsp");
    }

    protected IResponse showContentAdministration(SessionRequestData rdata, int contentId) {
        return new ForwardResponse("/ctrl/admin/openContentAdministration?contentId=" + contentId);
    }

}
