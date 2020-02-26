package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.mapper.RenterOrderWzFinishedTodoMapper;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * RenterOrderWzFinishedTodoService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzFinishedTodoService {

    @Resource
    private RenterOrderWzFinishedTodoMapper renterOrderWzFinishedTodoMapper;

    public void updateStatus(Integer id) {
        renterOrderWzFinishedTodoMapper.updateStatus(id);
    }

    public List<IllegalToDO> queryTodo(List<IllegalToDO> todoList) {
        List<IllegalToDO> list = this.queryToday();
        if (list==null || list.size()==0) {
            //如果没有查到今天有数据，则插入新数据
            this.batchInsert(todoList);
        }
        /**
         * 无论是否插入数据，均查询最新的数据结果（包含今天的和之前未执行完的）
         */
        return this.queryFinishedToDo();
    }

    private List<IllegalToDO> queryFinishedToDo() {
        return renterOrderWzFinishedTodoMapper.queryFinishedToDo();
    }

    private void batchInsert(List<IllegalToDO> todoList) {
        renterOrderWzFinishedTodoMapper.batchInsert(todoList);
    }

    private List<IllegalToDO> queryToday() {
        return renterOrderWzFinishedTodoMapper.queryToday();
    }
}
