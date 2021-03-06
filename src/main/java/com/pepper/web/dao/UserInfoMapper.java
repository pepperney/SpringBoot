package com.pepper.web.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pepper.web.model.entity.UserInfo;
@Component
public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

	List<UserInfo> selectAllUsers();
}