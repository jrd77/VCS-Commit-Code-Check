package com.atzuche.order.coreapi.utils;

import com.autoyol.commons.web.ErrorCode;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author 胡春林
 *
 */
public class SMSIcsocVoiceUtils {

    public  static Logger logger = LoggerFactory.getLogger(SMSIcsocVoiceUtils.class);

    private static final String ICSOC_URL="http://180.168.109.174:2180";
    private static final String ICSOC_ACCOUNT="icsoc";
    private static final String ICSOC_PASSWORD="abc123456";

    /**
     * 调用中通天虹语音接口地址
     * @param paramMap
     * @return
     */
    public static ErrorCode getIcsocNewServer(Map<String,Object> paramMap){
        Integer result=null;
        ErrorCode error=null;
        try {

            Integer	 lines= getIcsocLines();
            //主Map   注意：各参数的顺序不能变，否则会验签错误
            Map<String,Object> map = new HashMap<String,Object>();
            //存储返回数据
            paramMap.put("backData", "当前空闲数:"+lines+",不足10");
            if(lines-10 > 0){
                map.put("vccId", 2);
                map.put("proId",paramMap.get("proId"));
                map.put("jobId",paramMap.get("jobId"));
                if(paramMap.containsKey("ownerPhone")){
                    map.put("phoneNum", paramMap.get("ownerPhone") );
                }
                if(paramMap.containsKey("phone")){
                    map.put("phoneNum", paramMap.get("phone") );
                    map.put("varTelphone", "" );
                }
                if(paramMap.containsKey("renterPhone")){
                    map.put("varTelphone", paramMap.get("renterPhone") );
                }
                map.put("callNum",2);
                map.put("callInterval", 30);
                //存放语音信息Map
                Map<String,Object> templateInfo=new LinkedHashMap<String, Object>();

                if(paramMap.containsKey("tenantTips")){
                    templateInfo.put("tenantTips", paramMap.get("tenantTips") );
                }
                if(paramMap.containsKey("acceptTenant")){
                    templateInfo.put("acceptTenant", paramMap.get("acceptTenant") );
                }
                if(paramMap.containsKey("rejectTenant")){
                    templateInfo.put("rejectTenant", paramMap.get("rejectTenant") );
                }
                if(paramMap.containsKey("delayTips")){
                    templateInfo.put("delayTips", paramMap.get("delayTips") );
                }
                if(paramMap.containsKey("acceptDelay")){
                    templateInfo.put("acceptDelay", paramMap.get("acceptDelay") );
                }
                if(paramMap.containsKey("rejectDelay")){
                    templateInfo.put("rejectDelay", paramMap.get("rejectDelay") );
                }
                if(paramMap.containsKey("codeTips")){
                    templateInfo.put("codeTips", paramMap.get("codeTips") );
                }
                if(paramMap.containsKey("voiceTips")){
                    templateInfo.put("voiceTips", paramMap.get("voiceTips") );
                }
                if(paramMap.containsKey("breakrulesTips") ){
                    templateInfo.put("breakrulesTips", paramMap.get("breakrulesTips") );
                }
                if(paramMap.containsKey("order_change_wel") ){
                    templateInfo.put("order_change_wel", paramMap.get("order_change_wel") );
                }
                if(paramMap.containsKey("accept_autoorder") ){
                    templateInfo.put("accept_autoorder", paramMap.get("accept_autoorder") );
                }
                if(paramMap.containsKey("reject_autoorder") ){
                    templateInfo.put("reject_autoorder", paramMap.get("reject_autoorder") );
                }
                map.put("templateInfo", templateInfo);
                map.put("customParams", "");
                //获取sign
                TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
                treeMap.putAll(map);
                JSONObject jsonMap = new JSONObject();
                jsonMap.putAll(treeMap);
                jsonMap.put("templateInfo", templateInfo);
                String key = jsonMap.toString()+"a32a32aa7493ec96dd743401e1518d79";
                key.getBytes("UTF-8");
                map.put("sign",SHA1(key));
                String resContent = httpVoice(ICSOC_URL+"/dm/api/data",map);
                logger.info("[中通天鸿]生成语音提示:{}",ICSOC_URL+ "/dm/api/data >> "+resContent );
                if( resContent != null && !"".equals(resContent)){
                    paramMap.put("backData", resContent);
                    Map<String, Object> resMap = parseDataForMap(resContent);
                    if(!resMap.isEmpty()){
                        result = (Integer)resMap.get("code");
                    }
                    if(result == null||result != 200){
                        error = ErrorCode.FAILED;
                    }else{
                        error = ErrorCode.SUCCESS;
                    }
                }else{
                    //空值信息
                    paramMap.put("backData", "中通天虹未返回信息");
                }
            }
        }catch (Exception e) {
            error = ErrorCode.FAILED;
            logger.info("调用中通天虹语音接口异常",e);
        }
        return error;
    }

    /**
     * 获取中继线空闲数
     * @return
     */

    private static int getIcsocLines(){
        Map<String,Object> map = new HashMap<String,Object>();
        String account=ICSOC_ACCOUNT;
        String password=SHA1("icsoc"+Base64.getEncoder().encode(ICSOC_PASSWORD.getBytes()));
        String url=ICSOC_URL+ "/voiceserver.php";
        map.put("Account", account);
        map.put("password", password);
        try {
            logger.info("请求参数url={},Account={},password={}",url,account,password);
            String resContent = httpVoice(url,map);
            logger.info("返回结果为result={}",resContent);
            Map<String, Object> resMap = parseDataForMap(resContent);
            if(!resMap.isEmpty()){
                Integer idle = (Integer)resMap.get("idle");
                return idle;
            }
        } catch (Exception e) {
            logger.error("获取中通天虹中继空闲数异常",e);
        }
        return 0;
    }

    private static String httpVoice(String url, Map<String, Object> params) throws IOException {
        URL u = null;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (Objects.nonNull(params)) {
            for (Map.Entry<String, Object> e : params.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb.substring(0, sb.length() - 1);
        }
        System.out.println("send_url:" + url);
        u = new URL(url);
        con = (HttpURLConnection) u.openConnection();
        //// POST 只能为大写，严格限制，post会不识别
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-type", "application/json");

        OutputStreamWriter osw = new OutputStreamWriter( con.getOutputStream(),"UTF-8");
        JSONObject jsonMap = new JSONObject();

        jsonMap.putAll(params);
        System.out.println(jsonMap);
        osw.write(jsonMap.toString());
        osw.flush();
        osw.close();

        if (con != null) {
            con.disconnect();
        }
        logger.info("中通天鸿:send_url:" + url);
        logger.info("中通天鸿:send_parameter:" + jsonMap);

        // 读取返回内容
        StringBuffer buffer = new StringBuffer();
        //一定要有返回值，否则无法把请求发送给server端。
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        logger.info("中通天鸿:response:" + buffer.toString());

        return buffer.toString();
    }


    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            try {
                digest.update(decript.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将json字符串转换为map
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Object> parseDataForMap(Object data) {
        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject jsonObject = JSONObject.fromObject(data);
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            params.put(key, value);
        }
        return params;
    }
}
