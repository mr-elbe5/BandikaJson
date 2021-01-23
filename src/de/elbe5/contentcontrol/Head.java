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

public class Head {

    final static String head = """
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
            <title>{1}</title>
            <meta name="keywords" content="{2}">
            <meta name="description" content="{3}">
            <link rel="shortcut icon" href="/favicon.ico"/>
            <link rel="stylesheet" href="/static-content/css/bandika.css"/>
            <link rel="stylesheet" href="/static-content/theme/theme.css"/>
            <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
            <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
            <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
            <script type="text/javascript" src="/static-content/ckeditor/ckeditor.js"></script>
            <script type="text/javascript" src="/static-content/ckeditor/adapters/jquery.js"></script>
            <script type="text/javascript" src="/static-content/js/bandika.js"></script>
            """;

    public static String getHtml(ContentData currentContent) {
        String title = Application.getConfiguration().getApplicationName() + (currentContent != null ? " | " + currentContent.getDisplayName() : "");
        String keywords = currentContent != null ? currentContent.getKeywords() : title;
        String description = currentContent != null ? currentContent.getDescription() : "";
        return StringUtil.format(head,
                StringUtil.toHtml(title),
                StringUtil.toHtml(keywords),
                StringUtil.toHtml(description)
        );
    }

}
