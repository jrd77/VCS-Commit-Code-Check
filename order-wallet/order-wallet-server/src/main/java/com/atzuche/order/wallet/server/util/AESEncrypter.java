package com.atzuche.order.wallet.server.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @Author zg
 * @Date 2014年3月26日
 * @Comments （美国软件出口限制，AES算法，秘钥长度大于128位时需替换对应jdk版本的policy文件： ${java_home}/jre/lib/security/local_policy.jar 和 ${java_home}/jre/lib/security/US_export_policy.jar）
 */
public class AESEncrypter {
	private static final String ENCODEING = "UTF-8";
	private static final String ALGORITHM = "AES";
	private static final String KEY = "Ym4oFkIUJH7Pri6r9YJdYjQKAVA6KyIZUFRyxs6WQGYLoMbg4a";
	
	private static Cipher cipher_encrypt = null;//加密密码器
	private static Cipher cipher_decrypt = null;//解密密码器
	
	static{
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
	        secureRandom.setSeed(KEY.getBytes());
	        kgen.init(128, secureRandom); 
	        //kgen.init(256, secureRandom);//256位长度秘钥
			SecretKey secretKey = kgen.generateKey();
			byte[] secretKeyEncoded = secretKey.getEncoded();
			SecretKeySpec sks = new SecretKeySpec(secretKeyEncoded, ALGORITHM);
			cipher_encrypt = Cipher.getInstance(ALGORITHM);
			cipher_encrypt.init(Cipher.ENCRYPT_MODE, sks);
			cipher_decrypt = Cipher.getInstance(ALGORITHM);
			cipher_decrypt.init(Cipher.DECRYPT_MODE, sks);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加密
	 * @param content 需要加密的内容
	 * @return
	 */
	public static String encrypt(String content) throws Exception{
			byte[] result = cipher_encrypt.doFinal(content.getBytes(ENCODEING));
			return  Base64.encodeBase64String(result);
	}
	
	/**
	 * 解密
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content) throws Exception {
			byte[] result =  cipher_decrypt.doFinal(Base64.decodeBase64(content));
			return new String(result,ENCODEING);
	}
	
	/**
	 * 对字符串数组里的字符串加密
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String[] encryptStrArray(String ...content) throws Exception{
		for (int i = 0; i < content.length; i++) {
			byte[] result = cipher_encrypt.doFinal(content[i].getBytes(ENCODEING));
			String encryptedStr = Base64.encodeBase64String(result);
			content[i] = encryptedStr;
		}
		return content;
	}


	public static void main(String[] args) throws Exception {
//		String content = "习近平签署主席令公布修改五部法律";  //B821384ED84BC0EDD12E2B4DFBE93F4BDD2ACA552E18FDF09F106A41DF506CB8
//		String content = "605113196";//"上海路享信息科技有限公司"; //605113196
//		String content = "常州鑫田汽车销售服务有限公司";
		String content = "宋彩霞";
		//加密   
		System.out.println("加密前：" + content);  
		String codeStr = encrypt(content);
		System.out.println("加密后：" + codeStr);  
		System.out.println("加密后 length：" + codeStr.getBytes("UTF-8").length);
		//解密   
		String decryptResult = decrypt(codeStr);  
		System.out.println("解密后：" + decryptResult);
//		System.out.println(decrypt("SHCv6NC42nayJugZiKklEtFTSscKbd5chaMdAbBqtZA="));
//		System.out.println("done!");
//		System.out.println("------------------------");
//		System.out.println(decrypt("0GzavxdL/POMHTdHIVB0ir5fZXMhIxN9iIROqRIPVKw="));
//		System.out.println(decrypt("ysLCB2n18sN2JhJ7QwBv0zRkFcFa+8UBc4wYoJE+bd1jehXG6H/7fC0rbEyDXxPY"));
//		System.out.println(decrypt("v6z6rUfElvTFDSpSRmsQXJjx8oe32TcdhPIn4P1V/GY="));
//		
//		System.out.println("------------------------");
//		String ss = "128908496310106"; //"6222021202001621406";
//		System.out.println(encrypt(ss));
//		String decryptResult2 = decrypt("7xUn7mr08auJgn81jW++9g==");  
//		System.out.println("解密后2：" + decryptResult2);
//		if(ss.equals(decryptResult2)){
//			System.out.println("11111");
//		}else{
//			System.out.println("22222");
//		}
	}

}
