package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.vo.req.handover.rep.HandoverCarRespVO;
import com.atzuche.order.commons.vo.req.handover.rep.TransProgressListResVO;
import com.atzuche.order.commons.vo.req.handover.rep.TransProgressResVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.delivery.HandoverProVO;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/handover")
public class HandoverCarOilController {
    
    private final static Logger logger = LoggerFactory.getLogger(HandoverCarOilController.class);
    

    @Autowired
    HandoverCarInfoService handoverCarInfoService;
    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 更新油耗里程
     * @param handoverCarReqVO
     * @return
     */
    @PostMapping("/oil/setOil")
    public ResponseData<?> updateHandoverCarInfo(@RequestBody HandoverCarInfoReqVO handoverCarReqVO) {

        logger.info("updateHandoverCarInfo param is {}",handoverCarReqVO);
        handoverCarInfoService.updateHandoverCarInfo(handoverCarReqVO);
        return ResponseData.success();

    }

    @GetMapping("/oil/list")
    public ResponseData<HandoverCarRespVO> updateHandoverCarInfo(@RequestParam("orderNo") String  orderNo)
    {
        logger.info("updateHandoverCarInfo param is {}",orderNo);
        HandoverCarRespVO handoverCarRespVO = handoverCarService.getHandoverCarInfoByOrderNo(orderNo);
        return ResponseData.success(handoverCarRespVO);


    }

    @GetMapping("/trans/progress")
    public ResponseData<TransProgressListResVO> queryProgress(@RequestParam("orderNo") String  orderNo, @RequestParam("userType") Integer  userType){
        logger.info("updateHandoverCarInfo param is {}",orderNo);
        HandoverProVO handoverProData = handoverCarService.getHandoverProData(orderNo);
        if(handoverProData == null){
            return ResponseData.success(null);
        }
        TransProgressListResVO resVO = new TransProgressListResVO();
        List<TransProgressResVO> list = new ArrayList<>();
        TransProgressResVO transProgressResVO = null;
        if(userType.equals(1)){
            List<RenterHandoverCarRemarkEntity> renterHandoverCarInfoEntities = handoverProData.getRenterHandoverCarInfoEntities();
            if(!CollectionUtils.isEmpty(renterHandoverCarInfoEntities)){
                for (RenterHandoverCarRemarkEntity renterHandoverCarInfoEntity : renterHandoverCarInfoEntities) {
                    transProgressResVO = new TransProgressResVO();
                    transProgressResVO.setDescription(renterHandoverCarInfoEntity.getRemark());
                    transProgressResVO.setHandleTime(DateUtils.formate(renterHandoverCarInfoEntity.getUpdateTime(),DateUtils.DATE_DEFAUTE1));
                    list.add(transProgressResVO);
                }
            }
        }else{
            List<OwnerHandoverCarRemarkEntity> ownerHandoverCarInfoEntities = handoverProData.getOwnerHandoverCarInfoEntities();
            if(!CollectionUtils.isEmpty(ownerHandoverCarInfoEntities)){
                for (OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity : ownerHandoverCarInfoEntities) {
                    transProgressResVO = new TransProgressResVO();
                    transProgressResVO.setDescription(ownerHandoverCarRemarkEntity.getRemark());
                    transProgressResVO.setHandleTime(DateUtils.formate(ownerHandoverCarRemarkEntity.getUpdateTime(),DateUtils.DATE_DEFAUTE1));
                    list.add(transProgressResVO);
                }
            }
        }
        resVO.setList(list);
        return ResponseData.success(resVO);
    }



}
