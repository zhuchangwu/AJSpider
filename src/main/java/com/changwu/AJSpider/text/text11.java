package com.changwu.AJSpider.text;

import com.changwu.AJSpider.spiderCore.SpiderBootStrap;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Changwu
 * @Date: 2019/7/23 23:12
 */
public class text11 {
    public static void main(String[] args) {
       // 创建任务队列, 任意队列都可以,不要求线程安全
        LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();

        // 假设在准备任务

       // String url ="http://www.qluDemo.edu.cn/38/list.htm";
      //  taskQueue.offer(url);

      // for (int i=2;i<50;i++){
      //      String url2 = "http://www.qluDemo.edu.cn/38/list"+i+".htm";
       //     taskQueue.offer(url2);
      //  }

        String url = "https://www.58pic.com/c/16001082";
        taskQueue.offer(url);

            new SpiderBootStrap()
                     .initThreadExcutorGroup(1,MyExecutor.class)
                     .setTaskUrlQueue(taskQueue)
                     .build();

    }
}
