/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.data.IData;
import de.elbe5.request.*;
import de.elbe5.response.IResponse;
import de.elbe5.response.PageResponse;
import de.elbe5.response.StaticPageResponse;
import de.elbe5.rights.ContentRights;
import de.elbe5.servlet.ControllerCache;

public class PageController extends ContentController {

    private static PageController instance = null;

    public static void setInstance(PageController instance) {
        PageController.instance = instance;
    }

    public static PageController getInstance() {
        return instance;
    }

    public static void register(PageController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return PageData.TYPE_KEY;
    }

    public IResponse show(SessionRequestData rdata) {
        //Log.log("show");
        int contentId = rdata.getId();
        PageData data = contentContainer().getContent(contentId, PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(), data.getId()));
        //return new PageResponse(data);
        return new StaticPageResponse(data);
    }

    public IResponse show(String url, SessionRequestData rdata) {
        PageData data = contentContainer().getContent(url, PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(), data.getId()));
        //Log.log("show: "+data.getClass().getSimpleName());
        //return new PageResponse(data);
        return new StaticPageResponse(data);
    }

    public IResponse openEditPage(SessionRequestData rdata) {
        int contentId = rdata.getId();
        PageData original = contentContainer().getContent(contentId,PageData.class);
        assert(original!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), original.getId()));
        PageData data = IData.getEditableCopy(original);
        assert data!=null;
        data.setEditValues(original);
        data.copyPageAttributes(original);
        data.setViewType(ContentData.VIEW_TYPE_EDIT);
        rdata.setCurrentSessionContent(data);
        return new PageResponse(data);
    }

    public IResponse showEditPage(SessionRequestData rdata) {
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        return new PageResponse(data);
    }

    public IResponse savePage(SessionRequestData rdata) {
        int contentId = rdata.getId();
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data != null && data.getId() == contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        data.readPageRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return new PageResponse(data);
        }
        if (!contentContainer().updatePage(data, rdata.getUserId())){
            Log.warn("original data not found for update");
            setError(rdata, "_versionError");
            return new PageResponse(data);
        }
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        rdata.removeCurrentSessionContent();
        return show(rdata);
    }

    public IResponse cancelEditPage(SessionRequestData rdata) {
        PageData data = rdata.getCurrentSessionContent(PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        data.stopEditing();
        return new PageResponse(data);
    }

    public IResponse showDraft(SessionRequestData rdata){
        int contentId = rdata.getId();
        PageData data = contentContainer().getContent(contentId, PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(), data.getId()));
        data.setViewType(ContentData.VIEW_TYPE_SHOW);
        return new PageResponse(data);
    }

    public IResponse showPublished(SessionRequestData rdata){
        int contentId = rdata.getId();
        PageData data = contentContainer().getContent(contentId, PageData.class);
        assert(data!=null);
        checkRights(ContentRights.hasUserReadRight(rdata.getCurrentUser(), data.getId()));
        data.setViewType(ContentData.VIEW_TYPE_SHOWPUBLISHED);
        return new PageResponse(data);
    }

    public IResponse publishPage(SessionRequestData rdata){
        int contentId = rdata.getId();
        PageData data = contentContainer().getContent(contentId,PageData.class);
        assert(data != null);
        checkRights(ContentRights.hasUserApproveRight(rdata.getCurrentUser(), data.getId()));
        data.setViewType(ContentData.VIEW_TYPE_PUBLISH);
        data.setPublishDate(Application.getCurrentTime());
        return new PageResponse(data);
    }

}
