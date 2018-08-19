package com.gaofeng.filecenter.controller;

import com.didi.meta.javalib.JFileSyncService;
import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.filecenter.beans.Base64ImgUploadReq;
import com.gaofeng.filecenter.utils.ImageBase64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.ws.handler.HandlerResolver;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author: gaofeng
 * @Date: 2018-08-01
 * @Description:
 */
@RestController
@RequestMapping(value = "/v1/img")
public class ImgController {

    @RequestMapping(value = "uploadBase64", method = RequestMethod.POST)
    public JResponse imgUploadBase64(HttpServletRequest request, @Valid @RequestBody Base64ImgUploadReq
            base64ImgUploadReq) {

        JResponse jResponse = new JResponse();
        request.setAttribute("jResponse", jResponse);
        String path = "/Users/gaofeng/Pictures/image/" + base64ImgUploadReq.getFileName();
        JLog.info("uploadBase64:" + base64ImgUploadReq.getFileBase64());
        boolean result = ImageBase64Utils.base64ToImage(base64ImgUploadReq.getFileBase64(), path);

        if (!result) {
            jResponse.setErrNo(102072231);
            jResponse.setErrMsg("save img error");
            return jResponse;
        }

        String fileSyncUri = path;
        String downloadFileName = base64ImgUploadReq.getFileName();
        String fileFetchToken = JFileSyncService.genFileTokenExpire7Days(fileSyncUri, downloadFileName);

        if (fileFetchToken == null) {
            jResponse.setErrNo(102062256);
            jResponse.setErrMsg("generate fileFetchToken error");
            return jResponse;
        }
        jResponse.setData(fileFetchToken);
        return jResponse;
    }

    @RequestMapping(value = "downloadImg", method = RequestMethod.GET)
    public JResponse downloadImg(HttpServletRequest request, HttpServletResponse response) {
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);
        String fileFetchToken = request.getParameter("fileFetchToken");
        if (fileFetchToken == null) {
            response.setStatus(403);
            jResponse.setErrNo(102062258);
            jResponse.setErrMsg("fileFetchToken is null");
            return jResponse;
        }

        JFileSyncService.FetchToken fetchToken = JFileSyncService.checkFileFetchToken(fileFetchToken);
        if (fetchToken == null) {
            response.setStatus(403);
            jResponse.setErrNo(102062259);
            jResponse.setErrMsg("checkFileFetchToken failed");
            return jResponse;
        }

        String fileName = String.valueOf(System.currentTimeMillis());
        if (fetchToken.getFetchTokenData().getDownloadFileName() != null) {
            fileName = fetchToken.getFetchTokenData().getDownloadFileName();
        }

        try {
            response.setHeader("Content-Disposition", "inline;filename=" + fileName);
            response.setContentType("image/jpeg");
            ServletOutputStream out = response.getOutputStream();
            InputStream inputStream = new FileInputStream(fetchToken.getFetchTokenData().getFileSycUri());

            byte[] bytes = new byte[4096];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            inputStream.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            JLog.warn("downloadImg error errorMsg=" + e.getMessage());
            response.setStatus(500);
            jResponse.setErrNo(102062306);
            jResponse.setErrMsg("downloadImg error");
            return jResponse;
        }
        return jResponse;
    }
}
