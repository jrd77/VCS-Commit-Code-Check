package com.atzuche.order.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StringUtil {


    /**
     * 判断是否是新订单
     * @param orderNo 主订单号
     * @return 是否是新订单 true：是   false：不是
     */
    public static boolean isNewOrderNo(String orderNo){
        if(orderNo==null || orderNo.length()!=GlobalConstant.NEW_ORDER_NO_LEN){
            return false;
        }
        if(orderNo.endsWith(GlobalConstant.NEW_ORDER_NO_SUFFIX)){
            return true;
        }
        return false;
    }

    public static String getURLConnection(String urlStr) {
        try{
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(3000);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            int respCode = httpConn.getResponseCode();
            String result = "";
            if (respCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            }
            return result;
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
