/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.rights;

import de.elbe5.application.Application;
import de.elbe5.user.GroupData;
import de.elbe5.user.UserData;

import java.util.Set;

public class SystemRights {

    public static boolean hasUserAnySystemRight(UserData data) {
        if (data==null) {
            return false;
        }
        if (data.isRoot()) {
            return true;
        }
        for (int groupId : data.getGroupIds()){
            GroupData group = Application.getUsers().getGroup(groupId);
            if (group != null && !group.getSystemRights().isEmpty()){
                return true;
            }
        }
        return false;
    }

    public static boolean hasUserSystemRight(UserData data, SystemZone zone) {
        if (data==null) {
            return false;
        }
        if (data.isRoot()) {
            return true;
        }
        for (int groupId : data.getGroupIds()) {
            GroupData group = Application.getUsers().getGroup(groupId);
            if (group != null && group.getSystemRights().contains(zone)){
                return true;
            }
        }
        return false;
    }

}
