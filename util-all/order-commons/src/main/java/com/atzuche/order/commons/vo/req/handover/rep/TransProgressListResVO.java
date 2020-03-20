package com.atzuche.order.commons.vo.req.handover.rep;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * TransProgressListResVO
 *
 * @author shisong
 * @date 2020/3/20
 */
public class TransProgressListResVO implements Serializable {

    private List<TransProgressResVO> list;

    public List<TransProgressResVO> getList() {
        return list;
    }

    public void setList(List<TransProgressResVO> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "TransProgressListResVO{" +
                "list=" + list +
                '}';
    }
}
