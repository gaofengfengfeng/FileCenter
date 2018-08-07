package com.gaofeng.filecenter.beans;

/**
 * @Author: gaofeng
 * @Date: 2018-08-01
 * @Description:
 */
public class Base64ImgUploadReq {
    private String fileName;
    private String fileBase64;
    private String token;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
