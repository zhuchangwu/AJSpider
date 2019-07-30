package com.changwu.text;

import com.changwu.spiderCore.SingleThreadSpiderExecutor;
import com.changwu.spiderUtils.PersistenceUtil;
import com.changwu.spiderUtils.SpiderResolutionUtil;
import com.changwu.text.bean.News;

import java.util.List;

/**
 * @Author: Changwu
 * @Date: 2019/7/29 18:33
 */
public class MyThreadExcutor extends SpiderSingleThreadExecutor<News> {

    protected SpiderContainer resolution1(String s, SpiderContainer<News> spiderContainer, SpiderResolutionUtil spiderResolutionUtil) {

        String[] lis = spiderResolutionUtil.getElementsByIdAndTaggetName(s, "wp_news_w6", "ul", "li");
        // 遍历数组, 分别将其中的每一条元素的值取出添加到指定的bean中
        for (int i = 0; i < lis.length; i++) {
            String html = lis[i];
            News qluNew = new News();
            System.out.println(html);
            String title = spiderResolutionUtil.getValueByPrefixSuffix(html, "\">", "</a></span> <span class=");
            String time = spiderResolutionUtil.getValueByPrefixSuffix(html, "<span class=\"news_meta\">", "</span> </li>");
            String url1 = spiderResolutionUtil.getValueByPrefixSuffix(html, "class=\"news_title\"><a href=\"", "\" target=\"_blank\"");
            String perfix = "http://www.qlu.edu.cn";

            qluNew.setTitle(title);
            qluNew.setDate(time);

            String targetUrl = perfix + url1;

            // todo 推荐大家在解析拼接二级url时多加几层判断,保证二级url的正确性
            System.out.println(Thread.currentThread().getName() + "  url:   " + targetUrl);
            // 我们学校的新闻模块"http://www.qlu.edu.cnhttp://2019sdh.qlu.edu.cn/2019/0305/c7334a122472/page.htm";
            if (targetUrl.substring(5, targetUrl.length()).contains("http:")) {
                //  说明上面的url拼接从新处理这个 url
                //  url不同很大程度上意味着  resolution2()  按照不同的模板解析
                continue; // 在这里直接舍弃这个新闻了
            }

            // 第一步
            spiderContainer.getBeanList().add(qluNew);
            // 第二步
            spiderContainer.getUrlList().add(perfix + url1);
        }
        return spiderContainer;
    }


    @Override
    protected SpiderContainer resolution2(String[] htmltxt, SpiderContainer<News> spiderContainer, SpiderResolutionUtil util) {

        for (int i = 0; i < htmltxt.length; i++) {
            String body = util.getFirstElementValueByClass(htmltxt[i], "wp_articlecontent");
            spiderContainer.getBeanList().get(i).setBody(body);
        }

        for (News news : spiderContainer.getBeanList()) {
            System.out.println("Thread.name = " + Thread.currentThread().getName() + news.toString());
        }
        return spiderContainer;
    }


    public void persistence(List<News> list, PersistenceUtil<News> persistenceUtil) {

    }
}
