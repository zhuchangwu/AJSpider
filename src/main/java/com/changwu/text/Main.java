package com.changwu.text;

import com.changwu.spiderCore.SpiderBootStrap;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Changwu
 * @Date: 2019/7/29 18:41
 */

public class Main {
    public static void main(String[] args) {


        // 创建任务队列, 任意队列都可以,不要求线程安全
        LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue();
        // 假设在准备任务
          String url ="http://www.qlu.edu.cn/38/list.htm";
         taskQueue.offer(url);

          for (int i=2;i<50;i++){
            String url2 = "http://www.qlu.edu.cn/38/list"+i+".htm";
           taskQueue.offer(url2);
          }

        SpiderBootStrap spiderBootStrap = new SpiderBootStrap();
        spiderBootStrap
                .initThreadExcutorGroup(10,MyThreadExcutor.class)
                .setTaskUrlQueue(taskQueue)
                .build();
    }
}
