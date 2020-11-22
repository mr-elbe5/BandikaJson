/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.rights;

import de.elbe5.application.Application;
import de.elbe5.content.ContentData;
import de.elbe5.user.UserData;

public class ContentRights {

    public static boolean hasUserReadRight(UserData user, int contentId) {
        ContentData data = Application.getContent().getContent(contentId);
        if (data==null){
            return false;
        }
        return hasUserReadRight(user, data);
    }

    public static boolean hasUserReadRight(UserData user, ContentData data) {
        return SystemRights.hasUserSystemRight(user,SystemZone.CONTENTREAD) ||
                hasUserRight(user, data, Right.READ);
    }

    public static boolean hasUserEditRight(UserData user, int contentId) {
        ContentData data = Application.getContent().getContent(contentId);
        if (data==null){
            return false;
        }
        return hasUserEditRight(user, data);
    }

    public static boolean hasUserEditRight(UserData user, ContentData data) {
        return SystemRights.hasUserSystemRight(user,SystemZone.CONTENTEDIT) ||
                hasUserRight(user, data, Right.EDIT);
    }

    public static boolean hasUserApproveRight(UserData user, int contentId) {
        ContentData data = Application.getContent().getContent(contentId);
        if (data==null){
            return false;
        }
        return hasUserApproveRight(user, data);
    }

    public static boolean hasUserApproveRight(UserData user, ContentData data) {
        return SystemRights.hasUserSystemRight(user,SystemZone.CONTENTAPPROVE) ||
                hasUserRight(user, data, Right.APPROVE);
    }

    private static boolean hasUserRight(UserData user, ContentData data, Right right){
        if (data==null){
            return false;
        }
        if (data.isOpenAccess()){
            return true;
        }
        if (user==null) {
            return false;
        }
        for (int groupId : data.getGroupRights().keySet()){
            if (user.getGroupIds().contains(groupId) && data.getGroupRights().get(groupId).includesRight(right))
                return true;
        }
        return false;
    }

}
