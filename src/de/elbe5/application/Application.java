/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.file.DiskDirectory;
import de.elbe5.base.file.DiskFile;
import de.elbe5.base.log.Log;
import de.elbe5.ckeditor.CkEditorController;
import de.elbe5.content.ContentController;
import de.elbe5.content.ContentData;
import de.elbe5.content.ContentContainer;
import de.elbe5.fullpage.FullPageController;
import de.elbe5.fullpage.FullPageData;
import de.elbe5.templatepage.*;
import de.elbe5.template.TemplateCache;
import de.elbe5.user.UserContainer;
import de.elbe5.file.FileController;
import de.elbe5.file.FileData;
import de.elbe5.file.FileService;
import de.elbe5.content.MasterPages;
import de.elbe5.search.SearchController;
import de.elbe5.actionqueue.*;
import de.elbe5.user.GroupController;
import de.elbe5.user.GroupData;
import de.elbe5.user.UserController;
import de.elbe5.user.UserData;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Application {

    public static String ENCODING = "UTF-8";

    enum keys{
        salt,
        secretKey,
        defaultLocale,
        defaultPassword
    }

    private static String salt = "";
    private static String encodedSecretKey ="";
    private static Locale defaultLocale = Locale.GERMAN;
    private static final Map<String,Locale> locales = new HashMap<>();
    private static String defaultPassword = "";
    private static int nextId = 1000;

    private static final Configuration configuration = new Configuration();
    private static final ContentContainer content = new ContentContainer();
    private static final UserContainer users = new UserContainer();

    private static final Lock idLock = new ReentrantLock();
    private static boolean idChanged = false;

    // constructors

    static{
        locales.put("de",Locale.GERMAN);
        locales.put("en",Locale.ENGLISH);
    }

    // startup

    public static boolean initialize(){
        Log.log("initializing Bandika Json Application...");
        registerControllers();
        registerDataClasses();
        registerMasterPages();
        TemplateCache.loadTemplates();
        if (!ApplicationPath.getStaticsFile().exists()){
            Log.log("creating default static configuration");
            if (!initializeStatics()){
                return false;
            }
        }
        try{
            String json = ApplicationPath.getStaticsFile().readAsText();
            JSONObject obj = new JSONObject(json);
            fromJSONObject(obj);
            idChanged = false;
        }
        catch (Exception e){
            return false;
        }
        try {
            String s = ApplicationPath.getNextIdFile().readAsText();
            nextId = Integer.parseInt(s);
            idChanged = false;
        }
        catch(Exception e){
            return false;
        }
        configuration.initialize();
        content.initialize();
        users.initialize();
        Log.log("start action queue");
        CheckDataAction.register();
        SearchIndexAction.register();
        CleanupAction.register();
        ActionQueue.start();
        Log.log("Bandika Json initialized");
        return true;
    }

    private static void registerMasterPages() {
        MasterPages.loadNames();
    }

    private static void registerControllers(){
        AdminController.register(new AdminController());
        ContentController.register(new ContentController());
        CkEditorController.register(new CkEditorController());
        FullPageController.register(new FullPageController());
        TemplatePageController.register(new TemplatePageController());
        FileController.register(new FileController());
        GroupController.register(new GroupController());
        UserController.register(new UserController());
        SearchController.register(new SearchController());
    }

    private static void registerDataClasses(){
        ContentData.register();
        ContentData.childTypes.add(FullPageData.TYPE_KEY);
        ContentData.childTypes.add(TemplatePageData.TYPE_KEY);
        FileData.register();
        FullPageData.register();
        SectionData.register();
        TemplatePageData.register();
        TemplatePageData.partTypes.add(TemplatePartData.TYPE_KEY);
        TemplatePartData.register();
        TemplatePartData.fieldTypes.add(HtmlField.TYPE_KEY);
        TemplatePartData.fieldTypes.add(TextField.TYPE_KEY);
        TemplatePartData.fieldTypes.add(ScriptField.TYPE_KEY);
        HtmlField.register();
        ScriptField.register();
        TextField.register();
        GroupData.register();
        UserData.register();
    }

    // json methods

    private static JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(keys.salt.name(), salt);
        obj.put(keys.secretKey.name(), encodedSecretKey);
        obj.put(keys.defaultLocale.name(), defaultLocale.getLanguage());
        obj.put(keys.defaultPassword.name(), defaultPassword);
        return obj;
    }

    private static void fromJSONObject(JSONObject obj) {
        try {
            salt = obj.optString(keys.salt.name());
            encodedSecretKey = obj.optString(keys.secretKey.name());
            defaultLocale = new Locale(obj.optString(keys.defaultLocale.name()));
            defaultPassword = obj.optString(keys.defaultPassword.name());
        }
        catch (Exception e){
            Log.error("unable to read data", e);
        }
    }

    // getter and setter


    public static String getSalt() {
        return salt;
    }

    public static String getEncodedSecretKey() {
        return encodedSecretKey;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static ContentContainer getContent() {
        return content;
    }

    public static UserContainer getUsers() {
        return users;
    }

    public static void setDefaultLocale(Locale locale) {
        if (locale == null || !locales.containsValue(locale))
            return;
        defaultLocale = locale;
    }

    public static String getDefaultPassword() {
        return defaultPassword;
    }

    public static Map<String, Locale> getLocales() {
        return locales;
    }

    public static boolean hasLanguage(Locale locale) {
        return locales.containsValue(locale);
    }

    public static boolean hasLanguage(String language) {
        return locales.containsKey(language);
    }

    public static int getNextId(){
        try{
            idLock.tryLock();
            idChanged = true;
            return nextId++;
        }
        finally{
            idLock.unlock();
        }
    }

    public static LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }

    // other methods

    public static boolean checkIdChanged() {
        boolean result = false;
        try {
            idLock.lock();
            if (idChanged) {
                saveId();
                idChanged = false;
                result = true;
            }

        }
        finally{
            idLock.unlock();
        }
        return result;
    }

    private static boolean saveId(){
        return ApplicationPath.getNextIdFile().writeToDisk(Integer.toString(nextId));
    }

    public static boolean initializeStatics(){
        try {
            salt = PBKDF2Encryption.generateSaltBase64();
            defaultPassword =  PBKDF2Encryption.getEncryptedPasswordBase64("pass", salt);
            System.out.println("created new salt and default password");
            SecretKey secretKey= Keys.secretKeyFor(SignatureAlgorithm.HS256);
            encodedSecretKey = Encoders.BASE64.encode(secretKey.getEncoded());
            System.out.println("created new secret key");
        } catch (Exception e) {
            System.out.println("password and secret key generation failed");
            return false;
        }
        JSONObject obj = new JSONObject();
        obj.put(keys.salt.name(), salt);
        obj.put(keys.secretKey.name(), encodedSecretKey);
        obj.put(keys.defaultLocale.name(), defaultLocale.getLanguage());
        obj.put(keys.defaultPassword.name(), defaultPassword);
        String jsonString = obj.toString(2);
        Log.log("saving static configuration");
        if (!ApplicationPath.getStaticsFile().writeToDisk(jsonString)){
            return false;
        }
        return saveId();
    }

    public static boolean createBackup(){
        DiskDirectory backupDir = new DiskDirectory(ApplicationPath.getBackupDirectory(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")));
        if (!backupDir.ensureEmpty()){
            return false;
        }
        FileService.copyDir(ApplicationPath.getDataDirectory(), backupDir);
        Log.info("backup created in " + backupDir.getPath());
        return true;
    }

    public static boolean restoreBackup(String backupName){
        DiskDirectory backupDir = new DiskDirectory(ApplicationPath.getBackupDirectory(), backupName);
        if (!backupDir.exists()){
            return false;
        }
        if (!ApplicationPath.getDataDirectory().ensureEmpty()){
            return false;
        }
        FileService.copyDir(backupDir, ApplicationPath.getDataDirectory());
        Log.info("backup restored from " + backupName);
        return restart();
    }

    public static List<String> getBackupNames(){
        List<String> dirNames = new ArrayList<>();
        File[] files = ApplicationPath.getBackupDirectory().listFiles();
        if (files == null){
            return dirNames;
        }
        for (File file : files){
            if (file.isDirectory()){
                dirNames.add(file.getName());
            }
        }
        return dirNames;
    }

    public static boolean restart() {
        DiskFile f = new DiskFile(ApplicationPath.getAppBasePath() + "/ROOT/WEB-INF/web.xml");
        try {
            if (!f.touch()){
                return false;
            }
        } catch (IOException e) {
            Log.error("could not touch file " + f.getPath(), e);
            return false;
        }
        return true;
    }

}
