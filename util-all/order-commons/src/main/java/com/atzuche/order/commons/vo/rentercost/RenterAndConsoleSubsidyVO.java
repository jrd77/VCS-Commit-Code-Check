package com.atzuche.order.commons.vo.rentercost;

import java.util.List;

import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderSubsidyDetailEntity;

import lombok.Data;

@Data
public class RenterAndConsoleSubsidyVO {

	private List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList;
	private List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList;
}
