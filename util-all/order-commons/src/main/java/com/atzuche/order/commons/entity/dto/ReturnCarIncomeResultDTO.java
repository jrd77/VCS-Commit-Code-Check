package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.List;

/**
 * Created by dongdong.zhao on 2018/10/11.
 */
public class ReturnCarIncomeResultDTO{

    @AutoDocProperty(value = "结算方式信息")
    private List<ReturnCarIncomeDTO> returnCarIncomeDTOList;

    @AutoDocProperty(value = "提示文案,如为了避免纠纷,请主动与租客进行友好协商,有利于再次成单哦")
    private String noticeText;
}
