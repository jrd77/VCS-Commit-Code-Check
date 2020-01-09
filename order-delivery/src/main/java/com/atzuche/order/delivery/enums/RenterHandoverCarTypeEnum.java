package com.atzuche.order.delivery.enums;

/**
 * @author 胡春林
 * 交接车类型 1-车主向租客交车、2-租客向车主交车、3-车管家向租客交车、4-租客向车管家交车
 */
public enum RenterHandoverCarTypeEnum {

    OWNER_TO_RENTER(1, "车主向租客交车"),
    RENTER_TO_OWNER(2, "租客向车主交车"),
    RENYUN_TO_RENTER(3, "车管家向租客交车"),
    RENTER_TO_RENYUN(4, "租客向车管家交车");

    private Integer value;

    private String name;

    RenterHandoverCarTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
