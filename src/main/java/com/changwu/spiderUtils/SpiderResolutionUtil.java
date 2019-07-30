package com.changwu.spiderUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Changwu
 * @Date: 2019/7/26 20:29
 *
 *  解析html的工具对象
 */
public final class SpiderResolutionUtil {

    /**
     *  返回 prefix 和 suffix 之间的值 , 适合给前缀特别短的截取方法使用 -- 惰性匹配
     *  为了提高准确性,prefix 和 suffix 尽量长一点
     *  注意点:  prefix 和 suffix 不是直接从原网页的html中赋值粘贴过来的, 需要使用编译器将网页内容输出到控制台, 从控制台复制粘贴过来
     *
     * @param prefix
     * @param suffix
     * @return
     */
    public String getValueByPrefixSuffix(String htmltxt, String prefix, String suffix) {
        String regex = ".*"+prefix+"?"+"(.*)"+suffix;
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(htmltxt);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }


    /**
     *   id的作用是缩小匹配的范围
     *   根据id定位html片段, 并返回前后缀中间的值
     *   注意点:  prefix 和 suffix 不是直接从原网页的html中赋值粘贴过来的, 需要使用编译器将网页内容输出到控制台, 从控制台复制粘贴过来
     *
     * @param htmltxt
     * @param id
     * @param prefix
     * @param suffix
     * @return
     */
    public String getValueByIdAndPrefixSuffix(String htmltxt, String id, String prefix, String suffix) {
        Document document = Jsoup.parse(htmltxt);
        String html = document.getElementById(id).html();
        String regex = prefix + "(.*)" + suffix;
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(html);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }



    /**
     *
     *  使用id缩小匹配范围
     *  返回给定idName下的 给定的标签的名字的集合
     *  例: 获取出 id=first的div中的 ul下的 li集合
     *
     * @param html
     * @param idName  选出唯一的代码片段
     * @param targetNames 标签的名字, 如 li标签  a标签
     * @return
     */
    public  String[] getElementsByIdAndTaggetName(String html, String idName, String ...targetNames){

        idName="#"+idName+" ";
        for (int i=0;i<targetNames.length;i++){
            idName+=targetNames[i]+" ";
        }

        Document document = Jsoup.parse(html);
        Elements elements = document.select(idName);
        String[] elementArray = new String[elements.size()];
        int i = 0;
        for (Element element : elements) {
            elementArray[i]=element.toString();
            i++;
        }
        return elementArray;
    }


    /**
     * 获取html的中 class为给定值的 html源码的 集合
     * 例如: 很多卖图片的网站上, 图片位于 拥有相同class 值的div中
     *
     * @param html
     * @param className
     * @return
     */
    public String[] getElementsByClass(String html,String className){
        Document document = Jsoup.parse(html);

        System.out.println(document);
        Elements elements = document.select("."+className);
        String[] elementArray = new String[elements.size()];
        System.out.println("elements size "+elements.size());
        int i = 0; // i在方法内, 肯定不会出现并发问题
        for (Element element : elements) {
            elementArray[i]=element.toString();
            i++;
        }
        return elementArray;
    }



    /**
     * 获取指定class的第一个元素的内容 (最好保证真个页面的class值唯一)
     *
     * @param html
     * @param className
     */
    public String getFirstElementValueByClass(String html, String className) {
        if (html==null){// 这里为空,说明二级任务的url出问题了, 没下载下来,但是为了各个bean属性为null
            return "在resolution1 解析当前页面的url时出现异常";
        }
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementsByClass(className).get(0);
        return element.text();
    }
}
