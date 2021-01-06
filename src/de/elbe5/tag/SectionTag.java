/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.log.Log;
import de.elbe5.sectionpage.SectionData;
import de.elbe5.sectionpage.SectionPageData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;

public class SectionTag extends BaseTag {

    private String name = "";
    private String cssClass = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
            SectionData sectionData = contentData.ensureSection(name);
            if (sectionData != null) {
                sectionData.setCssClass(cssClass);
                rdata.put("sectionData", sectionData);
                String url;
                if (contentData.isEditing()) {
                    url = "/WEB-INF/_jsp/sectionpage/editSection.inc.jsp";
                } else {
                    url = "/WEB-INF/_jsp/sectionpage/section.inc.jsp";
                }
                getContext().include(url);
                request.removeAttribute("sectionData");
            }
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}
