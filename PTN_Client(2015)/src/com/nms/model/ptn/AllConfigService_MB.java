package com.nms.model.ptn;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.AllConfigInfo;
import com.nms.db.dao.ptn.AllConfigInfoMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class AllConfigService_MB extends ObjectService_Mybatis {
	private AllConfigInfoMapper mapper = null;
	
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	public void setMapper(AllConfigInfoMapper mapper) {
		this.mapper = mapper;
	}

	public List<AllConfigInfo> select(int siteId) throws Exception {
		List<AllConfigInfo> wholeConfigInfoList = null;
		if(siteId == 0){
			throw new Exception("siteId is null");
		}
		try {
			wholeConfigInfoList = this.mapper.queryBySiteId(siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return wholeConfigInfoList;
	}

	public void update(AllConfigInfo wholeConfigInfo) throws Exception {
		if(wholeConfigInfo == null){
			throw new Exception("wholeConfigInfo is null");
		}
		try {
			this.mapper.update(wholeConfigInfo);
			this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

}
