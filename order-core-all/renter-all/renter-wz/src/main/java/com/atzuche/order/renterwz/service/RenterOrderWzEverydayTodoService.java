package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzEverydayTodoEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzEverydayTodoMapper;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * RenterOrderWzEverydayTodoService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzEverydayTodoService {

    @Resource
    private RenterOrderWzEverydayTodoMapper renterOrderWzEverydayTodoMapper;

    public List<IllegalToDO> queryTodo(List<IllegalToDO> todoList) {
        List<IllegalToDO> list = this.queryToday();
        if (list==null || list.size()==0) {
            //如果没有查到今天有数据，则插入新数据
            this.batchInsert(todoList);
        }
        /**
         * 无论是否插入数据，均查询最新的数据结果（包含今天的和之前未执行完的），
         * 注:由于数据插入表的时候是BATCHINSERT的，所以不存在漏数据的情况，手动改数据的情况均不重新增加到all中！
         */
        return this.queryEveryDayToDo();
    }

    private List<IllegalToDO> queryEveryDayToDo() {
        return renterOrderWzEverydayTodoMapper.queryEveryDayToDo();
    }

    private void batchInsert(List<IllegalToDO> list) {
        if (list!=null && list.size()>0) {
            renterOrderWzEverydayTodoMapper.batchInsert(list);
        }
    }

    private List<IllegalToDO> queryToday() {
        return renterOrderWzEverydayTodoMapper.queryToday();
    }

    public void updateStatus(Integer id) {
        renterOrderWzEverydayTodoMapper.updateStatus(id);
    }
}
