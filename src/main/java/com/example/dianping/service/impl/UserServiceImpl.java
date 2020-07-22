package com.example.dianping.service.impl;

import com.example.dianping.dao.UserModelMapper;
import com.example.dianping.model.UserModel;
import com.example.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
