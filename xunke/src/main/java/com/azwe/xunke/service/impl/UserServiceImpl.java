package com.azwe.xunke.service.impl;

import com.azwe.xunke.dal.UserModelMapper;
import com.azwe.xunke.model.UserModel;
import com.azwe.xunke.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by AZ
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserModelMapper userModelMapper;

    @Override
    public UserModel getUser(Integer id) {
        return userModelMapper.selectByPrimaryKey(id);
    }
}
