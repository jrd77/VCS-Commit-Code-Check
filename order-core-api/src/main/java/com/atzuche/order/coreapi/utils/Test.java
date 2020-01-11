package com.atzuche.order.coreapi.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderAppReq;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;

public class Test {

	public static void main(String[] args) {
		ModifyOrderAppReq modifyOrderAppReq = new ModifyOrderAppReq();
		modifyOrderAppReq.setGetCarAddress("xxxxxxx");
		List<String> driverIds = new ArrayList<String>();
		driverIds.add("a");
		driverIds.add("f");
		modifyOrderAppReq.setDriverIds(driverIds);
		ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
		BeanUtils.copyProperties(modifyOrderAppReq, modifyOrderReq);
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<>();
		RenterOrderSubsidyDetailDTO sub1 = new RenterOrderSubsidyDetailDTO();
		sub1.setOrderNo("21332332");
		sub1.setSubsidyAmount(90);
		renterSubsidyList.add(sub1);
		RenterOrderSubsidyDetailDTO sub2 = new RenterOrderSubsidyDetailDTO();
		sub2.setOrderNo("21332332");
		sub2.setSubsidyAmount(80);
		renterSubsidyList.add(sub2);
		modifyOrderReq.setRenterSubsidyList(renterSubsidyList);
		ModifyOrderDTO modifyOrderDTO = new ModifyOrderDTO();
		BeanUtils.copyProperties(modifyOrderReq, modifyOrderDTO);
		System.out.println(modifyOrderDTO);
	}
}
