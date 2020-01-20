package com.atzuche.order.commons;

import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/19 8:50 下午
 **/
public class ResponseCheckUtil {
    private ResponseCheckUtil() {
    }

    public static void  checkResponse(ResponseData responseObject){
        if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
            RemoteCallException remoteCallException = null;
            if(responseObject!=null){
                remoteCallException = new RemoteCallException(responseObject.getResCode(),responseObject.getResMsg(),responseObject.getData());
            }else{
                remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                        com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
            }
            throw remoteCallException;
        }
    }


}
