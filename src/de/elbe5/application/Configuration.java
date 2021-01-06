/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.file.DiskFile;
import de.elbe5.base.file.MemoryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.mail.MailSender;
import de.elbe5.data.DataContainer;
import de.elbe5.file.FileService;
import de.elbe5.request.SessionRequestData;
import org.json.JSONException;
import org.json.JSONObject;

public class Configuration extends DataContainer {

    public static final String[] THEME_NAMES = {"blue", "carbon", "light", "paper"};

    enum keys{
        changerId,
        applicationName,
        theme,
        copyright,
        smtpHost,
        smtpPort,
        smtpConnectionType,
        smtpUser,
        smtpPassword,
        mailSendingUser,
        mailReceivingUser,
        timerInterval,
        serviceInterval,
        cleanupInterval,
        indexInterval
    }

    protected int changerId = 0;
    private String applicationName = "Bandika";
    private String theme = "blue";
    private String copyright = "2020 Elbe 5";
    private String smtpHost = "";
    private int smtpPort = 25;
    private MailSender.SmtpConnectionType smtpConnectionType = MailSender.SmtpConnectionType.plain;
    private String smtpUser = "";
    private String smtpPassword = "";
    private String mailSendingUser = "";
    private String mailReceivingUser = "";
    private int timerInterval = 1000; // in millis
    private int serviceInterval = 5; // in minutes
    private int cleanupInterval = 10; // in minutes
    private int indexInterval = 15; // in minutes

    private static final DiskFile logoFile = new DiskFile(ApplicationPath.getAppFilePath(), "logo.png");

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
        setSmtpHost(data.getSmtpHost());
        setSmtpPort(data.getSmtpPort());
        setSmtpConnectionType(data.getSmtpConnectionType());
        setSmtpUser(data.getSmtpUser());
        setSmtpPassword(data.getSmtpPassword());
        setMailSendingUser(data.getMailSendingUser());
        setMailReceivingUser(data.getMailReceivingUser());
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
        obj.put(keys.smtpHost.name(), smtpHost);
        obj.put(keys.smtpPort.name(), smtpPort);
        obj.put(keys.smtpConnectionType.name(), smtpConnectionType.name());
        obj.put(keys.smtpUser.name(), smtpUser);
        obj.put(keys.smtpPassword.name(), smtpPassword);
        obj.put(keys.mailSendingUser.name(), mailSendingUser);
        obj.put(keys.mailReceivingUser.name(), mailReceivingUser);
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
            smtpHost = obj.optString(keys.smtpHost.name());
            smtpPort = obj.optInt(keys.smtpPort.name());
            String s = obj.optString(keys.smtpConnectionType.name());
            if (!s.isEmpty()) {
                smtpConnectionType = MailSender.SmtpConnectionType.valueOf(s);
            }
            smtpUser = obj.optString(keys.smtpUser.name());
            smtpPassword = obj.optString(keys.smtpPassword.name());
            mailSendingUser = obj.optString(keys.mailSendingUser.name());
            mailReceivingUser = obj.optString(keys.mailReceivingUser.name());
            timerInterval = obj.optInt(keys.timerInterval.name());
            serviceInterval = obj.optInt(keys.serviceInterval.name());
            cleanupInterval = obj.optInt(keys.cleanupInterval.name());
            indexInterval = obj.optInt(keys.indexInterval.name());
        }
        catch (Exception e){
            Log.error("unable to read data", e);
        }
    }

    // request

    public void readSettingsRequestData(SessionRequestData rdata) {
        setApplicationName(rdata.getString("applicationName"));
        setTheme(rdata.getString("theme"));
        setCopyright(rdata.getString("copyright"));
        setSmtpHost(rdata.getString("smtpHost"));
        setSmtpPort(rdata.getInt("smtpPort"));
        setSmtpConnectionType(MailSender.SmtpConnectionType.valueOf(rdata.getString("smtpConnectionType")));
        setSmtpUser(rdata.getString("smtpUser"));
        setSmtpPassword(rdata.getString("smtpPassword"));
        setMailSendingUser(rdata.getString("mailSendingUsre"));
        setMailReceivingUser(rdata.getString("mailReceivingUser"));
        setTimerInterval(rdata.getInt("timerInterval"));
        setServiceInterval(rdata.getInt("serviceInterval"));
        setCleanupInterval(rdata.getInt("cleanupInterval"));
        setIndexInterval(rdata.getInt("indexInterval"));
        MemoryFile memoryFile = rdata.getFile("logo");
        if (memoryFile!=null){
            if (memoryFile.getFileName().endsWith(".png")) {
                if (!logoFile.writeToDisk(memoryFile)) {
                    rdata.addFormError("could not create file");
                }
            }
            else{
                Log.warn("wrong logo format - must be .png");
            }
        }
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

    public String getStyle(){
        return "/static-content/css/"+theme+"Theme.css";
    }

    public String getLayoutName(){
        switch (theme){
            case "blue":
            case "carbon":
            case "light":
            case "paper":
                return "paragraphLayout";
            default:
                return "emptyLayout";
        }
    }

    public String getLayout(){
        return "/WEB-INF/_jsp/_theme/"+getLayoutName()+".inc.jsp";
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public static DiskFile getLogoFile() {
        return logoFile;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    private void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    private void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public MailSender.SmtpConnectionType getSmtpConnectionType() {
        return smtpConnectionType;
    }

    private void setSmtpConnectionType(MailSender.SmtpConnectionType smtpConnectionType) {
        this.smtpConnectionType = smtpConnectionType;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    private void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    private void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getMailSendingUser() {
        return mailSendingUser;
    }

    private void setMailSendingUser(String mailSendingUser) {
        this.mailSendingUser = mailSendingUser;
    }

    public String getMailReceivingUser() {
        return mailReceivingUser;
    }

    private void setMailReceivingUser(String mailReceivingUser) {
        this.mailReceivingUser = mailReceivingUser;
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

    public MailSender getMailSender() {
        MailSender mailer = new MailSender();
        mailer.setSmtpHost(getSmtpHost());
        mailer.setSmtpPort(getSmtpPort());
        mailer.setSmtpConnectionType(getSmtpConnectionType());
        mailer.setSmtpUser(getSmtpUser());
        mailer.setSmtpPassword(getSmtpPassword());
        mailer.setFrom(getMailSendingUser());
        mailer.setReplyTo(getMailSendingUser());
        return mailer;
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
        Log.log("initializing configuration");
        String json = ApplicationPath.getConfigFile().readAsText();
        try {
            JSONObject obj = new JSONObject(json);
            fromJSONObject(obj);
            changed = false;
        } catch (JSONException e) {
            return false;
        }
        if (!logoFile.exists()){
            DiskFile defaultLogo = new DiskFile(ApplicationPath.getAppPath() + "/static-content/img/logo.png");
            FileService.copyFile(defaultLogo, logoFile);
        }
        return true;
    }

}
