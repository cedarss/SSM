package com.xhkj.upms.dao;

import com.xhkj.upms.entity.DbLog;
import com.xhkj.upms.entity.DbLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DbLogMapper {
    int countByExample(DbLogExample example);

    int deleteByExample(DbLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DbLog record);

    int insertSelective(DbLog record);

    List<DbLog> selectByExample(DbLogExample example);

    DbLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DbLog record, @Param("example") DbLogExample example);

    int updateByExample(@Param("record") DbLog record, @Param("example") DbLogExample example);

    int updateByPrimaryKeySelective(DbLog record);

    int updateByPrimaryKey(DbLog record);
}