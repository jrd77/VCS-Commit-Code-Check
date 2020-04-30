package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdditionalDriverService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private MemProxyService memberService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    private RenterAdditionalDriverService renterAdditionalDriverService;

    /**
     * 新增附加驾驶人
     * @param renterCostReqVO
     * @return
     * @throws Exception
     */
    @Transactional
    public void insertAdditionalDriver(AdditionalDriverInsuranceIdsReqVO renterCostReqVO){
        //根据订单号查询会员号
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new OrderNotFoundException(renterCostReqVO.getOrderNo());
        }
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renterCostReqVO.getOrderNo());
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus() || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()){
            log.error("已经结算不允许编辑orderNo={}",renterCostReqVO.getOrderNo());
            throw new NotAllowedEditException();
        }
        //封装对象
        ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
        costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
        costBaseDTO.setStartTime(orderEntity.getExpRentTime());
        costBaseDTO.setEndTime(orderEntity.getExpRevertTime());
        extraDriverDTO.setCostBaseDTO(costBaseDTO);

        List<String> driverIds = new ArrayList<String>();
        //参数
        List<CommUseDriverInfoSimpleDTO> listCommUseDriverIds = renterCostReqVO.getListCommUseDriverIds();
        for (CommUseDriverInfoSimpleDTO commUseDriverInfoSimpleDTO : listCommUseDriverIds) {
            driverIds.add(String.valueOf(commUseDriverInfoSimpleDTO.getId()));
        }
        extraDriverDTO.setDriverIds(driverIds);


//		计算费用

        //获取附加驾驶人保险金额
        //封装数据
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);
        extraDriverInsureAmtEntity.setUpdateOp(renterCostReqVO.getUpdateOp());
        extraDriverInsureAmtEntity.setCreateOp(renterCostReqVO.getCreateOp());
        extraDriverInsureAmtEntity.setOperatorId(renterCostReqVO.getOperatorId());

        //添加租客费用.
        int i = renterOrderCostDetailService.saveOrUpdateRenterOrderCostDetail(extraDriverInsureAmtEntity);
        if(i>0) {
            //最后更新  200305 huangjing
            renterOrderCostService.updateExtraDriverInsureAmtEntity(extraDriverInsureAmtEntity);
            log.info("附加驾驶人保险金额SUCCESS");
        }else {
            log.info("附加驾驶人保险金额FAILURE");
        }

        //添加附加驾驶人记录
        //保存附加驾驶人信息
        List<CommUseDriverInfoDTO> commUseDriverList = new ArrayList<CommUseDriverInfoDTO>();

        //租客会员信息
        RenterMemberDTO renterMemberDTO = memberService.getRenterMemberInfo(orderEntity.getMemNoRenter());
        List<CommUseDriverInfoDTO> commUseDriverListRemote = renterMemberDTO.getCommUseDriverList();  //远程调用

        //参数
        for (CommUseDriverInfoSimpleDTO commUseDriverInfoDTO : listCommUseDriverIds) {
            CommUseDriverInfoDTO dto = new CommUseDriverInfoDTO();

            CommUseDriverInfoDTO tmp = getCommUseDriverInfoDTOById(commUseDriverInfoDTO.getId(),commUseDriverListRemote);
            //属性赋值
            if(tmp != null) { //获取身份证号，准驾车型，开始时间和结束时间。该表字段是后面新加的。20200213
                BeanUtils.copyProperties(tmp,dto);
            }
            dto.setId(commUseDriverInfoDTO.getId());
            dto.setRealName(commUseDriverInfoDTO.getRealName());
            dto.setMobile(commUseDriverInfoDTO.getMobile());
            //记录操作人
            dto.setConsoleOperatorName(renterCostReqVO.getUpdateOp());


            commUseDriverList.add(dto); //注意封装数据
        }
        renterAdditionalDriverService.insertBatchAdditionalDriverBeforeDel(renterCostReqVO.getOrderNo(),
                renterCostReqVO.getRenterOrderNo(),driverIds,commUseDriverList);

        log.info("保存附加驾驶人信息SUCCESS");
    }

    private CommUseDriverInfoDTO getCommUseDriverInfoDTOById(Integer id,
                                                             List<CommUseDriverInfoDTO> commUseDriverListRemote) {
        CommUseDriverInfoDTO tmp = null;
        for (CommUseDriverInfoDTO commUseDriverInfoDTO : commUseDriverListRemote) {
            if(id.intValue() == commUseDriverInfoDTO.getId().intValue()) {  //id相等。
                tmp = commUseDriverInfoDTO;
                break;
            }
        }
        return tmp;
    }
}
