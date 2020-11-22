/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.ckeditor;

import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.file.FileData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.AjaxResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;
import de.elbe5.rights.ContentRights;
import de.elbe5.servlet.ControllerCache;

public class CkEditorController extends ContentController {

    public static final String KEY = "ckeditor";

    private static CkEditorController instance = null;

    public static void setInstance(CkEditorController instance) {
        CkEditorController.instance = instance;
    }

    public static CkEditorController getInstance() {
        return instance;
    }

    public static void register(CkEditorController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IResponse openLinkBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        return new AjaxResponse("/WEB-INF/_jsp/ckeditor/browseLinks.ajax.jsp");
    }

    public IResponse openImageBrowser(SessionRequestData rdata) {
        ContentData data=rdata.getCurrentSessionContent();
        assert(data!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        return new AjaxResponse("/WEB-INF/_jsp/ckeditor/browseImages.ajax.jsp");
    }

    public IResponse addImage(SessionRequestData rdata) {
        ContentData content=rdata.getCurrentSessionContent();
        assert(content!=null);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), content.getId()));
        FileData image=new FileData();
        image.setCreateValues(content,rdata.getUserId());
        image.readRequestData(rdata);
        contentContainer().addFile(image, rdata.getUserId());
        rdata.put("imageId", Integer.toString(image.getId()));
        return new AjaxResponse("/WEB-INF/_jsp/ckeditor/addImage.ajax.jsp");
    }


}
