package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * RenterOrderWzDetailService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzDetailService {

    @Resource
    private RenterOrderWzDetailMapper renterOrderWzDetailMapper;

    public void updateIsValid(String orderNo, String carNum) {
        renterOrderWzDetailMapper.updateIsValid(orderNo,carNum);
    }

    public void addIllegalDetailFromRenyun(RenterOrderWzDetailEntity illegal) {
        renterOrderWzDetailMapper.addIllegalDetailFromRenyun(illegal);
    }

    public int updateFeeByWzCode(RenterOrderWzDetailEntity illegal){
        return renterOrderWzDetailMapper.updateFeeByWzCode(illegal);
    }

    public int countIllegalDetailByOrderNo(String orderNo, Date illegalTime, String illegalAddr, String code, String plateNum) {
        Integer count = renterOrderWzDetailMapper.countIllegalDetailByOrderNo(orderNo,illegalTime,illegalAddr,code,plateNum);
        return count == null ? 0 : count;
    }

    public void batchInsert(List<RenterOrderWzDetailEntity> temps) {
        renterOrderWzDetailMapper.batchInsert(temps);
    }

    public List<RenterOrderWzDetailEntity> findDetailByOrderNo(String orderNo, String plateNum) {
        return renterOrderWzDetailMapper.findDetailByOrderNo(orderNo,plateNum);
    }

    public List<RenterOrderWzDetailEntity> findSendSmsIllegalRecord() {
        return renterOrderWzDetailMapper.findSendSmsIllegalRecord();
    }

    public void updateSmsStatus(String orderNo) {
        renterOrderWzDetailMapper.updateSmsStatus(orderNo);
    }

    public void updateOwnerSmsStatus(String orderNo) {
        renterOrderWzDetailMapper.updateOwnerSmsStatus(orderNo);
    }

    public int queryIllegalCountByCarNoAndOrders(List<String> orders, String carNo) {
        Integer count = renterOrderWzDetailMapper.queryIllegalCountByCarNoAndOrders(orders,carNo);
        return count == null ? 0 : count;
    }

    public List<RenterOrderWzDetailEntity> findTransIllegalDetailByOrderNo(String orderNo) {
        return renterOrderWzDetailMapper.findTransIllegalDetailByOrderNo(orderNo);
    }

    public Integer getIllegalDetailCount(String orderNo, String illegalNum) {
        return renterOrderWzDetailMapper.getIllegalDetailCount(orderNo,illegalNum);
    }

    /**
     * 变更违章为已处理状态
     * @param orderNo 订单号
     */
    public void updateIllegalStatus(String orderNo){
        renterOrderWzDetailMapper.updateIllegalStatus(orderNo);
    }

    /**
     * 新增违章记录
     * @param renterOrderWzDetail
     * @return
     */
    public Integer saveRenterOrderWzDetail(RenterOrderWzDetailEntity renterOrderWzDetail){
        return renterOrderWzDetailMapper.saveRenterOrderWzDetail(renterOrderWzDetail);
    }

    /**
     * 删除违章记录
     * @param id
     * @return
     */
    public Integer deleteRenterOrderWzDetailById(Long id){
        return renterOrderWzDetailMapper.deleteRenterOrderWzDetailById(id);
    }

    /**
     * 变更违章为已处理状态
     * @param id
     * @return
     */
    public Integer updateIllegalStatusById(Long id){
       return renterOrderWzDetailMapper.updateIllegalStatusById(id);
    }

    /**
     *
     * @return 查询列表
     */
   public List<RenterOrderWzDetailEntity> queryList(String orderNo){
       return renterOrderWzDetailMapper.queryList(orderNo);
   }
}
