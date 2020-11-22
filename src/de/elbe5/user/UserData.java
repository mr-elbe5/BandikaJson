/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.application.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.data.BaseData;
import de.elbe5.application.Application;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.*;

public class UserData extends BaseData {

    public static final String TYPE_KEY = "user";

    public static Constructor<UserData> defaultConstructor = DataFactory.createDefaultConstructor(UserData.class);

    public static final int ID_ROOT = 1;

    public static int MIN_LOGIN_LENGTH = 4;
    public static int MIN_PASSWORD_LENGTH = 8;

    public static void register(){
        DataFactory.addClass(UserData.TYPE_KEY, UserData.class);
    }

    private enum keys{
        title,
        firstName,
        lastName,
        email,
        login,
        passwordHash,
        street,
        zipCode,
        city,
        country,
        phone,
        groupIds
    }

    protected String title = "";
    protected String firstName = "";
    protected String lastName = "";
    protected String email = "";
    protected String login = "";
    protected String passwordHash = "";
    protected String street = "";
    protected String zipCode = "";
    protected String city = "";
    protected String country = "";
    protected String phone = "";

    protected Set<Integer> groupIds = new HashSet<>();

    // constructors

    public UserData(){

    }

    @Override
    public String getTypeKey(){
        return UserData.TYPE_KEY;
    }

    // copy and editing methods

    @Override
    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert(idata instanceof UserData);
        UserData data = (UserData) idata;
        setTitle(data.getTitle());
        setFirstName(data.getFirstName());
        setLastName(data.getLastName());
        setEmail(data.getEmail());
        setLogin(data.getLogin());
        setPasswordHash(data.getPasswordHash());
        setStreet(data.getStreet());
        setZipCode(data.getZipCode());
        setCity(data.getCity());
        setCountry(data.getCountry());
        setPhone(data.getPhone());
        copyGroupIds(data);
    }

    public void copyGroupIds(UserData data){
        groupIds.clear();
        groupIds.addAll(data.getGroupIds());
    }

    public void setEditValues(UserData data) {
        super.setEditValues(data);
        copyGroupIds(data);
    }

    // json methods
    
    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.title.name(), title);
        obj.put(keys.firstName.name(), firstName);
        obj.put(keys.lastName.name(), lastName);
        obj.put(keys.email.name(), email);
        obj.put(keys.login.name(), login);
        obj.put(keys.passwordHash.name(), passwordHash);
        obj.put(keys.street.name(), street);
        obj.put(keys.zipCode.name(), zipCode);
        obj.put(keys.city.name(), city);
        obj.put(keys.country.name(), country);
        obj.put(keys.phone.name(), phone);
        JSONArray gids = new JSONArray();
        for (Integer gid : groupIds){
            gids.put(gid);
        }
        obj.put(keys.groupIds.name(),gids);
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        title = obj.optString(keys.title.name());
        firstName = obj.optString(keys.firstName.name());
        lastName = obj.optString(keys.lastName.name());
        email = obj.optString(keys.email.name());
        login = obj.optString(keys.login.name());
        passwordHash = obj.optString(keys.passwordHash.name());
        street = obj.optString(keys.street.name());
        zipCode = obj.optString(keys.zipCode.name());
        city = obj.optString(keys.city.name());
        country = obj.optString(keys.country.name());
        phone = obj.optString(keys.phone.name());
        JSONArray gids = obj.optJSONArray(keys.groupIds.name());
        groupIds.clear();
        if (gids != null) {
            for (int i = 0; i < gids.length(); i++) {
                groupIds.add(gids.getInt(i));
            }
        }
    }

    // request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        readBasicData(rdata);
        setLogin(rdata.getString("login"));
        String s1 = rdata.getString("password");
        String s2 = rdata.getString("passwordCopy");
        if (!s1.isEmpty() || !s2.isEmpty()) {
            if (s1.equals(s2)) {
                setPassword(s1);
            }
            else{
                rdata.addFormError(Strings.string("_passwordsDontMatch",rdata.getLocale()));
                rdata.addFormField("password");
                rdata.addFormField("passwordCopy");
            }
        }
        setGroupIds(rdata.getIntegerSet("groupIds"));
        if (getLogin().isEmpty())
            rdata.addIncompleteField("login");
        if (isNew() && !hasPassword())
            rdata.addIncompleteField("password");
    }

    public void readProfileRequestData(SessionRequestData rdata) {
        readBasicData(rdata);
    }

    private void readBasicData(RequestData rdata) {
        setTitle(rdata.getString("title"));
        setFirstName(rdata.getString("firstName"));
        setLastName(rdata.getString("lastName"));
        setEmail(rdata.getString("email"));
        setStreet(rdata.getString("street"));
        setZipCode(rdata.getString("zipCode"));
        setCity(rdata.getString("city"));
        setCountry(rdata.getString("country"));
        setPhone(rdata.getString("phone"));
        if (getLastName().isEmpty())
            rdata.addIncompleteField("lastName");
        if (getEmail().isEmpty())
            rdata.addIncompleteField("email");
    }

    // getter and setter

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        if (firstName.length() == 0) {
            return lastName;
        }
        return firstName + ' ' + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean hasPassword() {
        return !passwordHash.isEmpty();
    }

    public void setPassword(String password) {
        if (password.isEmpty()) {
            setPasswordHash("");
        } else {
            setPasswordHash(UserSecurity.encryptPassword(password, Application.getSalt()));
        }
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isRoot(){
        return getId()== ID_ROOT;
    }

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

}
