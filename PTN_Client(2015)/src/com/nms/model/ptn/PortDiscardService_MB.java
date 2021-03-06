package com.nms.model.ptn;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.equipment.port.PortDiscardInfo;
import com.nms.db.dao.ptn.PortDiscardInstMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class PortDiscardService_MB extends ObjectService_Mybatis {

	private PortDiscardInstMapper mapper = null;

	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}

	public PortDiscardInstMapper getMapper() {
		return mapper;
	}

	public void setMapper(PortDiscardInstMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * 查询该网元下所有信息
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public List<PortDiscardInfo> select(int siteId) throws Exception{
		List<PortDiscardInfo> portDiscardInfos = null;
		if(siteId == 0){
			throw new Exception("siteId is null");
		}   
		try {
			portDiscardInfos = mapper.queryByCondition(siteId);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
		}
		return portDiscardInfos;
	}
}
