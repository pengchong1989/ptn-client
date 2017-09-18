package com.nms.model.ptn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.session.SqlSession;
import com.nms.db.bean.ptn.CccInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.dao.ptn.CccInfoMapper;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;


public class CccService_MB extends ObjectService_Mybatis {

	    private CccInfoMapper mapper = null;
		public void setPtnuser(String ptnuser) {
			super.ptnuser = ptnuser;
		}

		public void setSqlSession(SqlSession sqlSession) {
			super.sqlSession = sqlSession;
		}
		
		public CccInfoMapper getMapper() {
			return mapper;
		}

		public void setMapper(CccInfoMapper mapper) {
			this.mapper = mapper;
		}
		
		
		/**
		 * 界面过滤查询
		 * @param etreeInfo 查询条件
		 * @return
		 * @throws Exception
		 */
		public List<CccInfo> filterSelect(CccInfo cccInfo) throws Exception {		
			List<CccInfo> cccServiceList = null;
			List<CccInfo> cccList = new ArrayList<CccInfo>();
			AcPortInfoService_MB acService = null;
			List<Integer> acIds = null;
			try {
				acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo,this.sqlSession);
				cccServiceList = new ArrayList<CccInfo>();
				cccServiceList = this.mapper.filterSelect(cccInfo);
				
				if(cccInfo.getAportId() >0)
				{
					acIds = acService.acByPort(cccInfo.getAportId(), cccInfo.getaSiteId());
				}
				if(cccServiceList!=null && cccServiceList.size()>0){
					for (CccInfo cccInfo_result : cccServiceList) {
						cccInfo_result.setCreateTime(DateUtil.strDate(cccInfo_result.getCreateTime(), DateUtil.FULLTIME));
	//					存在通过端口查询
						if(acIds != null)
						{
							if(!acIds.isEmpty())
							{
								if(acByFilter(acIds,cccInfo_result.getAmostAcId()) )
								{
									cccList.add(cccInfo_result);
								}	
							}
						}else{
							cccList.add(cccInfo_result);
						}
					
				     }
				}
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}finally
			{
				acIds = null;
			}
			return cccList;
		}
		
		/**
		 * 查询a/Z端是否满足查询条件
		 * @param acIds
		 * @param etreeInfo_result
		 * @return
		 */
		private boolean acByFilter(List<Integer> acIds,String mostAcId){
			Set<Integer> acList = null;
			UiUtil uiutil  = null;
			try {
				uiutil = new UiUtil();
				acList = uiutil.getAcIdSets(mostAcId);
				for(Integer acId : acList){
					if(acIds.contains(acId)){
						return true;
					}
				}					
				}catch (Exception e) {
					ExceptionManage.dispose(e, getClass());
				}finally{
					acList = null;
					uiutil = null;
				}
				return false;
		}
		
		public List<CccInfo> selectCccBySite(int siteId) {
			List<CccInfo> cccInfoList = new ArrayList<CccInfo>();
			AcPortInfoService_MB acService = null;		
			try {
				acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo, this.sqlSession);		
				cccInfoList = new ArrayList<CccInfo>();
				cccInfoList = this.mapper.queryNodeBySite(siteId);
				if(cccInfoList!=null && cccInfoList.size()>0){
					for (CccInfo cccInfo : cccInfoList) {
						cccInfo.setNode(true);
						cccInfo.getAcPortList().addAll(this.getAcInfo(siteId, cccInfo, acService));
						
					}
				}
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
			return cccInfoList;
		}
		
		private List<AcPortInfo> getAcInfo(int siteId, CccInfo cccInfo, AcPortInfoService_MB acService) throws Exception {
			UiUtil uiutil = null;
			Set<Integer> acIds = null;
			List<Integer> acIdList = null;
			try {
				acIds = new HashSet<Integer>();
				uiutil = new UiUtil();
				acIds.addAll(uiutil.getAcIdSets(cccInfo.getAmostAcId()));			
				if(acIds.size() > 0)
				{
					acIdList = new ArrayList<Integer>(acIds);
					return  acService.select(acIdList);
				}
			} catch (Exception e)
			{
				ExceptionManage.dispose(e, getClass());
			}finally
			{
				 uiutil = null;
				 acIds = null;
				 acIdList = null;
			}
			return null;
		}
		/**
		 * 单网元名称验证
		 * 
		 * @param afterName
		 * @param beforeName
		 * @param siteId
		 * @return
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
		/**
		 * 通过serviceId查询一组etree业务
		 * 
		 * @param serviceId
		 * @return
		 */
		public List<CccInfo> selectByServiceId(int serviceId) {
			List<CccInfo> cccInfos = null;
			cccInfos = new ArrayList<CccInfo>();
			cccInfos = this.mapper.queryByServiceId(serviceId);
			return cccInfos;
		}

}
