package com.changwu.解析工具类;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Author: Changwu
 * @Date: 2019/7/28 9:09
 */
public class text03 {

    public static void NIO(String url, String path) throws Exception {
        URL url2 = null;
        try {
            url2 = new URL(url);
        } catch (Exception e) {

        }

        assert url2 != null;
        ReadableByteChannel readableByteChannel = Channels.newChannel(url2.openStream());
        FileChannel channel = new FileOutputStream(path).getChannel();
        channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        //等价于
        readableByteChannel.close();
        channel.close();
    }


    public static void main(String[] args) throws Exception {
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1564286455469&di=0dcbda7722cfa120d428e99095c6201e&imgtype=0&src=http%3A%2F%2Fimg.ph.126.net%2Fjn9M5aDDSYsfiF94N1xmlg%3D%3D%2F3717721492394456620.jpg";
        String path = "F:\\spider\\pic2.jpg";
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            path = "F:\\spider\\pic"+i+".jpg";
           // downloadPicture(url,path);
            //   NIO(url, path);
        }

        System.out.println("耗时: " + (System.currentTimeMillis() - start));
    }

  /*  //链接url下载图片
    private static void downloadPicture(String urlList, String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
