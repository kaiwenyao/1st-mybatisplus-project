package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public void deductBalance(Long id, Integer amount) {
        // 查询用户
        User user = getById(id);
        // 校验用户状态
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常！");
        }

        // 校验余额是否充足
        if (user.getBalance() < amount) {
            throw new RuntimeException("用户余额不足！");
        }

        // 扣减余额
//        baseMapper.deductBalance(id, amount);
        int remainBalance = user.getBalance() - amount;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, UserStatus.FROZEN)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 乐观锁
                .update();
    }


    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance,
                                 Integer maxBalance) {
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        // 查询用户
        User user = getById(id);
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常！");
        }


        // 查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();
        // 转user的po 到 vo
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 转地址 vo
        if (CollUtil.isNotEmpty(addresses)) {
            userVO.setAddressList(BeanUtil.copyToList(addresses, AddressVO.class));
        }
        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        // 查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        // 查询地址
        // 获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        // 根据用户id查询地址
        // 获取所有的地址！！！
        List<Address> addressList = Db.lambdaQuery(Address.class)
                .in(Address::getUserId, userIds)
                .list();
        List<AddressVO> addressVOList = BeanUtil.copyToList(addressList, AddressVO.class);
        // 按照不同用户 手动分组地址集合
        Map<Long, List<AddressVO>> addressMap = new HashMap<>();

        if (CollUtil.isNotEmpty(addressList)) {
            addressMap =
                    addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        // 转换 VO 返回
        List<UserVO> userList = new ArrayList<>(users.size());
        for (User user : users) {
            UserVO uservo = BeanUtil.copyProperties(user, UserVO.class);
            userList.add(uservo);
            // address vo
            uservo.setAddressList(addressMap.get(user.getId()));
        }

        return userList;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        // 条件
        String name = query.getName();
        Integer status = query.getStatus();
        Page<User> page = query.toMPPageDefaultSortByUpdateTime();

        // 查询
        Page<User> p = lambdaQuery()
                // 动态 SQL：如果 name 不为空，拼接 WHERE username LIKE %name%
                .like(name != null, User::getUsername, name)
                // 动态 SQL：如果 status 不为空，拼接 AND status = ?
                .eq(status != null, User::getStatus, status)
                // 【关键】传入之前准备好的 page 对象，触发分页插件
                .page(page);
        // .page(page) 是触发点。此时 MP 会拿着你设置的条件和 page 里的参数，去数据库执行两条 SQL。

//        return PageDTO.of(p, user -> BeanUtil.copyProperties(user, UserVO.class));
        return PageDTO.of(p, user -> {
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            userVO.setUsername(userVO.getUsername().substring(0,
                    userVO.getUsername().length() - 2) + "**");
            return userVO;
        });

    }
}
