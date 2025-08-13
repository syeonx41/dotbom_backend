package com.springboot.teamalbam.viewer.file;

public class FileUploadResponse {
    private String fileId;

    public FileUploadResponse(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
