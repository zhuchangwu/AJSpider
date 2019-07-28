package com.changwu.AJSpider.text;

/**
 * @Author: Changwu
 * @Date: 2019/7/28 18:03
 */
public class qluDemo {

       /* *//**
         * 如果可以在当前的html中收集全所有的bean的信息,创建出来的bean封装进spiderContainer的bean容器,然后直接返回 spiderContainer
         * 存在二级任务,将二级任务添加进 spiderContainer 的 urllist, 然后返回容器
         *
         * @param htmltxt
         * @param spiderContainer
         * @param util
         * @return
         *//*

        @Override
        protected SpiderContainer<News> resolution1(String htmltxt, SpiderContainer<News> spiderContainer, SpiderResolutionUtil util) {
            //System.out.println(htmltxt);

            String[] lis = util.getElementsByIdAndTaggetName(htmltxt, "wp_news_w6", "ul", "li");
            // 遍历数组, 分别将其中的每一条元素的值取出添加到指定的bean中
            for (int i = 0; i < lis.length; i++) {
                String html = lis[i];
                News qluNew = new News();
                System.out.println(html);
                String title = util.getValueByPrefixSuffixLazy(html, "\">", "</a></span> <span class=");
                String time = util.getValueByPrefixSuffixLazy(html, "<span class=\"news_meta\">", "</span> </li>");
                String url1 = util.getValueByPrefixSuffixLazy(html, "class=\"news_title\"><a href=\"", "\" target=\"_blank\"");
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
        protected SpiderContainer<News> resolution2(String[] htmltxt, SpiderContainer<News> spiderContainer, SpiderResolutionUtil util) {

            for (int i = 0; i < htmltxt.length; i++) {
                String body = util.getFirstElementValueByClass(htmltxt[i], "wp_articlecontent");
                spiderContainer.getBeanList().get(i).setBody(body);
            }

            for (News news : spiderContainer.getBeanList()) {
                //System.out.println("Thread.name = "+Thread.currentThread().getName()+news);
            }


            return spiderContainer;
        }

        *//* todo 持久化,支持使用注解进行持久化
         * todo @PersistenceToMySql
         * todo @PersistenceImageToLocalHost(value="磁盘目录")
         *//*
        @Override
        public void persistence(SpiderContainer<News> list) {
            System.err.println("list.getBeanList().size()   " + list.getBeanList().size());
        }*/

}
