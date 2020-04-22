package com.atzuche.order.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.validation.constraints.Min;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author pengcheng.fu
 * @date 2020/4/22 15:49
 */
@Slf4j
public class PropertitesUtil {

    public static Properties props;

    static {
        readPropertiesFile("/fields.properties");
    }

    public static Properties readPropertiesFile(String filePath) {
        log.info("filePath:[{}]",filePath);
        try {
            ClassPathResource classPathResource = new ClassPathResource(filePath);
            InputStream inputStream = classPathResource.getInputStream();
            props = new Properties();
            props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return props;
        } catch (Exception e) {
            log.info("properties file is empty.");
            return null;
        }
    }

    /**
     * 获取字段对应的中文名
     *
     * @param fieldName 字段名称
     * @return String
     */
    public static String getFieldChName(String fieldName) {
        return props.getProperty(fieldName);
    }

}
