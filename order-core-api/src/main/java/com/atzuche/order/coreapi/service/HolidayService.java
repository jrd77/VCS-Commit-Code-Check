package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.HolidaySettingSDK;
import com.atzuche.config.common.entity.HolidaySettingEntity;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.CancelOrderDutyEnum;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderJudgeDutyResDTO;
import com.atzuche.order.mem.MemProxyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节假日处理
 *
 * @author pengcheng.fu
 * @date 2020/3/5 14:02
 */

@Service
public class HolidayService {

    private static Logger logger = LoggerFactory.getLogger(HolidayService.class);

    @Autowired
    private HolidaySettingSDK holidaySetting;
    @Autowired
    private MemProxyService memProxyService;


    /**
     * 判断是否需要补贴罚金
     *
     * @param reqContext 请求参数
     * @param wrongdoer  责任方
     * @return true, 是 false,否
     */
    public CancelOrderJudgeDutyResDTO isSubsidyFineAmt(CancelOrderReqContext reqContext, Integer wrongdoer) {
        logger.info("判断是否需要补贴罚金A.param is:reqContext[{}],wrongdoer:[{}]", JSON.toJSONString(reqContext), wrongdoer);
        String memNo = null;
        if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_RENTER.getCode() == wrongdoer) {
            memNo = reqContext.getOwnerOrderEntity().getMemNo();
        } else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_OWNER.getCode() == wrongdoer) {
            memNo = reqContext.getRenterOrderEntity().getRenterMemNo();
        }

        if (StringUtils.isBlank(memNo)) {
            return new CancelOrderJudgeDutyResDTO(null,null,false);
        }
        LocalDateTime rentTime = reqContext.getRenterOrderEntity().getExpRentTime();
        LocalDateTime revertTime = reqContext.getRenterOrderEntity().getExpRevertTime();
        return isSubsidyFineAmt(memNo, rentTime, revertTime);
    }


    /**
     * 判断是否需要补贴罚金
     *
     * @param memNo      会员号
     * @param rentTime   租期开始时间
     * @param revertTime 租期截止时间
     * @return true, 是 false,否
     */
    public CancelOrderJudgeDutyResDTO isSubsidyFineAmt(String memNo, LocalDateTime rentTime, LocalDateTime revertTime) {
        logger.info("判断是否需要补贴罚金B.param is:memNo:[{}],rentTime:[{}],revertTime:[{}]", memNo,
                JSON.toJSONString(rentTime), JSON.toJSONString(revertTime));
        if (StringUtils.isBlank(memNo)) {
            return new CancelOrderJudgeDutyResDTO(null,null,false);
        }
        List<Integer> holidayIdList = listHolidayCircleInRentRevertDate(rentTime, revertTime);
        boolean isSubsidyFineAmt = memProxyService.countByHolidayCircleList(Integer.valueOf(memNo), holidayIdList);

        CancelOrderJudgeDutyResDTO cancelOrderJudgeDutyResDTO = new CancelOrderJudgeDutyResDTO();
        cancelOrderJudgeDutyResDTO.setMemNo(Integer.valueOf(memNo));
        cancelOrderJudgeDutyResDTO.setHolidayId(CollectionUtils.isEmpty(holidayIdList) ? null : holidayIdList.get(0));
        cancelOrderJudgeDutyResDTO.setIsSubsidyFineAmt(isSubsidyFineAmt);

        return cancelOrderJudgeDutyResDTO;
    }


    /**
     * 查询租期范围内命中的节假日周期
     *
     * @param rentTime   起租时间
     * @param revertTime 还车时间
     * @return List<Integer> 节假日ID列表
     */
    private List<Integer> listHolidayCircleInRentRevertDate(LocalDateTime rentTime, LocalDateTime revertTime) {
        logger.info("获取租期内包含的节假日信息.param is,rentTime:[{}],revertTime:[{}]", JSON.toJSONString(rentTime), JSON.toJSONString(revertTime));
        List<HolidaySettingEntity> holidayConfigList = holidaySetting.getConfig(new DefaultConfigContext());
        logger.info("当前节假日列表配置信息.holidayConfigList:[{}]", JSON.toJSONString(holidayConfigList));
        if (CollectionUtils.isEmpty(holidayConfigList)) {
            return new ArrayList<>();
        }

        int rentDate = Integer.valueOf(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(rentTime)).substring(0, 8));
        int revertDate = Integer.valueOf(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime)).substring(0, 8));

        List<Integer> holidayList = new ArrayList<>();
        holidayConfigList.forEach(holiday -> {
            int realStartDate = holiday.getRealStartDate();
            int realEndDate = holiday.getRealEndDate();
            boolean mark = false;
            if ((rentDate >= realStartDate && rentDate <= realEndDate)) {
                holidayList.add(holiday.getId());
                mark = true;
            }

            if (!mark && revertDate >= realStartDate && revertDate <= realEndDate) {
                holidayList.add(holiday.getId());
            }
        });
        logger.info("租期内包含的节假日信息.holidayList:[{}]", JSON.toJSONString(holidayList));
        return holidayList.stream().sorted().collect(Collectors.toList());

    }


}
