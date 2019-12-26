package com.atzuche.order.rentercost.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetFbcFeeRequestDTO {
    private List<GetFbcFeeRequestDetailDTO> req;
}