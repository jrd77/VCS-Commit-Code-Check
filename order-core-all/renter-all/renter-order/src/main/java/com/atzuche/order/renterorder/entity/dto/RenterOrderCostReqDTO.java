package com.atzuche.order.renterorder.entity.dto;

import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RenterOrderCostReqDTO implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8665130129924386005L;
	/**
     * 基本信息
     */
    private CostBaseDTO costBaseDTO;
    /**
     * 租车费用参数
     */
    private RentAmtDTO rentAmtDTO;

    /**
     * 平台保障费参数
     */
    private InsurAmtDTO insurAmtDTO;

    /**
     * 全面保障费参数
     */
    private AbatementAmtDTO abatementAmtDTO;

    /**
     * 附加驾驶人险
     */
    private ExtraDriverDTO extraDriverDTO;

    /**
     * 超里程费用参数
     */
    private MileageAmtDTO mileageAmtDTO;
    /**
     * 取还车费用参数
     */
    private GetReturnCarCostReqDto getReturnCarCostReqDto;
    /**
     * 超运能费用参数
     */
    private GetReturnCarOverCostReqDto getReturnCarOverCostReqDto;
    
    /**
     * 费用对应的补贴记录
     */
    private List<RenterOrderSubsidyDetailDTO> subsidyOutList;

}
