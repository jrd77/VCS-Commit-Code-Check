package com.atzuche.order.delivery.utils;

import com.atzuche.order.delivery.common.DeliveryConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


@Slf4j
public class CommonUtil {

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号 
        return uuid.replaceAll("-", "");
    }


    /**
     * 转换成map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> javaBeanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            Map<String, Object> map = BeanUtils.describe(obj);
            return map;
        } catch (Exception e) {
            log.info("转换成map失败");
            return null;
        }
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String md5Encode(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes("utf-8"));
        byte b[] = md.digest();
        int i;
        StringBuilder buf = new StringBuilder("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    /**
     * 获取sign
     *
     * @param map
     * @return
     */
    public static String getSign(Map<String,Object> map) {
        try {
            StringBuffer sbff = new StringBuffer();
            TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
            treeMap.putAll(map);
            for (Map.Entry<String, Object> m : treeMap.entrySet()) {
                sbff.append(m.getKey()).append(m.getValue());
            }
            String key = sbff.toString().toUpperCase() + DeliveryConstants.ACCESS_KEY;
            String md5str = md5Encode(key);
            return md5str;

        } catch (Exception e) {
            log.info("配送参数获取sign失败");
        }
        return null;
    }
}
