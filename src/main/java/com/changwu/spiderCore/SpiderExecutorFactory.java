package com.changwu.spiderCore;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @Author: Changwu
 * @Date: 2019/7/23 18:06
 * 爬虫的  线程执行器   由当前类实例化工作组的线程
 */
public class SpiderExecutorFactory implements Executor {

    private final ThreadFactory threadFactory;

    public SpiderExecutorFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }

}
