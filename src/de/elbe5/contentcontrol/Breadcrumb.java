/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.contentcontrol;

import de.elbe5.application.Application;
import de.elbe5.base.data.StringUtil;
import de.elbe5.content.ContentData;

import java.util.List;

public class Breadcrumb {

    final static String breadcrumbStart = """
            <section class="col-12">
                <ol class="breadcrumb">
                """;

    final static String parentLink = """
                    <li class="breadcrumb-item">
                        <a href="{1}">{2}
                        </a>
                    </li>
                    """;
    final static String contentLink = """
                    <li class="breadcrumb-item">
                        <a>{1}
                        </a>
                    </li>
                    """;
    final static String breadcrumbEnd = """
                </ol>
            </section>
                    """;

    public static String getHtml(ContentData currentContent){
        List<Integer> parentIds = Application.getContent().collectParentIds(currentContent != null ? currentContent.getId() : ContentData.ID_ROOT);
        StringBuilder sb = new StringBuilder();
        sb.append(breadcrumbStart);
        for (int i = parentIds.size() - 1; i >= 0; i--) {
            ContentData content = Application.getContent().getContent(parentIds.get(i));
            if (content != null) {
                sb.append(StringUtil.format(parentLink,
                        content.getUrl(),
                        StringUtil.toHtml(content.getDisplayName())
                ));
            }
        }
        if (currentContent != null) {
            sb.append(StringUtil.format(contentLink,
                    StringUtil.toHtml(currentContent.getDisplayName())
                    ));
        }
        sb.append(breadcrumbEnd);
        return sb.toString();
    }

}
