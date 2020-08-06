package com.example.dianping.service;

import com.example.dianping.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
public interface UserService {
    UserModel getUser(Integer id);

    UserModel register(UserModel userModel) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    UserModel login(String phone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException;
}
