package com.itheima.mp.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CoordinateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@ApiModel(description = "分页结果")
public class PageDTO<T> {
    @ApiModelProperty("总条数")
    private Long total;
    @ApiModelProperty("总页数")
    private Long pages;
    @ApiModelProperty("集合")
    private List<T> list;

    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Class<VO> clazz) {
        // 1. 创建返回给前端的大对象 PageDTO
        PageDTO<VO> pageDTO = new PageDTO<>();
        // 2. 把总条数、总页数放进去
        pageDTO.setTotal(p.getTotal());
        pageDTO.setPages(p.getPages());
        List<PO> records = p.getRecords();
        // 4. 【性能优化】如果查出来是空的，直接返回空列表，不要去执行拷贝逻辑了
        if (CollUtil.isEmpty(records)) {
            pageDTO.setList(Collections.emptyList());
            return pageDTO;
        }
        // 使用 Hutool 的 BeanUtil 工具，把 User 列表里的属性，批量拷贝到 UserVO 列表中
        pageDTO.setList(BeanUtil.copyToList(records, clazz));
        return pageDTO;

    }

    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Function<PO, VO> convertor) {
        // 1. 创建返回给前端的大对象 PageDTO
        PageDTO<VO> pageDTO = new PageDTO<>();
        // 2. 把总条数、总页数放进去
        pageDTO.setTotal(p.getTotal());
        pageDTO.setPages(p.getPages());
        List<PO> records = p.getRecords();
        // 4. 【性能优化】如果查出来是空的，直接返回空列表，不要去执行拷贝逻辑了
        if (CollUtil.isEmpty(records)) {
            pageDTO.setList(Collections.emptyList());
            return pageDTO;
        }
        pageDTO.setList(records.stream().map(convertor).collect(Collectors.toList()));
        return pageDTO;

    }
}
