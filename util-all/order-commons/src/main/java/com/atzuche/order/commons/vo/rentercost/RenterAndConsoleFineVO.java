package com.atzuche.order.commons.vo.rentercost;

import java.util.List;

import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderFineDeatailEntity;

import lombok.Data;

@Data
public class RenterAndConsoleFineVO {

	private List<ConsoleRenterOrderFineDeatailEntity> consoleFineList;
	private List<RenterOrderFineDeatailEntity> fineList;
}
