package com.atzuche.order.wallet.server.service;

import com.atzuche.order.commons.entity.dto.MemberOrderDebtDTO;
import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.commons.vo.req.SearchMemberOrderDebtListReqVO;
import com.atzuche.order.wallet.server.entity.BalanceEntity;
import com.atzuche.order.wallet.server.entity.DepositDebtVO;
import com.atzuche.order.wallet.server.entity.TransSupplementDetailEntity;
import com.atzuche.order.wallet.server.mapper.MemberMapper;
import com.atzuche.order.wallet.server.mapper.TransSupplementDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/16 4:18 下午
 **/
@Service
public class DebtService {
    
    private final static Logger logger = LoggerFactory.getLogger(DebtService.class);
    

    @Autowired
    private TransSupplementDetailMapper transSupplementDetailMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 返回用户名下的欠款
     * @param memNo
     * @return
     */
    public int getMemDebtTotal(String memNo){
        List<TransSupplementDetailEntity> detailEntityList = transSupplementDetailMapper.findDebtByMemNo(memNo);
        int totalDebt =0;
        for(TransSupplementDetailEntity entity:detailEntityList){
            totalDebt = totalDebt +entity.getAmt();
        }
        BalanceEntity balanceEntity = memberMapper.getByMemNo(memNo);

        if(balanceEntity!=null&&balanceEntity.getBalance()!=null&&balanceEntity.getBalance()<0){
            totalDebt = totalDebt+(-balanceEntity.getBalance());
        }

        return totalDebt;
    }

    protected int getOldDebt(String memNo){
        BalanceEntity balanceEntity = memberMapper.getByMemNo(memNo);
        if(balanceEntity!=null&&balanceEntity.getBalance()!=null&&balanceEntity.getBalance()<0){
             return -balanceEntity.getBalance();
        }
        return 0;
    }

    @Transactional
    public void deductDebt(String memNo,int amt){
        int oldDebt = getOldDebt(memNo);
        logger.info("oldDebt=[{}],memNo=[{}]",oldDebt,memNo);
        //有老欠款
        if(oldDebt>0) {
            if (amt <= oldDebt) {
                logger.info("deduct oldDebt amt=[{}],memNo=[{}]",amt,memNo);
                deductOldDebt(memNo,amt);
            } else {
                logger.info("deduct newDebt amt=[{}],oldDebt=[{}],memNo=[{}]",amt-oldDebt,oldDebt,memNo);
                deductOldDebt(memNo,oldDebt);
                deductNewDebt(memNo,amt-oldDebt);
            }
        }else{//无老欠款
            logger.info("deduct newDebt amt=[{}],memNo=[{}],no oldDebt",amt,memNo);
             deductNewDebt(memNo,amt);
        }
        //FIXME:添加日志
    }

    protected void deductOldDebt(String memNo,int amt){
        memberMapper.deductDebt(memNo, amt);
    }

    protected void deductNewDebt(String memNo,int amt){
        List<TransSupplementDetailEntity> detailEntityList = transSupplementDetailMapper.findDebtByMemNo(memNo);
        int left = amt;
        for(TransSupplementDetailEntity entity:detailEntityList){
            if(left>0){
                if(left>=entity.getAmt()) {
                    left = left - entity.getAmt();
                    deductDebt(entity.getId());
                }else{
                    deductDebt(entity.getId());
                    insertNewDebt(entity,entity.getAmt()-left);
                    left=0;
                }
            }
        }
    }

    protected void insertNewDebt(TransSupplementDetailEntity entity,int newDebt){
        entity.setAmt(newDebt);
        entity.setPayFlag(1);
        entity.setRemark("从记录"+entity.getId()+"中转移部分欠款");
        transSupplementDetailMapper.insertSelective(entity);
    }

    protected boolean deductDebt(Integer id){
        int count = transSupplementDetailMapper.updatePayStatus(id);
        if(count==0){
            return false;
        }
        return true;
    }
    
    
    /**
     * 返回用户名下的欠款(区分历史欠款和订单欠款还包含违章押金和车辆押金循环扣不下来的欠款)
     * @param memNo
     * @return DebtDetailVO
     */
    public DebtDetailVO getDebtDetailVO(String memNo){
        List<TransSupplementDetailEntity> detailEntityList = transSupplementDetailMapper.findDebtByMemNo(memNo);
        int orderDebtAmt = 0;
        if (detailEntityList != null && !detailEntityList.isEmpty()) {
        	for(TransSupplementDetailEntity entity:detailEntityList){
            	orderDebtAmt = orderDebtAmt + entity.getAmt();
            }
        }
        BalanceEntity balanceEntity = memberMapper.getByMemNo(memNo);
        int historyDebtAmt = 0;
        if(balanceEntity!=null&&balanceEntity.getBalance()!=null&&balanceEntity.getBalance()<0){
        	historyDebtAmt = -balanceEntity.getBalance();
        }
        // 车辆押金欠款
 		List<DepositDebtVO> rentList = transSupplementDetailMapper.listRentDepositDebt(memNo);
 		if (rentList != null && !rentList.isEmpty()) {
 			for (DepositDebtVO ddebt:rentList) {
 				if (ddebt != null && ddebt.getTransAmt() != null) {
 					historyDebtAmt += ddebt.getTransAmt();
 				}
 			}
 		}
 		// 违章押金欠款
 		List<DepositDebtVO> wzList = transSupplementDetailMapper.listIllegalDepositDebt(memNo);
 		if (wzList != null && !wzList.isEmpty()) {
 			for (DepositDebtVO ddebt:wzList) {
 				if (ddebt != null && ddebt.getTransAmt() != null) {
 					historyDebtAmt += ddebt.getTransAmt();
 				}
 			}
 		}
        DebtDetailVO debtDetailVO = new DebtDetailVO();
        debtDetailVO.setHistoryDebtAmt(historyDebtAmt);
        debtDetailVO.setOrderDebtAmt(orderDebtAmt);
        return debtDetailVO;
    }

    /**
     * 查询用户欠款订单
     * @param reqVO
     * @return
     */
    public List<MemberOrderDebtDTO> selectMemberOrderDebtList(SearchMemberOrderDebtListReqVO reqVO){
        return transSupplementDetailMapper.selectMemberOrderDebtList(reqVO);
    }

}
