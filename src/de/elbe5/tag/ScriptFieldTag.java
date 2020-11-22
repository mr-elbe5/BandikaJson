/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.tag;

import de.elbe5.base.data.StringUtil;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;
import de.elbe5.fieldsectionpart.FieldSectionPartData;
import de.elbe5.page.SectionPageData;
import de.elbe5.fieldsectionpart.PartScriptField;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class ScriptFieldTag extends FieldTag {

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
            FieldSectionPartData partData = rdata.get(RequestData.KEY_PART, FieldSectionPartData.class);

            PartScriptField field = partData.ensureScriptField(name);

            boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
            String content = field.getContent();
            if (editMode) {
                StringUtil.write(writer, "<textarea class=\"editField\" name=\"{1}\" rows=\"5\" >{2}</textarea>", field.getIdentifier(), StringUtil.toHtml(content));
            } else if (!content.isEmpty()) {
                StringUtil.write(writer, "<script type=\"text/javascript\">{1}</script>", content);
            }

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

