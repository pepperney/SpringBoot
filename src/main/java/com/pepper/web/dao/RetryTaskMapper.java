package com.pepper.web.dao;

import com.pepper.web.model.entity.RetryTask;

import java.util.List;


public interface RetryTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RetryTask record);

    int insertSelective(RetryTask record);

    RetryTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RetryTask record);

    int updateByPrimaryKeyWithBLOBs(RetryTask record);

    int updateByPrimaryKey(RetryTask record);

    RetryTask selectRetryTask(String type, String bizNo);
}