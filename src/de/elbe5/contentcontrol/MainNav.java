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

import java.util.ArrayList;
import java.util.List;


public class MainNav {

    final static String mainNavStart = """
            <section class="col-12 menu">
                            <nav class="navbar navbar-expand-lg navbar-light">
                                <a class="navbar-brand" href="/"><img src="/files/logo.png"
                                                                      alt="{1}"/></a>
                                <button class="navbar-toggler" type="button" data-toggle="collapse"
                                        data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                                        aria-expanded="false" aria-label="Toggle navigation">
                                    <span class="fa fa-bars"></span>
                                </button>
                                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                                    <ul class="navbar-nav mr-auto">
                                    """;

    static final String childrenDropDownStart = """
                                        <li class="nav-item dropdown">
                                            <a class="nav-link {1} dropdown-toggle"
                                               data-toggle="dropdown" href="{2}" role="button"
                                               aria-haspopup="true" aria-expanded="false">{3}
                                            </a>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item {4}"
                                                   href="{5}">{6}
                                                </a>
                                                """;
    static final String dropDownChildLink = """
                                                <a class="dropdown-item {1)"
                                                   href="{2}">{3}
                                                </a>
                                                """;

    static final String childrenDropDownEnd = """
                                            </div>
                                        </li>
                                        """;

    static final String childLink = """
                                        <li class="nav-item">
                                            <a class="nav-link {1}"
                                               href="{2}">{3}
                                            </a>
                                        </li>
                                        """;
    final static String mainNavEnd = """
                                    </ul>
                                </div>
                            </nav>
                        </section>
            """;

    public static String getHtml(ContentData currentContent, UserData currentUser){
        ContentData home = Application.getContent().getContentRoot();
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.format(mainNavStart,
                StringUtil.toHtml(Application.getConfiguration().getApplicationName())
        ));
        if (home != null) {
            List<Integer> activeIds = Application.getContent().collectParentIds(currentContent.getId());
            activeIds.add(currentContent.getId());
            for (ContentData contentData : home.getChildren()) {
                if (contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) && ContentRights.hasUserReadRight(currentUser, contentData)) {
                    List<ContentData> children = new ArrayList<>();
                    for (ContentData child : contentData.getChildren()) {
                        if (child.getNavType().equals(ContentData.NAV_TYPE_HEADER) && ContentRights.hasUserReadRight(currentUser, child))
                            children.add(child);
                    }
                    if (!children.isEmpty()) {
                        sb.append(StringUtil.format(childrenDropDownStart,
                                activeIds.contains(contentData.getId())? "active" : "",
                                contentData.getUrl(),
                                StringUtil.toHtml(contentData.getDisplayName()),
                                activeIds.contains(contentData.getId())? "active" : "",
                                contentData.getUrl(),
                                StringUtil.toHtml(contentData.getDisplayName())
                        ));
                        for (ContentData child : children) {
                            sb.append(StringUtil.format(dropDownChildLink,
                                    activeIds.contains(contentData.getId())? "active" : "",
                                    child.getUrl(),
                                    StringUtil.toHtml(child.getDisplayName())
                            ));
                        }
                        sb.append(childrenDropDownEnd);
                    } else {
                        sb.append(StringUtil.format(childLink,
                                activeIds.contains(contentData.getId())? "active" : "",
                                contentData.getUrl(),
                                StringUtil.toHtml(contentData.getDisplayName())
                                ));
                    }
                }
            }
        }
        sb.append(mainNavEnd);
        return sb.toString();
    }

}
