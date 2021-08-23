package com.github.jrd77.codecheck.data.model;

import java.util.Map;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/19 17:18
 */
public interface ModelJsonParser{

     public ModelJsonParser mapToBean(Map<String,Object> map);
}
