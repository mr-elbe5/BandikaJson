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
import de.elbe5.content.ContentData;
import de.elbe5.fieldsectionpart.FieldSectionPartData;
import de.elbe5.page.SectionPageData;
import de.elbe5.fieldsectionpart.PartTextField;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

public class TextFieldTag extends FieldTag {

    private int rows = 1;

    public void setRows(int rows) {
        this.rows = rows;
    }

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            SessionRequestData rdata = SessionRequestData.getRequestData(request);
            JspWriter writer = getContext().getOut();
            SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
            FieldSectionPartData partData = rdata.get(RequestData.KEY_PART, FieldSectionPartData.class);

            PartTextField field = partData.ensureTextField(name);

            boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
            String content = field.getContent();
            if (editMode) {
                if (rows > 1)
                    StringUtil.write(writer, "<textarea class=\"editField\" name=\"{1}\" rows=\"{2}\">{3}</textarea>", field.getIdentifier(), Integer.toString(rows), StringUtil.toHtml(content.isEmpty() ? placeholder : content));
                else
                    StringUtil.write(writer, "<input type=\"text\" class=\"editField\" name=\"{1}\" placeholder=\"{2}\" value=\"{3}\" />", field.getIdentifier(), field.getIdentifier(), StringUtil.toHtml(content));
            } else {
                if (content.length() == 0) {
                    writer.write("&nbsp;");
                } else {
                    writer.write(StringUtil.toHtmlMultiline(content));
                }
            }

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

