package com.nms.model.ptn;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.dao.ptn.SiteRoateMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;

/**
 * 倒换命令
 * 
 * @author sy
 * 
 */
public class SiteRoateService_MB extends ObjectService_Mybatis {

	private SiteRoateMapper mapper = null;

	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}

	public SiteRoateMapper getMapper() {
		return mapper;
	}

	public void setMapper(SiteRoateMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * 新增
	 * 
	 * @param siteRoate
	 * @return
	 * @throws Exception
	 */
	public int insert(SiteRoate siteRoate) throws Exception {
		int result = 0;

		try {
			result = mapper.insert(siteRoate);
			sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return result;
	}

	/**
	 * 条件查询 : 只查询一个结果
	 * 
	 * @param siteRoate
	 * @return
	 * @throws Exception
	 */
	public SiteRoate select(SiteRoate siteRoate) throws Exception {
		List<SiteRoate> siteRoateList = null;
		try {
			siteRoateList = querBySiteRoate(siteRoate);
			if (siteRoateList != null && siteRoateList.size() == 1) {
				siteRoate = siteRoateList.get(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return siteRoate;
	}

	/**
	 * 条件查询
	 * 
	 * @param siteRoate
	 * @return
	 * @throws Exception
	 */
	public List<SiteRoate> querBySiteRoate(SiteRoate siteRoate) throws Exception {
		List<SiteRoate> siteRoateList = null;
		try {
			siteRoateList = mapper.select(siteRoate);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return siteRoateList;
	}

	/**
	 * 更新
	 * @param siteRoate
	 * @return
	 * @throws Exception
	 */
	public int update(SiteRoate siteRoate)throws Exception{
		int result = 0;

		try {
			result = mapper.update(siteRoate);
			sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return result;
	}
	
	public List<SiteRoate> querSiteRoateByRoate(SiteRoate siteRoate) throws Exception {
		List<SiteRoate> siteRoateList = null;
		try {
			siteRoateList = mapper.selectByRoate(siteRoate);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return siteRoateList;
	}
}
