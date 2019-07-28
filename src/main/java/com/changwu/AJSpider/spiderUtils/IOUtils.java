package com.changwu.AJSpider.spiderUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: Changwu
 * @Date: 2019/7/26 17:56
 */
public class IOUtils {

    /**
     *  把输入字节流转换成字节数组
     *
     * @param inputStream
     * @return
     */
    public static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        byte[] byteBuffer = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int readlength = 0;
        // 把InputStream中的数据.读进 byteBuffer容器
        while((readlength = inputStream.read(byteBuffer))!=-1){
            // 把指定长度的 字节信息写入到 outputStream中
            byteArrayOutputStream.write(byteBuffer,0,readlength);
        }
        return byteArrayOutputStream.toByteArray();
    }


}
