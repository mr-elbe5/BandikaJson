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
import de.elbe5.rights.ContentRights;
import de.elbe5.user.UserData;

public class Footer {

    final static String footerStart = """
            <ul class="nav">
                <li class="nav-item">
                    <a class="nav-link">&copy; {1}
                    </a>
                </li>
                """;

    final static String footerLink = """           
                <li class="nav-item">
                    <a class="nav-link" href="{1}">{2}
                    </a>
                </li>
                """;

    final static String footerEnd = """
            </ul>
                """;

    public static String getHtml(ContentData currentContent, UserData currentUser) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.format(footerStart,
                StringUtil.toHtml(Application.getConfiguration().getCopyright())
        ));
        for (ContentData data : Application.getContent().getContentRoot().getChildren()) {
            if (data.getNavType().equals(ContentData.NAV_TYPE_FOOTER) && ContentRights.hasUserReadRight(currentUser, currentContent)) {
                sb.append(StringUtil.format(footerLink,
                        data.getUrl(),
                        StringUtil.toHtml(data.getDisplayName())
                ));
            }
        }
        sb.append(footerEnd);
        return sb.toString();
    }

}
