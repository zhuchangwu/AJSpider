package com.changwu.spiderUtils;

import java.io.*;
import java.net.URL;

/**
 * @Author: Changwu
 * @Date: 2019/7/28 10:47
 */
public class PersistenceUtil<T> {

    /**
     * 将url对应的image 持久化到本地指定的path目录下
     *
     */
    public void PersistenceImageToLocalhost(String imageUrl, String path) {
        URL url = null;
        try {
            url = new URL(imageUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            String imageName = Thread.currentThread().getName() + System.currentTimeMillis();

            if (imageUrl.contains("jpg")) {
                path=path+"\\"+imageName+".jpg";
                System.out.println("path111: "+path);
            } else if (imageUrl.contains("png")) {
                path=path+"\\"+imageName+".png";
                System.out.println("path222: "+path);

            }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
