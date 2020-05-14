package com.atzuche.order.coreapi.entity.vo;

import java.util.List;

import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;

import lombok.Data;

@Data
public class RenterAndConsoleFineVO {

	private List<ConsoleRenterOrderFineDeatailEntity> consoleFineList;
	private List<RenterOrderFineDeatailEntity> fineList;
}
