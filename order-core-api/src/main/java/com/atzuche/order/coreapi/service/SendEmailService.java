package com.atzuche.order.coreapi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SendEmailService
 *
 * @author shisong
 * @date 2020/1/15
 */
@Service
public class SendEmailService {
    public String getContent(List<String> orderNos) {
        if(CollectionUtils.isEmpty(orderNos)){
            return null;
        }
        String content = "<html>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <style>\n" +
                "        body {align :center;valign : middle}\n" +
                "        th {text-align:center;vertical-align:middle;width: 150px;}\n" +
                "        td {text-align:center;vertical-align:middle;width: 150px;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n"+
                "    您好，以下订单开始，但仍未支付违章押金:\n" +
                "<br><br>\n"
                ;
        StringBuilder orderNoStr = new StringBuilder();
        for (String orderNo : orderNos) {
            orderNoStr.append(orderNo);
            orderNoStr.append(",");
        }
        orderNoStr = new StringBuilder(orderNoStr.substring(0, orderNos.size() - 1));
        content += orderNoStr;
        content +="</body>\n" +
                "</html>";
        return content;
    }
}
