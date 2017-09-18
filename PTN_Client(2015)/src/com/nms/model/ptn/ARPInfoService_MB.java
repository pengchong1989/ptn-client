package com.nms.model.ptn;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.ARPInfo;
import com.nms.db.dao.ptn.ARPMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class ARPInfoService_MB extends ObjectService_Mybatis {
	private ARPMapper mapper = null;
		
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	public void setMapper(ARPMapper mapper) {
		this.mapper = mapper;
	}

	public List<ARPInfo> queryBySiteId(int siteId) {
		List<ARPInfo> arpList = new ArrayList<ARPInfo>();
		try {
			arpList = this.mapper.queryBySiteId(siteId);
			if(arpList == null){
				return new ArrayList<ARPInfo>();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return arpList;
	}
	
	/**
	 * 单网元名称验证
	 * 
	 * @param afterName
	 * @param beforeName
	 * @param siteId
	 * @return 重复/不重复 true/false
	 * @throws Exception
	 */
	public boolean nameRepetitionBySingle(String afterName, String beforeName, int siteId) throws Exception {
		int result = this.mapper.query_nameBySingle(afterName, beforeName, siteId);
		if (0 == result) {
			return false;
		} else {
			return true;
		}
	}
}
