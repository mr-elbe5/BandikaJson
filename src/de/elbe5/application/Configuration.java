/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.file.DiskDirectory;
import de.elbe5.base.log.Log;
import de.elbe5.data.DataContainer;
import de.elbe5.file.FileService;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration extends DataContainer {

    enum keys{
        changerId,
        applicationName,
        theme,
        copyright,
        timerInterval,
        serviceInterval,
        cleanupInterval,
        indexInterval
    }

    protected int changerId = 0;
    private String applicationName = "Bandika";
    private String theme = "blue";
    private String copyright = "2020 Elbe 5";
    private int timerInterval = 1000; // in millis
    private int serviceInterval = 5; // in minutes
    private int cleanupInterval = 10; // in minutes
    private int indexInterval = 15; // in minutes

    private List<String> themeNames = new ArrayList<>();

    // constructors

    public Configuration(){
    }

    // copy and editing methods

    public void copyEditableAttributesLocked(Configuration data) {
        try {
            dataLock.lock();
            copyEditableAttributes(data);
        }
        finally{
            dataLock.unlock();
        }
    }

    public void copyEditableAttributes(Configuration data){
        assert data!=null;
        setApplicationName(data.getApplicationName());
        setTheme(data.getTheme());
        setCopyright(data.getCopyright());
        setTimerInterval(data.getTimerInterval());
        setServiceInterval(data.getServiceInterval());
        setCleanupInterval(data.getCleanupInterval());
        setIndexInterval(data.getIndexInterval());
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        super.addJSONAttributes(obj);
        obj.put(keys.changerId.name(), changerId);
        obj.put(keys.applicationName.name(), applicationName);
        obj.put(keys.theme.name(), theme);
        obj.put(keys.copyright.name(), copyright);
        obj.put(keys.timerInterval.name(), timerInterval);
        obj.put(keys.serviceInterval.name(), serviceInterval);
        obj.put(keys.cleanupInterval.name(), cleanupInterval);
        obj.put(keys.indexInterval.name(), indexInterval);
        return obj;
    }

    public void fromJSONObject(JSONObject obj) {
        try {
            super.getJSONAttributes(obj);
            changerId = obj.optInt(keys.changerId.name());
            applicationName = obj.optString(keys.applicationName.name());
            theme = obj.optString(keys.theme.name());
            copyright = obj.optString(keys.copyright.name());
            timerInterval = obj.optInt(keys.timerInterval.name());
            serviceInterval = obj.optInt(keys.serviceInterval.name());
            cleanupInterval = obj.optInt(keys.cleanupInterval.name());
            indexInterval = obj.optInt(keys.indexInterval.name());
        }
        catch (Exception e){
            Log.error("unable to read data", e);
        }
        copyThemeFiles();
    }

    // request

    public void readSettingsRequestData(SessionRequestData rdata) {
        setApplicationName(rdata.getString("applicationName"));
        setTheme(rdata.getString("theme"));
        setCopyright(rdata.getString("copyright"));
        setTimerInterval(rdata.getInt("timerInterval"));
        setServiceInterval(rdata.getInt("serviceInterval"));
        setCleanupInterval(rdata.getInt("cleanupInterval"));
        setIndexInterval(rdata.getInt("indexInterval"));
    }

    // getter and setter


    public int getChangerId() {
        return changerId;
    }

    public void setChangerId(int changerId) {
        this.changerId = changerId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    private void setTimerInterval(int timerInterval) {
        this.timerInterval = timerInterval;
    }

    public int getServiceInterval() {
        return serviceInterval;
    }

    private void setServiceInterval(int serviceInterval) {
        this.serviceInterval = serviceInterval;
    }

    public int getCleanupInterval() {
        return cleanupInterval;
    }

    private void setCleanupInterval(int cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public int getIndexInterval() {
        return indexInterval;
    }

    public void setIndexInterval(int indexInterval) {
        this.indexInterval = indexInterval;
    }

    public List<String> getThemeNames() {
        return themeNames;
    }

    protected boolean save(){
        JSONObject obj = toJSONObject();
        String jsonString = obj.toString(4);
        if (ApplicationPath.getConfigFile().writeToDisk(jsonString)) {
            return true;
        }
        Log.error("configuration could not be saved");
        return false;
    }

    public boolean initialize() {
        if (!ApplicationPath.getConfigFile().exists()){
            Log.log("creating default configuration");
            save();
            changed = false;
        }
        initializeThemes();
        Log.log("initializing configuration");
        String json = ApplicationPath.getConfigFile().readAsText();
        try {
            JSONObject obj = new JSONObject(json);
            fromJSONObject(obj);
            changed = false;
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public void initializeThemes(){
        String[] themes = ApplicationPath.getThemesDirectory().list();
        if (themes==null || themes.length==0){
            FileService.copyDir(new DiskDirectory(ApplicationPath.getDefaultThemesPath()),ApplicationPath.getThemesDirectory());
            themes = ApplicationPath.getThemesDirectory().list();
        }
        if (themes==null) {
            Log.error("no themes found");
            return;
        }
        themeNames.addAll(Arrays.asList(themes));
    }

    public void copyThemeFiles(){
        Log.info("copying theme files for " + theme);
        if (!themeNames.contains(theme)){
            Log.error("theme not found: " + theme);
            return;
        }
        DiskDirectory dir = new DiskDirectory(ApplicationPath.getAppThemeFilePath()+ "/" + theme);
        if (!dir.exists()){
            Log.error("theme folder not found for: " + theme);
            return;
        }
        ApplicationPath.getThemeDirectory().clearDirectory();
        FileService.copyDir(dir, ApplicationPath.getThemeDirectory());
    }

}
