package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum WzLogOperateTypeEnums {
    OTHER(0,"未知"),
    MANUAL_SINGLE_ADD(1,"人工单条添加"),
    ANUAL_BATCH_IMPORT(2,"人工批量导入"),
    AUTO_QUERY(3,"凹凸自动查询"),
    RENYUN_SYNC_UPDATE(4,"仁云同步更新"),
    ANUAL_DELETE(5,"人工删除"),
    SYSTEM_DISTINCT(6,"系统去重"),
    CONFIRM_HANDLE(7,"确认已处理"),

    ;
    private int code;
    private String desc;

    public static WzLogOperateTypeEnums getRightByCode(Integer code){
        if(code == null){
            return OTHER;
        }
        Optional<WzLogOperateTypeEnums> first = Stream.of(WzLogOperateTypeEnums.values())
                .filter(x -> x.getCode() == code)

                .limit(1)
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }else{
            return OTHER;
        }

    }
}
