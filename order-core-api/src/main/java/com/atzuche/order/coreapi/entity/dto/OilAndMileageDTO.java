package com.atzuche.order.coreapi.entity.dto;

import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.autoyol.platformcost.model.FeeResult;
import lombok.Data;

@Data
public class OilAndMileageDTO {

    private RenterGetAndReturnCarDTO renterGetAndReturnCarDTO;

    private FeeResult feeResult;
}
