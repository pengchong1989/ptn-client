﻿package com.nms.model.ptn.path.pw;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.Businessid;
import com.nms.db.bean.ptn.LabelInfo;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.oam.OamMipInfo;
import com.nms.db.bean.ptn.path.ServiceInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosRelevance;
import com.nms.db.dao.ptn.BusinessidMapper;
import com.nms.db.dao.ptn.LabelInfoMapper;
import com.nms.db.dao.ptn.path.eth.EtreeInfoMapper;
import com.nms.db.dao.ptn.path.pw.MsPwInfoMapper;
import com.nms.db.dao.ptn.path.pw.PwInfoMapper;
import com.nms.db.dao.ptn.path.pw.PwNniInfoMapper;
import com.nms.db.dao.ptn.path.tunnel.TunnelMapper;
import com.nms.db.enums.EActionType;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.LabelInfoService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.ptn.qos.QosRelevanceService_MB;
import com.nms.model.util.LabelManage;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.BusinessIdException;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;

public class PwInfoService_MB extends ObjectService_Mybatis{
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	private PwInfoMapper mapper;

	public PwInfoMapper getPwInfoMapper() {
		return mapper;
	}

	public void setPwInfoMapper(PwInfoMapper PwInfoMapper) {
		this.mapper = PwInfoMapper;
	}

	public PwInfo selectByPwId(int pwId) throws Exception {
		PwInfo pwInfo = null;
		try {
			pwInfo = new PwInfo();
			pwInfo.setPwId(pwId);
			List<PwInfo> pwInfoList = this.select(pwInfo);
			if (null != pwInfoList && pwInfoList.size() == 1) {
				pwInfo = pwInfoList.get(0);
			} else {
				throw new Exception("查询pw出错");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwInfo;
	}
	
	public List<PwInfo> select(PwInfo pwinfoConditin) throws Exception {
		List<PwInfo> pwinfoList = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		MsPwInfo mspwinfoCondition = null;
		List<Tunnel> tunnels = null;
		List<PwInfo> pwInfoList1 = null;
		List<PwInfo> pwInfoList2 = null;
		try {
			pwinfoList = new ArrayList<PwInfo>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", pwinfoConditin);
			if(pwinfoConditin != null && pwinfoConditin.getType() != null){
				map.put("type", pwinfoConditin.getType().getValue());
			}else{
				map.put("type", 0);
			}
			pwInfoList1 = this.mapper.queryByCondition(map);
			if(pwInfoList1 != null && pwInfoList1.size() > 0){
				for (PwInfo pwInfo : pwInfoList1) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			//先通过端口号查tunnel，再通过tunnel查pw，然后过滤pw
			if(pwinfoConditin.getPortId() > 0)
			{
				TunnelMapper tunnelMapper = this.sqlSession.getMapper(TunnelMapper.class);
				tunnels = tunnelMapper.queryByportId(pwinfoConditin.getPortId());
				if(tunnels.size()>0)
				{
					List<Integer> tunnelIds = new ArrayList<Integer>();
					for(Tunnel tunnel:tunnels)
					{
						tunnelIds.add(tunnel.getTunnelId());
					}
					pwInfoList2 = this.mapper.queryByPwTunnelIdCondition(tunnelIds);
					for(PwInfo pw1 : pwInfoList1)
					{
						for(PwInfo pw2 : pwInfoList2)
						{
							if(pw2.getPwId() == pw1.getPwId())
							{
								pwinfoList.add(pw1);
							}
						}
					}
				}
			}
			else
			{
				pwinfoList.addAll(pwInfoList1);
			}
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			for (PwInfo pwInfo : pwinfoList) {// 封装对应的pwnniInfo
				mspwinfoCondition = new MsPwInfo();
				mspwinfoCondition.setPwId(pwInfo.getPwId());
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				infos = pwNniMapper.queryByCondition(pwNniInfo);
				for (PwNniInfo info : infos) {
					if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
						pwInfo.setaPwNniInfo(info);
					}
					if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
						pwInfo.setzPwNniInfo(info);
					}
				}
				pwInfo.setMsPwInfos(msPwMapper.queryByCondition(mspwinfoCondition));
			}
			this.getOAMandQoSforPw(pwinfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwinfoList;
	}
	
	public void getOAMandQoSforPw(List<PwInfo> pwList) {
		OamInfoService_MB oamInfoService = null;
		QosInfoService_MB qosInfoService = null;
		OamInfo oamInfo = null;
		OamMepInfo oamMepInfo = null;
		OamMipInfo oamMipInfo = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> pwNniInfoList = null;
		try {
			qosInfoService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo, this.sqlSession);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo, this.sqlSession);
			for (PwInfo pw : pwList) {
				oamInfo = new OamInfo();
				oamMepInfo = new OamMepInfo();
				oamMepInfo.setServiceId(pw.getPwId());
				oamMepInfo.setObjType("PW");
				oamInfo.setOamMep(oamMepInfo);
				oamMipInfo = new OamMipInfo();
				oamMipInfo.setServiceId(pw.getPwId());
				oamMipInfo.setObjType("PW");
				oamInfo.setOamMip(oamMipInfo);
				pw.setOamList(oamInfoService.queryByServiceId(oamInfo));
				// 查询qos
				pw.setQosList(qosInfoService.getQosByObj(EServiceType.PW.toString(), pw.getPwId()));
				//查询pw的vlan信息
				PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
				//查询此pw下的所有vlan信息
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pw.getPwId());
				pwNniInfoList = pwNniMapper.queryByCondition(pwNniInfo);
				//因为pw只有az两端  所以pwvlan信息最多又两个记录
				if( null != pwNniInfo && pwNniInfoList.size()>0 && pwNniInfoList.size()<3){
					for(PwNniInfo pwNniInfo_select : pwNniInfoList){
						if(pwNniInfo_select.getSiteId()==pw.getASiteId()){
							pw.setaPwNniInfo(pwNniInfo_select);
							continue;
						}
						if(pwNniInfo_select.getSiteId()==pw.getZSiteId()){
							pw.setzPwNniInfo(pwNniInfo_select);
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	public Object selectFilter(PwInfo filterPwInfo) {
		List<PwInfo> pwInfos = new ArrayList<PwInfo>();
		List<PwInfo> infos = null;
		Map<Integer,Tunnel> integers = null;
		TunnelService_MB tunnelService = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> pwNniInfos = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", filterPwInfo);
			if(filterPwInfo != null && filterPwInfo.getType() != null){
				map.put("type", filterPwInfo.getType().getValue());
			}else{
				map.put("type", 0);
			}
			infos = this.mapper.queryFilte(map);
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			if(infos != null && !infos.isEmpty()){
				for (PwInfo pwInfo : infos) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
					if(filterPwInfo.getIsSingle() == pwInfo.getIsSingle()){
						pwList.add(pwInfo);
					}
				}
				infos.clear();
				infos.addAll(pwList);
			}
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel, this.sqlSession);
			//根据端口查询所有tunnel
			if(filterPwInfo.getPortId()>0){
				List<Tunnel> tunnels = tunnelService.selectByPortIdAndSiteId(filterPwInfo.getASiteId(), filterPwInfo.getPortId());
				integers = new HashMap<Integer,Tunnel>();
				for(Tunnel tunnel :tunnels){
					integers.put(tunnel.getTunnelId(), tunnel);
				}
			}
			//根据tunnelid过滤
			if(filterPwInfo.getTunnelId()>0){
				if(integers == null){
					for(PwInfo pwInfo : infos){
						if(pwInfo.getTunnelId() == filterPwInfo.getTunnelId()){
							pwInfos.add(pwInfo);
						}	
					}
				}else{
					Tunnel tunnel = integers.get(filterPwInfo.getTunnelId());
					if(tunnel != null){
						for(PwInfo pwInfo : infos){
							if(pwInfo.getTunnelId() == tunnel.getTunnelId()){
								pwInfos.add(pwInfo);
							}	
						}
					}else{
						return pwInfos;
					}
				}
			}else{
				if(integers != null){
					for(PwInfo pwInfo : infos){
						if(integers.get(pwInfo.getTunnelId()) != null){
							pwInfos.add(pwInfo);
						}	
					}
				}else{
					pwInfos = infos;
				}
			}
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			for (PwInfo pwInfo : pwInfos) {// 封装对应的pwnniInfo
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				pwNniInfos = pwNniMapper.queryByCondition(pwNniInfo);
				for (PwNniInfo info : pwNniInfos) {
					if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
						pwInfo.setaPwNniInfo(info);
					}
					if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
						pwInfo.setzPwNniInfo(info);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return pwInfos;
	}

	/**
	 * 通过主键查询pw对象
	 * 只提供给多段PW来查询
	 * lable = 1 查询单站 否则查询所有
	 * @param pwId
	 * @return
	 * @throws Exception
	 */ 
	public PwInfo selectByPwIdForMulti(int pwId,int lable) throws Exception {
		List<PwInfo> pwInfoList = null;
		PwInfo pwInfo = null;
		try {
			pwInfo = new PwInfo();
			pwInfo.setPwId(pwId);
			pwInfo.setIsSingle(lable);
			pwInfoList = this.mapper.selectByPWId(pwInfo);
			if (null != pwInfoList && pwInfoList.size() == 1) {
				for (PwInfo pwinfo : pwInfoList) {
					pwinfo.setCreateTime(DateUtil.strDate(pwinfo.getCreateTime(), DateUtil.FULLTIME));
				}
				this.getOAMandQoSforPw(pwInfoList);
				pwInfo = pwInfoList.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			pwInfoList = null;
		}
		return pwInfo;
	}

	public List<PwInfo> selectBySiteId_network(int siteId) {
		List<PwInfo> pwlList = null;
		try {
			PwInfo condition = new PwInfo();
			condition.setASiteId(siteId);
			condition.setIsSingle(0);
			pwlList = this.mapper.queryPwBySiteIdAndIsSingle(condition);
			if(pwlList != null && pwlList.size() > 0){
				for (PwInfo pwInfo : pwlList) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			this.getOAMandQoSforPw(pwlList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwlList;
	}

	public PwInfo selectBypwid_notjoin(PwInfo pwinfo) {
		PwInfo pwInfo = null;
		List<PwInfo> pwInfoList = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		MsPwInfo mspwinfoCondition = null;
		try {
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			pwInfo = this.mapper.queryByPwidCondition_notjoin(pwinfo);
			if(pwInfo != null){
				pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
			}else{
				throw new Exception("select pw is failed");
			}
			pwInfoList = new ArrayList<PwInfo>();
			pwInfoList.add(pwInfo);
			MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			for (PwInfo pwInfo2 : pwInfoList) {// 封装对应的pwnniInfo
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				mspwinfoCondition = new MsPwInfo();
				mspwinfoCondition.setPwId(pwInfo.getPwId());
				infos = pwNniMapper.queryByCondition(pwNniInfo);
				for (PwNniInfo info : infos) {
					if (info.getSiteId() == pwInfo2.getASiteId() && pwInfo2.getApwServiceId() != 0) {
						pwInfo.setaPwNniInfo(info);
					}
					if (info.getSiteId() == pwInfo2.getZSiteId() && pwInfo2.getZpwServiceId() != 0) {
						pwInfo.setzPwNniInfo(info);
					}
				}
				pwInfo.setMsPwInfos(msPwMapper.queryByCondition(mspwinfoCondition));
			}
			this.getOAMandQoSforPw(pwInfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwInfo;
	}

	public List<PwInfo> findPwByPWIds(List<Integer> pwIdList) throws ParseException {
		List<PwInfo> pwList = this.mapper.findPwByIds(pwIdList);
		if(pwList != null && pwList.size() > 0){
			for (PwInfo pwInfo : pwList) {
				pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
			}
		}
		return pwList;
	}

	public List<PwInfo> selectPwInfoByTunnelId(List<Integer> tunnelIds) {
		List<PwInfo> pwList = null;
		try {
			pwList = this.mapper.queryByPwTunnelIdCondition(tunnelIds);
			if(pwList != null && pwList.size() > 0){
				for (PwInfo pwinfo : pwList) {
					pwinfo.setCreateTime(DateUtil.strDate(pwinfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			this.getOAMandQoSforPw(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwList;
	}

	/**
	 * 查询单网元下的pw
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> selectNodeBySiteid(int siteId) {
		List<PwInfo> pwInfoList = null;
		List<PwInfo> pwInfoList_result = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		PwInfo pwinfoSel = new PwInfo();
		try {
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			pwinfoSel.setASiteId(siteId);
			pwinfoSel.setZSiteId(siteId);
			pwInfoList_result = new ArrayList<PwInfo>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", pwinfoSel);
			if(pwinfoSel != null && pwinfoSel.getType() != null){
				map.put("type", pwinfoSel.getType().getValue());
			}else{
				map.put("type", 0);
			}
			pwInfoList = this.mapper.queryNode(map);
			for (PwInfo pwinfo : pwInfoList) {
				pwinfo.setCreateTime(DateUtil.strDate(pwinfo.getCreateTime(), DateUtil.FULLTIME));
				pwinfo.setNode(true);
				if (pwinfo.getIsSingle() == 1) {
					if (pwinfo.getASiteId() == siteId && pwinfo.getApwServiceId() != 0) {
						pwInfoList_result.add(pwinfo);
					} else if (pwinfo.getZSiteId() == siteId && pwinfo.getZpwServiceId() != 0) {
						pwInfoList_result.add(pwinfo);
					}
				} else {
					pwInfoList_result.add(pwinfo);
				}
			}
			for (PwInfo pwInfo : pwInfoList_result) {// 封装对应的pwnniInfo
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				infos = pwNniMapper.queryByCondition(pwNniInfo);
				MsPwInfo mspwinfoCondition = new MsPwInfo();
				mspwinfoCondition.setPwId(pwInfo.getPwId());
				for (PwNniInfo info : infos) {
					if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
						pwInfo.setaPwNniInfo(info);
					}
					if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
						pwInfo.setzPwNniInfo(info);
					}
				}
				
				pwInfo.setMsPwInfos(msPwMapper.queryByCondition(mspwinfoCondition));
			}
			
			getOAMandQoSforPw(pwInfoList);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwInfoList_result;
	}

	/**
	 * 搜索pw
	 * @param pwList
	 * @param mapMspwInfo
	 * @throws SQLException 
	 */
	public void doSearch(List<PwInfo> pwList, Map<Integer,List<MsPwInfo>> mapMspwInfo) throws SQLException {
		int newPwId = 0;
		String newPwName = "pw_" + System.currentTimeMillis();
		try {
			this.sqlSession.getConnection().setAutoCommit(true);
			List<Integer> pwIdList = new ArrayList<Integer>();
			for (PwInfo pw : pwList) {
				pwIdList.add(pw.getPwId());
			}
			this.mapper.doSearch(newPwName, pwList.get(0).getPwId(), pwList.get(pwList.size()-1).getPwId());
			newPwId = this.mapper.queryPwIdByName(newPwName);
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("newPwId", newPwId);
			conditionMap.put("pwIdList", pwIdList);
			//更新业务
			this.mapper.updateServiceByPwId(conditionMap);
			//更新pwnnibuffer关联表
			this.mapper.updatePwBuffByPwId(conditionMap);
			// 更新qos关联表
			this.mapper.updateQosRelevanceByPwId(conditionMap);
			// 更新oam表
			this.mapper.updateOamMepByPwId(conditionMap);
			//更新告警
			this.mapper.updateCurrAlarmByPwId(conditionMap);
			this.mapper.updateHisAlarmByPwId(conditionMap);
			//更新性能
			this.mapper.updatePerformanceByPwId(conditionMap);
			// 删除刚才建立双边数据所用的单边数据
			this.mapper.deleteByPwIdList(pwIdList);
			this.sqlSession.getConnection().setAutoCommit(false);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 根据tunnelId查询
	 * 
	 * @param tunnelId
	 *            tunnelID
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> selectByTunnelId(int tunnelId) throws Exception {
		List<PwInfo> pwInfoList = null;
		try {
			pwInfoList = new ArrayList<PwInfo>();
			pwInfoList = this.mapper.queryByTunnelId(tunnelId);
			this.getOAMandQoSforPw(pwInfoList);
		} catch (Exception e) {
			throw e;
		}
		return pwInfoList;
	}
	
	/**
	 * 通过pw过滤条件查询单网元的pw
	 * @param siteId 单网元的id
	 * @param pwFilterCondition pw的过滤条件
	 * @return
	 */
	public List<PwInfo> selectNodeByCondition(PwInfo pwFilterCondition) {
		List<PwInfo> pwInfoList1 = null;
		List<PwInfo> pwInfoList2 = new ArrayList<PwInfo>();
		List<PwInfo> pwInfoList = null;
		List<PwInfo> pwInfoList_result = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		List<Tunnel> tunnels = null;
		List<Integer> tunnelIds = null;
		try {
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			TunnelMapper tunnelMapper = this.sqlSession.getMapper(TunnelMapper.class);
			tunnelIds = new ArrayList<Integer>();
			pwInfoList = new ArrayList<PwInfo>();
			pwInfoList_result = new ArrayList<PwInfo>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", pwFilterCondition);
			if(pwFilterCondition != null && pwFilterCondition.getType() != null){
				map.put("type", pwFilterCondition.getType().getValue());
			}else{
				map.put("type", 0);
			}
			pwInfoList1 = this.mapper.queryNode(map);
			if(pwInfoList1 != null && pwInfoList1.size() > 0){
				for (PwInfo pwInfo : pwInfoList1) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			if(pwFilterCondition.getPortId() > 0)
			{
				//先通过端口号查tunnel，再通过tunnel查pw，然后过滤pw
				tunnels = tunnelMapper.queryByportId(pwFilterCondition.getPortId());
				for(Tunnel tunnel:tunnels)
				{
					tunnelIds.add(tunnel.getTunnelId());
				}
				if(!tunnelIds.isEmpty()){
					pwInfoList2 = this.mapper.queryByPwTunnelIdCondition(tunnelIds);
				}
				for(PwInfo pw1 : pwInfoList1)
				{
					for(PwInfo pw2 : pwInfoList2)
					{
						if(pw2.getPwId() == pw1.getPwId())
						{
							pwInfoList.add(pw1);
						}
					}
				}
			}
			else
			{
				pwInfoList.addAll(pwInfoList1);
			}
			
			for (PwInfo pwinfo : pwInfoList) {
				pwinfo.setNode(true);
				if (pwinfo.getIsSingle() == 1) {
					if (pwinfo.getASiteId() == pwFilterCondition.getASiteId() && pwinfo.getApwServiceId() != 0) {
						pwInfoList_result.add(pwinfo);
					} else if (pwinfo.getZSiteId() == pwFilterCondition.getZSiteId() && pwinfo.getZpwServiceId() != 0) {
						pwInfoList_result.add(pwinfo);
					}
				} else {
					pwInfoList_result.add(pwinfo);
				}
			}
			for (PwInfo pwInfo : pwInfoList_result) {// 封装对应的pwnniInfo
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				infos = pwNniMapper.queryByCondition(pwNniInfo);
				for (PwNniInfo info : infos) {
					if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
						pwInfo.setaPwNniInfo(info);
					}
					if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
						pwInfo.setzPwNniInfo(info);
					}
				}
			}
			this.getOAMandQoSforPw(pwInfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return pwInfoList_result;
	}

	/**
	 * 创建或修改时，验证此PW承载的tunnel的qos带宽是否可以承载此pw的qos
	 * @param pwInfo  要创建或修改的pw对象
	 * @param qosInfo  qos带宽
	 * @return true 可以承载 false 不可承载
	 * @throws Exception
	 */
	public boolean checkingQos(PwInfo pwInfo, List<QosInfo> qosInfoList, List<QosInfo> update_before_qosInfoList) {
		TunnelService_MB tunnelService = null;
		Tunnel tunnel = null;
		QosInfoService_MB qosInfoService = null;
		QosInfo qosinfo_pw = null;
		boolean flag = true;
		List<Tunnel> tunnelList = null;
		QosInfo qosInfo_pw_before = null;
		int before_cir = 0;
		int before_eir = 0;
		try {
			qosInfoService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo, this.sqlSession);
			// 根据tunnel主键 查询tunnel对象
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel, this.sqlSession);
			tunnel = new Tunnel();
			tunnel.setTunnelId(pwInfo.getTunnelId());
			tunnelList = tunnelService.selectNodeByTunnelId(tunnel);
			if (null != tunnelList && tunnelList.size() == 1) {
				tunnel = tunnelList.get(0);
			} else {
				throw new Exception("查询tunnel出错");
			}

			for (QosInfo qosInfo_tunnel : tunnel.getQosList()) {
				// 从qos集合中取出来qosui的对象
				qosinfo_pw = qosInfoService.getQosInfo(qosInfoList, qosInfo_tunnel.getCos(), qosInfo_tunnel.getDirection());
				// 如果没查询到 直接返回
				if (qosinfo_pw == null) {
					flag = false;
					break;
				} else {
					if(null!=update_before_qosInfoList){
						qosInfo_pw_before = qosInfoService.getQosInfo(update_before_qosInfoList, qosInfo_tunnel.getCos(), qosInfo_tunnel.getDirection());
						if (null != qosInfo_pw_before) {
							before_cir = qosInfo_pw_before.getCir();
							before_eir = qosInfo_pw_before.getEir();
						}
					}

					QosInfo qosInfo_result=qosInfoService.calculateQos(qosInfo_tunnel, EServiceType.TUNNEL, pwInfo.getTunnelId());
					if (qosInfo_result.getCir() + before_cir < qosinfo_pw.getCir() || qosInfo_result.getEir() + before_eir < qosinfo_pw.getEir()) {
						flag = false;
						break;
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return flag;
	}

	public List<PwInfo> selectSamePortByTunnelId(int tunnelId, int siteId) {
		List<PwInfo> pwInfoList = null;
		try {
			pwInfoList = this.mapper.selectSamePortByTunnelId(tunnelId,siteId);
			if(pwInfoList != null && pwInfoList.size() > 0){
				for (PwInfo pwInfo : pwInfoList) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return pwInfoList;
	}

	public List<PwInfo> select_synchro(int siteid, int pwServiceId, int pwtype) throws ParseException {
		List<PwInfo> pwInfoList = this.mapper.query_synchro(siteid, pwServiceId, pwtype);
		if(pwInfoList != null && pwInfoList.size() > 0){
			for (PwInfo pwInfo : pwInfoList) {
				pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
			}
		}
		MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
		for(PwInfo pwInfo :pwInfoList){
			MsPwInfo mspwinfoCondition = new MsPwInfo();
			mspwinfoCondition.setPwId(pwInfo.getPwId());
			pwInfo.setMsPwInfos(msPwMapper.queryByCondition(mspwinfoCondition));
		}
		return pwInfoList;
	}

	public int save(PwInfo pwinfo) throws Exception {
		if (pwinfo == null) {
			throw new Exception("pwinfo is null");
		}
		int pwId = 0;
		Businessid aServiceId = null;
		Businessid zServiceId = null;
		OamInfoService_MB oamInfoService = null;
		PwNniInfo aPwnniInfo = null;
		PwNniInfo zPwNniInfo = null;
		QosRelevanceService_MB qosRelevanceService = null;
		List<QosRelevance> qosRelevanceList = null;
		SiteService_MB siteService = null;
		LabelInfoService_MB labelInfoService = null;
		try {
			pwinfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			LabelInfoMapper labelInfoMapper = this.sqlSession.getMapper(LabelInfoMapper.class);
		    labelInfoService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO, this.sqlSession);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
			BusinessidMapper businessidMapper = this.sqlSession.getMapper(BusinessidMapper.class);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo, this.sqlSession);
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			// 给A和Z端从ID管理表分配设备ID
			if (pwinfo.getIsSingle() == 0) {
					if (pwinfo.getApwServiceId() == 0 ) {
						aServiceId = businessidMapper.queryList(pwinfo.getASiteId(), this.getPwType(pwinfo, "a", pwinfo.getASiteId())).get(0);
					} else {
						aServiceId = businessidMapper.queryByIdValueSiteIdtype(pwinfo.getApwServiceId(), pwinfo.getASiteId(), this.getPwType(pwinfo, "a", pwinfo.getASiteId()));
					}
					if (aServiceId == null) {
						throw new BusinessIdException(siteService.getSiteName(pwinfo.getASiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_PWID));
					}
					pwinfo.setApwServiceId(aServiceId.getIdValue());
					Businessid bCondition = new Businessid();
					bCondition.setId(aServiceId.getId());
					bCondition.setIdStatus(1);
					businessidMapper.update(bCondition);
				
					if (pwinfo.getZpwServiceId() == 0 ) {
						zServiceId = businessidMapper.queryList(pwinfo.getZSiteId(), this.getPwType(pwinfo, "z", pwinfo.getZSiteId())).get(0);
					} else {
						zServiceId = businessidMapper.queryByIdValueSiteIdtype(pwinfo.getZpwServiceId(), pwinfo.getZSiteId(), this.getPwType(pwinfo, "z", pwinfo.getZSiteId()));
					}
					if (zServiceId == null) {
						throw new BusinessIdException(siteService.getSiteName(pwinfo.getZSiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_PWID));
					}
					pwinfo.setZpwServiceId(zServiceId.getIdValue());
					bCondition.setId(zServiceId.getId());
					bCondition.setIdStatus(1);
					businessidMapper.update(bCondition);
				
			} else {
				if (pwinfo.getASiteId() > 0) {
					if (pwinfo.getApwServiceId() == 0) {
						aServiceId = businessidMapper.queryList(pwinfo.getASiteId(), this.getPwType(pwinfo, "a", pwinfo.getASiteId())).get(0);
					} else {
						aServiceId = businessidMapper.queryByIdValueSiteIdtype(pwinfo.getApwServiceId(), pwinfo.getASiteId(), this.getPwType(pwinfo, "a", pwinfo.getASiteId()));
					}
					if (aServiceId == null) {
						throw new BusinessIdException(siteService.getSiteName(pwinfo.getASiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_PWID));
					}
					pwinfo.setApwServiceId(aServiceId.getIdValue());
					Businessid bCondition = new Businessid();
					bCondition.setId(aServiceId.getId());
					bCondition.setIdStatus(1);
					businessidMapper.update(bCondition);
				} else if(pwinfo.getZSiteId() > 0) {
					if (pwinfo.getZpwServiceId() == 0) {
						zServiceId = businessidMapper.queryList(pwinfo.getZSiteId(), this.getPwType(pwinfo, "z", pwinfo.getZSiteId())).get(0);
					} else {
						zServiceId = businessidMapper.queryByIdValueSiteIdtype(pwinfo.getZpwServiceId(), pwinfo.getZSiteId(), this.getPwType(pwinfo, "z", pwinfo.getZSiteId()));
					}
					if (zServiceId == null) {
						throw new BusinessIdException(siteService.getSiteName(pwinfo.getZSiteId())+ResourceUtil.srcStr(StringKeysTip.TIP_PWID));
					}
					pwinfo.setZpwServiceId(zServiceId.getIdValue());
					Businessid bCondition = new Businessid();
					bCondition.setId(zServiceId.getId());
					bCondition.setIdStatus(1);
					businessidMapper.update(bCondition);
				}
			}
			this.getLabel(pwinfo, siteService, labelInfoMapper, labelInfoService);
			this.mapper.insert(pwinfo);
			//根据pw名称查询pwId
			pwId = pwinfo.getPwId();
//			pwId = this.mapper.queryPwIdByName(pwinfo.getPwName());
//			pwinfo.setPwId(pwId);

			if (pwinfo.getApwServiceId() > 0) {
				aPwnniInfo = this.getPwNniInfo(pwId, pwinfo.getASiteId(), pwinfo.getApwServiceId());
				pwNniMapper.insert(aPwnniInfo);
			}
			if (pwinfo.getZpwServiceId() > 0) {
				zPwNniInfo = this.getPwNniInfo(pwId, pwinfo.getZSiteId(), pwinfo.getZpwServiceId());
				pwNniMapper.insert(zPwNniInfo);
			}

			List<OamInfo> oamList = pwinfo.getOamList();
			if (oamList != null && oamList.size() > 0) {
				for (OamInfo oamInfo : oamList) {
					if (oamInfo.getOamType() == OamTypeEnum.AMEP) {
						oamInfo.getOamMep().setServiceId(pwId);
						oamInfo.getOamMep().setObjId(aServiceId.getIdValue());
						oamInfo.setOamType(OamTypeEnum.AMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.ZMEP) {
						oamInfo.getOamMep().setServiceId(pwId);
						oamInfo.getOamMep().setObjId(zServiceId.getIdValue());
						oamInfo.setOamType(OamTypeEnum.ZMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.MEP) {
						oamInfo.getOamMep().setServiceId(pwId);
						oamInfo.getOamMep().setObjId(ConstantUtil.siteId);
						oamInfo.setOamType(OamTypeEnum.MEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.MIP) {

					}
					oamInfoService.saveOrUpdate(oamInfo);
				}
			}
			if(pwinfo.getMsPwInfos() != null){
				for(MsPwInfo msPwInfo: pwinfo.getMsPwInfos()){
					msPwInfo.setPwId(pwId);
					msPwMapper.insert(msPwInfo);
				}
			}

			qosRelevanceService = (QosRelevanceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSRELEVANCE, this.sqlSession);
			qosRelevanceList = qosRelevanceService.getList(pwinfo);
			if(qosRelevanceList != null && qosRelevanceList.size() >0)
			{
				qosRelevanceService.save(qosRelevanceList);
			}

			//离线网元数据下载
			if(0!=pwinfo.getASiteId()){
				super.dateDownLoad(pwinfo.getASiteId(),pwinfo.getPwId(), EServiceType.PW.getValue(), EActionType.INSERT.getValue());
			}
			if(0!=pwinfo.getZSiteId()){
				super.dateDownLoad(pwinfo.getZSiteId(),pwinfo.getPwId(), EServiceType.PW.getValue(), EActionType.INSERT.getValue());
			}
			this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwId;
	}

	private PwNniInfo getPwNniInfo(int pwId, int siteId, int pwBusinessId) throws Exception {
		PwNniInfo pwNniInfo = new PwNniInfo();
		pwNniInfo.setPwId(pwId);
		pwNniInfo.setSiteId(siteId);
		pwNniInfo.setPwBusinessId(pwBusinessId);
		SiteService_MB siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
		/**
		 * 添加陈晓默认 vlan
		 */
		if (siteService.getManufacturer(siteId) == EManufacturer.CHENXIAO.getValue()) {
			pwNniInfo.setExitRule(UiUtil.getCodeByValue("exitRule", "0").getId());
			pwNniInfo.setSvlan("1");
		}
		// 武汉
		else {
			pwNniInfo.setExitRule(UiUtil.getCodeByValue("PORTTAGBEHAVIOR", "0").getId());
			pwNniInfo.setSvlan("2");
		}

		pwNniInfo.setVlanpri("0");
		pwNniInfo.setTpid(UiUtil.getCodeByValue("LAGVLANTPID", "1").getId());
		pwNniInfo.setHorizontalDivision(UiUtil.getCodeByValue("VCTRAFFICPOLICING", "1").getId());
		pwNniInfo.setMacAddressLearn(UiUtil.getCodeByValue("MACLEARN", "1").getId());
		pwNniInfo.setTagAction(UiUtil.getCodeByValue("TAGRECOGNITION", "0").getId());
		pwNniInfo.setControlEnable(UiUtil.getCodeByValue("ENABLEDSTATUE", "1").getId());
		return pwNniInfo;
	}

	private void getLabel(PwInfo pwInfo, SiteService_MB siteService, LabelInfoMapper labelInfoMapper, LabelInfoService_MB labelInfoService) throws Exception {
		if (null == pwInfo) {
			throw new Exception("pwInfo is null");
		}
		try {
			int manufacturerA = 0;
			int manufacturerZ = 0;
			int manufacturer = 0;
			int aLabel = 0;
			int zLabel = 0;
			// 等于1是晨晓设备,入标签网元唯一
			if(pwInfo.getASiteId() > 0){
				manufacturerA = siteService.getManufacturer(pwInfo.getASiteId());
			}
			if(pwInfo.getZSiteId() > 0){
				manufacturerZ = siteService.getManufacturer(pwInfo.getZSiteId());
			}
			//判断isSingle，为0，则代表网络侧，为1，则代表单网元
			if(pwInfo.getIsSingle() == 0){
				if (pwInfo.getInlabelValue() == 0) {
					//普通类型的PW
					if("0".equals(pwInfo.getBusinessType())){
						// 没有填写标签值说明是eline快速配置或者是批量创建普通PW，自动分配标签
						aLabel = this.matchLabel(pwInfo, labelInfoMapper, manufacturerA, manufacturerZ);
						this.updateLabelStatus(labelInfoMapper, aLabel, pwInfo.getASiteId(), manufacturerA);
						pwInfo.setInlabelValue(aLabel);
						zLabel = this.matchLabel(pwInfo, labelInfoMapper, manufacturerA, manufacturerZ);
						this.updateLabelStatus(labelInfoMapper, zLabel, pwInfo.getZSiteId(), manufacturerZ);
						pwInfo.setOutlabelValue(zLabel);
					}else{
						//批量创建多段pw时分配标签
						List<MsPwInfo> msPwList = pwInfo.getMsPwInfos();
						for (int i = 0; i < msPwList.size()+1; i++) {
							PwInfo pwTmp = new PwInfo();
							int aSiteId = 0;
							int zSiteId = 0;
							if(i == 0){
								aSiteId = pwInfo.getASiteId();
								zSiteId = msPwList.get(i).getSiteId();
							}else if(i == msPwList.size()){
								aSiteId = msPwList.get(i-1).getSiteId();
								zSiteId = pwInfo.getZSiteId();
							}else{
								aSiteId = msPwList.get(i-1).getSiteId();
								zSiteId = msPwList.get(i).getSiteId();
							}
							manufacturerA = siteService.getManufacturer(aSiteId);
							manufacturerZ = siteService.getManufacturer(zSiteId);
							pwTmp.setASiteId(aSiteId);
							pwTmp.setZSiteId(zSiteId);
							//a端标签
							aLabel = this.matchLabel(pwTmp, labelInfoMapper, manufacturerA, manufacturerZ);
							if(i == 0){
								this.updateLabelStatus(labelInfoMapper, aLabel, zSiteId, manufacturerZ);
								pwInfo.setInlabelValue(aLabel);
								msPwList.get(i).setFrontInlabel(aLabel);
							}else if(i == msPwList.size()){
								this.updateLabelStatus(labelInfoMapper, aLabel, aSiteId, manufacturerA);
								msPwList.get(i-1).setBackInlabel(aLabel);
								pwInfo.setBackInlabel(aLabel);
							}else{
								this.updateLabelStatus(labelInfoMapper, aLabel, aSiteId, manufacturerA);
								msPwList.get(i-1).setBackInlabel(aLabel);
								msPwList.get(i).setFrontInlabel(aLabel);
							}
							//z端标签
							zLabel = this.matchLabel(pwTmp, labelInfoMapper, manufacturerA, manufacturerZ);
							if(i == 0){
								this.updateLabelStatus(labelInfoMapper, zLabel, aSiteId, manufacturerA);
								pwInfo.setOutlabelValue(zLabel);
								msPwList.get(i).setBackOutlabel(zLabel);
							}else if(i == msPwList.size()){
								this.updateLabelStatus(labelInfoMapper, aLabel, zSiteId, manufacturerZ);
								msPwList.get(i-1).setFrontOutlabel(zLabel);
								pwInfo.setBackOutlabel(zLabel);
							}else{
								this.updateLabelStatus(labelInfoMapper, aLabel, zSiteId, manufacturerZ);
								msPwList.get(i-1).setFrontOutlabel(zLabel);
								msPwList.get(i).setBackOutlabel(zLabel);
							}
						}
					}
				} else {
					if("1".equals(pwInfo.getBusinessType())){
						LabelInfo labelCondition = new LabelInfo();
						labelCondition.setLabelValue(pwInfo.getOutlabelValue());
						labelCondition.setSiteid(pwInfo.getASiteId());
						labelCondition.setType(manufacturerA == 1 ? "CX" : "WH");
						labelInfoMapper.insertNewLabel(labelCondition);
						labelCondition.setLabelStatus(0);
						labelInfoMapper.updateBatch(labelCondition);
						for(MsPwInfo msPwInfo : pwInfo.getMsPwInfos()){
							manufacturer = siteService.getManufacturer(msPwInfo.getSiteId());
							labelCondition.setLabelValue(msPwInfo.getFrontInlabel());
							labelCondition.setSiteid(msPwInfo.getSiteId());
							labelCondition.setType(manufacturer == 1 ? "CX" : "WH");
							labelInfoMapper.insertNewLabel(labelCondition);
							labelInfoMapper.updateBatch(labelCondition);
							labelCondition.setLabelValue(msPwInfo.getBackInlabel());
							labelInfoMapper.insertNewLabel(labelCondition);
							labelInfoMapper.updateBatch(labelCondition);
						}
						labelCondition.setLabelValue(pwInfo.getBackInlabel());
						labelCondition.setSiteid(pwInfo.getZSiteId());
						labelCondition.setType(manufacturerZ == 1 ? "CX" : "WH");
						labelInfoMapper.insertNewLabel(labelCondition);
						labelInfoMapper.updateBatch(labelCondition);
					}else{
						LabelInfo labelCondition = new LabelInfo();
						labelCondition.setLabelValue(pwInfo.getInlabelValue());
						labelCondition.setSiteid(pwInfo.getASiteId());
						labelCondition.setType(manufacturerA == 1 ? "CX" : "WH");
						labelCondition.setLabelStatus(0);
						labelInfoMapper.insertNewLabel(labelCondition);
						labelInfoMapper.updateBatch(labelCondition);
						labelCondition.setLabelValue(pwInfo.getOutlabelValue());
						labelCondition.setSiteid(pwInfo.getZSiteId());
						labelCondition.setType(manufacturerZ == 1 ? "CX" : "WH");
						labelInfoMapper.insertNewLabel(labelCondition);
						labelInfoMapper.updateBatch(labelCondition);
					}
				}
			}else{
				// 填写了标签，说明是单网元配置，直接修改标签状态
				if(pwInfo.getZSiteId() > 0){
					labelInfoService.saveOrUpdate(pwInfo.getOutlabelValue(), pwInfo.getZSiteId(), 0, manufacturerZ == 1 ? "CX" : "WH");
				}else if(pwInfo.getASiteId() > 0){
					labelInfoService.saveOrUpdate(pwInfo.getInlabelValue(), pwInfo.getASiteId(), 0,manufacturerA == 1 ? "CX" : "WH");
				}
				if(pwInfo.getMsPwInfos() != null && pwInfo.getMsPwInfos().size() >0){
					
					for(MsPwInfo msPwInfo : pwInfo.getMsPwInfos()){
						manufacturer = siteService.getManufacturer(msPwInfo.getSiteId());
						LabelInfo labelCondition = new LabelInfo();
						labelCondition.setLabelValue(msPwInfo.getFrontInlabel());
						labelCondition.setSiteid(msPwInfo.getSiteId());
						labelCondition.setType(manufacturer == 1 ? "CX" : "WH");
						labelCondition.setLabelStatus(0);
						labelInfoMapper.insertNewLabel(labelCondition);
						labelInfoMapper.updateBatch(labelCondition);
						labelCondition.setLabelValue(msPwInfo.getBackInlabel());
						labelInfoMapper.insertNewLabel(labelCondition);
						labelInfoMapper.updateBatch(labelCondition);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private int matchLabel(PwInfo pwInfo, LabelInfoMapper labelInfoMapper, int manufacturerA, int manufacturerZ) throws Exception {
		int label = 0;
		Map<String, Object> map = new HashMap<String, Object>();
		//目前设备的芯片不支持同一端口的lsp的入标签和该端口上的pw的入标签一样,所以要用下面的代码
		String type = "PW";
		if(type.equals("TUNNEL") || type.equals("PW")){
			type = "WH";
		}
		//如果以后芯片支持同一端口的lsp的入标签和该端口上的pw的入标签一样,就用下面的代码,把上面的代码关掉
		map.put("aSiteId", pwInfo.getASiteId());
		map.put("zSiteId", pwInfo.getZSiteId());
		map.put("condition", new ArrayList<Integer>());
		map.put("type", type);
		map.put("manufacturerA", manufacturerA);
		map.put("manufacturerZ", manufacturerZ);
		List<Integer> labelList = labelInfoMapper.queryLabelListBySite(map);
		if (labelList.size() == 0) {
			label = this.initLabel(pwInfo.getASiteId(), pwInfo.getZSiteId(), labelInfoMapper, manufacturerA, manufacturerZ);
		}else{
			label = labelList.get(0);
		}
		return label;
	}
	
	private void updateLabelStatus(LabelInfoMapper labelInfoMapper, int label, int siteId, int manufacturer) throws Exception{
		LabelInfo labelCondition = new LabelInfo();
		labelCondition.setLabelValue(label);
		labelCondition.setSiteid(siteId);
		labelCondition.setType(manufacturer == 1 ? "CX" : "WH");
		labelCondition.setLabelStatus(0);
		labelInfoMapper.updateBatch(labelCondition);
	}

	/**
	 * 初始化标签
	 */
	private int initLabel(int aSiteId, int zSiteId, LabelInfoMapper labelInfoMapper, int manufacturerA, int manufacturerZ) throws Exception {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//目前设备的芯片不支持同一端口的lsp的入标签和该端口上的pw的入标签一样,所以要用下面的代码
			String type = "PW";
			if(type.equals("TUNNEL") || type.equals("PW")){
				type = "WH";
			}
			//如果以后芯片支持同一端口的lsp的入标签和该端口上的pw的入标签一样,就用下面的代码,把上面的代码关掉
			while (true) {
				LabelManage labelManage = new LabelManage();
				labelManage.addLabel(aSiteId, zSiteId, "WH", labelInfoMapper);
				this.sqlSession.commit();
				map.put("aSiteId", aSiteId);
				map.put("zSiteId", zSiteId);
				map.put("condition", new ArrayList<Integer>());
				map.put("type", type);
				map.put("manufacturerA", manufacturerA);
				map.put("manufacturerZ", manufacturerZ);
				List<Integer> labelList = labelInfoMapper.queryLabelListBySite(map);
				for (Integer label : labelList) {
					if(label > 0){
						return label;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private String getPwType(PwInfo pwinfo, String type, int siteId) throws Exception {
		SiteService_MB siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
		if (siteService.getManufacturer(siteId) == EManufacturer.valueOf("WUHAN").getValue()) {
			return "ethpw";
		}
		if (pwinfo.getType() == EPwType.PDH_SDH) {
			if ("a".equals(type)) {
				return "pdhpw";
			} else {
				return "sdhpw";
			}
		} else if (pwinfo.getType() == EPwType.SDH_PDH) {
			if ("a".equals(type)) {
				return "sdhpw";
			} else {
				return "pdhpw";
			}
		} else {
			return pwinfo.getType().toString().toLowerCase() + "pw";
		}
	}

	public int update(PwInfo pwinfo) throws Exception {
		if (pwinfo == null) {
			throw new Exception("pwinfo is null");
		}
		int result = 0;
		PwInfo oldPwInfo = null;
		OamInfoService_MB oamInfoService = null;
		QosRelevanceService_MB qosRelevanceService = null;
		LabelInfoService_MB labelService = null;
		try {
			this.sqlSession.getConnection().setAutoCommit(true);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo, this.sqlSession);
		    labelService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO, this.sqlSession);
		    MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			//先释放之前的标签，在更新pw，更新标签
			//释放标签
		    oldPwInfo = new PwInfo();
		    oldPwInfo.setPwId(pwinfo.getPwId());
			oldPwInfo = selectBypwid_notjoin(oldPwInfo);
			if(oldPwInfo.getIsSingle() == 0){
				//修改标签状态，如果是网络侧，修改a，z两端，否则只修改一端
				if("1".equals(oldPwInfo.getBusinessType())){
					labelService.updateBatch(oldPwInfo.getOutlabelValue(), oldPwInfo.getASiteId(), 1, "PW");
					for(MsPwInfo msPwInfo: oldPwInfo.getMsPwInfos()){
						labelService.updateBatch(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), 1, "PW");
						labelService.updateBatch(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(), 1, "PW");
					}
					labelService.updateBatch(oldPwInfo.getBackInlabel(), oldPwInfo.getZSiteId(), 1, "PW");
				}else{
					labelService.updateBatch(oldPwInfo.getInlabelValue(), oldPwInfo.getASiteId(), 1, "PW");
					labelService.updateBatch(oldPwInfo.getOutlabelValue(), oldPwInfo.getZSiteId(), 1,"PW");
				}
				
			}else{
				if(oldPwInfo.getASiteId()>0){
					labelService.updateBatch(oldPwInfo.getInlabelValue(), oldPwInfo.getASiteId(), 1, "PW");
				}else if(oldPwInfo.getZSiteId()>0){
					labelService.updateBatch(oldPwInfo.getOutlabelValue(), oldPwInfo.getZSiteId(), 1, "PW");
				}else
				{
					if(oldPwInfo.getMsPwInfos() != null && oldPwInfo.getMsPwInfos().size()> 0)
					{
						for(MsPwInfo msPwInfo: oldPwInfo.getMsPwInfos()){
							labelService.updateBatch(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), 1, "PW");
							labelService.updateBatch(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(), 1, "PW");
						}	
					}
				}
			}
			//更新标签
			//将labelInfo中没有的数据插入labelInfo中
			if(pwinfo.getIsSingle() == 0){
				if("1".equals(pwinfo.getBusinessType())){
					labelService.insertNewLabel(pwinfo.getOutlabelValue(), pwinfo.getASiteId(), "PW");
					labelService.updateBatch(pwinfo.getOutlabelValue(), pwinfo.getASiteId(), 0, "PW");
					for(MsPwInfo msPwInfo: pwinfo.getMsPwInfos()){
						labelService.insertNewLabel(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), "PW");
						labelService.updateBatch(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), 0, "PW");
						labelService.insertNewLabel(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(),"PW");
						labelService.updateBatch(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(), 0, "PW");
					}
					labelService.insertNewLabel(pwinfo.getBackInlabel(), pwinfo.getZSiteId(), "PW");
					labelService.updateBatch(pwinfo.getBackInlabel(), pwinfo.getZSiteId(), 0, "PW");
				}else{
					labelService.insertNewLabel(pwinfo.getInlabelValue(), pwinfo.getASiteId(), "PW");
					labelService.insertNewLabel(pwinfo.getOutlabelValue(), pwinfo.getZSiteId(),"PW");
					// 修改前向标签状态
					labelService.updateBatch(pwinfo.getInlabelValue(), pwinfo.getASiteId(), 0,"PW");
					// 修改后向标签状态
					labelService.updateBatch(pwinfo.getOutlabelValue(), pwinfo.getZSiteId(), 0,"PW");
				}
			}else{
				if(pwinfo.getZSiteId()>0){
					labelService.insertNewLabel(pwinfo.getOutlabelValue(), pwinfo.getZSiteId(), "PW");
					labelService.updateBatch(pwinfo.getOutlabelValue(), pwinfo.getZSiteId(), 0, "PW");
				}else if(pwinfo.getASiteId()>0){
					labelService.insertNewLabel(pwinfo.getInlabelValue(), pwinfo.getASiteId(), "PW");
					labelService.updateBatch(pwinfo.getInlabelValue(), pwinfo.getASiteId(), 0, "PW");
				}else
				{
					if(pwinfo.getMsPwInfos() != null && pwinfo.getMsPwInfos().size()> 0)
					{
						for(MsPwInfo msPwInfo: pwinfo.getMsPwInfos()){
							labelService.insertNewLabel(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), "PW");
							labelService.updateBatch(msPwInfo.getFrontInlabel(), msPwInfo.getSiteId(), 0, "PW");
							labelService.insertNewLabel(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(),"PW");
							labelService.updateBatch(msPwInfo.getBackInlabel(), msPwInfo.getSiteId(), 0, "PW");
						}	
					}
					
				}
			}
			result = this.mapper.update(pwinfo);

			List<OamInfo> oamList = pwinfo.getOamList();
			if (oamList != null && oamList.size() > 0) {
				for (OamInfo oamInfo : oamList) {
					if (oamInfo.getOamType() == OamTypeEnum.AMEP) {
						oamInfo.getOamMep().setServiceId(pwinfo.getPwId());
						oamInfo.getOamMep().setObjId(pwinfo.getApwServiceId());
						oamInfo.setOamType(OamTypeEnum.AMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.ZMEP) {
						oamInfo.getOamMep().setServiceId(pwinfo.getPwId());
						oamInfo.getOamMep().setObjId(pwinfo.getZpwServiceId());
						oamInfo.setOamType(OamTypeEnum.ZMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.MEP) {
						oamInfo.getOamMep().setServiceId(pwinfo.getPwId());
						oamInfo.getOamMep().setObjId(ConstantUtil.siteId);
						oamInfo.setOamType(OamTypeEnum.MEP);
					}
					oamInfoService.saveOrUpdate(oamInfo);
				}
			}

			qosRelevanceService = (QosRelevanceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSRELEVANCE, this.sqlSession);
			if(null != pwinfo.getQosList() && pwinfo.getQosList().size() > 0){
				qosRelevanceService.synchro(pwinfo.getPwId(), pwinfo.getQosList().get(0), EServiceType.PW.toString());
			}

			//离线网元数据下载
			if(0!=pwinfo.getASiteId()){
				super.dateDownLoad(pwinfo.getASiteId(),pwinfo.getPwId(), EServiceType.PW.getValue(), EActionType.UPDATE.getValue());
			}
			if(0!=pwinfo.getZSiteId()){
				super.dateDownLoad(pwinfo.getZSiteId(),pwinfo.getPwId(), EServiceType.PW.getValue(), EActionType.UPDATE.getValue());
			}
			if(pwinfo.getMsPwInfos() != null){
				for(MsPwInfo msPwInfo : pwinfo.getMsPwInfos()){
					msPwMapper.update(msPwInfo);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			this.sqlSession.getConnection().setAutoCommit(false);
		}
		return result;
	}

	/**
	 * 根据site查询
	 * 
	 * @param siteId
	 *            网元ID
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> select(int siteId) throws Exception {

		List<PwInfo> pwlList = new ArrayList<PwInfo>();
		try {

			pwlList = mapper.queryBySiteId(siteId);
			if (null != pwlList && pwlList.size() != 0) {
				getOAMandQoSforPw(pwlList);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwlList;
	}
	
	/**
	 * 根据网元ID获取可用的pw集合
	 * 
	 * @param siteId
	 *            网元ID
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> getAvailable(int siteId,EPwType ePwType) throws Exception {

		List<PwInfo> pwInfoList = null;
		List<PwInfo> pwInfoList_result = null;
		try {
			pwInfoList_result = new ArrayList<PwInfo>();
			pwInfoList = this.selectNodeBySiteid(siteId);

			if (null != pwInfoList && pwInfoList.size() > 0) {
				for (PwInfo pwInfo : pwInfoList) {
					
					//eth类型
					if(ePwType.getValue() == EPwType.ETH.getValue()){
						if (pwInfo.getType() == EPwType.ETH) {
							if(this.pwIsAvailable(pwInfo, siteId)){
								pwInfoList_result.add(pwInfo);
							}
						}
					}else if(ePwType.getValue() == EPwType.PDH.getValue()){		//pdh类型
						if (pwInfo.getASiteId() == siteId) {
							if (pwInfo.getType().getValue() == EPwType.PDH.getValue() || pwInfo.getType().getValue() == EPwType.PDH_SDH.getValue()) {
								if(this.pwIsAvailable(pwInfo, siteId)){
									pwInfoList_result.add(pwInfo);
								}
							}
						} else {
							if (pwInfo.getType().getValue() == EPwType.PDH.getValue() || pwInfo.getType().getValue() == EPwType.SDH_PDH.getValue()) {
								if(this.pwIsAvailable(pwInfo, siteId)){
									pwInfoList_result.add(pwInfo);
								}
							}
						}
					}else {		//sdh类型
						if (pwInfo.getASiteId() == siteId) {
							if (pwInfo.getType().getValue() == EPwType.SDH.getValue() || pwInfo.getType().getValue() == EPwType.SDH_PDH.getValue()) {
								if(this.pwIsAvailable(pwInfo, siteId)){
									pwInfoList_result.add(pwInfo);
								}
							}
						} else {
							if (pwInfo.getType().getValue() == EPwType.SDH.getValue() || pwInfo.getType().getValue() == EPwType.PDH_SDH.getValue()) {
								if(this.pwIsAvailable(pwInfo, siteId)){
									pwInfoList_result.add(pwInfo);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return pwInfoList_result;
	}
	
	/**
	 * 验证已被用的pw，是否被单网元的业务被用。
	 * 如果是，验证此单网元业务是否为参数网元的业务，如果不是。说明此pw的参数网元端没有被使用
	 * @param pwInfo 
	 * @param siteId
	 * @return
	 * @throws Exception 
	 */
	private boolean pwIsAvailable(PwInfo pwInfo , int siteId) throws Exception{
		boolean flag=false;
		List<EtreeInfo> serviceInfoList=null;
		EtreeInfoMapper etreeInfoMapper=null;
		try {
			
			//如果没被任何业务使用，可直接返回
			if(pwInfo.getRelatedServiceId() == 0){
				flag=true;
			}else{
				//如果被使用，并且是网络侧数据。 需要验证是否被siteId端所使用
				if(pwInfo.getIsSingle() == 0){
					etreeInfoMapper = sqlSession.getMapper(EtreeInfoMapper.class);
					serviceInfoList=etreeInfoMapper.queryByPwId(pwInfo.getPwId());
					for(EtreeInfo serviceInfo : serviceInfoList){
						if(serviceInfo.getServiceType() == EServiceType.ETREE.getValue()){
							serviceInfo.setaSiteId(serviceInfo.getRootSite());
							serviceInfo.setzSiteId(serviceInfo.getBranchSite());
						}else if(serviceInfo.getServiceType() == EServiceType.DUAL.getValue()){
							serviceInfo.setaSiteId(serviceInfo.getRootSite());
							if(serviceInfo.getBranchMainSite() != 0){
								serviceInfo.setzSiteId(serviceInfo.getBranchMainSite());
							}else if(serviceInfo.getBranchProtectSite()!= 0){
								serviceInfo.setzSiteId(serviceInfo.getBranchProtectSite());
							}
						}
						if(serviceInfo.getaSiteId() == siteId || serviceInfo.getzSiteId() == siteId){
							flag=false;
							break;
						}else{
							flag=true;
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally{
			serviceInfoList=null;
		}
		return flag;
	}

	public List<PwInfo> selectServiceIdsByPwIds(List<Integer> pwIdList) throws ParseException {
		List<PwInfo> pwList = this.mapper.findPwByIds(pwIdList);
		if(pwList != null && pwList.size() > 0){
			for (PwInfo pwInfo : pwList) {
				pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
			}
		}
		return pwList;
	}

	public void setUser(int pwId, ServiceInfo etreeInfo) {
		try {
			if (pwId == 0) {
				throw new Exception("pwinfo is null");
			}
			this.mapper.setUser(pwId, etreeInfo.getId(), etreeInfo.getServiceType());
			this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} 
	}

	public PwInfo queryByPwId(PwInfo pwinfo) {
		PwInfo pwInfo = null;
		try {
			List<Integer> pwIdList = new ArrayList<Integer>();
			pwIdList.add(pwinfo.getPwId());
			List<PwInfo> pwList = this.mapper.findPwByIds(pwIdList);
			if(pwList != null && pwList.size() > 0){
				for (PwInfo pw : pwList) {
					pw.setCreateTime(DateUtil.strDate(pw.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			this.getOAMandQoSforPw(pwList);
			pwInfo = pwList.get(0);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwInfo;
	}
	
	/**
	 * 通过网元ID查询
	 * 
	 * @param siteid
	 *            网元id
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> queryBySiteId(int siteid) throws Exception {
		PwInfo pwinfo = new PwInfo();
		pwinfo.setZSiteId(siteid);
		pwinfo.setASiteId(siteid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pwInfo", pwinfo);
		if(pwinfo != null && pwinfo.getType() != null){
			map.put("type", pwinfo.getType().getValue());
		}else{
			map.put("type", 0);
		}
		return this.mapper.queryNode(map);
	}
	
	/**
	 * 根据siteID，业务id查询pw
	 * 
	 * @param siteId
	 * @param serviceId
	 * @return
	 */
	public List<PwInfo> selectBysiteIdAndServiceId(int siteId,int serviceId){
		List<PwInfo> pwInfos = null;
		try {
			pwInfos = new ArrayList<PwInfo>();
			pwInfos = this.mapper.queryBySiteAndServiceId(siteId, serviceId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwInfos;
	}

	/**
	 * 修改时 验证qos是否与AC匹配
	 * @param pwInfo  要修改的pw对象
	 * @param qosInfoList 修改后的qos对象
	 * @return
	 * @throws Exception 
	 */
	public boolean checkQosPwAndAc(PwInfo pwInfo,List<QosInfo> qosInfoList) throws Exception{
		if(null==pwInfo){
			throw new Exception("pwInfo is null");
		}
		if(null==qosInfoList || qosInfoList.size()==0){
			throw new Exception("qosInfoList is null");
		}
		boolean flag=true;
		List<EtreeInfo> serviceInfoList=null;
		List<Integer> acIdList=new ArrayList<Integer>();
		AcPortInfoService_MB acInfoService=null;
		List<AcPortInfo> acPortInfoList=null;
		QosInfoService_MB qosInfoService=null;
		UiUtil uiUtil = new UiUtil();
		try { 
			//如果是eth的pw并且已经关联了业务。 才做此操作
			if(pwInfo.getType()==EPwType.ETH && pwInfo.getRelatedServiceId()>0){
				//根据pw查询业务对象
				EtreeInfoMapper etreeMapper = this.sqlSession.getMapper(EtreeInfoMapper.class);
				serviceInfoList = etreeMapper.queryByPwId(pwInfo.getPwId());
				//把业务对象的AZ端AC放入集合中。 
				if(null!=serviceInfoList && serviceInfoList.size()==1){
					if(serviceInfoList.get(0).getaAcId()>0){
						acIdList.add(serviceInfoList.get(0).getaAcId());
					}
					if(serviceInfoList.get(0).getzAcId()>0){
						acIdList.add(serviceInfoList.get(0).getzAcId());
					}
					if(serviceInfoList.get(0).getAmostAcId() != null && !serviceInfoList.get(0).getAmostAcId().equals(""))
					{
						acIdList.addAll(uiUtil.getAcIdSets(serviceInfoList.get(0).getAmostAcId()));
					}
					if(serviceInfoList.get(0).getZmostAcId() != null && !serviceInfoList.get(0).getZmostAcId().equals(""))
					{
						acIdList.addAll(uiUtil.getAcIdSets(serviceInfoList.get(0).getZmostAcId()));
					}
				}
				//根据ID主键集合查询AC对象
				acInfoService=(AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo, this.sqlSession);
				if(acIdList != null && acIdList.size() > 0){
					acPortInfoList = acInfoService.select(acIdList);
					//计算新的pw的qos和ac的qos是否匹配
					qosInfoService=(QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo, this.sqlSession);
					flag=qosInfoService.checkPwAndAcQos(pwInfo,qosInfoList, acPortInfoList);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 验证名字是否重复
	 * @author kk
	 * @param afterName 修改之后的名称
	 * @param beforeName 修改之前的名称
	 * @return
	 * @throws Exception
	 * @Exception 异常对象
	 */
	public boolean nameRepetition(String afterName, String beforeName) throws Exception {
		int result = this.mapper.query_name(afterName, beforeName);
		if (0 == result) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 单网元名称验证
	 * @param afterName
	 * @param beforeName
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public boolean nameRepetitionBySingle(String afterName, String beforeName,int siteId) throws Exception {
		int result = this.mapper.query_nameBySingle(afterName, beforeName, siteId);
		if (0 == result) {
			return false;
		} else {
			return true;
		}
	}
	
	public PwInfo select(int siteId, int serviceId) throws Exception {
		List<PwInfo> pwinfos = null;
		MsPwInfoMapper msPwInfoMapper = null;
		MsPwInfo mspwinfoCondition = null;
		PwInfo pwinfo = null;
		try {
			pwinfos = this.mapper.queryBySiteAndServiceId(siteId, serviceId);
			if(pwinfos != null && pwinfos.size()>0)
			{
				pwinfo = pwinfos.get(0);
				mspwinfoCondition = new MsPwInfo();
				msPwInfoMapper = sqlSession.getMapper(MsPwInfoMapper.class);
				mspwinfoCondition.setPwId(pwinfo.getPwId());
				pwinfo.setMsPwInfos(msPwInfoMapper.queryByCondition(mspwinfoCondition));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwinfo;
	}
	
	/**
	 * 查询不是eth类型的pw
	 * 
	 * @param siteId
	 *            为0说明是查询网络侧的。 不为0说明是查询单网元的
	 * @return
	 * @throws Exception
	 */
	public List<PwInfo> selectNotEth(PwInfo pwInfoCondition) throws Exception{
		List<PwInfo> pwInfoList = new ArrayList<PwInfo>();
		pwInfoList = this.mapper.queryNotEth(pwInfoCondition);
		return pwInfoList;
	}
	
	public List<PwInfo> select() throws Exception {

		List<PwInfo> pwinfoList = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		MsPwInfo mspwinfoCondition = null;
		try {
			PwInfo pwinfo = new PwInfo();
			pwinfoList = new ArrayList<PwInfo>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", pwinfo);
			if(pwinfo != null && pwinfo.getType() != null){
				map.put("type", pwinfo.getType().getValue());
			}else{
				map.put("type", 0);
			}
			pwinfoList = this.mapper.queryByCondition(map);
			PwNniInfoMapper pwNniMapper =null; 
			for (PwInfo pwInfo : pwinfoList) {// 封装对应的pwnniInfo
				mspwinfoCondition = new MsPwInfo();
				mspwinfoCondition.setPwId(pwInfo.getPwId());
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setPwId(pwInfo.getPwId());
				pwNniMapper=this.sqlSession.getMapper(PwNniInfoMapper.class);
				infos = new ArrayList<PwNniInfo>();
				infos =  pwNniMapper.queryByCondition(pwNniInfo);
				if(infos!=null && infos.size()>0){
					for (PwNniInfo info : infos) {
						if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
							pwInfo.setaPwNniInfo(info);
						}
						if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
							pwInfo.setzPwNniInfo(info);
						}
					}
				}
				MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
				List<MsPwInfo> mspwinfo = new ArrayList<MsPwInfo>();
				mspwinfo =msPwMapper.queryByCondition(mspwinfoCondition);
				pwInfo.setMsPwInfos(mspwinfo);
			}
			getOAMandQoSforPw(pwinfoList);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwinfoList;
	}
	
	/**
	 * 查询数据库中条件等于参数pwinfo的pwinfo集合。（eline快速配置业务时，查询是否存在可用pwinfo时用）
	 * 
	 * @param pwinfo
	 *            页面传递的pwinfo对象
	 * @return tunnel 对象
	 * @throws Exception
	 * @throws Exception
	 */
	public PwInfo selectExistPwinfo(PwInfo pwInfo) throws Exception {
		List<Integer> tunnelIds = new ArrayList<Integer>();
		List<PwInfo> pwinfoList = null;
		PwInfo pwInfoResult = null;
		QosInfoService_MB qosInfoService = null;
		try {
			//从数据库查出此tunnel中的所有pw
			qosInfoService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo, this.sqlSession);
			tunnelIds.add(pwInfo.getTunnelId());
			pwinfoList = this.mapper.queryByPwTunnelIdCondition(tunnelIds);
			
			//如果有pw 比较qos  
			if (null != pwinfoList && pwinfoList.size() > 0) {
				for (PwInfo pwInfo_db : pwinfoList) {
					if(pwInfo_db.getRelatedServiceId() == 0 && pwInfo_db.getType().getValue() == pwInfo.getType().getValue()){
						//如果qos匹配。返回pw对象
						if (qosInfoService.compareQos(pwInfo_db.getPwId(), EServiceType.PW, pwInfo.getQosList())) {
							pwInfoResult = pwInfo_db;
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return pwInfoResult;
	}
	
	public List<PwInfo> selectbyType(PwInfo pwinfoConditin) throws Exception {
		List<PwInfo> pwinfoList = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> infos = null;
		MsPwInfo mspwinfoCondition = null;
		List<Tunnel> tunnels = null;
		List<PwInfo> pwInfoList1 = null;
		List<PwInfo> pwInfoList2 = null;
		try {
			PwNniInfoMapper pwNniMapper = this.sqlSession.getMapper(PwNniInfoMapper.class);
			pwinfoList = new ArrayList<PwInfo>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwInfo", pwinfoConditin);
			if(pwinfoConditin != null && pwinfoConditin.getType() != null){
				map.put("type", pwinfoConditin.getType().getValue());
			}else{
				map.put("type", 0);
			}
			pwInfoList1 = this.mapper.queryByType(map);
			if(pwInfoList1 != null && pwInfoList1.size() > 0){
				for (PwInfo pwInfo : pwInfoList1) {
					pwInfo.setCreateTime(DateUtil.strDate(pwInfo.getCreateTime(), DateUtil.FULLTIME));
				}
			}
			//先通过端口号查tunnel，再通过tunnel查pw，然后过滤pw
			if(pwinfoConditin.getPortId() > 0)
			{
				TunnelMapper tunnelMapper = this.sqlSession.getMapper(TunnelMapper.class);
				tunnels = tunnelMapper.queryByportId(pwinfoConditin.getPortId());
				if(tunnels.size()>0)
				{
					List<Integer> tunnelIds = new ArrayList<Integer>();
					for(Tunnel tunnel:tunnels)
					{
						tunnelIds.add(tunnel.getTunnelId());
					}
					pwInfoList2 = this.mapper.queryByPwTunnelIdCondition(tunnelIds);
					for(PwInfo pw1 : pwInfoList1)
					{
						for(PwInfo pw2 : pwInfoList2)
						{
							if(pw2.getPwId() == pw1.getPwId())
							{
								pwinfoList.add(pw1);
							}
						}
					}
				}
			}
			else
			{
				pwinfoList.addAll(pwInfoList1);
			}
			MsPwInfoMapper msPwMapper = this.sqlSession.getMapper(MsPwInfoMapper.class);
			for (PwInfo pwInfo : pwinfoList) {// 封装对应的pwnniInfo
				mspwinfoCondition = new MsPwInfo();
				mspwinfoCondition.setPwId(pwInfo.getPwId());
//				pwNniInfo = new PwNniInfo();
//				pwNniInfo.setPwId(pwInfo.getPwId());
//				infos = pwNniMapper.queryByCondition(pwNniInfo);
//				for (PwNniInfo info : infos) {
//					if (info.getSiteId() == pwInfo.getASiteId() && pwInfo.getApwServiceId() != 0) {
//						pwInfo.setaPwNniInfo(info);
//					}
//					if (info.getSiteId() == pwInfo.getZSiteId() && pwInfo.getZpwServiceId() != 0) {
//						pwInfo.setzPwNniInfo(info);
//					}
//				}
				pwInfo.setMsPwInfos(msPwMapper.queryByCondition(mspwinfoCondition));
			}

//			getOAMandQoSforPw(pwinfoList);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return pwinfoList;
	}
	
	public void updateActiveStatus(List<Integer> pwIdList, int status) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pwIdList", pwIdList);
			map.put("status", status);
			this.mapper.updateStatus(map);
			this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 通过主键查询pw对象
	 * 只提供给多段PW来查询
	 * lable = 1 查询单站 否则查询所有
	 * @param pwId
	 * @return
	 * @throws Exception
	 */ 
	public PwInfo selectMsPwByPwId(int pwId,int lable) throws Exception {
		List<PwInfo> pwInfoList = null;
		PwInfo pwInfo = null;
		try {
			pwInfo = new PwInfo();
			pwInfo.setPwId(pwId);
			pwInfo.setIsSingle(lable);
			pwInfoList = this.mapper.selectMsPwByPwId(pwInfo);
			if (null != pwInfoList && pwInfoList.size() == 1) {
				for (PwInfo pwinfo : pwInfoList) {
					pwinfo.setCreateTime(DateUtil.strDate(pwinfo.getCreateTime(), DateUtil.FULLTIME));
				}
				this.getOAMandQoSforPw(pwInfoList);
				pwInfo = pwInfoList.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			pwInfoList = null;
		}
		return pwInfo;
	}
}
