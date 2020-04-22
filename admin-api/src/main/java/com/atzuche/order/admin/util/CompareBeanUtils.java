package com.atzuche.order.admin.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 比较对象之间属性是否变化，并以指定标签返回字符串
 *
 * @param <T>
 * @author pengcheng.fu
 * @date 2020/04/22 11:32
 */
public class CompareBeanUtils<T> {

    /**
     * 修改前数据
     */
    private T oldObject;
    /**
     * 修改后数据
     */
    private T newObject;

    /**
     * 数据对象类型
     */
    private Class<T> clazz;

    /**
     * 是否新增
     */
    private boolean isNew = true;

    /**
     * 变更内容
     */
    private StringBuffer content;

    /**
     * 需要对比字段描述
     */
    private Map<Class, PropertyEditor> propEditorMap = new HashMap<Class, PropertyEditor>();

    /**
     * 构造
     *
     * @param clazz     数据对象类型
     * @param oldObject 修改后数据
     * @param newObject 修改前数据
     */
    public CompareBeanUtils(Class<T> clazz, T oldObject, T newObject) {
        this(clazz);
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.isNew = false;
    }

    /**
     * 构造
     *
     * @param clazz     数据对象类型
     * @param newObject 修改后数据
     */
    public CompareBeanUtils(Class<T> clazz, T newObject) {
        this(clazz);
        this.newObject = newObject;
    }

    /**
     * 构造
     *
     * @param clazz 数据对象类型
     */
    protected CompareBeanUtils(Class<T> clazz) {
        super();
        content = new StringBuffer();
        this.clazz = clazz;
        //注册日期类型
        register(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }


    public static <T> CompareBeanUtils<T> newInstance(T newObj) {
        if (null == newObj) {
            return null;
        }
        Class clazz = newObj.getClass();
        return new CompareBeanUtils<T>(clazz, newObj);
    }


    public static <T> CompareBeanUtils<T> newInstance(T oldObj, T newObj) {
        if (null == oldObj && null == newObj) {
            return null;
        }
        Class clazz;
        clazz = (newObj == null ? oldObj.getClass() : newObj.getClass());
        return new CompareBeanUtils<T>(clazz, oldObj, newObj);
    }

    public <CC> void register(Class<CC> clazz, PropertyEditor pe) {
        propEditorMap.put(clazz, pe);
    }

    public void compare(String prop, String propLabel) {

        try {
            Field field = clazz.getDeclaredField(prop);
            ReflectionUtils.makeAccessible(field);
            Object newValue = field.get(newObject == null ? oldObject : newObject);
            if (isNew) {
                if (!isNullOrEmpty(newValue)) {
                    comparedIsAdd(propLabel, newValue);
                }
            } else {
                if (null == oldObject) {
                    return;
                }
                if (null == newObject) {
                    comparedIsdel(propLabel);
                    return;
                }
                Object oldValue = field.get(oldObject);
                if (notEquals(oldValue, newValue)) {
                    comparedIsEdit(propLabel, oldValue, newValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean notEquals(Object oldValue, Object newValue) {
        String tmpOld, tmpNew;
        if (isNullOrEmpty(oldValue)) {
            tmpOld = "";
        } else {
            tmpOld = oldValue.toString();
        }
        if (isNullOrEmpty(newValue)) {
            tmpNew = "";
        } else {
            tmpNew = newValue.toString();
        }
        return !StringUtils.equals(tmpNew, tmpOld);
    }

    /*
    记录新值
     */
    private void comparedIsAdd(String propLabel, Object newValue) {
        content.append("创建了[");
        content.append(propLabel);
        content.append("],新值为:");
        content.append(format(newValue));
        content.append(";");
    }

    /**
     * 数据删除
     *
     * @param propLabel 字段标题
     */
    private void comparedIsdel(String propLabel) {
        content.append("删除了[");
        content.append(propLabel);
        content.append("]");
        content.append(";");
    }

    /**
     * 记录变更数据
     *
     * @param propLabel 字段标题
     * @param oldValue 修改前的值
     * @param newValue 修改后的值
     */
    private void comparedIsEdit(String propLabel, Object oldValue, Object newValue) {

        content.append("修改了[");
        content.append(propLabel);
        content.append("],");
        content.append("旧值为:");
        content.append(format(oldValue));
        content.append(",新值为:");
        content.append(format(newValue));
        content.append(";");
    }

    /**
     * 格式化数据类型
     *
     * @param obj 数据
     * @return Object
     */
    private Object format(Object obj) {
        if (isNullOrEmpty(obj)) {
            return "";
        }
        Class clz = obj.getClass();
        if (propEditorMap.containsKey(clz)) {
            PropertyEditor pe = propEditorMap.get(clz);
            pe.setValue(obj);
            return pe.getAsText();
        } else {
            return obj;
        }
    }

    /**
     * 判断数据是否为null或是空值
     *
     * @param val 数据对象
     * @return boolean
     */
    private boolean isNullOrEmpty(Object val) {
        if(Objects.isNull(val)) {
            return true;
        }

        if (val instanceof String) {
            return (StringUtils.isEmpty(String.valueOf(val)));
        } else {
            return false;
        }
    }

    /**
     * 获取更新内容
     *
     * @return String
     */
    public String toResult() {
        return content.toString();
    }
}
