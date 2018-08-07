package com.gaofeng.filecenter.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * @Author: gaofeng
 * @Date: 2018-03-15
 * @Description: 处理图片和base64的工具类
 */
public class ImageBase64Utils {

    /**
     * 将字节数组经过base64编码生成字符串
     *
     * @param bytes
     *
     * @return
     */
    private static String bytesToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将图片文件转化为字节数组，并对其进行base64编码
     *
     * @param path
     *
     * @return
     */
    public static String imageToBase64(String path) {
        byte[] data = null;
        //读取图片字节数组
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesToBase64(data);
    }

    /**
     * 将base64转化为图片存储在指定地点
     *
     * @param base64
     * @param path
     *
     * @return
     */
    public static boolean base64ToImage(String base64, String path) {

        try {
            OutputStream outputStream = new FileOutputStream(path);
            return base64ToImageOutput(base64, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理base64解码并输出流
     *
     * @param base64
     * @param outputStream
     *
     * @return
     */
    public static boolean base64ToImageOutput(String base64, OutputStream outputStream) {

        try {
            //String dataPrix = "";
            String data = "";
            if (base64 == null || "".equals(base64)) {
                throw new Exception("上传失败，上传图片数据为空");
            } else {
                if (base64.contains("base64,")) {
                    String[] d = base64.split("base64,");
                    if (d != null && d.length == 2) {
                        //dataPrix = d[0];
                        data = d[1];
                    } else {
                        throw new Exception("上传失败，数据不合法");
                    }
                } else {
                    data = base64;
                }

            }

            // String suffix = "";
            // if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){//data:image/jpeg;base64,base64编码的jpeg图片数据
            //     suffix = ".jpg";
            // } else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){//data:image/x-icon;base64,base64编码的icon图片数据
            //     suffix = ".ico";
            // } else if("data:image/gif;".equalsIgnoreCase(dataPrix)){//data:image/gif;base64,base64编码的gif图片数据
            //     suffix = ".gif";
            // } else if("data:image/png;".equalsIgnoreCase(dataPrix)){//data:image/png;base64,base64编码的png图片数据
            //     suffix = ".png";
            // }else{
            //     throw new Exception("上传图片格式不合法");
            // }

            byte[] bs = Base64.decodeBase64(data);
            try {
                outputStream.write(bs);
                outputStream.flush();
            } catch (Exception ee) {
                throw new Exception("上传失败，写入文件失败，" + ee.getMessage());
            }
        } catch (Exception e) {
            //JLog.error("上传图片失败", 101143011);
            return false;
        }
        return true;

    }
}
