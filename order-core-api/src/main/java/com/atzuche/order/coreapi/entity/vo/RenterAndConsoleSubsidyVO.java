package com.atzuche.order.coreapi.entity.vo;

import java.util.List;

import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;

import lombok.Data;

@Data
public class RenterAndConsoleSubsidyVO {
	private List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList;
	private List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList;
}
