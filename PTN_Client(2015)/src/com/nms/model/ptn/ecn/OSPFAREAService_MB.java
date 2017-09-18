package com.nms.model.ptn.ecn;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.ecn.OSPFAREAInfo;
import com.nms.db.dao.ptn.ecn.OSPFAREAInfoMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class OSPFAREAService_MB extends ObjectService_Mybatis {
	private OSPFAREAInfoMapper mapper = null;
	
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	public void setMapper(OSPFAREAInfoMapper mapper) {
		this.mapper = mapper;
	}

	public List<OSPFAREAInfo> queryByNeID(int neId) throws Exception {
		if (neId == 0) {
			throw new Exception("NeID is null");
		}
		List<OSPFAREAInfo> oSPFAREAInfoList = null;
		try {
			oSPFAREAInfoList = this.mapper.queryByNeID(neId+"");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return oSPFAREAInfoList;
	}
}
