package com.atzuche.order.commons.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * @Author ZhangBin
 * @Date 2019/12/19 11:28
 * @Description: 会员权益-内部员工权益值
 *
 **/
@Data
@AllArgsConstructor
public class MemberRightStaffDTO {
    private int carDeposit;
    private int wzDeposit;


}
