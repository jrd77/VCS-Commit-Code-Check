package com.github.jrd77.codecheck.data;

import com.github.jrd77.codecheck.util.Assert;
import com.github.jrd77.codecheck.util.PropsUtil;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * InternationalUtil
 * 国际化语言工具
 */
public class InterUtil {

    private static Logger logger = Logger.getLogger(InterUtil.class.getName());

    private static final String DEFAULT_LANGUAGE_ZH ="language/zh.properties";
    private static final String DEFAULT_LANGUAGE_EN ="language/en.properties";
    private static final String DEFAULT_LANGUAGE ="zh";
    private static PropsUtil.Props INTERNATIONAL_PROPS =null;
    static {
        Locale aDefault = Locale.getDefault();
        String pluginPath = getPluginPath();
        if(aDefault.getLanguage().equals(DEFAULT_LANGUAGE)){
            INTERNATIONAL_PROPS=PropsUtil.getProperties(pluginPath+DEFAULT_LANGUAGE_ZH);
        }else{
            INTERNATIONAL_PROPS=PropsUtil.getProperties(pluginPath+DEFAULT_LANGUAGE_EN);
        }
    }

    /**
     * 获取国际化提示
     * @param name
     * @return
     */
    public static String getValue(String name){

        Assert.notNull(INTERNATIONAL_PROPS,"can't get language tips file");
        return INTERNATIONAL_PROPS.getProperty(name);
    }


    private static String getPluginPath(){

        String pluginProject = InterUtil.class.getResource("/").getPath();
        logger.info(String.format("the plugin path is %s",pluginProject));
        return pluginProject;
    }
}
