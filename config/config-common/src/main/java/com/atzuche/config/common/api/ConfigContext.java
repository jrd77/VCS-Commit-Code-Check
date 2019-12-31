package com.atzuche.config.common.api;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/26 4:38 下午
 **/
public interface ConfigContext {
    /**
     * 查看该上下文是否需要请求预发环境的配置
     * @return
     */
    boolean preConfig();
}
