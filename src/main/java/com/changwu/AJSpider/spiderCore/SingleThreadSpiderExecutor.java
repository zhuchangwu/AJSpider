package com.changwu.AJSpider.spiderCore;

import com.changwu.AJSpider.spiderUtils.IOUtils;
import com.changwu.AJSpider.spiderUtils.PersistenceUtil;
import com.changwu.AJSpider.spiderUtils.SpiderResolutionUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Changwu
 * @Date: 2019/7/23 19:24
 * 由本类维护任务队列和唯一的一条线程
 * 泛型T 对象的类型
 */
public abstract class SingleThreadSpiderExecutor<T> {

    private Thread thread; // 与线程执行器唯一绑定的线程

    private ConcurrentLinkedQueue<String> taskUrlQueue; // 任务队列

    private Executor excutor; //线程执行器,由他开启线程

    private SpiderContainer<T> spiderContainer; // 存放 当前url中能获取到的 Bean 的集合

     private PersistenceUtil<T> persistenceUtil;

    private SpiderResolutionUtil spiderResolutionUtil;  // 解析html的工具对象

    public SingleThreadSpiderExecutor() {
        this.taskUrlQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * 设置线程执行器,并开启线程
     *
     * @param excutor
     */
    public void setExcutor(Executor excutor) {
        this.excutor = excutor;
        doStartThread(); // 开启当前的SingleThreadExecutor维护的线程
    }

    /**
     * 添加任务
     *
     * @param task
     */
    public void addTask(String task) {
        taskUrlQueue.offer(task);
        System.out.println(this + "  当前队列任务数: " + taskUrlQueue.size());
    }

    /**
     * 判断是否在当前线程,只有cpu轮询到当前对象对应的thread时返回true
     *
     * @return
     */
    private boolean inThisThread() {
        return this.thread == Thread.currentThread();
    }

    /**
     * 通过线程执行器开启线程
     * 并将创建的线程与当前的 SingleThreadExecutor做唯一的绑定
     *
     */
    private void doStartThread() {
        assert thread == null;
        // 断言线程为空, 然后才创建新的线程
        excutor.execute(() -> {
            this.thread = Thread.currentThread();
            this.persistenceUtil=new PersistenceUtil<T>();
            this.spiderResolutionUtil = new SpiderResolutionUtil();
            this.spiderContainer = new SpiderContainer<T>(new ArrayList(),new ArrayList<T>());
            System.out.println("Thread name == " + thread.getName());
            runAllTask();
        });
    }

    /**
     * 根据url 下载html
     *
     * @param url
     * @return
     */
    private String downLoadHtml(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = parseEntity(entity, "utf-8");
            response.close();
            httpClient.close();
        } catch (IOException e) {
            // todo 记录下出问题的url
            System.err.println("指定的url 不合法: "+url);
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 根据指定的url,批量下载出对应的html
     *
     * @param urlList
     * @return
     */
    private String[] downLoadHtmlList(List<String> urlList) {
        String[] strings = new String[urlList.size()];
        int i=0;
        for (String url : urlList) {
            String html = downLoadHtml(url);
            strings[i]=html;
            i++;
        }
        return strings;
    }





    /**
     *  不同的网站的编码是不同的, 本方法的作用是将目标服务器返回的 Response 中的HttpEntity ,解码成String字符串
     *
     * @param entity
     * @param defualtCharset
     * @return
     * @throws IOException
     */
    private String parseEntity(HttpEntity entity, String defualtCharset) throws IOException {

        String findCharset = null;
        String htmlSource = null;

        // 不管是什么编码,都转换成字节数组
        byte[] contentByteArray = IOUtils.convertInputStreamToByteArray(entity.getContent());

        // 通过entity
        findCharset = EntityUtils.getContentCharSet(entity);

        if (findCharset == null) {
            // 将html的内容按照默认的编码集进行解码,不关系html的body部分是否出现乱码, 只确保使用utf-8解码html header部分一定不会是乱码就行
            htmlSource = new String(contentByteArray, defualtCharset);
            BufferedReader bufferedReader = new BufferedReader(new StringReader(htmlSource));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {  // 按行读取
                line = line.trim().toLowerCase();   // 去除每行的空格,转小写
                if (line.contains("<meta")) {  //逐行检查是否有 meta    ([\s\S]*)"
                   // 使用正则匹配出 charset
                    Pattern compile = Pattern.compile("charset=[\"]?([\\s\\S]*?)[\">]");
                    Matcher matcher = compile.matcher(line);
                    findCharset =   matcher.group(1);
                    if (findCharset != null) {
                        break;
                    }
                } else if (line.contains("</head>")) {
                    // 如果找到了 </head> 还没有找到,就没必要往下找了, 直接使用默认编码
                    break;
                }
            }
            bufferedReader.close();
            if (findCharset == null) {
                findCharset = defualtCharset;
            }
            if (findCharset.equals(defualtCharset)) { //如果找到的编码和默认的编码相同,直接返回 entity的Content, 因为可以用默认的的编码解析,上面的htmlSource并没有出现乱码
                return htmlSource;
            } else { // 如果不相等,说明一定有乱码,用新的编码解析,返回
                return new String(contentByteArray, findCharset);
            }
        } else { // 直接找到,直接编码返回
            return new String(contentByteArray, findCharset);
        }
    }




    /**
     *  解析html,封装bean
     *  一级任务:
     *         第一步: 解析当前的html需要的字段接触出来封装进用户new 的实体类中
     *         第二步: 将实体对象添加进 spiderContainer容器中的实体集合中
     *         第三步: 返回容器
     *
     *   多级任务:
     *         第一步: 将html需要的字段,解析出来封装进用户创建的实体对象中
     *         第二步: 将实体对象添加进 spiderContainer 容器中的实体集合中
     *                 将当前实体需要的其他字段信息所在的 二级url 添加进 spiderContainer 容器中的url集合中
     *         第三步: 返回容器
     *
     * @param htmltxt 将被解析的html
     * @param spiderContainer
     * @return
     */
    protected abstract  SpiderContainer<T> resolution1(String htmltxt, SpiderContainer<T> spiderContainer, SpiderResolutionUtil util);


    /**
     * 二级任务 ,例如采集新闻时,新闻列表在页面1, 新闻体在页面2 ,需要二次解析html
     * 当前方法为普通空方法, 由用户根据需求 选择实现
     *
     * @param htmls
     * @param spiderContainer
     * @param util
     * @return
     */
    protected SpiderContainer<T> resolution2(String[] htmls, SpiderContainer<T> spiderContainer, SpiderResolutionUtil util) {
        spiderContainer.getUrlList().clear();
        return null;
    }

    /**
     * 三级任务
     * 当前方法为普通空方法, 由用户根据需求 选择实现
     *
     * @param html
     * @param spiderContainer
     * @return
     */
    protected SpiderContainer<T> resolution3(String[] html, SpiderContainer<T> spiderContainer, SpiderResolutionUtil util) {
        spiderContainer.getUrlList().clear();
        return null;
    }

    /**
     * 用户自定义持久化的方式
     * @param beanList
     * @param util
     */
    public  abstract void   persistence(List<T> beanList,PersistenceUtil<T> util);


    /**
     *   执行全部任务
     *
     */
    private void runAllTask() {

        for (; ; ) {
            if (inThisThread()) {
                // 获取url
                String url = this.taskUrlQueue.poll();

                if (url != null) {
                    // 存放二级任务的url集合
                    SpiderContainer<T> spiderContainer = null;
                    // 存放二级任务的url对应的html
                    String [] htmls = null;

                    // 根据url下载html资源
                    String html = downLoadHtml(url);

                    // 解析html中的可变数量的实体对象,
                    spiderContainer = resolution1(html, this.spiderContainer, spiderResolutionUtil);

                    // 满足如下条件说明存在二级任务,进一步 解析下载二级任务  把新值封装进对象,
                    if (0 != spiderContainer.getUrlList().size()) {
                        int preNumber = spiderContainer.urlList.size();
                        htmls = downLoadHtmlList(spiderContainer.getUrlList());
                        spiderContainer = resolution2(htmls, spiderContainer, spiderResolutionUtil);

                        // 满足如下条件说明存在三级任务,进一步 解析下载三级任务  把新值封装进对象,
                        if (preNumber!=spiderContainer.getUrlList().size()){
                            htmls   =  downLoadHtmlList(spiderContainer.getUrlList());
                            spiderContainer = resolution3(htmls, spiderContainer, spiderResolutionUtil);
                        }
                    }
                    // 持久化;
                    persistence((List<T>) spiderContainer.getBeanList(),this.persistenceUtil);

                    // 清空容器
                    this.spiderContainer.getUrlList().clear();
                    this.spiderContainer.getBeanList().clear();
                } else {
                    try {
                        System.err.println(Thread.currentThread().getName() + "暂无任务可以执行,睡眠两秒");
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 容器,盛放url集合,以及bean的集合
     *
     * @param <T>
     */
   public class SpiderContainer<T>{
        private List<String> urlList;
        private List<T> beanList;

        public SpiderContainer(List urlList,List<T> beanList){
            this.urlList=urlList;
            this.beanList=beanList;
        }

        public List<String> getUrlList(){
            return this.urlList;
        }

        public List<T> getBeanList(){
            return this.beanList;
        }
    }
}