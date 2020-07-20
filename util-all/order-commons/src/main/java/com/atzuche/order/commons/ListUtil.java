package com.atzuche.order.commons;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 列表处理工具
 *
 * @author pengcheng.fu
 * @date 2020/07/20
 */
public class ListUtil {

    private static final String ENCODING = "UTF-8";


    /**
     * 判断一个List是否是empty
     *
     * @param list 数据列表
     * @return boolean
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 将List变成split分割的字符串
     *
     * @param list  数据列表
     * @param split 分隔符
     * @return String
     */
    public static String reduce(List<?> list, String split) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> it = list.iterator();

        while (it.hasNext()) {
            builder.append(it.next().toString());
            if (it.hasNext()) {
                builder.append(split);
            }

        }
        return builder.toString();

    }

    public static List<Integer> parse(String supportCities, String s) {
        List<Integer> result = new ArrayList<>();
        if (StringUtils.hasText(supportCities)) {
            if (StringUtils.hasText(s)) {
                String[] stArrs = supportCities.split(s);
                for (String str : stArrs) {
                    try {
                        Integer value = Integer.parseInt(str);
                        result.add(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    public static List<Integer> strArrToList(String[] strArr) {
        if (null == strArr) {
            return null;
        }
        List<Integer> ls = new ArrayList<>();
        for (String str : strArr) {
            ls.add(Integer.valueOf(str));
        }
        return ls;
    }

    /**
     * 把map按指定的split拼接成String
     *
     * @param map   数据
     * @param split 分隔符
     * @return String
     */
    public static String mapToStrBySplit(Map<String, Object> map, String split)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(128);
        for (Entry<String, Object> entry : map.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue())) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue().toString(),
                                ENCODING)).append(split);
            }
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    /**
     * 依据map的key获取对应value的拼接String
     *
     * @param map  数据
     * @param keys 键列表
     * @return String
     */
    public static String findValueBykey(Map<Integer, String> map, List<Integer> keys) {
        StringBuilder sb = new StringBuilder(128);
        for (Integer key : keys) {
            if (!StringUtils.isEmpty(map.get(key))) {
                sb.append(map.get(key)).append(",");
            }
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    public static List<Long> parseLng(String supportCities, String s) {
        List<Long> result = new ArrayList<>();
        if (StringUtils.hasText(supportCities)) {
            if (StringUtils.hasText(s)) {
                String[] stArrs = supportCities.split(s);
                for (String str : stArrs) {
                    try {
                        Long value = Long.valueOf(str);
                        result.add(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    public static List<String> parseString(String supportCities, String s) {
        List<String> result = new ArrayList<>();
        if (StringUtils.hasText(supportCities)) {
            if (StringUtils.hasText(s)) {
                String[] stArrs = supportCities.split(s);
                for (String str : stArrs) {
                    try {
                        result.add(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
