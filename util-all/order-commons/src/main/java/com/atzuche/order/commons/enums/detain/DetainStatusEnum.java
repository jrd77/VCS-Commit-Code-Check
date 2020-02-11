/**
 * 
 */
package com.atzuche.order.commons.enums.detain;

import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * @author hiabao.yan
 * 暂扣状态 处理状态 1：暂扣 2：取消暂扣
 */
@Getter
public enum DetainStatusEnum {
	/**
     * 暂扣
     **/
    DETAIN(1, "暂扣"),
    /**
     * 取消暂扣
     **/
    DETAIN_CANCEL(2, "取消暂扣"),

    ;

    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;

    DetainStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code取text
     * @param code
     * @return
     */
    public static String getFlagText(String code){
        if(Objects.isNull(code)){
            return null;
        }
        for(DetainStatusEnum detainStatusEnum :DetainStatusEnum.values()){
            if(detainStatusEnum.code.equals(code)){
                return detainStatusEnum.msg;
            }
        }
        return null;
    }
}
