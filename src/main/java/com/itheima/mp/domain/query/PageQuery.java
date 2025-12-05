package com.itheima.mp.domain.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "分页查询实体")
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo = 1;
    @ApiModelProperty("页面大小")
    private Integer pageSize = 5;
    @ApiModelProperty("排序字段")
    private String sortBy;
    @ApiModelProperty("是否升序")
    private Boolean isAsc = true;

    public <T> Page<T> toMPPage(OrderItem... items) {

        Page<T> page = Page.of(pageNo, pageSize);
        if (StrUtil.isNotBlank(sortBy)) {
            page.addOrder(new OrderItem(sortBy, isAsc));
        } else if (items != null) {
            page.addOrder(items);
        }
        return page;
    }

    public <T> Page<T> toMPPage(String defaultSortBy, Boolean defaultAsc) {
        return toMPPage(new OrderItem(defaultSortBy, defaultAsc));
    }

    public <T> Page<T> toMPPageDefaultSortByCreateTime() {
        return toMPPage(new OrderItem("create_time", false));

    }

    public <T> Page<T> toMPPageDefaultSortByUpdateTime() {
        return toMPPage(new OrderItem("update_time", false));

    }

}
