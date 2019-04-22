package com.pepper.web.serviceImpl;

import java.util.List;

import com.pepper.common.consts.RedisKey;
import com.pepper.web.aspect.Read;
import com.pepper.web.aspect.Write;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pepper.common.util.JsonUtil;
import com.pepper.web.helper.RedisHelper;
import com.pepper.web.dao.UserInfoMapper;
import com.pepper.web.model.entity.UserInfo;
import com.pepper.web.service.UserInfoService;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisHelper redisHelper;

    @Override
    @Write
    public void addUser(UserInfo user) {
        userInfoMapper.insertSelective(user);

    }

    @Override
    @Write
    public void delUser(int userId) {
        userInfoMapper.deleteByPrimaryKey(userId);

    }

    @Override
    @Write
    public void updateUser(UserInfo user) {
        userInfoMapper.updateByPrimaryKeySelective(user);

    }

    @Override
    @Read
    public UserInfo getUser(int userId) {
        return userInfoMapper.selectByPrimaryKey(userId);
    }

    @Override
    @Read
    public List<UserInfo> getAllUsers() {
        return userInfoMapper.selectAllUsers();
    }


    @Override
    @Read
    public UserInfo getUserDetail(int userId) {
        UserInfo data = null;
        String key = RedisKey.USER_ID.getKey() + userId;
        String value = redisHelper.get(key);
        if (StringUtils.isNotEmpty(value)) {
            data = JsonUtil.jsonToObject(value, UserInfo.class);
        } else {
            data = userInfoMapper.selectByPrimaryKey(userId);
            redisHelper.setex(key, RedisKey.USER_ID.getExpireTime(), JsonUtil.toJson(data));
        }
        return data;
    }

    @Override
    public UserInfo getByToken(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Write
    public void saveUser(UserInfo user) {
        if (null == user.getUserId()) {
            userInfoMapper.insert(user);
        } else {
            userInfoMapper.updateByPrimaryKeySelective(user);
            redisHelper.del(RedisKey.USER_ID.getKey() + user.getUserId());
        }
    }


}
