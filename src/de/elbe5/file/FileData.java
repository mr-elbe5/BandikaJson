/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.file.DiskFile;
import de.elbe5.base.file.MemoryFile;
import de.elbe5.data.BaseData;
import de.elbe5.base.data.StringUtil;
import de.elbe5.content.ContentData;
import de.elbe5.data.DataFactory;
import de.elbe5.data.IData;
import de.elbe5.request.RequestData;
import org.json.JSONException;
import org.json.JSONObject;

public class FileData extends BaseData {

    public static final String TYPE_KEY = "file";

    private enum keys{
        fileName,
        displayName,
        description,
        contentType,
        fileType
    }

    public static int MAX_PREVIEW_SIDE = 200;

    public static void register(){
        DataFactory.addClass(FileData.TYPE_KEY, FileData.class);
    }

    private String fileName = "";
    private String extension = "";
    private String displayName = "";
    private String description = "";
    protected String contentType = null;
    protected FileType fileType = FileType.unknown;

    protected int parentId = 0;
    protected int parentVersion = 0;

    public int maxWidth=0;
    public int maxHeight=0;
    public int maxPreviewSide= MAX_PREVIEW_SIDE;

    private DiskFile tempFile = null;
    private DiskFile tempPreviewFile = null;

    private DiskFile file = null;
    private DiskFile previewFile = null;

    // constructors

    public FileData() {
    }

    @Override
    public String getTypeKey(){
        return FileData.TYPE_KEY;
    }

    // copy and editing methods

    public void copyEditableAttributes(IData idata){
        super.copyEditableAttributes(idata);
        assert (idata instanceof FileData);
        FileData data = (FileData) idata;
        setFileName(data.getFileName());
        setDisplayName(data.getDisplayName());
        setDescription(data.getDescription());
        setContentType(data.getContentType());
        setFileType(data.getFileType());
        setMaxWidth(data.getMaxWidth());
        setMaxHeight(data.getMaxHeight());
        setMaxPreviewSide(data.getMaxPreviewSide());
    }

    public void setCreateValues(ContentData parent, int userId) {
        super.setCreateValues(userId);
        setParentId(parent.getId());
        setParentVersion(parent.getVersion());
        createDiskFiles();
    }

    public void setEditValues(FileData data) {
        copyFixedAttributes(data);
        copyEditableAttributes(data);
        setParentId(data.getParentId());
        setParentVersion(data.getParentVersion());
        createDiskFiles();
    }

    // json methods

    @Override
    public void addJSONAttributes(JSONObject obj) {
        super.addJSONAttributes(obj);
        obj.put(keys.fileName.name(), fileName);
        obj.put(keys.displayName.name(), displayName);
        obj.put(keys.description.name(), description);
        obj.put(keys.contentType.name(), contentType);
        obj.put(keys.fileType.name(), fileType.name());
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        fileName = obj.optString(keys.fileName.name());
        displayName = obj.optString(keys.displayName.name());
        description = obj.optString(keys.description.name());
        contentType = obj.optString(keys.contentType.name());
        try {
            fileType = FileType.valueOf(obj.optString(keys.fileType.name()));
        }
        catch (Exception e){
            fileType = FileType.unknown;
        }
        createDiskFiles();
    }

    //request

    @Override
    public void readRequestData(RequestData rdata){
        super.readRequestData(rdata);
        setDisplayName(rdata.getString("displayName").trim());
        setDescription(rdata.getString("description"));
        MemoryFile memoryFile = rdata.getFile("file");
        if (memoryFile!=null){
            setFileName(memoryFile.getFileName());
            setContentType(memoryFile.getContentType());
            setFileTypeFromContentType();
            tempFile = new DiskFile(ApplicationPath.getAppTempFilePath() + "/" + getIdFileName());
            if (!tempFile.writeToDisk(memoryFile)){
                rdata.addFormError("could not create file");
                return;
            }
            if (isImage()){
                MemoryFile memoryPreviewFile = memoryFile.createPreview(FileData.MAX_PREVIEW_SIDE);
                tempPreviewFile = new DiskFile(ApplicationPath.getAppTempFilePath() + "/" + getPreviewName());
                if (!tempPreviewFile.writeToDisk(memoryPreviewFile)){
                    rdata.addFormError("could not create file");
                    return;
                }
            }
            if (getDisplayName().isEmpty()){
                setDisplayName(FileService.getFileNameWithoutExtension(getFileName()));
            }
        }
        else if (isNew()){
            rdata.addIncompleteField("file");
        }
    }

    public boolean moveTempFiles(){
        if (getTempFile() !=null ){
            //Log.log("moving temp files");
            createDiskFiles();
            if (!FileService.moveFile(tempFile,file)) {
                return false;
            }
            if (isImage() && tempPreviewFile!=null && !FileService.moveFile(tempPreviewFile, previewFile)) {
                return false;
            }
            setTempFile(null);
            setTempPreviewFile(null);
        }
        return true;
    }

    // getter and setter

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = StringUtil.toSafeWebName(fileName);
        extension = FileService.getExtension(fileName);
    }

    public String getIdFileName(){
        return getId() + FileService.getExtension(getFileName());
    }

    public String getURL(){
        return "/files/" + getIdFileName();
    }

    public String getDownloadURL(){
        return "/files/" + getIdFileName() + "?download=true";
    }

    public String getDisplayFileName(){
        return getDisplayName() + extension;
    }

    public String getPreviewName(){
        return "preview" + getId() + FileService.JPEG_EXT;
    }

    public String getPreviewURL(){
        return "/files/preview" + getId() + FileService.JPEG_EXT;
    }

    public String getDisplayName() {
        if (displayName.isEmpty())
            return FileService.getFileNameWithoutExtension(getFileName());
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isDocument() {
        return fileType.equals(FileType.document);
    }

    public boolean isImage() {
        return fileType.equals(FileType.image);
    }

    public boolean isVideo() {
        return fileType.equals(FileType.video);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        setFileType(FileType.fromContentType(contentType));
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setFileTypeFromContentType(){
        fileType = FileType.fromContentType(contentType);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxPreviewSide() {
        return maxPreviewSide;
    }

    public void setMaxPreviewSide(int maxPreviewSide) {
        this.maxPreviewSide = maxPreviewSide;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(int parentVersion) {
        this.parentVersion = parentVersion;
    }

    public void setParentIdAndVersion(ContentData parent) {
        setParentId(parent.getId());
        setParentVersion(parent.getVersion());
    }

    public DiskFile getTempFile() {
        return tempFile;
    }

    public void setTempFile(DiskFile tempFile) {
        this.tempFile = tempFile;
    }

    public DiskFile getTempPreviewFile() {
        return tempPreviewFile;
    }

    public void setTempPreviewFile(DiskFile tempPreviewFile) {
        this.tempPreviewFile = tempPreviewFile;
    }

    public DiskFile getFile() {
        return file;
    }

    public void setFile(DiskFile file) {
        this.file = file;
    }

    public DiskFile getPreviewFile() {
        return previewFile;
    }

    public void setPreviewFile(DiskFile previewFile) {
        this.previewFile = previewFile;
    }

    public void createDiskFiles() {
        file = new DiskFile(ApplicationPath.getAppFilePath() + "/" + getIdFileName());
        previewFile = new DiskFile(ApplicationPath.getAppFilePath() + "/" + getPreviewName());
    }

    public DiskFile getViewableFile(RequestData rdata){
        if (tempFile != null){
            return tempFile;
        }
        return file;
    }

    public DiskFile getViewablePreviewFile(RequestData rdata){
        if (tempPreviewFile != null){
            return tempPreviewFile;
        }
        return previewFile;
    }

}
