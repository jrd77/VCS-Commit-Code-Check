package com.atzuche.order.commons.http;


import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {
    /**
     * 验证签名
     * @param key
     * @param macSign 上送的签名字符串
     * @param fieldsName 签名的字段值
     * @return
     * @throws Exception
     */
    public static boolean verifySign(String key, String macSign, Object ...fieldsName) throws Exception{
        StringBuilder sb = new StringBuilder();
        for (Object obj : fieldsName) {
            sb.append(obj);
        }
        System.out.println("签名Key：" + key);
        System.out.println("待签名：" + sb);
        String str = encodeHmacMD5(sb.toString(), key);
        System.out.println("签名后的字符串：" + str);
        System.out.println("上送的macSign：" + macSign);
        return str.equalsIgnoreCase(macSign);
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //System.out.println("HMAC:" + HMAC.encodeHmacMD5("The quick brown fox jumps over the lazy dog", "key"));
		/*String s1 = "测试718121219127qqqtest@163.com550511";
		String str = encodeHmacMD5(s1, "20140102134024650");
		System.out.println(str);*/

    }

    public static String encodeHmacMD5(String content,String key) throws Exception{

        SecretKey secretKey = new SecretKeySpec(key.getBytes(),"HmacMD5");

        Mac mac = Mac.getInstance(secretKey.getAlgorithm());

        mac.init(secretKey);
        return HMAC.byteArray2HexString(mac.doFinal(content.getBytes("UTF-8")));

    }

    private static String byteArray2HexString(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        for (byte b : arr) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() < 2)
                tmp = "0" + tmp;
            sbd.append(tmp);
        }
        return sbd.toString();
    }	
}
