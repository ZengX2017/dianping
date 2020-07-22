package com.example.dianping.service.impl;

import com.example.dianping.common.ResultEnum;
import com.example.dianping.dao.UserModelMapper;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.UserModel;
import com.example.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author Adward_Z
 * @date 2020/7/22
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserModelMapper mapper;

    @Override
    public UserModel getUser(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public UserModel register(UserModel userModel) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        userModel.setPassword(encoudeByMD5(userModel.getPassword()));
        userModel.setCreatedTime(new Date());
        userModel.setUpdateTime(new Date());
        try{
            mapper.insertSelective(userModel);

        }catch (DuplicateKeyException e){
            throw new BussinessException(ResultEnum.REGISTER_DUP_FAIL);
        }
        return getUser(userModel.getId());
    }

    private String encoudeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest(password.getBytes("utf-8")));
    }
}
