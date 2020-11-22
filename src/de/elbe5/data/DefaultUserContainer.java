/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.user.GroupData;
import de.elbe5.user.UserContainer;
import de.elbe5.user.UserData;

import java.time.LocalDateTime;

public class DefaultUserContainer extends UserContainer {

    public DefaultUserContainer(){
        Log.log("creating default content");
        setChangeDate(Application.getCurrentTime());
        initializeRootUser();
        initializeDefaultGroups();
    }

    private void initializeRootUser(){
        UserData user = new UserData();
        user.setNew(true);
        user.setCreationDate(LocalDateTime.now());
        user.setCreatorId(UserData.ID_ROOT);
        user.setId(UserData.ID_ROOT);
        user.setLastName("Administrator");
        user.setEmail("admin@myhost.tld");
        user.setLogin("root");
        user.setPasswordHash(Application.getDefaultPassword());
        addUser(user,UserData.ID_ROOT);
    }

    private void initializeDefaultGroups(){
        GroupData group = new GroupData();
        group.setNew(true);
        group.setCreationDate(LocalDateTime.now());
        group.setCreatorId(UserData.ID_ROOT);
        group.setId(GroupData.ID_GLOBAL_ADMINISTRATORS);
        group.setName("Administrators");
        addGroup(group,UserData.ID_ROOT);
        group = new GroupData();
        group.setNew(true);
        group.setCreationDate(LocalDateTime.now());
        group.setCreatorId(UserData.ID_ROOT);
        group.setId(GroupData.ID_GLOBAL_APPROVERS);
        group.setName("Approvers");
        addGroup(group,UserData.ID_ROOT);
        group = new GroupData();
        group.setNew(true);
        group.setCreationDate(LocalDateTime.now());
        group.setCreatorId(UserData.ID_ROOT);
        group.setId(GroupData.ID_GLOBAL_EDITORS);
        group.setName("Editors");
        addGroup(group,UserData.ID_ROOT);
        group = new GroupData();
        group.setNew(true);
        group.setCreationDate(LocalDateTime.now());
        group.setCreatorId(UserData.ID_ROOT);
        group.setId(GroupData.ID_GLOBAL_READERS);
        group.setName("Readers");
        addGroup(group,UserData.ID_ROOT);
    }

}
