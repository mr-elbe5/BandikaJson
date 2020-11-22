/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.file;

import de.elbe5.base.data.ImageUtil;
import de.elbe5.file.FileService;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class MemoryFile {

    protected String fileName = null;
    protected String contentType = null;
    protected int size = 0;
    protected byte[] bytes = null;

    public MemoryFile() {
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return null;
        }
        return fileName.substring(pos + 1).toLowerCase();
    }

    public String getFileNameWithoutExtension() {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isImage() {
        return contentType.startsWith("image/");
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getFileSize() {
        return size;
    }

    public void setFileSize(int size) {
        this.size = size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytesFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[0x4000];
        int len;
        while ((len = inputStream.read(buffer, 0, 0x4000)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        bytes = outputStream.toByteArray();
    }

    public boolean readFromDisk(DiskFile file){
        byte[] bytes = file.readAsBinary();
        if (bytes == null){
            return false;
        }
        setBytes(bytes);
        setFileName(file.getFileName());
        setFileSize(file.getFileSize());;
        return true;
    }

    public MemoryFile createResizedJpeg(int width, int height, boolean expand) {
        if (!isImage()) {
            return null;
        }
        try {
            MemoryFile resizedImage = new MemoryFile();
            BufferedImage bi = ImageUtil.createResizedImage(getBytes(), getContentType(), width, height, expand);
            resizedImage.setFileName(FileService.setJpegExtension(getFileName()));
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(FileService.JPEG_TYPE);
            resizedImage.setContentType("image/jpeg");
            ImageWriter writer = writers.next();
            assert (bi != null);
            resizedImage.setBytes(ImageUtil.writeImage(writer, bi));
            resizedImage.setFileSize(resizedImage.getBytes().length);
            return resizedImage;
        } catch (IOException e) {
            return null;
        }
    }

    public MemoryFile createScaledJpeg(int scalePercent){
        if (!isImage()) {
            return null;
        }
        try {
            MemoryFile resizedImage = new MemoryFile();
            BufferedImage bi = ImageUtil.createScaledImage(getBytes(), getContentType(), scalePercent);
            resizedImage.setFileName(FileService.setJpegExtension(getFileName()));
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(FileService.JPEG_TYPE);
            resizedImage.setContentType("image/jpeg");
            ImageWriter writer = writers.next();
            assert (bi != null);
            resizedImage.setBytes(ImageUtil.writeImage(writer, bi));
            resizedImage.setFileSize(resizedImage.getBytes().length);
            return resizedImage;
        } catch (IOException e) {
            return null;
        }
    }

    public MemoryFile createPreview(int maxPreviewSide){
        if (!isImage()) {
            return null;
        }
        try {
            MemoryFile preview = new MemoryFile();
            BufferedImage source = ImageUtil.createImage(getBytes(), getContentType());
            if (source != null) {
                float factor = ImageUtil.getResizeFactor(source, maxPreviewSide, maxPreviewSide, false);
                BufferedImage image = ImageUtil.copyImage(source, factor);
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(FileService.JPEG_TYPE);
                ImageWriter writer = writers.next();
                preview.setBytes(ImageUtil.writeImage(writer, image));
                preview.setFileSize(preview.getBytes().length);
                preview.setContentType("image/jpeg");
            }
            return preview;
        }
        catch (IOException e){
            return null;
        }
    }

}
