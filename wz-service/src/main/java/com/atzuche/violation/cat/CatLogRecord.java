package com.atzuche.violation.cat;



import com.atzuche.violation.constant.cat.CatConstant;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;

/**
 * Created by qincai.lin on 2020/1/10.
 */
public class CatLogRecord {

    /**
     * cat记录成功日志
     * @param name
     * @param url
     * @param object
     */
    public static void successLog(String name, String url, Object object){
        Cat.logMetricForCount(name);
        Cat.logEvent(CatConstant.URL, url, Event.SUCCESS, object.toString());
    }

    /**
     * cat记录失败日志
     * @param name
     * @param url
     * @param object
     */
    public static void failLog(String name, String url, Object object, Exception e){
        Cat.logError(name, e);
        Cat.logMetricForCount(name);
        Cat.logEvent(CatConstant.URL, url, CatConstant.FAILED, object.toString());
    }
}
