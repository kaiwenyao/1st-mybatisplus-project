package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiModel("body参数")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @ApiOperation("新增用户接口")
    @PostMapping
    public void saveUser(@RequestBody UserFormDTO userFormDTO) {
        // DTO 拷贝到PO
//        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        // 新增
        userService.save(user);
    }

    @ApiOperation("删除用户接口")
    @DeleteMapping("{id}")
    public void deleteUserById(@ApiParam("用户ID") @PathVariable("id") Long id) {
        userService.removeById(id);
    }

    @ApiOperation("根据id查询用户")
    @GetMapping("{id}")
    public UserVO queryUserById(@ApiParam("用户ID") @PathVariable("id") Long id) {
        // 查询PO
        return userService.queryUserAndAddressById(id);
    }

    @ApiOperation("根据多个id批量查询用户")
    @GetMapping
    public List<UserVO> queryUserByIds(@ApiParam("用户ID集合") @RequestParam("ids") List<Long> ids) {
        // 查询PO
        return userService.queryUserAndAddressByIds(ids);


    }

    @ApiOperation("扣减用户余额")
    @PutMapping("/{id}/deduction/{amount}")
    public void deductMoneyById(
            @ApiParam("用户id") @PathVariable("id") Long id,
            @ApiParam("扣减金额") @PathVariable("amount") Integer amount
    ) {
        userService.deductBalance(id, amount);
    }

    @ApiOperation("根据复杂条件，查询多个用户")
    @GetMapping("/list")
    public List<UserVO> queryUsers(UserQuery query) {
        List<User> users = userService.queryUsers(query.getName(),
                query.getStatus(),
                query.getMinBalance(),
                query.getMaxBalance()
        );
        return BeanUtil.copyToList(users, UserVO.class);
    }
}
