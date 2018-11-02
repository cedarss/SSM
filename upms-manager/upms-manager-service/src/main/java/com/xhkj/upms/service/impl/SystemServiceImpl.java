package com.xhkj.upms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xhkj.common.exception.CustomException;
import com.xhkj.upms.dao.DbLogMapper;
import com.xhkj.upms.entity.DbLog;
import com.xhkj.upms.service.log.SystemService;

@Service
public class SystemServiceImpl implements SystemService{

	
	@Autowired
	DbLogMapper dbDao;
	
	@Override
	public int addDbLog(DbLog dbLog){
		if(dbDao.insertSelective(dbLog) != 1){
			throw new CustomException("保存日志失败");
		}
		
		return 1;
	}
}
