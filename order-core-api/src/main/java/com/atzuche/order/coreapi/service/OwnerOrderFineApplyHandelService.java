package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.DispatcherStatusEnum;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车主取消罚金后续处理
 *
 * @author pengcheng.fu
 * @date 2020/1/15 10:21
 */

@Service
public class OwnerOrderFineApplyHandelService {

    private static Logger logger = LoggerFactory.getLogger(OwnerOrderFineApplyHandelService.class);

    @Autowired
    private OwnerOrderFineApplyService ownerOrderFineApplyService;
    @Autowired
    private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;

    @Transactional(rollbackFor = Exception.class)
    public boolean handleFineApplyRecord(String orderNo, DispatcherStatusEnum dispatcherStatus) {
        logger.info("Handle owner order fine apply record. param is,orderNo:[{}],dispatcherStatus:[{}]",
                orderNo, dispatcherStatus);

        OwnerOrderFineApplyEntity ownerOrderFineApplyEntity = ownerOrderFineApplyService.getByOrderNo(orderNo);
        if (null == ownerOrderFineApplyEntity) {
            logger.warn("Not fund ownerOrderFineApplyEntity. orderNo:[{}]", orderNo);
            return false;
        }

        if (null == dispatcherStatus) {
            logger.warn("Dispatcher status is empty. orderNo:[{}]", orderNo);
            return false;
        }

        OwnerOrderFineDeatailEntity entity = new OwnerOrderFineDeatailEntity();
        entity.setOrderNo(ownerOrderFineApplyEntity.getOrderNo());
        entity.setOwnerOrderNo(ownerOrderFineApplyEntity.getOwnerOrderNo());
        entity.setMemNo(ownerOrderFineApplyEntity.getMemNo().toString());
        entity.setFineAmount(-Math.abs(ownerOrderFineApplyEntity.getFineAmount()));
        entity.setFineType(ownerOrderFineApplyEntity.getFineType());
        entity.setFineTypeDesc(ownerOrderFineApplyEntity.getFineTypeDesc());
        entity.setFineSubsidySourceCode(ownerOrderFineApplyEntity.getFineSubsidySourceCode());
        entity.setFineSubsidySourceDesc(ownerOrderFineApplyEntity.getFineSubsidySourceDesc());
        //todo:处理罚金补贴信息
        if (DispatcherStatusEnum.DISPATCH_SUCCESS.getCode() == dispatcherStatus.getCode()) {
            //调度成功,罚金补贴给平台
            entity.setFineSubsidyCode(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyCode());
            entity.setFineSubsidyDesc(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyDesc());
        } else if (DispatcherStatusEnum.DISPATCH_FAIL.getCode() == dispatcherStatus.getCode()) {
            //调度失败,罚金补贴给租客
            entity.setFineSubsidyCode(FineSubsidyCodeEnum.RENTER.getFineSubsidyCode());
            entity.setFineSubsidyDesc(FineSubsidyCodeEnum.RENTER.getFineSubsidyDesc());
            //租客收益信息处理
            consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(null);
        } else {
            logger.warn("Dispatcher status is invalid. orderNo:[{}],dispatcherStatus:[{}]", orderNo, dispatcherStatus);
            return false;
        }

        //todo:处理车主罚金信息
        ownerOrderFineDeatailService.addOwnerOrderFineRecord(entity);

        //todo:删除罚金请求信息
        return ownerOrderFineApplyService.setInvalid(ownerOrderFineApplyEntity.getId()) > 0;
    }


}
