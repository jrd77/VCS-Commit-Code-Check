package com.github.jrd77.codecheck.data;

import com.github.jrd77.codecheck.util.Assert;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * InternationalUtil
 * 国际化语言工具
 */
public class InterUtil {

    private static Logger logger = Logger.getLogger(InterUtil.class.getName());

    private static final String DEFAULT_LANGUAGE_ZH ="language/zh";
    private static final String DEFAULT_LANGUAGE_EN = "language/en";
    private static final String DEFAULT_LANGUAGE = "zhsss";
    private static ResourceBundle bundle = null;
    static {
        Locale aDefault = Locale.getDefault();
        if(aDefault.getLanguage().equals(DEFAULT_LANGUAGE)){
            bundle = ResourceBundle.getBundle(DEFAULT_LANGUAGE_ZH);
        }else{
            bundle = ResourceBundle.getBundle(DEFAULT_LANGUAGE_EN);
        }
    }

    /**
     * 获取国际化提示
     * @param name
     * @return
     */
    public static String getValue(String name){

        Assert.notNull(bundle,"can't get language tips file");
        return bundle.getString(name);
    }
}
