package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.AdminDeliveryCarService;
import com.atzuche.order.admin.service.DeliveryRemoteService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.util.CompareBeanUtils;
import com.atzuche.order.commons.exceptions.DeliveryOrderException;
import com.atzuche.order.commons.vo.RenterOwnerSummarySectionDeliveryVO;
import com.atzuche.order.commons.vo.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.delivery.DeliveryCarVO;
import com.atzuche.order.commons.vo.delivery.OwnerGetAndReturnCarDTO;
import com.atzuche.order.commons.vo.delivery.RenterGetAndReturnCarDTO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * @author 胡春林
 * 配送服务接口
 */
@RestController
@RequestMapping("/console/api")
@Slf4j
public class AdminDeliveryCarController extends BaseController {

    @Autowired
    private AdminDeliveryCarService deliveryCarInfoService;
    @Autowired
    private DeliveryRemoteService deliveryRemoteService;
    @Autowired
    private AdminLogService adminLogService;
    /**
     * 获取配送信息
     * @param deliveryCarDTO
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车配送服务信息")
    @AutoDocGroup(group = "管理后台取还车配送服务信息")
    @AutoDocMethod(description = "取还车配送", value = "取还车配送",response = DeliveryCarVO.class)
    @PostMapping("/delivery/list")
    public ResponseData<DeliveryCarVO> findDeliveryListByOrderNo(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
        if (null == deliveryCarDTO || StringUtils.isBlank(deliveryCarDTO.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客订单编号为空");
        }
        try {
            DeliveryCarVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
            if (Objects.nonNull(deliveryCarRepVO)) {
            	RenterOwnerSummarySectionDeliveryVO sectionDelivery = deliveryCarRepVO.getSectionDelivery();
            	if (sectionDelivery != null) {
            		deliveryCarRepVO.setDistributionMode(sectionDelivery.getDistributionMode());
            	}
                return ResponseData.success(deliveryCarRepVO);
            }
            return ResponseData.success();
        } catch (DeliveryOrderException ex) {
            DeliveryCarVO deliveryCarVO = new DeliveryCarVO();
            deliveryCarVO.setIsGetCar(0);
            deliveryCarVO.setIsReturnCar(0);
            return ResponseData.success(deliveryCarVO);
        } catch (Exception e) {
            log.error("取还车配送接口出现异常", e);
            Cat.logError("取还车配送接口出现异常", e);
            return ResponseData.error();
        }
    }

    /**
     * 取还车（是否取还车）更新接口
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车更新")
    @AutoDocGroup(group = "管理后台取还车更新")
    @AutoDocMethod(description = "取还车更新", value = "取还车更新",response = ResponseData.class)
    @RequestMapping(value = "/delivery/update", method = RequestMethod.POST)
    public ResponseData<?> updateDeliveryCarInfo(@RequestBody @Validated DeliveryCarVO deliveryCarVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
            deliveryCarInfoService.updateDeliveryCarInfo(deliveryCarVO);
            return ResponseData.success();
        } catch (DeliveryOrderException ex) {
            log.error("配送取还车更新接口有問題", ex);
            Cat.logError("配送取还车更新接口有問題", ex);
            return ResponseData.createErrorCodeResponse(ex.getErrorCode(), ex.getMessage());
        } catch (Exception e) {
            log.error("配送取还车更新接口出现异常", e);
            Cat.logError("配送取还车更新出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "配送取还车更新接口出现错误");
        }
    }


    /**
     * 取还车服务数据更新接口
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车更新")
    @AutoDocGroup(group = "管理后台取还车更新")
    @AutoDocMethod(description = "交接车更新", value = "交接车更新",response = ResponseData.class)
    @RequestMapping(value = "/handover/update", method = RequestMethod.POST)
    public ResponseData<?> updateHandoverCarInfo(@RequestBody @Validated DeliveryCarVO deliveryCarVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        DeliveryCarVO deliveryCarDTO = null;
        try{
            DeliveryCarRepVO deliveryCarRepVO = new DeliveryCarRepVO();
            deliveryCarRepVO.setOrderNo(deliveryCarVO.getOrderNo());
            deliveryCarDTO = deliveryRemoteService.getDeliveryCarVO(deliveryCarRepVO);
        }catch (Exception e){
            log.error("保存日志时查询异常",e);
        }

        try {
            deliveryCarInfoService.updateHandoverCarInfo(deliveryCarVO);
        } catch (Exception e) {
            log.error("取还车更新接口出现异常", e);
            Cat.logError("取还车更新接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "取还车更新接口出现错误");
        }
        try{

            //记录日志
            if(deliveryCarDTO != null){
                OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = deliveryCarDTO.getOwnerGetAndReturnCarDTO();
                RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = deliveryCarDTO.getRenterGetAndReturnCarDTO();
                String desc = "";
                if(ownerGetAndReturnCarDTO!= null && deliveryCarVO.getOwnerGetAndReturnCarDTO()!= null){
                    desc += "车主处取还车 ：【";
                    if(CompareBeanUtils.compareString(ownerGetAndReturnCarDTO.getKM,deliveryCarVO.getOwnerGetAndReturnCarDTO().getKM)){
                        desc += "取车里程数 " + ownerGetAndReturnCarDTO.getKM +" 修改为 " + deliveryCarVO.getOwnerGetAndReturnCarDTO().getKM;
                    }
                    if(CompareBeanUtils.compareString(ownerGetAndReturnCarDTO.returnKM,deliveryCarVO.getOwnerGetAndReturnCarDTO().returnKM)){
                        desc += "还车里程数 " + ownerGetAndReturnCarDTO.returnKM +" 修改为 " + deliveryCarVO.getOwnerGetAndReturnCarDTO().returnKM;
                    }
                    if(CompareBeanUtils.compareString(ownerGetAndReturnCarDTO.getCarOil,deliveryCarVO.getOwnerGetAndReturnCarDTO().getCarOil)){
                        desc += "取车油表刻度 " + ownerGetAndReturnCarDTO.getCarOil +" 修改为 " + deliveryCarVO.getOwnerGetAndReturnCarDTO().getCarOil;
                    }
                    if(CompareBeanUtils.compareString(ownerGetAndReturnCarDTO.returnCarOil,deliveryCarVO.getOwnerGetAndReturnCarDTO().returnCarOil)){
                        desc += "还车油表刻度 " + ownerGetAndReturnCarDTO.getCarOil +" 修改为 " + deliveryCarVO.getOwnerGetAndReturnCarDTO().getCarOil;
                    }
                    desc += "】";
                }
                if(renterGetAndReturnCarDTO != null  && deliveryCarVO.getRenterGetAndReturnCarDTO()!= null){
                    desc += "  租客处取还车 ：【";
                    if(CompareBeanUtils.compareString(renterGetAndReturnCarDTO.getKM,deliveryCarVO.getRenterGetAndReturnCarDTO().getKM)){
                        desc += "交车里程数 " + renterGetAndReturnCarDTO.getKM +" 修改为 " + deliveryCarVO.getRenterGetAndReturnCarDTO().getKM;
                    }
                    if(CompareBeanUtils.compareString(renterGetAndReturnCarDTO.returnKM,deliveryCarVO.getRenterGetAndReturnCarDTO().returnKM)){
                        desc += "收车里程数 " + renterGetAndReturnCarDTO.returnKM +" 修改为 " + deliveryCarVO.getRenterGetAndReturnCarDTO().returnKM;
                    }
                    if(CompareBeanUtils.compareString(renterGetAndReturnCarDTO.getCarOil,deliveryCarVO.getRenterGetAndReturnCarDTO().getCarOil)){
                        desc += "交车油表刻度 " + renterGetAndReturnCarDTO.getCarOil +" 修改为 " + deliveryCarVO.getRenterGetAndReturnCarDTO().getCarOil;
                    }
                    if(CompareBeanUtils.compareString(renterGetAndReturnCarDTO.returnCarOil,deliveryCarVO.getRenterGetAndReturnCarDTO().returnCarOil)){
                        desc += "收车油表刻度 " + renterGetAndReturnCarDTO.getCarOil +" 修改为 " + deliveryCarVO.getRenterGetAndReturnCarDTO().getCarOil;
                    }
                    desc += "】";
                }
                adminLogService.insertLog(AdminOpTypeEnum.OWNER_RENTER_GET_RETURN_CAR,deliveryCarVO.getOrderNo(),null,null,desc);
            }

        }catch (Exception e){
            log.error("租客车主处取还车 日志记录异常",e);
        }
        return ResponseData.success();
    }



}
