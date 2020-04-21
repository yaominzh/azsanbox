package com.azwe.xunke.service;

import com.azwe.xunke.common.BusinessException;
import com.azwe.xunke.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by AZ
 */

public interface UserService {
    UserModel getUser(Integer id);
    UserModel register(UserModel userModel) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;
    UserModel login(String telphone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException;
    Integer countAllUser();

}
