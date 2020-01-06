package com.atzuche.order.coreapi.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 地图api
 *
 * @author zg
 * @version 创建时间：2014年6月4日
 */
public class BizAreaUtil {

    private static final Logger logger = LoggerFactory.getLogger(BizAreaUtil.class);

    private static final String GET_BIZ_AREA_URL = "http://api.map.baidu.com/geocoder/v2/?ak=03c191741fa52a27375ba9684e7b0970&callback=renderReverse&output=json&pois=1&location=";

    /**
     * 根据经纬度获取地址信息
     *
     * @param lon 经度
     * @param lat 维度
     * @return String 地址信息
     */
    public static String getBizArea(String lon, String lat) {
        String bizArea = "";
        try {
            String url = GET_BIZ_AREA_URL + lat + "," + lon;
            StringBuilder resStr = sendGetRequest(url);
            int bizIdx = resStr.indexOf("\"business\":");
            if (bizIdx == -1) {
                return "";
            }
            int idx = bizIdx + 12;
            resStr.delete(0, idx);
            idx = resStr.indexOf("\"");
            bizArea = resStr.substring(0, idx);
        } catch (Exception e) {
            logger.error("根据经纬度获取地址信息异常.", e);
        }
        return bizArea;
    }

    /**
     * 该方法能获取json字符串中addressComponent中的属性对应的值
     *
     * @param obj (key 为city 或者 province)
     * @param lon 经度
     * @param lat 维度
     * @return String
     */
    public static String getObjString(String obj, String lon, String lat) throws Exception {
        String resStr = sendGetRequest
                ("http://api.map.baidu.com/geocoder/v2/?ak=03c191741fa52a27375ba9684e7b0970&output=json&location=" + lat + "," + lon, 2000, 2000).toString();
        JSONObject jsonObject = JSONObject.fromObject(resStr);
        Map map = new HashMap();
        map = (Map) jsonObject.get("result");
        map = (Map) map.get("addressComponent");
        return map.get(obj) == null ? "" : map.get(obj).toString();
    }


    /**
     * 根据经纬度获取地址信息
     *
     * @param lon 经度
     * @param lat 维度
     * @return String[] 地址信息
     */
    public static String[] getBizAreas(String lon, String lat) {
        String area = getBizArea(lon, lat);
        if (!StringUtils.hasText(area)) {
            return null;
        }
        return area.split(",");
    }

    /**
     * 根据ip获取当前的城市或者省份
     * key 为city 或者 province
     *
     * @param obj (key 为city 或者 province)
     * @param ip  IP地址
     * @return String
     */
    public static String getObjString(String obj, String ip) throws Exception {
        String reString = sendGetRequest("http://api.map.baidu.com/location/ip?ak=03c191741fa52a27375ba9684e7b0970&ip=" + ip + "&coor=bd09ll", 2000, 2000);
        JSONObject jsonObject = JSONObject.fromObject(reString);
        Map map = (Map) jsonObject.get("content");
        map = (Map) map.get("address_detail");
        return (String) map.get(obj) == null ? "" : (String) map.get(obj);
    }

    /**
     * 依据经纬度获取地址信息
     *
     * @param lon 经度
     * @param lat 维度
     * @return String
     */
    public static String getReqAddrFromLonLat(String lon, String lat) {
        if(StringUtils.isEmpty(lon) || StringUtils.isEmpty(lat)) {
            return null;
        }
        try {
            //第一步：根据高德的经纬度转换成百度的经纬度。
            String reString = sendGetRequest("http://api.map.baidu.com/geoconv/v1/?coords=" + lon + "," + lat + "&from=3&to=5&ak=aBQ9e7D7XhKnMbnbWwER96m1", 2000, 2000);
            JSONObject json = JSONObject.fromObject(reString);

            if ((Integer) json.get("status") != 0) {
                return "mistakeLonLat";
            }

            List list = (List) json.get("result");
            Map map = (Map) list.get(0);
            lon = String.valueOf(map.get("x"));
            lat = String.valueOf(map.get("y"));

            //根据百度的经纬度查询地址。
            String resStr = sendGetRequest("http://api.map.baidu.com/geocoder/v2/?ak=03c191741fa52a27375ba9684e7b0970&output=json&location=" + lat + "," + lon, 2000, 2000).toString();
            JSONObject jsonObject = JSONObject.fromObject(resStr);
            if ((Integer) jsonObject.get("status") != 0) {
                return "mistakeGetAddr";
            }

            map.clear();
            map = (Map) jsonObject.get("result");
            return map.get("formatted_address") == null ? "" : map.get("formatted_address").toString();
        } catch (Exception e) {
            return "mistakeError";
        }
    }

    /**
     * 百度接口
     * 该方法能获取json字符串中addressComponent中的属性对应的值
     *
     * @param lon 经度
     * @param lat 维度
     * @return Map<String, String>
     */
    public static Map<String, String> getPlaceFromBMap(String lon, String lat) throws Exception {
        String resStr = sendGetRequest
                ("http://api.map.baidu.com/geocoder/v2/?ak=03c191741fa52a27375ba9684e7b0970&output=json&location=" + lat + "," + lon, 2000, 2000).toString();
        JSONObject jsonObject = JSONObject.fromObject(resStr);
        Map<String, String> resultMap = new HashMap<String, String>();
        Map map = new HashMap();
        map = (Map) jsonObject.get("result");
        map = (Map) map.get("addressComponent");
        resultMap.put("city", String.valueOf(map.get("city")));
        resultMap.put("province", String.valueOf(map.get("province")));
        return resultMap;
    }

    /**
     * 高德接口
     *
     * @param lon 经度
     * @param lat 纬度
     * @return 返回的Map(key 为city 或者 province)
     */
    public static Map<String, String> getPlaceFromMap(double lon, double lat) throws Exception {
        Map<String, String> resultMap = new HashMap<String, String>();
        String resStr = sendGetRequest
                ("http://restapi.amap.com/v3/geocode/regeo?output=json&location=" + lon + "," + lat + "&key=5ac4c2e00b7fb4e43acbf68fffd87e3f&extensions=base", 2000, 2000).toString();
        if (StringUtils.hasText(resStr)) {
            JSONObject jsonObject = JSONObject.fromObject(resStr);
            JSONObject jsonRegeocode = jsonObject.getJSONObject("regeocode");


            if (jsonRegeocode != null) {
                JSONObject addressJson = jsonRegeocode.getJSONObject("addressComponent");
                if (addressJson != null) {
                    resultMap.put("city", addressJson.getString("city"));
                    resultMap.put("province", addressJson.getString("province"));
                }
            }
        }
        return resultMap;
    }

    /**
     * 行政区域查询经纬度范围
     *
     * @param cityCode 城市编码
     * @return List<Map < String, Double>>
     */
    public static List<Map<String, Double>> getDistrictPoints(String cityCode) throws Exception {
        String resStr = sendGetRequest
                ("http://restapi.amap.com/v3/config/district?key=5ac4c2e00b7fb4e43acbf68fffd87e3f&keywords=" + cityCode + "&level=city&subdistrict=0&extensions=all", 2000, 2000).toString();
        List<Map<String, Double>> polygonPoints = new ArrayList<Map<String, Double>>();
        if (StringUtils.hasText(resStr)) {
            JSONObject jsonObject = JSONObject.fromObject(resStr);
            JSONArray jsonRegeocode = jsonObject.getJSONArray("districts");
            JSONObject object = JSONObject.fromObject(jsonRegeocode.get(0));
            String lonAndLatStr = object.get("polyline").toString();
            if (StringUtils.isEmpty(lonAndLatStr)) {
                return polygonPoints;
            }
            String[] lonAndLats = lonAndLatStr.split(";");
            for (String lonAndLat : lonAndLats) {
                if (lonAndLat.contains("|")) {
                    String[] str = lonAndLat.split("\\|");
                    Map<String, Double> lonAndLatMap = new HashMap<String, Double>();
                    String s = str[0];
                    String lon = s.split(",")[0];
                    String lat = s.split(",")[1];
                    lonAndLatMap.put("lon", Double.parseDouble(lon));
                    lonAndLatMap.put("lat", Double.parseDouble(lat));
                    polygonPoints.add(lonAndLatMap);
                    lonAndLatMap = new HashMap<>();
                    lon = str[1].split(",")[0];
                    lat = str[1].split(",")[1];
                    lonAndLatMap.put("lon", Double.parseDouble(lon));
                    lonAndLatMap.put("lat", Double.parseDouble(lat));
                    polygonPoints.add(lonAndLatMap);
                } else {
                    Map<String, Double> lonAndLatMap = new HashMap<String, Double>();
                    String lon = lonAndLat.split(",")[0];
                    String lat = lonAndLat.split(",")[1];
                    lonAndLatMap.put("lon", Double.parseDouble(lon));
                    lonAndLatMap.put("lat", Double.parseDouble(lat));
                    polygonPoints.add(lonAndLatMap);
                }
            }
        }
        return polygonPoints;
    }

    /**
     * 高德接口 导航路径
     * 求距离
     *
     * @param sLon：起点经度
     * @param sLat：起点纬度
     * @param eLon：终点经度
     * @param eLat：终点纬度
     * @param type：0：速度优先（时间）；1：费用优先（不走收费路段的最快道路）；2：距离优先；3：不走快速路；4：躲避拥堵；5：多策略（同时使用速度优先、费用优先、距离优先三个策略计算路径）；6：不走高速7不走高速且避免收费；8：躲避收费和拥堵；9：不走高速且躲避收费和拥堵
     * @return Long ：返回距离单位为米
     */

    public static Long getPdirectionMap(double sLon, double sLat, double eLon, double eLat, int type) throws Exception {
        try {
            String url = "http://restapi.amap.com/v3/direction/driving?output=json&origin=" + sLon + "," + sLat + "&destination=" + eLon + "," + eLat + "&strategy=" + type + "&key=5ac4c2e00b7fb4e43acbf68fffd87e3f&extensions=base";
            String resStr = sendGetRequest(url, 2000, 2000).toString();
            System.out.println(resStr);
            if (StringUtils.hasText(resStr)) {
                JSONObject jsonObject = JSONObject.fromObject(resStr);
                JSONObject jsonroute = jsonObject.getJSONObject("route");
                if (jsonroute != null) {
                    JSONArray pathsJson = jsonroute.getJSONArray("paths");
                    if (pathsJson != null) {
                        JSONObject pathObjectJson = pathsJson.getJSONObject(0);
                        if (pathObjectJson != null) {
                            return pathObjectJson.getLong("distance");
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("调高德获取距离出错", e);
        }
        return null;
    }

    /**
     * 根据ip获取当前的城市或者省份
     *
     * @param ip
     * @return 返回的Map(key 为city 或者 province)
     */
    public static Map<String, String> getPlaceFromMap(String ip) throws Exception {
        String reString = sendGetRequest("http://api.map.baidu.com/location/ip?ak=03c191741fa52a27375ba9684e7b0970&ip=" + ip + "&coor=bd09ll", 10000, 8000);
        JSONObject jsonObject = JSONObject.fromObject(reString);
        Map map = (Map) jsonObject.get("content");
        map = (Map) map.get("address_detail");
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("city", String.valueOf(map.get("city")));
        resultMap.put("province", String.valueOf(map.get("province")));
        return resultMap;
    }


    private static StringBuilder sendGetRequest(String reqUrl) throws Exception {
        logger.info("查询商圈：{}", reqUrl);
        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader bin = null;
        StringBuilder sb = null;
        try {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(8000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream in = conn.getInputStream();
            bin = null;
            int resCode = conn.getResponseCode();
            sb = new StringBuilder();
            if (resCode == 200) {
                bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = null;
                while ((line = bin.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                logger.error("获取“商圈”错误，ResponseCode:{}", resCode);
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            conn.disconnect();
            if (bin != null) {
                bin.close();
            }
        }
        logger.info("查询商圈返回：{}", sb);
        return sb;
    }

    private static String sendGetRequest(String reqUrl, int connectOutTime, int readOutTime) throws Exception {
        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectOutTime);
        conn.setReadTimeout(readOutTime);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        BufferedReader bin = null;
        int resCode = conn.getResponseCode();
        StringBuilder sb = new StringBuilder();
        if (resCode == 200) {
            bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while ((line = bin.readLine()) != null) {
                sb.append(line);
            }
        } else {
            logger.error("获取地址错误，ResponseCode:{}", resCode);
        }
        if (bin != null) {
            bin.close();
        }
        return sb.toString();
    }
}
 