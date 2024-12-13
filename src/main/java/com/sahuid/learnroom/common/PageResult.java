package com.sahuid.learnroom.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;

    private List<T> data;


    public static <T> PageResult<T> pageToResult(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setData(page.getRecords());
        return result;
    }
}
