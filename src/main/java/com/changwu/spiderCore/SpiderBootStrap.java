package com.changwu.spiderCore;


import java.util.Queue;

/**
 *  @Author: Changwu
 *  @Date: 2019/7/23 18:02
 *  启动器程序,
 *  todo  1. 由当前的程序完成爬虫的启动
 *  todo  2. 分发任务
 *  todo  3. 待拓展, 添加监控的功能
 *
 */

public final class SpiderBootStrap {

    // 盛放任务url的队列
    private Queue<String> taskQueue;

    // 维护线程执行器
    private SpiderReactor spiderReactor;

    public SpiderBootStrap() {}

    /**
     * 初始化线程执行器组
     *
     * @param threadExcutorClass   用户自定义的线程执行器
     * @return
     */
    public SpiderBootStrap initThreadExcutorGroup(Class threadExcutorClass) {
        int threadNumber = Runtime.getRuntime().availableProcessors() * 2;
        // 初始化任务执行线程
        initThreadExcutorGroup(threadNumber, threadExcutorClass);
        return this;
    }

    /**
     *  初始化线程执行器组
     *
     * @param threadNumber   线程执行器组的线程的数量
     * @param threadExcutorClass
     * @return
     */
    public SpiderBootStrap initThreadExcutorGroup(int threadNumber, Class threadExcutorClass) {
        // 初始化任务执行线程
        spiderReactor = new SpiderReactor(threadNumber, threadExcutorClass);
        return this;
    }

    /**
     *  添加任务队列
     * @param queue
     * @return
     */
    public SpiderBootStrap setTaskUrlQueue(Queue queue) {
        this.taskQueue = queue;
        return this;
    }


    public void build() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        distributeTask();
    }


    /**
     *  将任务分发给 任务执行器
     */
    private void distributeTask() {
        while (taskQueue.size() > 0) {
            // 通过轮询算法 , 轮询选出线程执行器
            SingleThreadSpiderExecutor singleThreadExecutor = spiderReactor.nextSingleThreadExecutor();
            String url = taskQueue.poll();
            // 把任务丢进当前执行器的队列,由他们自己执行
            singleThreadExecutor.addTask(url);
        }
        System.err.println("任务分发完成.............................................. ");
    }
}
