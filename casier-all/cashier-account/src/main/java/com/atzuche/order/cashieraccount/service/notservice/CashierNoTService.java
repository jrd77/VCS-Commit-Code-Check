package com.atzuche.order.cashieraccount.service.notservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterCostException;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterCostReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.exception.AccountRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.common.AESUtil;
import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.exception.OrderPaySignParamException;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.commons.IpUtil;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.vo.NotifyDataDTO;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.autopay.gateway.constant.DataAppIdConstant;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPaySourceConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.util.AESSecurityUtils;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.util.RSASecurityUtils;
import com.autoyol.autopay.gateway.vo.req.BatchPayVo;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.autopay.gateway.vo.req.RefundVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderRefundMq;

import lombok.extern.slf4j.Slf4j;



/**
 * 收银表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierNoTService {
    @Autowired
    private CashierMapper cashierMapper;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    AccountRenterDepositService accountRenterDepositService;
    @Autowired
    AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired
    AccountRenterCostSettleService accountRenterCostSettleService;
    @Value("${env_t}")
    private String env;
    @Autowired private BaseProducer baseProducer;
    
	public static final String ANDROID = "ANDROID";
	public static final String IOS = "IOS";
	//信用预授权  App支付宝支付
	public static final String CREDITIOS_ALIPAY = "CREDITIOS-ALIPAY";
	public static final String CREDITANDROID_ALIPAY = "CREDITANDROID-ALIPAY";
	public static final String MICROPROGRAM_COMMON = "MICROPROGRAM-COMMON";

    /**
     * 收银台根据主单号 向订单模块查询子单号
     * @param orderNo
     * @return
     */
    public RenterOrderEntity getRenterOrderNoByOrderNo(String orderNo){
        RenterOrderEntity renterOrderEntity =  renterOrderService.getRenterOrderByOrderNoAndWaitPay(orderNo);
        if(Objects.isNull(renterOrderEntity) || StringUtil.isBlank(renterOrderEntity.getRenterOrderNo())){
           return new RenterOrderEntity();
        }
        return renterOrderEntity;
    }
    
    public RenterOrderEntity getRenterOrderNoByOrderNoAfter(String orderNo){
        RenterOrderEntity renterOrderEntity =  renterOrderService.getRenterOrderByOrderNoAndWaitPayAfter(orderNo);
        if(Objects.isNull(renterOrderEntity) || StringUtil.isBlank(renterOrderEntity.getRenterOrderNo())){
           return new RenterOrderEntity();
        }
        return renterOrderEntity;
    }
    
    public RenterOrderEntity getRenterOrderNoByOrderNoIncrement(String orderNo){
        RenterOrderEntity renterOrderEntity =  renterOrderService.getRenterOrderByOrderNoAndWaitPayIncrement(orderNo);
        if(Objects.isNull(renterOrderEntity) || StringUtil.isBlank(renterOrderEntity.getRenterOrderNo())){
           return new RenterOrderEntity();
        }
        return renterOrderEntity;
    }
    

    /**
     * 收银台根据主单号 向订单模块查询子单号
     * @param orderNo
     * @return
     */
    public RenterOrderEntity getRenterOrderNoByOrderNoAndFinish(String orderNo){
        RenterOrderEntity renterOrderEntity =  renterOrderService.getRenterOrderNoByOrderNoAndFinish(orderNo);
        if(Objects.isNull(renterOrderEntity) || StringUtil.isBlank(renterOrderEntity.getRenterOrderNo())){
            return new RenterOrderEntity();
        }
        return renterOrderEntity;
	}

    
    /**
     * 根据会员号查询，to补付列表
     * @param orderNo
     * @return
     */
    public List<RenterOrderEntity> getRenterOrderNoByMemNo(String memNo){
        List<RenterOrderEntity> listRenterOrderEntity =  renterOrderService.getRenterOrderByMemNoAndWaitPay(memNo);
        if(CollectionUtils.isEmpty(listRenterOrderEntity)){
           return new ArrayList<RenterOrderEntity>();
        }
        return listRenterOrderEntity;
    }
    
    public List<RenterOrderEntity> getRenterOrderNoByMemNoAndOrderNos(String memNo,List<String> orderNoList){
        List<RenterOrderEntity> listRenterOrderEntity =  renterOrderService.getRenterOrderByMemNoOrderNosAndWaitPay(memNo,orderNoList);
        if(CollectionUtils.isEmpty(listRenterOrderEntity)){
           return new ArrayList<RenterOrderEntity>();
        }
        return listRenterOrderEntity;
    }


    /**
     * 收银台支付记录
     */
    public CashierEntity getCashierEntity(String orderNo,String memNo,String payKind){
        CashierEntity cashierEntity = cashierMapper.getPayAmtByPayKind(orderNo,memNo,payKind);
        return cashierEntity;
    }
    
    /**
     * 收银台支付记录，不含钱包支付记录
     */
    public CashierEntity getCashierEntityNoWallet(String orderNo,String memNo,String payKind){
        CashierEntity cashierEntity = cashierMapper.getPayAmtByPayKindNoWallet(orderNo,memNo,payKind);
        return cashierEntity;
    }
    
    /**
     * 收银台记录应收违章押金
     * @param createOrderRenterWZDepositReq
     */
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq) {
        //1 校验
        Assert.notNull(createOrderRenterWZDepositReq, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterWZDepositReq.check();

        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterWZDepositReq,cashierEntity);
        cashierEntity.setPayAmt(createOrderRenterWZDepositReq.getYingfuDepositAmt());
        int result = cashierMapper.insertSelective(cashierEntity);
        if(result==0){
            throw new AccountRenterWZDepositException();
        }
    }
    /**
     * 收银台记录应收车俩押金
     * @param createOrderRenterDeposit
     */
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDeposit) {
        //1 校验
        Assert.notNull(createOrderRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterDeposit.check();
        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterDeposit,cashierEntity);
        cashierEntity.setPayAmt(createOrderRenterDeposit.getYingfuDepositAmt());
        int result = cashierMapper.insertSelective(cashierEntity);
        if(result==0){
            throw new AccountRenterDepositDBException();
        }
    }

    /**
     * 下单成功  收银台落 收银记录（此时没有应付 ，应付从租车费用中取）
     * @param createOrderRenterCost
     */
    public void insertRenterCost(CreateOrderRenterCostReqVO createOrderRenterCost) {
        //1 校验
        Assert.notNull(createOrderRenterCost, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterCost.check();
        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterCost,cashierEntity);
        cashierEntity.setPayAmt(NumberUtils.INTEGER_ZERO);
        int result = cashierMapper.insertSelective(cashierEntity);
        if(result==0){
            throw new AccountRenterCostException();
        }
    }

    /**
     * 支付成功异步回调 车俩押金参数初始化
     * @param notifyDataVo
     * @return
     */
    public PayedOrderRenterDepositReqVO getPayedOrderRenterDepositReq(NotifyDataVo notifyDataVo,RenterCashCodeEnum renterCashCodeEnum) {
        PayedOrderRenterDepositReqVO vo = new PayedOrderRenterDepositReqVO();
        BeanUtils.copyProperties(notifyDataVo,vo);
        vo.setPayStatus(notifyDataVo.getTransStatus());
        if(StringUtils.isNoneBlank(notifyDataVo.getOrderTime())) {
        	vo.setPayTime(LocalDateTimeUtils.parseStringToDateTime(notifyDataVo.getOrderTime(),LocalDateTimeUtils.YYYYMMDDHHMMSSS_PATTERN));
        }else {
        	vo.setPayTime(LocalDateTime.now()); //默认当前时间
        }
        //容错处理，07微信的一定是消费  200413
        //基于支付的提前
        if(DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType())){
	        if(DataPaySourceConstant.WEIXIN_APP.equals(notifyDataVo.getPaySource()) 
	        		|| DataPaySourceConstant.WEIXIN_H5.equals(notifyDataVo.getPaySource())
	        		|| DataPaySourceConstant.WEIXIN_MP.equals(notifyDataVo.getPaySource()) ) {
	        	notifyDataVo.setPayType(DataPayTypeConstant.PAY_PUR);
	        }
        }
        //"01"：消费
        if(DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType())){
        	vo.setIsAuthorize(0);
            Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
            vo.setShifuDepositAmt(settleAmount);
            vo.setSurplusDepositAmt(settleAmount);
        }
        //"02"：预授权 TODO 预授权到期时间 （分为信用 和 芝麻）
        if(DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType())){
        	vo.setIsAuthorize(1);  //默认值，普通预授权
        	//区分双免和非双免,根据是否信用减免来区分
        	putPayPreValue(notifyDataVo, vo);
            
        }

        //TODO 预授权到期时间
        //车辆押金进出明细
        DetainRenterDepositReqVO detainRenterDeposit = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(notifyDataVo,detainRenterDeposit);
        Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
        detainRenterDeposit.setAmt(settleAmount);
        detainRenterDeposit.setUniqueNo(notifyDataVo.getQn());
        detainRenterDeposit.setRenterCashCodeEnum(renterCashCodeEnum);
        vo.setDetainRenterDepositReqVO(detainRenterDeposit);
        return vo;
    }


    /**
     * 预授权赋值方法
     * @param notifyDataVo
     * @param vo
     */
	private void putPayPreValue(NotifyDataVo notifyDataVo, PayedOrderRenterDepositReqVO vo) {
		//否则下面的Double.valueOf报错。
		if(StringUtils.isBlank(notifyDataVo.getTotalFreezeCreditAmount())){
			//设置默认值。
			notifyDataVo.setTotalFreezeCreditAmount(notifyDataVo.getSettleAmount());
		}
		if(Double.valueOf(notifyDataVo.getTotalFreezeCreditAmount()).doubleValue() == 0d) {   //考虑到带小数点的情况。
			//预授权方式
			Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Double.valueOf(notifyDataVo.getSettleAmount()).intValue();
		    vo.setAuthorizeDepositAmt(settleAmount);
		    vo.setSurplusAuthorizeDepositAmt(settleAmount);
		    vo.setIsAuthorize(1); //普通预授权
		}else {  
			//存在信用支付的方式
			vo.setIsAuthorize(2);
			if(Double.valueOf(notifyDataVo.getTotalFreezeFundAmount()).doubleValue() == 0d) {  
				//全部按信用支付
				Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Double.valueOf(notifyDataVo.getSettleAmount()).intValue();
		        vo.setCreditPayAmt(settleAmount);
		        vo.setSurplusCreditPayAmt(settleAmount);
			}else {
				//一半一半的情况，转换成整数。
				Integer fundAmount = notifyDataVo.getTotalFreezeFundAmount()==null?0:Double.valueOf(notifyDataVo.getTotalFreezeFundAmount()).intValue();
//		        vo.setShifuDepositAmt(fundAmount);
//		        vo.setSurplusDepositAmt(fundAmount);
				//存在一半一半的情况，预授权和信用是共存的， 信用和资金预授权是共存的。!!
		        vo.setAuthorizeDepositAmt(fundAmount);
			    vo.setSurplusAuthorizeDepositAmt(fundAmount);
		        
		        Integer creditAmount = notifyDataVo.getTotalFreezeCreditAmount()==null?0:Double.valueOf(notifyDataVo.getTotalFreezeCreditAmount()).intValue();
		        vo.setCreditPayAmt(creditAmount);
		        vo.setSurplusCreditPayAmt(creditAmount);
			}
		}
	}

    /**
     * 更新收银台租车押金已支付
     * @param notifyDataVo
     */
    public Boolean updataCashier(NotifyDataDTO notifyDataVo) {
    	log.info("updataCashier notifyDataVo = [{}]",GsonUtils.toJson(notifyDataVo));
        CashierEntity cashierEntity = cashierMapper.selectCashierEntity(notifyDataVo.getPayMd5());
        int result =0;
        if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getId())){
        	//解决重复操作
        	//数据库中的状态
        	if(!"00".equals(cashierEntity.getTransStatus())) {  //成功 00
	            CashierEntity cashier = new CashierEntity(); 
	            BeanUtils.copyProperties(notifyDataVo,cashier);
	            cashier.setId(cashierEntity.getId());
	            cashier.setVersion(cashierEntity.getVersion());
	            cashier.setPaySn(cashierEntity.getPaySn()+1);
	            cashier.setPayEvn(notifyDataVo.getPayEnv());
	            cashier.setOs(notifyDataVo.getReqOs());
//	            cashier.setPayTransNo(notifyDataVo.getQn());
	            cashier.setPayTransNo(notifyDataVo.getAtpayNewTransId());  //支付交易号。20200601
	            //重新赋值
	            cashier.setQn(notifyDataVo.getQn());
	            
	            cashier.setPayTime(notifyDataVo.getOrderTime());
	            cashier.setPayTitle(getPayTitle(notifyDataVo.getOrderNo(),notifyDataVo.getPayKind()));
	            cashier.setPayChannel(notifyDataVo.getPayChannel());
	            cashier.setPayLine(notifyDataVo.getPayLine());
	            cashier.setVirtualAccountNo(notifyDataVo.getVirtualAccountNo());
	            String amtStr = notifyDataVo.getSettleAmount();
	            amtStr = Objects.isNull(amtStr)?"0":amtStr;
	            cashier.setPayAmt(Integer.valueOf(amtStr));
	            log.info("updateByPrimaryKeySelective cashier=[{}]",GsonUtils.toJson(cashier));
	            result = cashierMapper.updateByPrimaryKeySelective(cashier);
	            if(result == 0){
	                throw new OrderPayCallBackAsnyException();
	            }
        	}else {
        		log.info("当前状态已经为成功,orderNo=[{}],payMd5=[{}]",notifyDataVo.getOrderNo(),notifyDataVo.getPayMd5());
        	}
        	return false;
        }else {
            try {
            	CashierEntity cashier = new CashierEntity();
                BeanUtils.copyProperties(notifyDataVo,cashier);
                cashier.setPayEvn(notifyDataVo.getPayEnv());
                cashier.setOs(notifyDataVo.getReqOs());
//                cashier.setPayTransNo(notifyDataVo.getQn());
                cashier.setPayTransNo(notifyDataVo.getAtpayNewTransId());  //支付交易号。20200601
                //重新赋值
	            cashier.setQn(notifyDataVo.getQn());
	            
                cashier.setPayTime(notifyDataVo.getOrderTime());
                cashier.setPayTitle(getPayTitle(notifyDataVo.getOrderNo(),notifyDataVo.getPayKind()));
                String amtStr = notifyDataVo.getSettleAmount();
                amtStr = Objects.isNull(amtStr)?"0":amtStr;
                cashier.setPayAmt(Integer.valueOf(amtStr));
                log.info("insertSelective cashier=[{}]",GsonUtils.toJson(cashier));
                result = cashierMapper.insertSelective(cashier);
                if(result == 0){
                    throw new OrderPayCallBackAsnyException();
                }
			} catch (Exception e) {
				log.error("收银台入库违反唯一约束：params=[{}]",GsonUtils.toJson(notifyDataVo),e);
			}
            return true;
        }

    }


//    /**
//     * 更新收银台租车押金已支付
//     * @param notifyDataVo
//     */
//    public Boolean updataCashier(NotifyDataVo notifyDataVo) {
//        CashierEntity cashierEntity = cashierMapper.selectCashierEntity(notifyDataVo.getPayMd5());
//        int result =0;
//         if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getId())){
//             CashierEntity cashier = new CashierEntity();
//             BeanUtils.copyProperties(notifyDataVo,cashier);
//             cashier.setId(cashierEntity.getId());
//             cashier.setVersion(cashierEntity.getVersion());
//             cashier.setPaySn(cashierEntity.getPaySn()+1);
//             cashier.setPayEvn(notifyDataVo.getPayEnv());
//             cashier.setOs(notifyDataVo.getReqOs());
//             cashier.setPayTransNo(notifyDataVo.getQn());
//             cashier.setPayTime(notifyDataVo.getOrderTime());
//             cashier.setPayTitle(getPayTitle(notifyDataVo.getOrderNo(),notifyDataVo.getPayKind()));
//             String amtStr = notifyDataVo.getSettleAmount();
//             amtStr = Objects.isNull(amtStr)?"0":amtStr;
//             cashier.setPayAmt(Integer.valueOf(amtStr));
//             result = cashierMapper.updateByPrimaryKeySelective(cashier);
//             if(result == 0){
//                 throw new OrderPayCallBackAsnyException();
//             }
//             return false;
//         }else {
//             CashierEntity cashier = new CashierEntity();
//             BeanUtils.copyProperties(notifyDataVo,cashier);
//             cashier.setPayEvn(notifyDataVo.getPayEnv());
//             cashier.setOs(notifyDataVo.getReqOs());
//             cashier.setPayTransNo(notifyDataVo.getQn());
//             cashier.setPayTime(notifyDataVo.getOrderTime());
//             cashier.setPayTitle(getPayTitle(notifyDataVo.getOrderNo(),notifyDataVo.getPayKind()));
//             String amtStr = notifyDataVo.getSettleAmount();
//             amtStr = Objects.isNull(amtStr)?"0":amtStr;
//             cashier.setPayAmt(Integer.valueOf(amtStr));
//             result = cashierMapper.insertSelective(cashier);
//             if(result == 0){
//                 throw new OrderPayCallBackAsnyException();
//             }
//             return true;
//         }
//
//    }

    private String getPayTitle(String orderNo ,String payKind){
        String result ="";
        if(DataPayKindConstant.RENT.equals(payKind)){
            result = "订单号："+orderNo+"租车押金";
        }
        if(DataPayKindConstant.DEPOSIT.equals(payKind)){
            result = "订单号："+orderNo+"违章押金";
        }
        if(DataPayKindConstant.RENT_AMOUNT.equals(payKind)){
            result = "订单号："+orderNo+"租车费用";
        }
        return result;
    }
    /**
     * 支付成功异步回调 违章押金参数初始化
     * @param notifyDataVo
     * @return
     */
    public PayedOrderRenterWZDepositReqVO getPayedOrderRenterWZDepositReq(NotifyDataVo notifyDataVo,RenterCashCodeEnum renterCashCodeEnum) {
        PayedOrderRenterWZDepositReqVO vo = new PayedOrderRenterWZDepositReqVO();
        BeanUtils.copyProperties(notifyDataVo,vo);
//        Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
//        vo.setShishouDeposit(settleAmount);
        
        //"01"：消费
        if(DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType())){
        	vo.setIsAuthorize(0);
            Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
            vo.setShishouDeposit(settleAmount);
            vo.setSurplusDepositAmt(settleAmount);
        }
        //"02"：预授权 TODO 预授权到期时间 （分为信用 和 芝麻）
        if(DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType())){
        	vo.setIsAuthorize(1); //默认普通预授权
        	//区分双免和非双免,根据是否信用减免来区分
        	putPayPreValue(notifyDataVo, vo);
            
        }
        
        //违章押金进出明细
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(notifyDataVo,payedOrderRenterDepositDetail);
        payedOrderRenterDepositDetail.setUniqueNo(notifyDataVo.getQn());
        payedOrderRenterDepositDetail.setRenterCashCodeEnum(renterCashCodeEnum);
        payedOrderRenterDepositDetail.setPayChannel(notifyDataVo.getPaySource());
        payedOrderRenterDepositDetail.setPayment(notifyDataVo.getPayType());
        Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
        payedOrderRenterDepositDetail.setAmt(settleAmount);
        vo.setPayedOrderRenterDepositDetailReqVO(payedOrderRenterDepositDetail);
        return vo;
    }
    
    
    /**
     * 预授权赋值方法
     * @param notifyDataVo
     * @param vo
     */
	private void putPayPreValue(NotifyDataVo notifyDataVo, PayedOrderRenterWZDepositReqVO vo) {
		//按0处理
		if(StringUtils.isBlank(notifyDataVo.getTotalFreezeCreditAmount())) {
			notifyDataVo.setTotalFreezeCreditAmount("0");
		}
		//按0处理
		if(StringUtils.isBlank(notifyDataVo.getTotalFreezeFundAmount())) {
			notifyDataVo.setTotalFreezeFundAmount("0");
		}
		
		if(Double.valueOf(notifyDataVo.getTotalFreezeCreditAmount()).doubleValue() == 0d) {   
			//预授权方式
			Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Double.valueOf(notifyDataVo.getSettleAmount()).intValue();
		    vo.setAuthorizeDepositAmt(settleAmount);
		    vo.setSurplusAuthorizeDepositAmt(settleAmount);
		    vo.setIsAuthorize(1);
		}else {  //存在信用支付的方式
			vo.setIsAuthorize(2);
			if(Double.valueOf(notifyDataVo.getTotalFreezeFundAmount()).doubleValue() == 0d) {  //全部按信用支付
				Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Double.valueOf(notifyDataVo.getSettleAmount()).intValue();
		        vo.setCreditPayAmt(settleAmount);
		        vo.setSurplusCreditPayAmt(settleAmount);
			}else {
				//一半一半的情况
				Integer fundAmount = notifyDataVo.getTotalFreezeFundAmount()==null?0:Double.valueOf(notifyDataVo.getTotalFreezeFundAmount()).intValue();
//		        vo.setShishouDeposit(fundAmount);
//		        vo.setSurplusDepositAmt(fundAmount);
				//存在一半一半的情况，预授权和信用是共存的， 信用和资金预授权是共存的。!!
		        vo.setAuthorizeDepositAmt(fundAmount);
			    vo.setSurplusAuthorizeDepositAmt(fundAmount);
		        
		        Integer creditAmount = notifyDataVo.getTotalFreezeCreditAmount()==null?0:Double.valueOf(notifyDataVo.getTotalFreezeCreditAmount()).intValue();
		        vo.setCreditPayAmt(creditAmount);
		        vo.setSurplusCreditPayAmt(creditAmount);
		        
			}
		}
	}
	

    /**
     * 支付成功异步回调 补付租车费用回调
     * @param notifyDataVo
     * @return
     */
    public AccountRenterCostReqVO getAccountRenterCostReq(NotifyDataVo notifyDataVo,RenterCashCodeEnum renterCashCodeEnum) {
        AccountRenterCostReqVO vo = new AccountRenterCostReqVO();
        BeanUtils.copyProperties(notifyDataVo,vo);
        Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
        vo.setShifuAmt(settleAmount);
        //费用明细
        AccountRenterCostDetailReqVO accountRenterCostDetail = new AccountRenterCostDetailReqVO();
        BeanUtils.copyProperties(notifyDataVo,accountRenterCostDetail);
        accountRenterCostDetail.setPaySource(notifyDataVo.getPaySource());
        accountRenterCostDetail.setPayTypeCode(notifyDataVo.getPayType());
        accountRenterCostDetail.setTime(notifyDataVo.getOrderTime());
        accountRenterCostDetail.setAmt(settleAmount);
        accountRenterCostDetail.setRenterCashCodeEnum(renterCashCodeEnum);
        accountRenterCostDetail.setPaySourceCode(notifyDataVo.getPaySource());
        accountRenterCostDetail.setPaySource(PaySourceEnum.getFlagText(notifyDataVo.getPaySource()));
        vo.setAccountRenterCostDetailReqVO(accountRenterCostDetail);
        return vo;
    }
    /**
     * 计算租车费用应付
     * @param payableVOs
     * @return
     */
    public int sumRentOrderCost(List<PayableVO> payableVOs) {
        int amt = NumberUtils.INTEGER_ZERO;
        if(!CollectionUtils.isEmpty(payableVOs)){
            return payableVOs.stream().mapToInt(PayableVO::getAmt).sum();
        }
        return amt;
    }



    /**
     * 构造参数 PayVo (押金、违章押金)
     * @param cashierEntity   计算paySn，是在异步通知支付成功的时候落库的。
     * @param orderPaySign
     * @param freeDepositType  免押方式(1:绑卡减免,2:芝麻减免,3:消费) 
     * @return
     */
    public PayVo getPayVO(String orderNo,CashierEntity cashierEntity,OrderPaySignReqVO orderPaySign,int amt ,String title,String payKind,String payIdStr ,String extendParams,int freeDepositType) {
        PayVo vo = new PayVo();
        Integer paySn = (Objects.isNull(cashierEntity)|| Objects.isNull(cashierEntity.getPaySn()))?0:cashierEntity.getPaySn();
        vo.setInternalNo("1");
        vo.setExtendParams(extendParams);
        vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
        vo.setMemNo(orderPaySign.getMenNo());
//        vo.setOrderNo(orderPaySign.getOrderNo());
        //支持多订单号
        vo.setOrderNo(orderNo);
        vo.setOpenId(orderPaySign.getOpenId());
        vo.setReqOs(orderPaySign.getReqOs());
        vo.setPayAmt(String.valueOf(Math.abs(amt)));
        vo.setPayEnv(env);
        vo.setPayId(payIdStr);
        vo.setPayKind(payKind);
        vo.setPaySn(String.valueOf(paySn));
        vo.setPaySource(orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00"));
        vo.setPayTitle(title);
//        if(freeDepositType == 2) {
//        	vo.setPayType(DataPayTypeConstant.PAY_PRE); //预授权的方式。
//        }else {
        	vo.setPayType(orderPaySign.getPayType());  //默认
//        }
        
        //只有押金才有预授权的情况
        if(DataPayKindConstant.RENT.equals(payKind) || DataPayKindConstant.DEPOSIT.equals(payKind)) {
	        String sourceType = orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00");
	        if(DataPaySourceConstant.ALIPAY.equals(sourceType)){
				//只有押金的时候才有是预授权，其他的情况都是消费
				vo.setPayType(DataPayTypeConstant.PAY_PRE); 
			}else if(DataPaySourceConstant.WEIXIN_APP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_MP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_H5.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else {
				//默认
				vo.setPayType(orderPaySign.getPayType());
			}
        }
        
        vo.setReqIp(IpUtil.getLocalIp());
        vo.setAtpaySign(StringUtils.EMPTY);
        return vo;
    }
    
    /**
     * 需要变更reqOs字段。
     * @param orderNo
     * @param cashierEntity
     * @param orderPaySign
     * @param amt
     * @param title
     * @param payKind
     * @param payIdStr
     * @param extendParams
     * @param freeDepositType
     * @return
     */
    public PayVo getPayVOChangeReqOs(String orderNo,CashierEntity cashierEntity,OrderPaySignReqVO orderPaySign,int amt ,String title,String payKind,String payIdStr ,String extendParams,int freeDepositType) {
        PayVo vo = new PayVo();
        Integer paySn = (Objects.isNull(cashierEntity)|| Objects.isNull(cashierEntity.getPaySn()))?0:cashierEntity.getPaySn();
        String reqOs = orderPaySign.getReqOs();
        vo.setInternalNo("1");
        vo.setExtendParams(extendParams);
        vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
        vo.setMemNo(orderPaySign.getMenNo());
//        vo.setOrderNo(orderPaySign.getOrderNo());
        //支持多订单号
        vo.setOrderNo(orderNo);
        vo.setOpenId(orderPaySign.getOpenId());
        //默认赋值
        vo.setReqOs(reqOs);
        vo.setPayAmt(String.valueOf(Math.abs(amt)));
        vo.setPayEnv(env);
        vo.setPayId(payIdStr);
        vo.setPayKind(payKind);
        vo.setPaySn(String.valueOf(paySn));
        vo.setPaySource(orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00"));
        vo.setPayTitle(title);
//        if(freeDepositType == 2) {
//        	vo.setPayType(DataPayTypeConstant.PAY_PRE); //预授权的方式。
//        }else {
        	vo.setPayType(orderPaySign.getPayType());  //默认
//        }
        
        //只有押金才有预授权的情况
        if(DataPayKindConstant.RENT.equals(payKind) || DataPayKindConstant.DEPOSIT.equals(payKind)) {
	        String sourceType = orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00");
	        if(DataPaySourceConstant.ALIPAY.equals(sourceType)){
				//只有押金的时候才有是预授权，其他的情况都是消费
				vo.setPayType(DataPayTypeConstant.PAY_PRE); 
				//设置OS,否则支付宝小程序走的都是信用支付
				
				minigramChangeReqOs(orderNo, vo, reqOs);
				
			}else if(DataPaySourceConstant.WEIXIN_APP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_MP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_H5.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else {
				//默认
				vo.setPayType(orderPaySign.getPayType());
			}
        }
        
        vo.setReqIp(IpUtil.getLocalIp());
        vo.setAtpaySign(StringUtils.EMPTY);
        return vo;
    }
    
    /**
     * 支付宝小程序遇到租客欠款，不允许走信用支付，支付平台根据reqOs来判断来源。
     * @param orderNo
     * @param vo
     * @param reqOs
     */
	public void minigramChangeReqOs(String orderNo, PayVo vo, String reqOs) {
		if(reqOs == null) {
			return;
		}
		log.info("minigramChangeReqOs params orderNo=[{}],vo=[{}],reqOs=[{}]",orderNo,GsonUtils.toJson(vo),reqOs);
		
		//租客押金
		AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositService.selectByOrderNo(orderNo);
		//违章押金
		AccountRenterWzDepositEntity accountRenterWzDepositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
		
		if( (accountRenterDepositEntity != null && accountRenterDepositEntity.getFreeDepositType() == 3) || 
				(accountRenterWzDepositEntity != null && accountRenterWzDepositEntity.getFreeDepositType() == 3) ){
			//比较忽略大小写
			if("miniprogram-alipay".equals(reqOs.toLowerCase())) {
				vo.setReqOs(MICROPROGRAM_COMMON);  //普通预授权
				
			}
			//非支付宝小程序，默认传参本身。
		}else{
			//需要根据标识来区分
			if(!"miniprogram-alipay".equals(reqOs.toLowerCase())) {  //非支付宝小程序
				String convertReqOs = reqOs;
				if(IOS.equals(reqOs.toUpperCase())){
					convertReqOs = CREDITIOS_ALIPAY;
				}else if(ANDROID.equals(reqOs.toUpperCase())){
					convertReqOs = CREDITANDROID_ALIPAY;
				}else{
					convertReqOs = CREDITIOS_ALIPAY;
				}
				vo.setReqOs(convertReqOs);  //信用预授权
			}
			//支付宝小程序，默认传参本身。 miniprogram-alipay
		}
		
		log.info("(result)minigramChangeReqOs params orderNo=[{}],vo=[{}],reqOs=[{}]",orderNo,GsonUtils.toJson(vo),reqOs);
		
	}
    
    
    public PayVo getPayVOForOrderSupplementDetail(String orderNo,CashierEntity cashierEntity,OrderPaySignReqVO orderPaySign,int amt ,String title,String payKind,String payIdStr ,String extendParams,int freeDepositType) {
        PayVo vo = new PayVo();
        Integer paySn = (Objects.isNull(cashierEntity)|| Objects.isNull(cashierEntity.getPaySn()))?0:cashierEntity.getPaySn();
        vo.setInternalNo("1");
        vo.setExtendParams(extendParams);
        vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
        vo.setMemNo(orderPaySign.getMenNo());
//        vo.setOrderNo(orderPaySign.getOrderNo());
        //支持多订单号
        vo.setOrderNo(orderNo);
        vo.setOpenId(orderPaySign.getOpenId());
        vo.setReqOs(orderPaySign.getReqOs());
//        vo.setPayAmt(String.valueOf(Math.abs(amt)));
        vo.setPayAmt(String.valueOf(amt));  //order_supplement_detailk考虑到是正负号
        vo.setPayEnv(env);
        vo.setPayId(payIdStr);
        vo.setPayKind(payKind);
        vo.setPaySn(String.valueOf(paySn));
        vo.setPaySource(orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00"));
        vo.setPayTitle(title);
//        if(freeDepositType == 2) {
//        	vo.setPayType(DataPayTypeConstant.PAY_PRE); //预授权的方式。
//        }else {
        	vo.setPayType(orderPaySign.getPayType());
//        }
        
      //只有押金才有预授权的情况
        if(DataPayKindConstant.RENT.equals(payKind) || DataPayKindConstant.DEPOSIT.equals(payKind)) {
	        String sourceType = orderPaySign.getPaySource().stream().filter(x -> !"00".equals(x)).findFirst().orElse("00");
	        if(DataPaySourceConstant.ALIPAY.equals(sourceType)){
				//只有押金的时候才有是预授权，其他的情况都是消费
				vo.setPayType(DataPayTypeConstant.PAY_PRE); 
			}else if(DataPaySourceConstant.WEIXIN_APP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_MP.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else if(DataPaySourceConstant.WEIXIN_H5.equals(sourceType)){
				vo.setPayType(DataPayTypeConstant.PAY_PUR); 
			}else {
				//默认
				vo.setPayType(orderPaySign.getPayType());
			}
        }
        
        vo.setReqIp(IpUtil.getLocalIp());
        vo.setAtpaySign(StringUtils.EMPTY);
        return vo;
    }
    



    /**
     * 租车押金 收银台回调
     * @param notifyDataVo
     * @param payedOrderRenterDeposit
     */
    public void updataCashierAndRenterDeposit(NotifyDataVo notifyDataVo, PayedOrderRenterDepositReqVO payedOrderRenterDeposit) {
        //1更新收银台,收银台的支付或退款记录都需要记录下来。
        NotifyDataDTO notifyDataDTO = new NotifyDataDTO();
        BeanUtils.copyProperties(notifyDataVo,notifyDataDTO);
        boolean bool = updataCashier(notifyDataDTO);
        //退款的时候，结算记录已经添加了资金进出明细。只有支付的时候才记录。
        if(bool && ( DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType()) )
        		&& "00".equals(notifyDataVo.getTransStatus())){
            //2 租车押金 更新数据
            accountRenterDepositService.updateRenterDeposit(payedOrderRenterDeposit);
        }


    }
    /**
     * 违章押金 收银台回调
     * @param notifyDataVo
     * @param payedOrderRenterWZDeposit
     */
    public void updataCashierAndRenterWzDeposit(NotifyDataVo notifyDataVo, PayedOrderRenterWZDepositReqVO payedOrderRenterWZDeposit) {
        //1更新收银台
        NotifyDataDTO notifyDataDTO = new NotifyDataDTO();
        BeanUtils.copyProperties(notifyDataVo,notifyDataDTO);
        boolean bool = updataCashier(notifyDataDTO);
        if(bool && ( DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType()) )
        		&& "00".equals(notifyDataVo.getTransStatus())){
            //2 违章押金 更新数据
            accountRenterWzDepositService.updateRenterWZDeposit(payedOrderRenterWZDeposit);
        }

    }

    /**
     * 租车费用/补付租车费用
     * @param notifyDataVo
     * @param accountRenterCostReq
     */
    public void updataCashierAndRenterCost(NotifyDataVo notifyDataVo,AccountRenterCostReqVO accountRenterCostReq) {
        //1更新收银台
        NotifyDataDTO notifyDataDTO = new NotifyDataDTO();
        BeanUtils.copyProperties(notifyDataVo,notifyDataDTO);
        boolean bool = updataCashier(notifyDataDTO);
        if(bool && ( DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType()) )
        		&& "00".equals(notifyDataVo.getTransStatus())){  //考虑支付成功
            //2  实收租车费用落库 更新数据
            accountRenterCostSettleService.insertRenterCostDetail(accountRenterCostReq);
        }

    }

    /**
     * 返回租车费用补付记录数
     * @param orderNo
     * @param menNo
     * @param payKind   按消费来处理。
     * @return
     */
    public String getCashierRentCostPaySn(String orderNo, String menNo, String payKind) {
//        List<CashierEntity> sashierEntitys = cashierMapper.getCashierRentCosts(orderNo,menNo,DataPayKindConstant.RENT_AMOUNT,DataPayTypeConstant.PAY_PUR);
    	List<CashierEntity> sashierEntitys = cashierMapper.getCashierRentCosts(orderNo,menNo,payKind,DataPayTypeConstant.PAY_PUR);
        if(CollectionUtils.isEmpty(sashierEntitys)){
            return "1";
        }
        return String.valueOf(sashierEntitys.size()+1);
    }

    /**
     * 支付签名
     * @param payVos
     * @return
     * TODO 公私钥
     */
    public String getPaySignByPayVos(List<PayVo> payVos) {
        String signs = StringUtils.EMPTY;
        try {
            if(!CollectionUtils.isEmpty(payVos)){
                for(int i=0;i<payVos.size();i++){
                    PayVo payVo = payVos.get(i);
                    String reqContent = FasterJsonUtil.toJson(payVo);
                    //私钥加密
                    String sign = RSASecurityUtils.privateKeySignature(AESUtil.keySign,reqContent);
                    payVo.setAtpaySign(sign);
                }
            }
            BatchPayVo bpv = new BatchPayVo();
            bpv.setLstPayVo(payVos);
            String reqContents = FasterJsonUtil.toJson(bpv);
            //私钥加密
            signs = AESSecurityUtils.encrypt(AESUtil.keyAec, reqContents);
        } catch (Exception e) {
            log.error("getPaySignByPayVos 支付验签数据失败 ，param ;[{}],e:[{}]",FasterJsonUtil.toJson(payVos),e);
            throw new OrderPaySignParamException();
        }
        return signs;
    }

    /**
     * 钱包支付成功 收银台落库
     * @param orderPaySign
     * @param amtWallet   DataPayKindConstant.RENT_AMOUNT
     */
    public CashierEntity insertRenterCostByWallet(OrderPaySignReqVO orderPaySign, int amtWallet,RenterCashCodeEnum renterCashCodeEnum,String payKind) {
    	CashierEntity cashier = null;
        if(amtWallet!=0){
//1查询 收银台保存 钱包支付信息
//            CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),DataPayKindConstant.RENT_AMOUNT,DataPayTypeConstant.PAY_PUR);
        	
            int result = 0;
            int cashierId=0;
//            RenterCashCodeEnum renterCashCodeEnum = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
            cashier = new CashierEntity ();
            BeanUtils.copyProperties(orderPaySign,cashier);
            ///add 200819
            cashier.setOs(orderPaySign.getReqOs());
            cashier.setPayEvn(env);
            cashier.setInternalNo("1");
            cashier.setQn("666666667777777788888888"+payKind);
            cashier.setPayTime(LocalDateTimeUtils.dateToString(new Date()));
            ///
            cashier.setMemNo(orderPaySign.getMenNo());
            cashier.setPayAmt(amtWallet);
            cashier.setPaySource(PaySourceEnum.WALLET_PAY.getCode());
            cashier.setPayTitle("订单号：" + orderPaySign.getOrderNo() + "钱包支付金额：" + amtWallet + "元");
            cashier.setPayKind(payKind);
            cashier.setPayType(DataPayTypeConstant.PAY_PUR);
            cashier.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
            cashier.setTransStatus("00");
            cashier.setPaySn(NumberUtils.INTEGER_ONE);
            ///add 200819
            String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(cashier));
            cashier.setPayMd5(payMd5);
            
            result = cashierMapper.insertSelective(cashier);
            cashierId = cashier.getId();
            if(result ==0){
                throw new AccountRenterDepositDBException();
            }

            /**
             * 走异步通知来初始化数据，避免重复操作。
             */
            //2 构造参数  记录个人 租车费用户头 记录 钱包支付信息
//            AccountRenterCostReqVO accountRenterCostReq = new AccountRenterCostReqVO();
//            BeanUtils.copyProperties(orderPaySign,accountRenterCostReq);
//            accountRenterCostReq.setShifuAmt(Math.abs(amtWallet));
//            accountRenterCostReq.setMemNo(orderPaySign.getMenNo());
//
//            AccountRenterCostDetailReqVO accountRenterCostDetailReq = new AccountRenterCostDetailReqVO();
//            BeanUtils.copyProperties(orderPaySign,accountRenterCostDetailReq);
//            accountRenterCostDetailReq.setMemNo(orderPaySign.getMenNo());
//            accountRenterCostDetailReq.setUniqueNo(String.valueOf(cashierId));
//            accountRenterCostDetailReq.setAmt(Math.abs(amtWallet));
//            accountRenterCostDetailReq.setTransTime(LocalDateTime.now());
//            accountRenterCostDetailReq.setRenterCashCodeEnum(renterCashCodeEnum);
//            accountRenterCostDetailReq.setPaySource(PaySourceEnum.WALLET_PAY.getText());
//            accountRenterCostDetailReq.setPaySourceCode(PaySourceEnum.WALLET_PAY.getCode());
//            accountRenterCostDetailReq.setPayTypeCode(orderPaySign.getPayType());
//            accountRenterCostDetailReq.setPayType(PayTypeEnum.getFlagText(orderPaySign.getPayType()));
//            accountRenterCostReq.setAccountRenterCostDetailReqVO(accountRenterCostDetailReq);
//            accountRenterCostSettleService.insertRenterCostDetail(accountRenterCostReq);
        }
        
        return cashier;
    }

    /**
     * 退款请求参数构造
     * @param cashierRefundApply
     * @return
     */
    public RefundVo getRefundVo(CashierRefundApplyEntity cashierRefundApply) {
        RefundVo refundVo = new RefundVo();
        BeanUtils.copyProperties(cashierRefundApply,refundVo);
        refundVo.setRefundId(cashierRefundApply.getId().toString());
//        refundVo.setPayType(DataPayTypeConstant.PUR_RETURN);
        refundVo.setPayType(cashierRefundApply.getPayType());    //根据退款表中的记录来处理。这里不处理逻辑。
        refundVo.setReqIp(IpUtil.getLocalIp());
        refundVo.setPaySn(String.valueOf(cashierRefundApply.getNum()+1));
        refundVo.setExtendParams(GsonUtils.toJson(cashierRefundApply));
        refundVo.setAtpaySign(StringUtils.EMPTY);
        refundVo.setPayEnv(env);
        refundVo.setInternalNo("1");
        refundVo.setReqOs("IOS");
        refundVo.setRefundAmt(String.valueOf(Math.abs(cashierRefundApply.getAmt())));
//        ///重新赋值
//        if("04".equals(cashierRefundApply.getPayType())) {
//        	refundVo.setQn(cashierRefundApply.getPayTransNo());
//        }
        
        String payMd5 =  MD5.MD5Encode(FasterJsonUtil.toJson(refundVo));
        refundVo.setPayMd5(payMd5);
        String reqContent = FasterJsonUtil.toJson(refundVo);
        //TODO 签名串
        String sign = StringUtils.EMPTY;
        try {
            sign =RSASecurityUtils.privateKeySignature(AESUtil.keySign,reqContent);
        } catch (Exception e) {
            log.error("refundOrderPay 支付验签数据失败 ，param ;[{}],e:[{}]",FasterJsonUtil.toJson(refundVo),e);
            throw new OrderPaySignParamException();
        }
        refundVo.setAtpaySign(sign);
        return refundVo;
    }

    /**
     * 查询补付记录
     * @param orderNo
     * @param renterMemNo
     * @param payKind
     * @return
     */
    public List<CashierEntity> getCashierEntitys(String orderNo, String renterMemNo, String payKind) {
        List<CashierEntity> cashierEntity = cashierMapper.getPayAmtByPayKinds(orderNo,renterMemNo,payKind);
        if(CollectionUtils.isEmpty(cashierEntity)){
            return Collections.emptyList();
        }
        return cashierEntity;
    }
    /**
     * 退款成功事件
     * @param orderNo
     */
    public void sendOrderRefundSuccessMq(String orderNo, FineSubsidyCodeEnum type,AutoPayResultVo notifyDataVo) {
        OrderRefundMq orderSettlementMq = new OrderRefundMq();
        orderSettlementMq.setType(Integer.valueOf(type.getFineSubsidyCode()));
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS.routingKey,orderMessage);
    }
    /**
     * 退款失败事件
     * @param orderNo
     */
    public void sendOrderRefundFailMq(String orderNo, FineSubsidyCodeEnum type) {
        OrderRefundMq orderSettlementMq = new OrderRefundMq();
        orderSettlementMq.setType(Integer.valueOf(type.getFineSubsidyCode()));
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_REFUND_FAIL.exchange,NewOrderMQActionEventEnum.ORDER_REFUND_FAIL.routingKey,orderMessage);
    }
    
    /*
     * @Author ZhangBin
     * @Date 2020/6/3 15:27 
     * @Description: 根据订单号和交易流水好查询
     * 
     **/
    public CashierEntity getCashierBypayTransNo(String orderNo,String payTransNo){
        CashierEntity cashierEntity = cashierMapper.getCashierBypayTransNo(orderNo, payTransNo);
        return cashierEntity;
    }
}
