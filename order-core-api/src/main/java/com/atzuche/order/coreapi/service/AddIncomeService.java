package com.atzuche.order.coreapi.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountownerincome.service.AddIncomeExcelService;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.exceptions.ImportAddIncomeExcelException;
import com.atzuche.order.coreapi.entity.dto.MemberSimpleDTO;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddIncomeService {

	@Autowired
	private AddIncomeExcelService addIncomeExcelService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	
	private static final Integer RENTER_MEM_TYPE = 0;
	private static final Integer OWNER_MEM_TYPE = 1;
	
	/**
	 * 保存追加收益
	 * @param addIncomeImportDTO
	 */
	public void saveAddIncomeExcel(AddIncomeImportDTO addIncomeImportDTO) {
		log.info("保存追加收益addIncomeImportDTO=[{}]",addIncomeImportDTO);
		List<AddIncomeExcelContextEntity> list = addIncomeImportDTO.getContentList();
		for (int i=0;i<list.size();i++) {
			AddIncomeExcelContextEntity context = list.get(i);
			String orderNo = context.getOrderNo();
			Integer memNo = context.getMemNo();
			Long mobile = context.getMobile();
			Integer memType = context.getMemType();
			if (StringUtils.isNotBlank(orderNo)) {
				
			}
		}
	}
	
	
	public MemberSimpleDTO getMemberSimpleDTOByOrderNo(String orderNo, Integer memType, int i) {
		MemberSimpleDTO memberSimpleDTO = null;
		if (RENTER_MEM_TYPE.equals(memType)) {
			RenterMemberEntity renter = renterMemberService.getRenterMemberByOrderNo(orderNo);
			if (renter == null) {
				throw new ImportAddIncomeExcelException("第"+(i+1)+"行数据,订单号、会员号、手机号不正确！");
			}
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(renter.getMemNo()), renter.getRealName(), Long.valueOf(renter.getPhone()));
		} else {
			// 获取修改前有效车主订单信息
			OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
			if (ownerOrderEntity == null) {
				throw new ImportAddIncomeExcelException("第"+(i+1)+"行数据,订单号、会员号、手机号不正确！");
			}
			OwnerMemberDTO owner = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo(), false);
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(owner.getMemNo()), owner.getRealName(), Long.valueOf(owner.getPhone()));
		}
		return memberSimpleDTO;
	}
}
