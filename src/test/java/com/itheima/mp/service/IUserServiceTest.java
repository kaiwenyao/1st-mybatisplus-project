package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class IUserServiceTest {
    @Autowired
    private IUserService userService;
    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("Kw");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo(UserInfo.of(122, "laoshi", "female"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);
    }
    @Test
    void testPage() {
        // 分页条件
        int pageNo = 1,  pageSize = 2;
        Page<User> page = Page.of(pageNo, pageSize);
        // 排序条件
        page.addOrder(new OrderItem("balance", true));
        page.addOrder(new OrderItem("id", true));

        Page<User> p = userService.page(page);
        long total = p.getTotal();
        System.out.println("total = " + total);
        long pages = p.getPages();
        System.out.println("pages = " + pages);
        List<User> records = p.getRecords();
        records.forEach(System.out::println);
    }
}
