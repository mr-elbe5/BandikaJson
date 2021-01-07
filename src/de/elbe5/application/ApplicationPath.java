/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.file.DiskDirectory;
import de.elbe5.base.file.DiskFile;

import java.io.File;

public class ApplicationPath {

    private static String appPath = "";
    private static String jspPath = "";
    private static String masterPath = "";
    private static String appBasePath = "";
    private static String appDataPath = "";
    private static String appStaticsFilePath = "";
    private static String nextIdFilePath = "";
    private static String appConfigFilePath = "";
    private static String appThemeFilePath = "";
    private static String appContentFilePath = "";
    private static String appUsersFilePath = "";
    private static String appTemplatePath = "";
    private static String appFilePath = "";
    private static String appTempFilePath = "";
    private static String appBackupPath = "";
    private static String appSearchIndexPath = "";

    private static DiskFile staticsFile;
    private static DiskFile nextIdFile;
    private static DiskFile configFile;
    private static DiskFile themeFile;
    private static DiskFile contentFile;
    private static DiskFile usersFile;

    private static DiskDirectory masterDirectory;
    private static DiskDirectory dataDirectory;
    private static DiskDirectory templateDirectory;
    private static DiskDirectory fileDirectory;
    private static DiskDirectory tempFileDirectory;
    private static DiskDirectory backupDirectory;
    private static DiskDirectory indexDirectory;

    public static String getAppPath() {
        return appPath;
    }

    public static String getJspPath() {
        return jspPath;
    }

    public static String getMasterPath() {
        return masterPath;
    }

    public static String getAppBasePath() {
        return appBasePath;
    }

    public static String getAppDataPath() {
        return appDataPath;
    }

    public static String getAppStaticsFilePath() {
        return appStaticsFilePath;
    }

    public static String getNextIdFilePath() {
        return nextIdFilePath;
    }

    public static String getAppBackupPath() {
        return appBackupPath;
    }

    public static String getAppSearchIndexPath() {
        return appSearchIndexPath;
    }

    public static String getAppConfigFilePath() {
        return appConfigFilePath;
    }

    public static String getAppThemeFilePath() {
        return appThemeFilePath;
    }

    public static String getAppContentFilePath() {
        return appContentFilePath;
    }

    public static String getAppUsersFilePath() {
        return appUsersFilePath;
    }

    public static String getAppTemplatePath() {
        return appTemplatePath;
    }

    public static String getAppFilePath() {
        return appFilePath;
    }

    public static String getAppTempFilePath() {
        return appTempFilePath;
    }

    public static DiskFile getStaticsFile() {
        return staticsFile;
    }

    public static DiskFile getNextIdFile() {
        return nextIdFile;
    }

    public static DiskFile getConfigFile() {
        return configFile;
    }

    public static DiskFile getThemeFile() {
        return themeFile;
    }

    public static DiskFile getContentFile() {
        return contentFile;
    }

    public static DiskFile getUsersFile() {
        return usersFile;
    }

    public static DiskDirectory getMasterDirectory() {
        return masterDirectory;
    }

    public static DiskDirectory getDataDirectory() {
        return dataDirectory;
    }

    public static DiskDirectory getTemplateDirectory() {
        return templateDirectory;
    }

    public static DiskDirectory getFileDirectory() {
        return fileDirectory;
    }

    public static DiskDirectory getTempFileDirectory() {
        return tempFileDirectory;
    }

    public static DiskDirectory getBackupDirectory() {
        return backupDirectory;
    }

    public static DiskDirectory getIndexDirectory() {
        return indexDirectory;
    }

    public static void initializePath(File baseDir, File appDir) {
        if (baseDir == null || appDir ==null) {
            return;
        }
        appBasePath = baseDir.getAbsolutePath().replace('\\', '/');
        System.out.println("application base path is: " + getAppBasePath());
        appPath = appDir.getAbsolutePath().replace('\\', '/');
        jspPath = appPath + "/WEB-INF/_jsp";
        masterPath = jspPath + "/_master";
        appDataPath = appBasePath + "_data";
        appStaticsFilePath = appDataPath + "/statics.json";
        nextIdFilePath = appDataPath + "/next.id";
        appConfigFilePath = appDataPath + "/config.json";
        appThemeFilePath = appDataPath + "/theme.json";
        appContentFilePath = appDataPath + "/content.json";
        appUsersFilePath = appDataPath + "/users.json";
        appTemplatePath = appDataPath + "/templates";
        appFilePath = appDataPath + "/files";
        appTempFilePath = appFilePath + "/tmp";
        appSearchIndexPath = appDataPath + "/index";
        appBackupPath = appBasePath + "_backups";

        staticsFile = new DiskFile(getAppStaticsFilePath());
        nextIdFile = new DiskFile(getNextIdFilePath());
        configFile = new DiskFile(getAppConfigFilePath());
        themeFile = new DiskFile(getAppThemeFilePath());
        contentFile = new DiskFile(getAppContentFilePath());
        usersFile = new DiskFile(getAppUsersFilePath());

        masterDirectory = new DiskDirectory(masterPath);
        dataDirectory = new DiskDirectory(appDataPath);
        templateDirectory = new DiskDirectory(appTemplatePath);
        fileDirectory = new DiskDirectory(appFilePath);
        tempFileDirectory = new DiskDirectory(appTempFilePath);
        backupDirectory = new DiskDirectory(appBackupPath);
        indexDirectory = new DiskDirectory(appSearchIndexPath);

        boolean success = dataDirectory.ensureExists();
        success &= templateDirectory.ensureExists();
        success &= fileDirectory.ensureExists();
        success &= tempFileDirectory.ensureExists();
        success &= indexDirectory.ensureExists();
        success &= backupDirectory.ensureExists();
        assert success;
    }

}
