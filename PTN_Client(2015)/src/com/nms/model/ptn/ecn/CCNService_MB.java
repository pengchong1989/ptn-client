package com.nms.model.ptn.ecn;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.ecn.CCN;
import com.nms.db.dao.ptn.ecn.CCNMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class CCNService_MB extends ObjectService_Mybatis {
	private CCNMapper mapper = null;
	
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	public void setMapper(CCNMapper mapper) {
		this.mapper = mapper;
	}

	public List<CCN> queryByNeID(String NeID) throws Exception {
		if (NeID == null) {
			throw new Exception("NeID is null");
		}
		List<CCN> ccnList = null;
		try {
			ccnList = this.mapper.queryByNeID(NeID);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return ccnList;
	}
}
