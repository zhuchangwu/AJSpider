package com.changwu.spiderUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @Author: Changwu
 * @Date: 2019/7/28 10:47
 */
public final class PersistenceUtil<T> {

    /**
     * 将url对应的image 持久化到本地指定的path目录下
     */
    public void PersistenceImageToLocalhost(String imageUrl, String path) {
        URL url = null;
        try {
            url = new URL(imageUrl);

            String imageName = Thread.currentThread().getName() + System.currentTimeMillis();
            if (imageUrl.contains("jpg")) {
                path = path + "\\" + imageName + ".jpg";
            } else if (imageUrl.contains("png")) {
                path = path + "\\" + imageName + ".png";
            }

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileChannel channel = new FileOutputStream(path).getChannel();
            channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            readableByteChannel.close();
            channel.close();
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName()+" 下载url:  "+url+"  失败");
            e.printStackTrace();
        }
    }

}
