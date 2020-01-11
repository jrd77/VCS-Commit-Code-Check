package com.atzuche.order.admin.description;

import com.atzuche.order.admin.constant.cat.CatConstant;
import com.atzuche.order.admin.constant.description.DescriptionConstant;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;

/**
 * Created by qincai.lin on 2020/1/10.
 */
public class LogDescription {

    /**
     * 获取描述信息
     * @param head
     * @param tail
     * @return
     */
    public static String getLogDescription(String head, String tail){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(head);
        stringBuffer.append(tail);
        stringBuffer.append(DescriptionConstant.BRACE);
        return stringBuffer.toString();
    }


    /**
     * 获取描述信息
     * @param head
     * @param tail
     * @return
     */
    public static String getCatDescription(String head, String tail){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(head);
        stringBuffer.append(tail);
        return stringBuffer.toString();
    }
}
