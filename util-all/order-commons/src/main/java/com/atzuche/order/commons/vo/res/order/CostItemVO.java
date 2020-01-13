package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * 费用信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class CostItemVO {

    @AutoDocProperty(value = "费用项key值,如:车辆租金:rentAmt")
    private String itemKey;

    @AutoDocProperty(value = "费用项名称,如:车辆租金、上门送取服务费、基本保险费等")
    private String itemName;

    @AutoDocProperty(value = "费用项小i跳转url,绝对路径,如:https://www.baidu.com")
    private String itemHintUrl;

    @AutoDocProperty(value = "费用项计算公式,如:40元*1.13天")
    private String itemFormula;

    @AutoDocProperty(value = "费用项值,如:60")
    private String itemValue;

    @AutoDocProperty(value = "费用项类型,1:普通项,2:有开关按钮")
    private String itemType;

    @AutoDocProperty(value = "开关是否打开,itemType为2时才有,0:关闭,1:打开")
    private String itemButtonValue;


    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemHintUrl() {
        return itemHintUrl;
    }

    public void setItemHintUrl(String itemHintUrl) {
        this.itemHintUrl = itemHintUrl;
    }

    public String getItemFormula() {
        return itemFormula;
    }

    public void setItemFormula(String itemFormula) {
        this.itemFormula = itemFormula;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemButtonValue() {
        return itemButtonValue;
    }

    public void setItemButtonValue(String itemButtonValue) {
        this.itemButtonValue = itemButtonValue;
    }
}
