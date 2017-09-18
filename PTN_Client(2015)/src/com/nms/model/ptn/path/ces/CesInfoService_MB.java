package com.nms.model.ptn.path.ces;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.port.PortStmTimeslot;
import com.nms.db.bean.ptn.Businessid;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.dao.equipment.port.PortInstMapper;
import com.nms.db.dao.equipment.port.PortStmTimeslotMapper;
import com.nms.db.dao.ptn.BusinessidMapper;
import com.nms.db.dao.ptn.path.ces.CesInfoMapper;
import com.nms.db.dao.ptn.path.pw.PwInfoMapper;
import com.nms.db.enums.EActionType;
import com.nms.db.enums.ECesType;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.port.PortStmTimeslotService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.BusinessIdException;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTip;

public class CesInfoService_MB extends ObjectService_Mybatis{
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	private CesInfoMapper cesInfoMapper;
	
	public CesInfoMapper getCesInfoMapper() {
		return cesInfoMapper;
	}

	public void setCesInfoMapper(CesInfoMapper cesInfoMapper) {
		this.cesInfoMapper = cesInfoMapper;
	}
	
	/**
	 * 过滤查询，ces列表页面用
	 * @param cesInfo 过滤条件
	 * @return
	 * @throws Exception
	 */
	public List<CesInfo> filterSelect(CesInfo cesInfo) throws Exception{
		return this.cesInfoMapper.filterQuery(cesInfo);
	}
	
	/**
	 * 查询单网元下的ces
	 * 
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public List<CesInfo> selectNodeBySite(int siteId) throws Exception {
		List<CesInfo> cesinfos = null;
		try {
			cesinfos = this.cesInfoMapper.queryNodeBySite(siteId);

			for (CesInfo cesInfo : cesinfos) {
				cesInfo.setNode(true);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

		return cesinfos;
	}

	public List<CesInfo> filterSingle(CesInfo cesInfo, int siteId) throws Exception{
		return this.cesInfoMapper.filterSingle(cesInfo, siteId);
	}
	
	
	public CesInfo selectByid(CesInfo cesInfo) throws Exception {
		CesInfo cesInfo2 = null;
		List<CesInfo> cesInfoList = null;
		try {
			cesInfoList = new ArrayList<CesInfo>();
			cesInfo2 =new CesInfo();
			cesInfoList = this.cesInfoMapper.queryByIdCondition(cesInfo);
			if(cesInfoList!=null && cesInfoList.size()>0){
				cesInfo2=cesInfoList.get(0);
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return cesInfo2;
	}
	
	public void doSearch(List<CesInfo> cesInfos) throws Exception{
		List<Integer> integers = new ArrayList<Integer>();
		for (CesInfo cesInfo : cesInfos) {
			integers.add(cesInfo.getId());
		}
		String name = "ces_"+System.currentTimeMillis();
		int s1Id = cesInfos.get(0).getId();
		int s2Id = cesInfos.get(1).getId();
		this.cesInfoMapper.doSearche_insert(name,s1Id,s2Id);
		this.cesInfoMapper.deleteByIds(integers);
		sqlSession.commit();
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<CesInfo> select() throws Exception {
		List<CesInfo> cesInfoList = null;
		try {
			cesInfoList = new ArrayList<CesInfo>();
			cesInfoList = this.cesInfoMapper.queryByCondition(new CesInfo());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return cesInfoList;
	}
	
	/**
	 * 获取ces所用到端口名称 显示用
	 * 
	 * @param cesInfo
	 *            ces业务对象
	 * @param type
	 *            表示A或Z端
	 * @return
	 * @throws Exception
	 */
	public String getCesPortName(CesInfo cesInfo, String type) throws Exception {

		int portid = 0;
		String type_port = null;
		PortService_MB portService = null;
		PortInst portinst = null;
		List<PortInst> portInstList = null;
		PortStmTimeslotService_MB portStmTimeslotService = null;
		PortStmTimeslot portStmTimeslot = null;
		String result = null;
		try {

			if ("a".equals(type)) {
				if (cesInfo.getCestype() == ECesType.PDH.getValue() || cesInfo.getCestype() == ECesType.PDHSDH.getValue()) {
					type_port = "pdh";
				} else {
					type_port = "sdh";
				}
				portid = cesInfo.getaAcId();
			} else {
				if (cesInfo.getCestype() == ECesType.PDH.getValue() || cesInfo.getCestype() == ECesType.SDHPDH.getValue()) {
					type_port = "pdh";
				} else {
					type_port = "sdh";
				}
				portid = cesInfo.getzAcId();
			}

			if ("pdh".equals(type_port)) {
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT, this.sqlSession);
				portinst = new PortInst();
				portinst.setPortId(portid);
				portInstList = new ArrayList<PortInst>();
				portInstList = portService.select(portinst);

				if (null != portInstList && portInstList.size() == 1) {
					result = portInstList.get(0).getPortName();
				} else {
					result = "";
				}
			} else {
				portStmTimeslotService = (PortStmTimeslotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTSTMTIMESLOT, this.sqlSession);
				portStmTimeslot =new PortStmTimeslot();
				portStmTimeslot = portStmTimeslotService.selectById(portid);

				if (null != portStmTimeslot) {
					result = portStmTimeslot.getTimeslotnumber();
				} else {
					result = "";
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			type_port = null;
			portinst = null;
			portInstList = null;
			portStmTimeslot = null;
		}
		return result;
	}
	
	/**
	 * 验证名字是否重复
	 * 
	 * @author kk
	 * 
	 * @param afterName
	 *            修改之后的名字
	 * @param beforeName
	 *            修改之前的名字
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public boolean nameRepetition(String afterName, String beforeName,int siteId) throws Exception {

		int result = this.cesInfoMapper.query_name(afterName, beforeName, siteId);
		if (0 == result) {
			return false;
		} else {
			return true;
		}

	}
	
	/**
	 * 同步时查询ces
	 * 
	 * @param siteId
	 *            网元id
	 * @param xcid
	 *            设备名称
	 * @return
	 * @throws Exception
	 */
	public List<CesInfo> select_synchro(int siteId, int xcid) throws Exception {

		List<CesInfo> cesInfoList = null;
		try {
			cesInfoList = this.cesInfoMapper.querySynchro(siteId, xcid);
		} catch (Exception e) {
			throw e;
		}
		return cesInfoList;
	}
	
	public int save(CesInfo cesInfo) throws Exception,BusinessIdException {

		if (cesInfo == null) {
			throw new Exception("pwinfo is null");
		}
		int resultcesId = 0;
		Businessid aServiceId = null;
		Businessid zServiceId = null;
		SiteService_MB siteService = null;
		BusinessidMapper businessidMapper = null;
		PwInfoMapper pwInfoMapper = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
			businessidMapper = sqlSession.getMapper(BusinessidMapper.class);
			pwInfoMapper = sqlSession.getMapper(PwInfoMapper.class);
			if(cesInfo.getaSiteId()!=0){
				
				if(cesInfo.getAxcId()==0){
					if(siteService.getManufacturer(cesInfo.getaSiteId()) == EManufacturer.valueOf("WUHAN").getValue()){
						aServiceId = businessidMapper.queryList(cesInfo.getaSiteId(), "eline").get(0);
					}else{
						aServiceId = businessidMapper.queryList(cesInfo.getaSiteId(), "ces").get(0);
					}	
				}else {
					if(siteService.getManufacturer(cesInfo.getaSiteId()) == EManufacturer.valueOf("WUHAN").getValue()){
						aServiceId=businessidMapper.queryByIdValueSiteIdtype(cesInfo.getAxcId(), cesInfo.getaSiteId(), "eline");
					}else{
						aServiceId=businessidMapper.queryByIdValueSiteIdtype(cesInfo.getAxcId(), cesInfo.getaSiteId(), "ces");
					}
				}
				if (aServiceId == null) {
					throw new BusinessIdException(siteService.getSiteName(cesInfo.getaSiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_CESID));
				}
				cesInfo.setAxcId(aServiceId.getIdValue());
				aServiceId.setIdStatus(1);
				businessidMapper.update(aServiceId);

			}
			if(cesInfo.getzSiteId()!=0){
				
				if(cesInfo.getZxcId()==0){
					if(siteService.getManufacturer(cesInfo.getzSiteId()) == EManufacturer.valueOf("WUHAN").getValue()){
						zServiceId = businessidMapper.queryList(cesInfo.getzSiteId(), "eline").get(0);
					}else{
						zServiceId = businessidMapper.queryList(cesInfo.getzSiteId(), "ces").get(0);
					}	
				}else {
					if(siteService.getManufacturer(cesInfo.getzSiteId()) == EManufacturer.valueOf("WUHAN").getValue()){
						zServiceId=businessidMapper.queryByIdValueSiteIdtype(cesInfo.getZxcId(), cesInfo.getzSiteId(), "eline");
					}else{
						zServiceId=businessidMapper.queryByIdValueSiteIdtype(cesInfo.getZxcId(), cesInfo.getzSiteId(), "ces");
					}
				}
				if (zServiceId == null) {
					throw new BusinessIdException(siteService.getSiteName(cesInfo.getzSiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_CESID));
				}

				cesInfo.setZxcId(zServiceId.getIdValue());
				zServiceId.setIdStatus(1);
				businessidMapper.update(zServiceId);
			}
			cesInfoMapper.insert(cesInfo);
			resultcesId = cesInfo.getId();
			pwInfoMapper.setUser(cesInfo.getPwId(), resultcesId, EServiceType.CES.getValue());
			setUsedForPort(cesInfo, 1); // 更新端口或者时隙也被使用

			//离线网元数据下载
			if(0!=cesInfo.getaSiteId()){
				super.dateDownLoad(cesInfo.getaSiteId(),resultcesId, EServiceType.CES.getValue(), EActionType.INSERT.getValue());
			}
			if(0!=cesInfo.getzSiteId()){
				super.dateDownLoad(cesInfo.getzSiteId(),resultcesId, EServiceType.CES.getValue(), EActionType.INSERT.getValue());
			}
			sqlSession.commit();
		} catch (BusinessIdException e) {
			sqlSession.rollback();
			throw e;
		}catch (Exception e) {
			sqlSession.rollback();
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			businessidMapper = null;
			pwInfoMapper = null;
		}
		return resultcesId;
	}
	
	private void setUsedForPort(CesInfo cesInfo, int isused) {
		List<Integer> pdhportList = null; // e1
		PortStmTimeslotMapper portStmTimeslotMapper = null;
		PortInstMapper portInstMapper = null;
		try {
			portStmTimeslotMapper = sqlSession.getMapper(PortStmTimeslotMapper.class);
			portInstMapper = sqlSession.getMapper(PortInstMapper.class);
			pdhportList = new ArrayList<Integer>();
			if (cesInfo.getCestype() == ECesType.SDH.getValue()) {
				portStmTimeslotMapper.setUsed(cesInfo.getaAcId(), isused);
				portStmTimeslotMapper.setUsed(cesInfo.getzAcId(), isused);
			} else if (cesInfo.getCestype() == ECesType.SDHPDH.getValue()) {
				portStmTimeslotMapper.setUsed(cesInfo.getaAcId(), isused);
				
				pdhportList.add(cesInfo.getzAcId());
				portInstMapper.updateOccupyByIdList(pdhportList, isused);
				
			} else if (cesInfo.getCestype() == ECesType.PDH.getValue()) {

				pdhportList.add(cesInfo.getzAcId());
				pdhportList.add(cesInfo.getaAcId());
				portInstMapper.updateOccupyByIdList(pdhportList, isused);

			} else if (cesInfo.getCestype() == ECesType.PDHSDH.getValue()) {
				portStmTimeslotMapper.setUsed(cesInfo.getzAcId(), isused);

				pdhportList.add(cesInfo.getaAcId());
				portInstMapper.updateOccupyByIdList(pdhportList, isused);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			pdhportList = null;
			portStmTimeslotMapper = null;
			portInstMapper = null;
		}
	}
	
	public void update(List<CesInfo> cesInfoList) throws Exception {
		for (CesInfo cesInfo : cesInfoList) {
			this.update(cesInfo);
		}
	}
	
	public void update(CesInfo cesInfo) throws Exception {

		if (cesInfo == null) {
			throw new Exception("pwinfo is null");
		}
		CesInfo beforCes = null;
		PwInfoMapper pwInfoMapper = null;
		try {
			beforCes = new CesInfo();
			beforCes.setId(cesInfo.getId());
			beforCes = cesInfoMapper.queryByCondition_nojoin(beforCes).get(0);
			pwInfoMapper = sqlSession.getMapper(PwInfoMapper.class);
			this.cesInfoMapper.updateByPrimaryKey(cesInfo);

			if (beforCes.getPwId() != cesInfo.getPwId()) {
				pwInfoMapper.setUser(beforCes.getPwId(), 0, 0);
				pwInfoMapper.setUser(cesInfo.getPwId(), cesInfo.getId(), EServiceType.CES.getValue());
			}
			setUsedForPort(beforCes,0);//释放修改过的端口端口或者时隙也被使用
			setUsedForPort(cesInfo, 1); // 更新端口或者时隙也被使用
			
			//离线网元数据下载
			if(0!=cesInfo.getaSiteId()){
				super.dateDownLoad(cesInfo.getaSiteId(),cesInfo.getId(), EServiceType.CES.getValue(), EActionType.UPDATE.getValue());
			}
			if(0!=cesInfo.getzSiteId()){
				super.dateDownLoad(cesInfo.getzSiteId(),cesInfo.getId(), EServiceType.CES.getValue(), EActionType.UPDATE.getValue());
			}
			sqlSession.commit();
		} catch (Exception e) {
			sqlSession.rollback();
			ExceptionManage.dispose(e,this.getClass());
		} finally{
			pwInfoMapper = null;
		}
	}

	public List<CesInfo> selectCesByPwId(List<Integer> pwIdList) throws Exception {
		List<CesInfo> infoList = null;
		try {
			infoList = this.cesInfoMapper.queryByPwId(pwIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return infoList;
	}
	
	public List<CesInfo> selectBysiteIdandE1id(int siteID,int e1Id){
		List<CesInfo> infoList = null;
		try {
			infoList = this.cesInfoMapper.selectBysiteIdandE1id(siteID,e1Id);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return infoList;
	}
}
