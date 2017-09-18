package com.nms.model.ptn;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.EthLoopInfo;
import com.nms.db.dao.ptn.EthLoopMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

/**
 * @author zk
 *
 */
public class EthLoopServcie_MB extends ObjectService_Mybatis{
	   private EthLoopMapper mapper = null;
		
		public void setPtnuser(String ptnuser) {
			super.ptnuser = ptnuser;
		}

		public void setSqlSession(SqlSession sqlSession) {
			super.sqlSession = sqlSession;
		}
		
		public EthLoopMapper getMapper() {
			return mapper;
		}

		public void setMapper(EthLoopMapper mapper) {
			this.mapper = mapper;
		}
	
	
		/**
		 * 查询该网元下所有信息
		 * @param siteId
		 * @return
		 * @throws Exception
		 */
		public List<EthLoopInfo> select(int siteId) throws Exception{
			List<EthLoopInfo> ethLoopInfoList = null;
			if(siteId == 0){
				throw new Exception("siteId is null");
			}
			try {
				ethLoopInfoList = new ArrayList<EthLoopInfo>();
				ethLoopInfoList = this.mapper.queryBySiteId(siteId);
				
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}finally{
				
			}
			return ethLoopInfoList;
		}
		
}
