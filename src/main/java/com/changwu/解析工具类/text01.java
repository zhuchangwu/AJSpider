package com.changwu.解析工具类;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Changwu
 * @Date: 2019/7/26 19:45
 */
public class text01 {


    public static void tt(){
        String text="<li class=\"news n12 clearfix\"> <span class=\"news_title\"><a href=\"/2019/0710/c38a131516/page.htm\" target=\"_blank\" title=\"学校（科学院）举行大学生社会实践基地签约暨揭牌仪式\">学校（科学院）举行大学生社会实践基地签约暨揭牌仪式</a></span> <span class=\"news_meta\">2019-07-10</span> </li>";

        //String re2 = "\">"+"?"+"(.*)"+"</a></span> <span class=";

        String re2 = ".*\">?"+"(.*)"+"</a></span> <span class=";

        Pattern compile = Pattern.compile(re2);

        Matcher matcher = compile.matcher(text);

        if (matcher.find()){
            System.out.println(matcher.group(0));
            System.out.println("---------");
            System.out.println(matcher.group(1));
        }
    }


    public  static List<String> getElementsByIdAndTaggetName(String id, String ...targetNames){
        id="#"+id+" ";
        for (int i=0;i<targetNames.length;i++){
            id+=targetNames[i]+" ";
        }
        System.out.println(id);
        return null;
    }

    public static void main(String[] args) {
     //   getElementsByIdAndTaggetName("id","ul","li");
        tt();
    }



public void text02(){

    /**
     *  java.util.regex包

     *  类 Pattern
     *      -- 正则表达式的编译表示形式
     *      -- Pattern p = Pattern.complile(r,int)  // 建立正则表达式

     *  类 Matcher
     *      -- 通过解释 Pattern 对character sequence 执行匹配的操作引擎
     *      Matcher m = p.matcher(str); // 匹配str的字符串
     */



    // 看 /w+ 是否匹配的上  suifsabf234saaf
    String s = "suifsabf2$$34saaf";

    // 获取正则表达式匹配对象
    Pattern pattern = Pattern.compile("\\w+");

    // 关联正则和执行器对象
    Matcher matcher = pattern.matcher(s);


    if (matcher.matches()){  // 尝试把拿到了整个序列和正则进行匹配
        System.out.println("yes");
    }else{
        System.out.println("no");
    }



    if (matcher.find()){  // 分块匹配,  假如是 24234&&32423  她会匹配到 前后两部分
        System.out.println("yes");
    }else{
        System.out.println("no");
    }



    // 找到并返回匹配结果
    if (matcher.find()){

        // group()  和 group(0) 相等
        System.out.println(matcher.group());
        System.out.println(matcher.group(0));
    }else{
        System.out.println("nothing");
    }



    //   matcher.replaceAll("换成我");  把匹配出来的 内容  替换成  换成我

    //    String对象的  split(regenx); 方法也是对字符串的   正则     切割






}
}



