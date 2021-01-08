/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Strings;
import de.elbe5.base.log.Log;
import de.elbe5.content.JspContentData;
import de.elbe5.request.*;
import de.elbe5.rights.SystemRights;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;
import de.elbe5.servlet.ControllerCache;
import de.elbe5.response.*;
import io.jsonwebtoken.orgjson.io.OrgJsonSerializer;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UserController extends Controller {

    private static UserController instance = null;

    public static void setInstance(UserController instance) {
        UserController.instance = instance;
    }

    public static UserController getInstance() {
        return instance;
    }

    public static void register(UserController controller){
        setInstance(controller);
        ControllerCache.addController(controller.getKey(),getInstance());
    }

    @Override
    public String getKey() {
        return UserData.TYPE_KEY;
    }

    public IResponse openCreateUser(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        UserData data = new UserData();
        data.setCreateValues(rdata.getUserId());
        rdata.setSessionObject(RequestData.KEY_USER, data);
        return showEditUser();
    }

    public IResponse openEditUser(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        int userId = rdata.getId();
        UserData data = new UserData();
        UserData original = userContainer().getUser(userId);
        data.setEditValues(original);
        rdata.setSessionObject(RequestData.KEY_USER, data);
        return showEditUser();
    }

    public IResponse saveUser(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        UserData data = (UserData) rdata.getSessionObject(RequestData.KEY_USER);
        assert(data!=null);
        data.readRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showEditUser();
        }
        if (data.isNew()){
            userContainer().addUser(data, rdata.getUserId());
        }
        else{
            if (!userContainer().updateUser(data, rdata.getUserId())){
                Log.warn("original data not found for update.");
                setError(rdata,"_versionError");
                return showEditUser();
            }
        }
        if (rdata.getUserId() == data.getId()) {
            rdata.setSessionUser(data);
        }
        rdata.removeSessionObject(RequestData.KEY_USER);
        setSuccess(rdata,"_userSaved");
        return new CloseDialogResponse("/ctrl/admin/openPersonAdministration?userId=" + data.getId());
    }

    public IResponse deleteUser(SessionRequestData rdata) {
        checkRights(SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.USER));
        int id = rdata.getId();
        int version = rdata.getInt("version");
        if (id == UserData.ID_ROOT) {
            setError(rdata,"_notDeletable");
            return showPersonAdministration();
        }
        UserData data = userContainer().getUser(id);
        if (data == null || data.getVersion() != version){
            Log.warn("original data not found for update.");
            setError(rdata,"_deleteError");
            return showPersonAdministration();
        }
        if (!userContainer().removeUser(data)){
            Log.warn("user could not be deleted");
            setError(rdata,"_deleteError");
            return showPersonAdministration();
        }
        setSuccess(rdata,"_userDeleted");
        return showPersonAdministration();
    }

    public IResponse openProfile(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showProfile(rdata);
    }

    public IResponse openChangePassword(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangePassword();
    }

    public IResponse changePassword(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn() && rdata.getUserId() == rdata.getId());
        UserData user = userContainer().getUser(rdata.getCurrentUser().getId());
        assert(user!=null);
        String oldPassword = rdata.getString("oldPassword");
        String newPassword = rdata.getString("newPassword1");
        String newPassword2 = rdata.getString("newPassword2");
        Locale locale = rdata.getLocale();
        if (newPassword.length() < UserData.MIN_PASSWORD_LENGTH) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_passwordLengthError",locale));
            return showChangePassword();
        }
        if (!newPassword.equals(newPassword2)) {
            rdata.addFormField("newPassword1");
            rdata.addFormField("newPassword2");
            rdata.addFormError(Strings.string("_passwordsDontMatch",locale));
            return showChangePassword();
        }
        UserData data = userContainer().getUser(user.getLogin(), oldPassword);
        if (data == null) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_badLogin",locale));
            return showChangePassword();
        }
        userContainer().updateUserPassword(data,newPassword);
        setSuccess(rdata,"_passwordChanged");
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    public IResponse openChangeProfile(SessionRequestData rdata) {
        checkRights(rdata.isLoggedIn());
        return showChangeProfile();
    }

    public IResponse changeProfile(SessionRequestData rdata) {
        int userId = rdata.getId();
        checkRights(rdata.isLoggedIn() && rdata.getUserId() == userId);
        UserData data = userContainer().getUser(userId);
        data.readProfileRequestData(rdata);
        if (rdata.hasFormErrors()) {
            return showChangeProfile();
        }
        if (!userContainer().updateUser(data, rdata.getUserId())){
            Log.warn("original data not found for update.");
            setError(rdata,"_saveError");
            return showChangeProfile();
        }
        rdata.setSessionUser(userContainer().getUser(data.getId()));
        setSuccess(rdata,"_userSaved");
        return new CloseDialogResponse("/ctrl/user/openProfile");
    }

    public IResponse openLogin(SessionRequestData rdata) {
        return showLogin();
    }

    public IResponse login(SessionRequestData rdata) {
        checkRights(rdata.isPostback());
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            setError(rdata,"_notComplete");
            return showLogin();
        }
        UserData data = userContainer().getUser(login, pwd);
        if (data == null){
            Log.info("bad login of "+login);
            setError(rdata,"_badLogin");
            return showLogin();
        }
        rdata.setSessionUser(data);
        return new CloseDialogResponse("/");
    }

    public IResponse login(ApiRequestData rdata) {
        Log.log("try login");
        checkRights(rdata.isPostback());
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        String loginDuration = rdata.getString("duration");
        Log.log("login with "+login+"/"+pwd);
        if (login.length() == 0 || pwd.length() == 0) {
            return new StatusResponse(HttpServletResponse.SC_UNAUTHORIZED);
        }
        UserData data = userContainer().getUser(login, pwd);
        if (data == null) {
            Log.info("bad login of "+login);
            return new StatusResponse(HttpServletResponse.SC_UNAUTHORIZED);
        }
        JSONObject tokenObject = ApiWebToken.getTokenObject(data, loginDuration);
        String jsonDataString = (new OrgJsonSerializer().serializeObject(tokenObject));
        if (!jsonDataString.isEmpty()) {
            return new JsonResponse(jsonDataString);
        }
        return new StatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public IResponse checkTokenLogin(ApiRequestData rdata) {
        checkRights(rdata.isPostback());
        return new StatusResponse(rdata.checkLogin());
    }

    public IResponse logout(SessionRequestData rdata) {
        Locale locale = rdata.getLocale();
        rdata.setSessionUser(null);
        rdata.resetSession();
        setSuccess(rdata,"_loggedOut");
        return showHome();
    }

    // calls

    protected IResponse showPersonAdministration(){
        return new ForwardResponse("/ctrl/admin/openPersonAdministration");
    }

    protected IResponse showLogin() {
        return new AjaxForwardResponse("/WEB-INF/_jsp/user/login.ajax.jsp");
    }

    protected IResponse showEditUser() {
        return new AjaxForwardResponse("/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected IResponse showProfile(SessionRequestData rdata) {
        JspContentData contentData = new JspContentData("/WEB-INF/_jsp/user/profile.jsp");
        return new ContentResponse(contentData);
    }

    protected IResponse showChangePassword() {
        return new AjaxForwardResponse("/WEB-INF/_jsp/user/editPassword.ajax.jsp");
    }

    protected IResponse showChangeProfile() {
        return new AjaxForwardResponse("/WEB-INF/_jsp/user/editProfile.ajax.jsp");
    }

}
