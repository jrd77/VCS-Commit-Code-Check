package com.atzuche.order.delivery.utils;

import com.atzuche.order.delivery.common.DeliveryConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author 胡春林
 * commonUtils
 */
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

    /**
     * 获取为null的属性
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target){
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }
}
