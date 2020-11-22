/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.application.Application;
import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import de.elbe5.data.*;
import de.elbe5.file.FileData;
import de.elbe5.page.PageData;
import de.elbe5.rights.Right;
import de.elbe5.servlet.ResponseException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

public class ContentContainer extends DataContainer {

    private enum keys{
        contentRoot
    }

    protected ContentData contentRoot = null;

    private Map<Integer, ContentData> contentMap = new HashMap<>();
    private Map<String, ContentData> urlMap = new HashMap<>();
    private Map<Integer, FileData> fileMap = new HashMap<>();

    // constructors and initializers

    public ContentContainer(){
    }

    public boolean initialize(){
        Log.log("initializing content");
        if (!ApplicationPath.getContentFile().exists()) {
            ContentContainer dc = new DefaultContentContainer();
            dc.save();
        }
        String json = ApplicationPath.getContentFile().readAsText();
        try{
            JSONObject obj = new JSONObject(json);
            fromJSONObject(obj);
            changed = false;
            return true;
        }
        catch (JSONException e){
            return false;
        }
    }

    // json methods

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        super.addJSONAttributes(obj);
        obj.put(keys.contentRoot.name(), contentRoot.toJSONObject());
        return obj;
    }

    public void fromJSONObject(JSONObject obj) {
        try {
            JSONObject rootObj = obj.getJSONObject(keys.contentRoot.name());
            String contentRootType = rootObj.optString(IData.typeKey);
            contentRoot = DataFactory.createObject(contentRootType, ContentData.class);
            if (contentRoot == null){
                throw new ResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            contentRoot.fromJSONObject(rootObj);
            mapContent();
            super.getJSONAttributes(obj);
        }
        catch (Exception e){
            Log.error("unable to read data", e);
        }
    }

    public void mapContent() {
        Map<Integer, ContentData> cmap = new HashMap<>();
        Map<Integer, FileData> fmap = new HashMap<>();
        Map<String, ContentData> umap = new HashMap<>();
        mapContent(contentRoot, cmap, umap, fmap);
        contentMap = cmap;
        urlMap = umap;
        fileMap = fmap;
        Log.log("content mapped to ids and urls");
    }

    private void mapContent(ContentData data, Map<Integer, ContentData> contentMap, Map<String, ContentData> urlMap, Map<Integer, FileData> fileMap) {
        contentMap.put(data.getId(), data);
        urlMap.put(data.getUrl(), data);
        for (ContentData child : data.getChildren()) {
            child.setParentIdAndVersion(data);
            child.generatePath(data);
            mapContent(child, contentMap, urlMap, fileMap);
        }
        for (FileData file : data.getFiles()) {
            file.setParentIdAndVersion(data);
            fileMap.put(file.getId(), file);
        }
    }

    // getter and setter

    // content

    public ContentData getContentRoot() {
        return contentRoot;
    }

    public ContentData getContent(int id) {
        return contentMap.get(id);
    }

    public ContentData getContent(int id, int version) {
        ContentData data = getContent(id);
        if (data == null || data.getVersion() != version) {
            return null;
        }
        return data;
    }

    public <T extends ContentData> T getContent(int id, Class<T> cls) {
        try {
            return cls.cast(contentMap.get(id));
        } catch (NullPointerException | ClassCastException e) {
            return null;
        }
    }

    public <T extends ContentData> T getContent(int id, int version, Class<T> cls) {
        T data = getContent(id, cls);
        if (data == null || data.getVersion() != version) {
            return null;
        }
        return data;
    }

    public ContentData getContent(String url) {
        return urlMap.get(url);
    }

    public <T extends ContentData> T getContent(String url, Class<T> cls) {
        try {
            return cls.cast(urlMap.get(url));
        } catch (NullPointerException | ClassCastException e) {
            return null;
        }
    }

    public Collection<ContentData> getContents(){
        return contentMap.values();
    }

    public <T extends ContentData> List<T> getContents(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (ContentData data : contentMap.values()) {
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        } catch (NullPointerException | ClassCastException e) {
            return list;
        }
        return list;
    }

    public List<Integer> collectParentIds(int contentId) {
        List<Integer> ids = new ArrayList<>();
        ContentData content = getContent(contentId);
        if (content == null){
            return ids;
        }
        content = getContent(content.getParentId());
        while (content != null){
            ids.add(content.getId());
            content = getContent(content.getParentId());
        }
        return ids;
    }

    // content changes

    public boolean addContent(ContentData data, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData parent = getContent(data.getParentId());
            if (parent != null && parent.getVersion() == data.getParentVersion()) {
                data.setNew(false);
                data.setParentIdAndVersion(parent);
                data.inheritRightsFromParent(parent);
                data.generatePath(parent);
                parent.getChildren().add(data);
                contentMap.put(data.getId(), data);
                urlMap.put(data.getUrl(), data);
                data.setChangerId(userId);
                data.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("adding content - content not found: " + data.getParentId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean updateContent(ContentData data, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData original = getContent(data.getId(), data.getVersion());
            if (original != null) {
                original.copyEditableAttributes(data);
                original.increaseVersion();
                original.setChangerId(userId);
                original.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("updating content - content not found: " + data.getId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean updatePage(PageData data, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            PageData original = getContent(data.getId(), data.getVersion(), PageData.class);
            if (original != null) {
                original.copyPageAttributes(data);
                original.increaseVersion();
                original.setChangerId(userId);
                original.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("updating content - content not found: " + data.getId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean publishPage(PageData data) {
        try {
            dataLock.lock();
            data.setPublishDate(Application.getCurrentTime());
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;
    }

    public boolean moveContent(ContentData data, int newParentId, int parentVersion, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData oldParent = getContent(data.getParentId(), data.getParentVersion());
            ContentData newParent = getContent(newParentId, parentVersion);
            if (oldParent!=null && newParent!=null) {
                oldParent.getChildren().remove(data);
                data.setParentIdAndVersion(newParent);
                newParent.getChildren().add(data);
                data.inheritRightsFromParent(newParent);
                data.increaseVersion();
                data.setChangerId(userId);
                data.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("moving content - content not found: " + data.getParentId() + "," + newParentId);
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean updateChildRanking(ContentData data, Map<Integer,Integer> rankMap, int userId) {
        boolean success;
        try {
            dataLock.lock();
            for (int id : rankMap.keySet()){
                for (ContentData child: data.getChildren()){
                    if (child.getId() == id){
                        child.setRanking(rankMap.get(id));
                    }
                }
            }
            data.getChildren().sort(new ContentRankingComparator());
            setHasChanged();
            success = true;
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean updateContentRights(ContentData data, Map<Integer, Right> rightMap, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData original = getContent(data.getId(), data.getVersion());
            if (original != null) {
                original.getGroupRights().clear();
                original.getGroupRights().putAll(rightMap);
                original.setChangerId(userId);
                original.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("updating content rights - content not found: " + data.getId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean removeContent(ContentData data) {
        try {
            dataLock.lock();
            contentMap.remove(data.getId());
            for (ContentData child : data.getChildren()) {
                removeContent(child);
            }
            for (FileData file : data.getFiles()) {
                fileMap.remove(file.getId());
            }
            setHasChanged();
        } finally {
            dataLock.unlock();
        }
        return true;
    }

    // files

    public FileData getFile(int id) {
        return fileMap.get(id);
    }

    public FileData getFile(int id, int version) {
        FileData data = getFile(id);
        if (data == null || data.getVersion() != version) {
            return null;
        }
        return data;
    }

    public <T extends FileData> T getFile(int id, Class<T> cls) {
        try {
            return cls.cast(fileMap.get(id));
        } catch (NullPointerException | ClassCastException e) {
            return null;
        }
    }

    public <T extends FileData> List<T> getFiles(Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            for (FileData data : fileMap.values()) {
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        } catch (NullPointerException | ClassCastException e) {
            return null;
        }
        return list;
    }

    // file changes

    public boolean addFile(FileData data, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData parent = getContent(data.getParentId(), data.getParentVersion());
            if (parent != null && data.getTempFile() != null) {
                data.setNew(false);
                data.setParentIdAndVersion(parent);
                parent.getFiles().add(data);
                fileMap.put(data.getId(), data);
                data.setChangerId(userId);
                data.setChangeDate(Application.getCurrentTime());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("adding file - content or file not found: " + data.getParentId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean updateFile(FileData data, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            FileData original = getFile(data.getId(), data.getVersion());
            if (original != null) {
                original.copyEditableAttributes(data);
                if (data.getTempFile() != null) {
                    original.setTempFile(data.getTempFile());
                    original.setFileTypeFromContentType();
                    original.setPreviewFile(data.getPreviewFile());
                }
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("updating file - file not found: " + data.getId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean moveFile(FileData data, int newParentId, int newParentVersion, int userId) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData oldParent = getContent(data.getParentId(), data.getParentVersion());
            ContentData newParent = getContent(newParentId, newParentVersion);
            if (oldParent != null && newParent!=null) {
                oldParent.getFiles().remove(data);
                newParent.getFiles().add(data);
                data.setChangerId(userId);
                data.setChangeDate(Application.getCurrentTime());
                data.increaseVersion();
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("moving file - content not found: " + data.getParentId() + "," + newParentId);
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    public boolean removeFile(FileData data) {
        boolean success = false;
        try {
            dataLock.lock();
            ContentData parent = getContent(data.getParentId(), data.getParentVersion());
            if (parent != null) {
                parent.getFiles().remove(data);
                fileMap.remove(data.getId());
                setHasChanged();
                success = true;
            }
            else{
                Log.warn("removing file from content - content not found: " + data.getParentId());
            }
        } finally {
            dataLock.unlock();
        }
        return success;
    }

    // binary files

    public boolean moveTempFiles(){
        boolean success = true;
        for (FileData file : fileMap.values()){
            success &= file.moveTempFiles();
        }
        return success;
    }

    public void cleanupFiles(){
        Set<String> fileNames = new HashSet<>();
        for (FileData file : fileMap.values()){
            fileNames.add(file.getIdFileName());
            fileNames.add(file.getPreviewName());
        }
        File[] files = ApplicationPath.getFileDirectory().listFiles();
        if (files!=null){
            for (File file: files){
                if (!fileNames.contains(file.getName())){
                    if (!file.delete()){
                        Log.warn("could not delete file " + file.getName());
                    }
                }
                else{
                    Log.log("file ok: " + file.getName());
                }
            }
        }
    }

    //persistance

    protected boolean save(){
        if (!moveTempFiles()){
            Log.warn("not all files saved");
        }
        JSONObject obj = toJSONObject();
        String jsonString = obj.toString(4);
        return ApplicationPath.getContentFile().writeToDisk(jsonString);
    }

}
