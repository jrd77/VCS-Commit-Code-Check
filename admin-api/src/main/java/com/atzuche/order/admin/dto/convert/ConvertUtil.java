package com.atzuche.order.admin.dto.convert;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.vo.res.consolecost.TempCarDepoistInfoResVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据转换
 *
 * @author pengcheng.fu
 * @date 2020/4/24 15:45
 */
public class ConvertUtil {

    /**
     * list convert dto
     *
     * @param tempCarDepoists 数据
     * @return CarDepositDetainOptLogDTO
     */
    public static CarDepositDetainOptLogDTO carDepositInfoListConvertDto(List<TempCarDepoistInfoResVO> tempCarDepoists) {
        CarDepositDetainOptLogDTO dto = new CarDepositDetainOptLogDTO();

        Map<String, TempCarDepoistInfoResVO> dataMap =
                tempCarDepoists.stream().collect(Collectors.toMap(TempCarDepoistInfoResVO::getCashNo, x -> x));

        dto.setTempWzFine(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_FINE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_FINE.getCashNo()).getCashAmt() :
                OrderConstant.ZERO);

        dto.setTempWzStopCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_STOPCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_WZ_STOPCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);

        dto.setTempClaimRepairCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_REPAIRCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_REPAIRCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);

        dto.setTempClaimStopCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_STOPCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_CLAIM_STOPCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);

        dto.setTempRiskRepairCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_REPAIRCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_REPAIRCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);

        dto.setTempRiskStopCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_STOPCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_STOPCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);

        dto.setTempRiskCollectCharge(Objects.nonNull(dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_COLLECTCHARGE.getCashNo())) ?
                dataMap.get(ConsoleCashCodeEnum.CAR_DEPOSIT_DETAIN_RISK_COLLECTCHARGE.getCashNo()).getCashAmt() : OrderConstant.ZERO);
        return dto;
    }


}
