/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.data.DataFactory;
import de.elbe5.request.*;
import de.elbe5.response.AjaxResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.ForwardResponse;
import de.elbe5.rights.ContentRights;
import de.elbe5.servlet.ControllerCache;

public class SectionPageController extends PageController {

    private static SectionPageController instance = null;

    public static void setInstance(SectionPageController instance) {
        SectionPageController.instance = instance;
    }

    public static SectionPageController getInstance() {
        return instance;
    }

    public static void register(SectionPageController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return SectionPageData.TYPE_KEY;
    }

    public IResponse addPart(SessionRequestData rdata) {
        int contentId = rdata.getId();
        SectionPageData data = rdata.getCurrentSessionContent(SectionPageData.class);
        assert(data != null && data.getId() == contentId);
        checkRights(ContentRights.hasUserEditRight(rdata.getCurrentUser(), data.getId()));
        int fromPartId = rdata.getInt("fromPartId", -1);
        String partType = rdata.getString("partType");
        SectionPartData pdata = DataFactory.createObject(partType, SectionPartData.class);
        assert(pdata != null);
        pdata.setCreateValues(rdata);
        data.addPart(pdata, fromPartId, true);
        rdata.put(RequestData.KEY_PART, pdata);
        return new AjaxResponse("/WEB-INF/_jsp/sectionpage/newPart.ajax.jsp");
    }

}
