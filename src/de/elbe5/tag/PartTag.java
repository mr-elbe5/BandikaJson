/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.data.StringUtil;
import de.elbe5.base.log.Log;
import de.elbe5.page.SectionPageData;
import de.elbe5.page.SectionPartData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class PartTag extends BaseTag {

    private String cssClass = "";

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    static String editStart="<div id=\"{1}\" class=\"partWrapper {2}\" title=\"{3}\">";
    static String viewStart="<div id=\"{1}\" class=\"partWrapper {2}\">";
    static String end="</div>";

    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
            SectionPartData partData = rdata.get(RequestData.KEY_PART,SectionPartData.class);
            JspWriter writer = getContext().getOut();
            if (partData != null) {
                partData.setCssClass(cssClass);

                if (contentData.isEditing()) {
                    StringUtil.write(writer,editStart,
                            partData.getPartWrapperId(),
                            StringUtil.toHtml(partData.getCssClass()),
                            StringUtil.toHtml(partData.getEditTitle(rdata.getLocale()))
                    );
                    getContext().include("/WEB-INF/_jsp/sectionpage/editPartHeader.inc.jsp", true);
                }
                else{
                    StringUtil.write(writer,viewStart,
                            partData.getPartWrapperId(),
                            StringUtil.toHtml(partData.getCssClass())
                    );
                }
            }
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag(){
        try {
            JspWriter writer = getContext().getOut();
            StringUtil.write(writer,end);
        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return EVAL_PAGE;
    }

}
