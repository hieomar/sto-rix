package com.example.storix;

public class UploadedFiles {
    private String fileName, fileUrl, fileSize, fileUploadedDate;

    public UploadedFiles() {
    }

    public UploadedFiles(String fileName, String fileSize, String fileUploadedDate, String fileUrl) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUploadedDate = fileUploadedDate;
        this.fileUrl = fileUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileUploadedDate(String fileUploadedDate) {
        this.fileUploadedDate = fileUploadedDate;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileUploadedDate() {
        return fileUploadedDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
