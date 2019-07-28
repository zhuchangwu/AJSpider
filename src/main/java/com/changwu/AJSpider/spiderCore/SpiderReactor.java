package com.changwu.AJSpider.spiderCore;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Changwu
 * @Date: 2019/7/23 18:02
 *  爬虫的  线程反应堆
 */

public class SpiderReactor {

    private ExcutorChooser chooser; // 选择器

    private SingleThreadSpiderExecutor[] executors; // 线程执行器数组

    private SpiderExecutorFactory executor; // 线程执行器,他可以创建创建的新的线程


    public SpiderReactor(int threadNum, Class threadClass) {
        this.chooser = new ExcutorChooser(threadNum); // 初始化选择器
        initExcutors(threadNum, threadClass);
    }


    /**
     * 通过反射 创建指定数量的线程执行器
     *
     * @param threadNum
     * @param threadClazz
     */
    private void initExcutors(int threadNum, Class threadClazz) {
        // 创建出线程执行器来,之过不还没有进行start, 填充进下面的 执行器数组
        executor = new SpiderExecutorFactory(Executors.defaultThreadFactory());
        executors = new SingleThreadSpiderExecutor[threadNum];

        for (int i = 0; i < threadNum; i++) {
            try {
                SingleThreadSpiderExecutor singleThreadExecutor = (SingleThreadSpiderExecutor) threadClazz.newInstance();
                // 让他们全部指向一个线程执行器对象， 这个线程执行器可以创建出新的线程
                singleThreadExecutor.setExcutor(executor);
                executors[i] = singleThreadExecutor;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  获取线程执行器选择器
     *
     * @return
     */
    public SingleThreadSpiderExecutor nextSingleThreadExecutor() {
        return this.chooser.chooseExcutor();
    }

    ///选择器内部类, 内置轮询算法,选出线程执行器
    private class ExcutorChooser {
        private final AtomicInteger idx = new AtomicInteger();
        private int num;
        public ExcutorChooser(int num) {
            this.num = num;
        }

        public SingleThreadSpiderExecutor chooseExcutor() {
            if (isPowerOfTwo(num)){
                return executors[idx.getAndIncrement() & executors.length - 1];
            }else{
                return executors[Math.abs(idx.getAndIncrement() % executors.length)];
            }
        }
        private boolean isPowerOfTwo(int val) {
            return (val & -val) == val;
        }
    }
}