package com.nms.model.ptn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.MacLearningInfo;
import com.nms.db.dao.ptn.MacLearningLimitMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

public class MacLearningService_MB extends ObjectService_Mybatis {
	    private MacLearningLimitMapper mapper = null;
		
		public void setPtnuser(String ptnuser) {
			super.ptnuser = ptnuser;
		}

		public void setSqlSession(SqlSession sqlSession) {
			super.sqlSession = sqlSession;
		}
		
		public MacLearningLimitMapper getMapper() {
			return mapper;
		}

		public void setMapper(MacLearningLimitMapper mapper) {
			this.mapper = mapper;
		}
		
		public List<Integer> selectAllPortId(int siteId) throws SQLException {
			this.sqlSession.getConnection().setAutoCommit(false);
			List<Integer> integerList = new ArrayList<Integer>();
			try {
				integerList = this.mapper.selectAllPortId(siteId);
				if(!this.sqlSession.getConnection().getAutoCommit()){
					this.sqlSession.getConnection().commit();
				}
			} catch (Exception e) {
				this.sqlSession.getConnection().rollback();
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				this.sqlSession.getConnection().setAutoCommit(true);
			}
			return integerList;
		}
		
		/**
		 * 根据网元id查询对应的mac
		 * @param siteId
		 * @return
		 * @throws SQLException
		 */
		public List<MacLearningInfo> selectBySiteId(int siteId) throws SQLException {
			this.sqlSession.getConnection().setAutoCommit(false);
			List<MacLearningInfo> macLearningInfoList = new ArrayList<MacLearningInfo>();
			try {
				macLearningInfoList = this.mapper.selectBySiteId(siteId);
				
				if(!this.sqlSession.getConnection().getAutoCommit()){
					this.sqlSession.getConnection().commit();
				}
			} catch (Exception e) {
				this.sqlSession.getConnection().rollback();
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				this.sqlSession.getConnection().setAutoCommit(true);
			}
			return macLearningInfoList;
		}
		
		public MacLearningInfo selectById(int id) throws SQLException {
			MacLearningInfo macLearningInfo = new MacLearningInfo();
			try {
				macLearningInfo = this.mapper.selectById(id);				
			} catch (Exception e) {
				this.sqlSession.getConnection().rollback();
				ExceptionManage.dispose(e, this.getClass());
			}
			return macLearningInfo;
		}
}
