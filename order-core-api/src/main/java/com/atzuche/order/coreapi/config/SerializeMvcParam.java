package com.atzuche.order.coreapi.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializeMvcParam {
    /*@Bean
    public Converter<String, LocalDateTime> LocalDateTimeConvert() {
        return source -> {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = null;
            try {
                date = LocalDateTime.parse(source,df);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return date;
        };
    }*/
}
