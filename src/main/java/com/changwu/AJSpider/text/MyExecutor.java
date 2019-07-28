package com.changwu.AJSpider.text;

import com.changwu.AJSpider.spiderCore.SingleThreadSpiderExecutor;
import com.changwu.AJSpider.spiderUtils.PersistenceUtil;
import com.changwu.AJSpider.spiderUtils.SpiderResolutionUtil;

import java.util.List;

/**
 * @Author: Changwu
 * @Date: 2019/7/25 22:45
 */
public class MyExecutor extends SingleThreadSpiderExecutor<Image> {

    /**
     * 如果可以在当前的html中收集全所有的bean的信息,创建出来的bean封装进spiderContainer的bean容器,然后直接返回 spiderContainer
     * 存在二级任务,将二级任务添加进 spiderContainer 的 urllist, 然后返回容器
     * @param htmltxt
     * @param spiderContainer
     * @param util
     * @return
     */
    @Override
    protected SpiderContainer<Image> resolution1(String htmltxt, SpiderContainer<Image> spiderContainer, SpiderResolutionUtil util) {


        // todo 前后缀需要从 htmltxt 复制
        String html1 = util.getFirstElementByClass(htmltxt, "flow-box favorite-flow clearfix");

        String[] divList = util.getElementsByClass(html1, "qt-card-primary");

        // 遍历数组, 分别将其中的每一条元素的值取出添加到指定的bean中
        for (int i=0;i<divList.length;i++){
            String url = util.getValueByPrefixSuffixLazy(divList[i], "<div class=\"card-trait\"></div><img src=\"//", "!/fw/580/compress/true/clip/580x772a0a0\" alt=\"\" data-id=");

            Image image = new Image();
            image.setUrl(url);

            // 第一步
            spiderContainer.getBeanList().add(image);

            // 第二步
            // spiderContainer.getUrlList().add();

        }
        return spiderContainer;
    }


    /* todo 持久化,支持使用注解进行持久化
     * todo @PersistenceToMySql
     * todo @PersistenceImageToLocalHost(value="磁盘目录")
     */
    @Override
    public void persistence(List<Image> list, PersistenceUtil<Image> persistenceUtil) {

        for (Image image : list) {
            System.out.println("http://"+image.getUrl());
        }

        String path="F:\\spider";
        list.forEach(image->{
            String url = "http://"+image.getUrl();
            persistenceUtil.PersistenceImageToLocalhost(url,path);
        });


    }


}
