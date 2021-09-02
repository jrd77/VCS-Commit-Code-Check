package com.github.jrd77.codecheck.data.model;

import java.util.Map;

/**
 * @author zhen.wang
 * @description 解析接口
 * @date 2021/8/19 17:18
 */
@Deprecated
public interface ModelJsonParser{

     public ModelJsonParser mapToBean(Map<String,Object> map);
}
