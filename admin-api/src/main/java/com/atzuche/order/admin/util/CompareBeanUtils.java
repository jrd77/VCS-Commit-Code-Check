package com.atzuche.order.admin.util;


import lombok.extern.slf4j.Slf4j;
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
 * 比较对象之间属性是否变化，并以指定标签返回变化内容
 *
 * @author pengcheng.fu
 * @date 2020/04/22 11:32
 */
@Slf4j
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
    private Map<Class, PropertyEditor> propEditorMap = new HashMap<>();


    public CompareBeanUtils() {

    }

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
        propEditorMap.put(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }


    /**
     * 初始化
     *
     * @param newObj 修改后的数据
     * @return CompareBeanUtils
     */
    public static <T> CompareBeanUtils<T> newInstance(T newObj) {
        if (null == newObj) {
            return null;
        }
        Class clazz = newObj.getClass();
        return new CompareBeanUtils<T>(clazz, newObj);
    }


    /**
     * 初始化
     *
     * @param oldObj 修改前的数据
     * @param newObj 修改后的数据
     * @return CompareBeanUtils
     */
    public static <T> CompareBeanUtils<T> newInstance(T oldObj, T newObj) {
        if (null == oldObj && null == newObj) {
            return null;
        }
        Class clazz;
        clazz = (newObj == null ? oldObj.getClass() : newObj.getClass());
        return new CompareBeanUtils<T>(clazz, oldObj, newObj);
    }


    /**
     * 数据比较
     *
     * @param fieldName   数据对应的属性字段名
     * @param fieldChName 数据对应的属性字段中文名
     */
    public void compare(String fieldName, String fieldChName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object newValue = field.get(newObject == null ? oldObject : newObject);
            if (isNew) {
                if (!isNullOrEmpty(newValue)) {
                    comparedIsAdd(fieldChName, newValue);
                }
            } else {
                if (null == oldObject) {
                    return;
                }
                if (null == newObject) {
                    comparedIsdel(fieldChName);
                    return;
                }
                Object oldValue = field.get(oldObject);
                if (notEquals(oldValue, newValue)) {
                    comparedIsEdit(fieldChName, oldValue, newValue);
                }
            }
        } catch (Exception e) {
           log.error("字段值对比异常.fieldName:[{}],fieldChName:[{}]", fieldName, fieldChName, e);
        }
    }


    /**
     * 数据比较
     *
     * @param fieldName 数据对应的属性字段名
     */
    public void compare(String fieldName) {
        if (null == oldObject || null == newObject) {
            return;
        }
        String fieldChName = PropertitesUtil.getFieldChName(fieldName);
        compare(fieldName, StringUtils.isNotBlank(fieldChName) ? fieldChName : fieldName);
    }

    /**
     * 数据比较(全量比较)
     *
     * <p>不可和其他方法(compare)组合使用</p>
     * @return String
     */
    public String compare() {
        if (null == oldObject || null == newObject) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldChName = PropertitesUtil.getFieldChName(field.getName());
            compare(field.getName(), StringUtils.isNotBlank(fieldChName) ? fieldChName : field.getName());
        }

        return toResult();
    }

    /**
     * 数据比较(批量比较)
     *
     * @param fieldNames 字段名集合
     * @return String
     */
    public String compare(String[] fieldNames) {
        if (null == fieldNames || fieldNames.length == 0) {
            return null;
        }
        for (String fieldName : fieldNames) {
            compare(fieldName);
        }

        return toResult();
    }

    /**
     * 数据比较(批量比较)
     *
     * @param fieldNames 字段名集合
     * @return String
     */
    public String compare(String[] fieldNames, String[] fieldChNames) {
        if (null == fieldNames || fieldNames.length == 0) {
            return null;
        }

        if (null == fieldChNames || fieldChNames.length == 0) {
            return null;
        }

        if (fieldNames.length != fieldChNames.length) {
            return null;
        }

        for (int i = 0; i < fieldNames.length; i++) {
            compare(fieldNames[i], fieldChNames[i]);
        }
        return toResult();
    }


    /**
     * 获取更新内容
     *
     * @return String
     */
    public String toResult() {
        return content.toString();
    }


    /**
     * 改前后知否相同
     *
     * @param oldValue 修改前的值
     * @param newValue 修改后的值
     * @return boolean
     */
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

    /**
     * 记录新值
     *
     * @param fieldChName 字段标题
     * @param newValue    修改后的值
     */
    private void comparedIsAdd(String fieldChName, Object newValue) {
        content.append("新增[");
        content.append(fieldChName);
        content.append("],值为:");
        content.append(format(newValue));
        content.append(";");
    }

    /**
     * 数据删除
     *
     * @param fieldChName 字段标题
     */
    private void comparedIsdel(String fieldChName) {
        content.append("删除[");
        content.append(fieldChName);
        content.append("]");
        content.append(";");
    }

    /**
     * 记录变更数据
     *
     * @param fieldChName 字段标题
     * @param oldValue    修改前的值
     * @param newValue    修改后的值
     */
    private void comparedIsEdit(String fieldChName, Object oldValue, Object newValue) {

        content.append("将【");
        content.append(fieldChName);
        content.append("】");
        content.append("从'");
        content.append(format(oldValue));
        content.append("'改成'");
        content.append(format(newValue));
        content.append("';");
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
        if (Objects.isNull(val)) {
            return true;
        }

        if (val instanceof String) {
            return (StringUtils.isEmpty(String.valueOf(val)));
        } else {
            return false;
        }
    }

}
