package com.atzuche.order.commons.http;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class SecurityUtils {

	public static final String ALGORITHM_MD5 = "MD5";
	public static final String ALGORITHM_3DES = "DESede";
	private static final String ENCODING_UTF8 = "UTF-8";
	
	public static final String ALGORITHM_SHA512 = "SHA-512";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//System.out.println(cryption(key, "111@163.com111"));
		//System.out.println("md5:" + SecurityUtils.Md5("322690874"));

		///System.out.println("3des加密:" + byteArray2HexString(encryptMode(hexString2ByteArray(Md5("1234567890")), "12345".getBytes("utf-8"))));
		//System.out.println("3des解密:" + new String(decryptMode(hexString2ByteArray(Md5("1234567890")), hexString2ByteArray("ad64592ee4a4296c")), "utf-8"));

		/*String key = "123456789abc";
		String content = "我爱北京天安门";
		String miWen = cryption(key, content);
		System.out.println("密文："+miWen);
		System.out.println(decryption(key, miWen));*/
		
		//System.out.println(Md5("appid=5DF7406B-3E3C-2CD3-B71F-3634DA8C8CD&methodname=getdevicesinfo05575642-FC0D-4331-AF23-956E0ED14B45"));
		
		/*String str = "范围为服务范围分为分为范围为非法违法我教育局111";
		String strHex = DigestUtils.sha512Hex(str);
		System.out.println(strHex);
		System.out.println(strHex.getBytes("UTF-8").length);
		
		String strHex1 = SecurityUtils.SHA512(str);
		System.out.println(strHex1);
		System.out.println(strHex1.getBytes("UTF-8").length);
		
		System.out.println(strHex.equals(strHex1));*/
		
		System.out.println(SHA512("FWEW范围分为非"));
	}

	/**
	 * 对上送到服务端请求报文加密
	 * @param key
	 * @param content
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static String cryption(String key, String content) throws UnsupportedEncodingException, Exception{
		return byteArray2HexString(encryptMode(hexString2ByteArray(Md5(key)), content.getBytes()));
	}
	
	/**
	 * 对客户端上送过来的请求报文解密
	 * @param key
	 * @param content
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static String decryption(String key, String content) throws UnsupportedEncodingException, Exception{
		content = new String(content.getBytes(),"UTF-8");
		return new String(decryptMode(hexString2ByteArray(Md5(key)), hexString2ByteArray(content)), ENCODING_UTF8);
	}
	
	/**
	 * MD5签名算法 返回 Hex string
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public static String Md5(String plainText) throws Exception {
		MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
		md.update(plainText.getBytes());
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
		return buf.toString().toUpperCase();

	}
	
	public static String SHA512(String plainText) throws Exception{
		MessageDigest md = MessageDigest.getInstance(ALGORITHM_SHA512);
		byte[] byteArr = md.digest(plainText.getBytes(ENCODING_UTF8));
		return byteArray2HexString(byteArr);
	}

	//---------------------------------------------------------- private methods ------------------------------------------------------------
	private static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			if (keybyte.length < 24) {
				keybyte = SecurityUtils.convertKey(keybyte);
			}
			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM_3DES);
			// 加密
			Cipher c1 = Cipher.getInstance(ALGORITHM_3DES);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区

	private static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			//System.out.println("key length: " + keybyte.length);
			if (keybyte.length < 24) {
				keybyte = SecurityUtils.convertKey(keybyte);
			}
			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM_3DES);
			Cipher c1 = Cipher.getInstance("DESede/ECB/NoPadding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// 将16字节的密钥转换成24字节
	private static byte[] convertKey(byte[] srcKey) {
		byte[] destKey = null;
		byte[] keyFirst = new byte[8];
		ByteBuffer buffer = ByteBuffer.wrap(srcKey);
		buffer.get(keyFirst);
		buffer.clear();
		buffer = ByteBuffer.allocate(24);
		buffer.put(srcKey);
		buffer.put(keyFirst);
		buffer.flip();
		destKey = buffer.array();
		buffer.clear();
		return destKey;
	}

	/*private static String byteArray2HexString(byte[] arr) {
		StringBuilder sbd = new StringBuilder();
		for (byte b : arr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2)
				tmp = "0" + tmp;
			sbd.append(tmp);
		}
		return sbd.toString();
	}*/
	
	private static String byteArray2HexString(byte[] byteArr) {
    	if(byteArr == null || byteArr.length == 0){
    		return null;
    	}
		StringBuilder sbd = new StringBuilder("");
		for (byte b : byteArr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2){
				sbd.append(0);
			}
			sbd.append(tmp);
		}
		return sbd.toString();
	}

	private static byte[] hexString2ByteArray(String hexStr) {
		if (hexStr == null)
			return null;
		if (hexStr.length() % 2 != 0) {
			return null;
		}
		byte[] data = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			char hc = hexStr.charAt(2 * i);
			char lc = hexStr.charAt(2 * i + 1);
			byte hb = hexChar2Byte(hc);
			byte lb = hexChar2Byte(lc);
			if (hb < 0 || lb < 0) {
				return null;
			}
			int n = hb << 4;
			data[i] = (byte) (n + lb);
		}
		return data;
	}

	private static byte hexChar2Byte(char c) {
		if (c >= '0' && c <= '9')
			return (byte) (c - '0');
		if (c >= 'a' && c <= 'f')
			return (byte) (c - 'a' + 10);
		if (c >= 'A' && c <= 'F')
			return (byte) (c - 'A' + 10);
		return -1;
	}
	
}
