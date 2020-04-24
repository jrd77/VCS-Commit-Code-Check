package com.atzuche.order.admin.controller.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUser;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.dto.convert.CarDepositDetainOptLogDTO;
import com.atzuche.order.admin.dto.convert.ConvertUtil;
import com.atzuche.order.admin.service.CarDepositReturnDetailService;
import com.atzuche.order.admin.service.RenterWzService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.util.CompareBeanUtils;
import com.atzuche.order.admin.vo.req.car.CarDepositDetainInfoReqVO;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.req.renterWz.CarDepositTemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@AutoDocVersion(version = "车辆押金信息")
public class CarDepositReturnDetailController {

    @Autowired
    private CarDepositReturnDetailService carDepositReturnDetailService;
    @Resource
    private RenterWzService renterWzService;
    @Resource
    private AdminLogService adminLogService;
    @Resource
    private FeignOrderCostService feignOrderCostService;

    @AutoDocMethod(description = "【liujun】车辆押金信息", value = "车辆押金信息", response = CarDepositRespVo.class)
    @PostMapping(value = "/console/deposit/getCarDepositReturnDetail")
    public ResponseData<CarDepositRespVo> getCarDepositReturnDetail(@Valid @RequestBody CarDepositReqVO reqVo, BindingResult bindingResult) {
        log.info("车辆押金信息-reqVo={}", JSON.toJSONString(reqVo));
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        return carDepositReturnDetailService.getCarDepositReturnDetail(reqVo);
    }

    /*@AutoDocMethod(description = "【liujun】车辆押金暂扣处理", value = "车辆押金暂扣处理", response = CarDepositOtherRespVO.class)
    @GetMapping(value = "/console/deposit/return/detail/otherInfo")
    public ResponseData <?> getCarDepositReturnDetailOtherInfo(@Valid CarDepositOtherReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】车辆押金返还处理列表", value = "车辆押金返还处理列表", response = CarDepositReturnDetailResVO.class)
    @GetMapping(value = "/console/deposit/return/detail/list")
    public ResponseData <?> getCarDepositReturnDetail(@Valid CarDepositReturnDetailListReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】保存车辆押金返还处理", value = "保存车辆押金返还处理", response = ResponseData.class)
    @GetMapping(value = "/console/deposit/return/detail/save")
    public ResponseData <?> saveCarDepositReturnDetail(@Valid CarDepositReturnDetailResVO reqVo, BindingResult bindingResult) {

        return null;
    }*/

    @AutoDocMethod(description = "暂扣/取消暂扣租车押金", value = "暂扣/取消暂扣租车押金",response = ResponseData.class)
    @PostMapping("/console/save/carDeposit/temporaryRefund")
    public ResponseData saveCarDepositTemporaryRefund(@Valid @RequestBody CarDepositTemporaryRefundReqVO req, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);

        renterWzService.saveCarDepositTemporaryRefund(req);
        try{
            adminLogService.insertLog(AdminOpTypeEnum.TEMPORARY_WZ_REFUND,req.getOrderNo(), JSON.toJSONString(req));
        }catch (Exception e){
            log.warn("暂扣租车押金日志记录失败",e);
        }
        return ResponseData.success();
    }


    /**
     * 获取订单车辆押金暂扣扣款明细接口
     *
     * @param reqVO 请求参数
     * @return ResponseData<GetTempCarDepositInfoResVO>
     */
    @AutoDocMethod(description = "获取订单车辆押金暂扣扣款明细接口", value = "获取订单车辆押金暂扣扣款明细接口",response = GetTempCarDepositInfoResVO.class)
    @PostMapping("/console/get/carDpoist/detain")
    public ResponseData<GetTempCarDepositInfoResVO> getTempCarDepoists(@Valid @RequestBody GetTempCarDepositInfoReqVO reqVO,BindingResult bindingResult) {
        log.info("User [{}] get order carDepoist info.param is,reqVO:[{}]",
                AdminUserUtil.getAdminUser().getAuthName(), JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        return feignOrderCostService.getTempCarDepoists(reqVO);
    }


    /**
     * 保存订单车辆押金暂扣扣款信息接口
     *
     * @param reqVO 请求参数
     * @return ResponseData
     */
    @AutoDocMethod(description = "保存订单车辆押金暂扣扣款信息接口", value = "保存订单车辆押金暂扣扣款信息接口")
    @PostMapping("/console/save/carDpoist/detain")
    public ResponseData saveTempCarDepoist(@Valid @RequestBody CarDepositDetainInfoReqVO reqVO,BindingResult bindingResult){
        log.info("User [{}] save order carDepoist info.param is,reqVO:[{}]",
                AdminUserUtil.getAdminUser().getAuthName(), JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        //拉取存储数据
        GetTempCarDepositInfoReqVO infoReqVO = new GetTempCarDepositInfoReqVO();
        infoReqVO.setOrderNo(reqVO.getOrderNo());
        infoReqVO.setMemNo(reqVO.getMemNo());
        ResponseData<GetTempCarDepositInfoResVO>  res = feignOrderCostService.getTempCarDepoists(infoReqVO);

        //更新数据
        SaveTempCarDepositInfoReqVO req  = new SaveTempCarDepositInfoReqVO();
        BeanUtils.copyProperties(reqVO, req);
        req.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        ResponseData responseData = feignOrderCostService.saveTempCarDepoist(req);
        if(Objects.nonNull(responseData) && StringUtils.equals(responseData.getResCode(),
                ErrorCode.SUCCESS.getCode())) {
            // 记录操作日志
            try{
                CarDepositDetainOptLogDTO oldData =
                        ConvertUtil.carDepositInfoListConvertDto(res.getData().getTempCarDepoists());
                CarDepositDetainOptLogDTO newData = ConvertUtil.carDepositInfoListConvertDto(reqVO.getTempCarDepoists());
                adminLogService.insertLog(AdminOpTypeEnum.TEMPORARY_CAR_DEPOSIT, req.getOrderNo(), CompareBeanUtils.newInstance(oldData, newData).compare());
            }catch (Exception e){
                log.warn("保存订单车辆押金暂扣扣款信息日志记录失败",e);
            }
        }
        return responseData;
    }



}
