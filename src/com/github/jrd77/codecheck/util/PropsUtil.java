package com.github.jrd77.codecheck.util;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Props工具类<br>
 * 提供静态方法获取配置文件
 *
 * @author looly
 * @since 5.1.3
 */
public class PropsUtil {

	private static Logger logger = Logger.getLogger(PropsUtil.class.getName());
	/**
	 * 配置文件缓存
	 */
	private static final Map<String, Props> propsMap = new ConcurrentHashMap<>();


	public static Props getProperties(String path){

		return Props.create(path);
	}
	public static class Props extends Properties{

		/**
		 * 默认配置文件扩展名
		 */
		public final static String EXT_NAME = "properties";

		public String path;

		/**
		 * 构建一个空的Props，用于手动加入参数
		 *
		 * @return Setting
		 * @since 5.4.3
		 */
		public static Props create(String path) {

			if(propsMap.containsKey(path)){
				return propsMap.get(path);
			}
			Props props = new Props(path);

			try {

				props.load(IoUtil.getReader(new FileReader(new File(path))));
			} catch (IOException e) {
				logger.severe("read internal file failed");
				e.printStackTrace();
			}
			propsMap.put(path,props);
			return props;
		}

		private Props(String path) {
			this.path = path;
		}

	}
}
