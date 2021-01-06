/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentContainer;
import de.elbe5.content.ContentData;
import de.elbe5.templatepage.TemplatePageData;
import de.elbe5.user.UserData;

import java.time.LocalDateTime;

public class DefaultContentContainer extends ContentContainer {

    public DefaultContentContainer(){
        Log.log("creating default content");
        setChangeDate(Application.getCurrentTime());
        initializeRootContent();
    }

    public void initializeRootContent(){
        TemplatePageData content = new TemplatePageData();
        content.setNew(false);
        content.setId(ContentData.ID_ROOT);
        content.setCreationDate(LocalDateTime.now());
        content.setChangeDate(content.getCreationDate());
        content.setCreatorId(UserData.ID_ROOT);
        content.setChangerId(UserData.ID_ROOT);
        content.setName("");
        content.setDisplayName("Home");
        content.setDescription("Content Root");
        content.setLayout("defaultSectionPage");
        content.setPublishDate(LocalDateTime.now());
        content.setPublishedContent("test");
        content.setVersion(1);
        contentRoot = content;
    }

}
