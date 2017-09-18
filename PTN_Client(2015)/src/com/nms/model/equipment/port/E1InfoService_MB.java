package com.nms.model.equipment.port;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.equipment.port.E1Info;
import com.nms.db.dao.equipment.port.E1InfoMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class E1InfoService_MB extends ObjectService_Mybatis {
	private E1InfoMapper mapper = null;
	
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	
	public E1InfoMapper getMapper() {
		return mapper;
	}

	public void setMapper(E1InfoMapper mapper) {
		this.mapper = mapper;
	}

	public List<E1Info> selectByCondition(E1Info e1Info) throws Exception {
		List<E1Info> e1InfoList = null;		
		try {
			e1InfoList = new ArrayList<E1Info>();
			e1InfoList = mapper.queryByCondition(e1Info);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return e1InfoList;
	}
}
