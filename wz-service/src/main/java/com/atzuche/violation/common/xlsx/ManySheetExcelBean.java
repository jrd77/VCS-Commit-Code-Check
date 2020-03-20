package com.atzuche.violation.common.xlsx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author 胡春林
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ManySheetExcelBean {

    /**
     * sheet的名称
     */
    private String fileName;

    /**
     * sheet里的标题
     */
    private String[] handers;

    /**
     * sheet里的数据集
     */
    private List<Object[]> dataset;
}