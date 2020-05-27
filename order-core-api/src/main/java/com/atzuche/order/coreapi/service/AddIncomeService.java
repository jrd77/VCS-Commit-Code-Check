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
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.autoyol.member.detail.vo.res.MemberCoreInfo;

@Service
public class AddIncomeService {

	@Autowired
	private AddIncomeExcelService addIncomeExcelService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	@Autowired
	private MemProxyService memProxyService;
	
	private static final Integer RENTER_MEM_TYPE = 0;
	
	/**
	 * 保存追加收益
	 * @param addIncomeImportDTO
	 */
	public void saveAddIncomeExcel(AddIncomeImportDTO addIncomeImportDTO) {
		List<AddIncomeExcelContextEntity> list = addIncomeImportDTO.getContentList();
		for (int i=0;i<list.size();i++) {
			AddIncomeExcelContextEntity context = list.get(i);
			String orderNo = context.getOrderNo();
			Integer memNo = context.getMemNo();
			Long mobile = context.getMobile();
			Integer memType = context.getMemType();
			MemberSimpleDTO memberSimpleDTO = null;
			if (StringUtils.isNotBlank(orderNo)) {
				memberSimpleDTO = getMemberSimpleDTOByOrderNo(orderNo, memType, i);
			} else if (memNo != null) {
				memberSimpleDTO = getMemberSimpleDTOByMemNo(memNo, i);
			} else if (mobile != null) {
				memberSimpleDTO = getMemberSimpleDTOByMobile(mobile, i);
			}
			if (memberSimpleDTO != null) {
				context.setMemNo(memberSimpleDTO.getMemNo());
				context.setMobile(memberSimpleDTO.getMobile());
				context.setName(memberSimpleDTO.getName());
			}
		}
		addIncomeExcelService.saveAddIncomeExcel(addIncomeImportDTO);
	}
	
	
	/**
	 * 根据订单号获取会员信息
	 * @param orderNo
	 * @param memType
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByOrderNo(String orderNo, Integer memType, int index) {
		MemberSimpleDTO memberSimpleDTO = null;
		if (RENTER_MEM_TYPE.equals(memType)) {
			RenterMemberEntity renter = renterMemberService.getRenterMemberByOrderNo(orderNo);
			if (renter == null) {
				throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,订单号不正确！");
			}
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(renter.getMemNo()), renter.getRealName(), Long.valueOf(renter.getPhone()));
		} else {
			// 获取修改前有效车主订单信息
			OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
			if (ownerOrderEntity == null) {
				throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,订单号不正确！");
			}
			OwnerMemberDTO owner = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo(), false);
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(owner.getMemNo()), owner.getRealName(), Long.valueOf(owner.getPhone()));
		}
		return memberSimpleDTO;
	}
	
	
	/**
	 * 根据会员号获取会员信息
	 * @param memNo
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByMemNo(Integer memNo, int index) {
		MemberCoreInfo memberCoreInfo = memProxyService.getMemberCoreInfoByMemNo(memNo);
		if (memberCoreInfo == null) {
			throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,会员号不正确！");
		}
		MemberSimpleDTO memberSimpleDTO = new MemberSimpleDTO(memNo, memberCoreInfo.getRealName(), memberCoreInfo.getMobile());
		return memberSimpleDTO;
	}
	
	
	/**
	 * 根据手机号获取会员信息
	 * @param mobile
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByMobile(Long mobile, int index) {
		Integer memNo = memProxyService.getMemNoByMoileEx(mobile);
		if (memNo == null) {
			throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,手机号不正确！");
		}
		return getMemberSimpleDTOByMemNo(memNo, index);
	}
}
