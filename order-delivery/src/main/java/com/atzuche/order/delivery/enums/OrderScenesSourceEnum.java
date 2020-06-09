package com.atzuche.order.delivery.enums;

/**
 * @author 胡春林
 * 订单场景
 */
public enum OrderScenesSourceEnum {

    SHOUCHANG("EX001", "收藏"),
    WANGZHAN("EX003", "网站"),
    JINGCAI_PINDAO("EX004", "精彩频道"),
    LISHI_DINGDAN("EX005", "历史订单"),
    HOUTAI_GUANLI("EX007", "后台管理系统"),
    DAIYONG("EX008", "待用"),
    PUTONG("EX009", "H5-普通"),
    H5_YIJIANZUCHE("EX010", "H5-一键租车"),
    CHUXIANDAIBU("EX011", "H5-出险代步车"),
    ZHONGDAOJIUYUAN("EX012", "H5-中道救援套餐"),
    KUAIJIEZUCHE("EX021", "新快捷租车"),
    ANLIANDAIBUCHE("EX022", "h5-安联代步车"),
    LIPINKA("LPK001", "礼品卡套餐"),
    ZIJIACHUYOU("S001", "自驾出游"),
    SHANGWUJIEDAI("S002", "商务接待"),
    YUEHUI("S003", "约会"),
    TIYANBUTONGCHE("S004", "体验不同车"),
    SHIJIADINGDAN("SJ001", "试驾订单"),
    YIJIANZUCHE("U001", "一键租车"),
    JINGZHUNZHAOCHE("U002", "精准找车"),
    SUIBIANKANKAN("U003", "随便看看"),
    SHOUYESHOUSUO("U005", "首页搜索"),
    FENGYUNBANG("U006", "首页车型风云榜"),
    CHENGSHITUIJIAN("U007", "首页热门城市推荐"),
    SHANZU("U008", "24小时闪租"),
    CHEXINGTAOCANG("U009", "精选车型套餐"),
    ZIZHUZHAOCHE("U010", "自主找车"),
    GEXINGSHOUCHE("U011", "个性车搜索"),
    TEJIACHE("U012", "特价车"),
    XIANGSHICHELIANG("U013", "相似车辆"),
    CHEZHUYOUHUIQUAN("U014", "车主优惠券"),
    WEINITUIJIANDINGDAN("U015", "为你推荐-订单"),
    WEINITUIJIANSHOUYE("U016", "为你推荐-首页"),
    TEJIACHEGONGGE("U017", "特价车-宫格"),
    CHANGZUTEJIACHE("U019", "长租特价车"),
    REMENTUIJIANSHOUYE("U020", "热门推荐-首页"),;

    private String value;

    private String name;

    OrderScenesSourceEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getOrderScenesSource(String value) {
        for (OrderScenesSourceEnum orderScenesSourceEnum : values()) {
            if (orderScenesSourceEnum.getValue().equals(value)) {
                return orderScenesSourceEnum.getName();
            }
        }
        return "";
    }

}
