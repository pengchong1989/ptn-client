package com.nms.model.ptn.port;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.ptn.path.protect.DualProtect;
import com.nms.db.bean.ptn.path.protect.DualRelevance;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.dao.ptn.path.tunnel.TunnelMapper;
import com.nms.db.dao.ptn.port.DualProtectMapper;
import com.nms.db.enums.EDualProtectType;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;

public class DualProtectService_MB extends ObjectService_Mybatis{
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	private DualProtectMapper dualProtectMapper;

	public DualProtectMapper getDualProtectMapper() {
		return dualProtectMapper;
	}

	public void setDualProtectMapper(DualProtectMapper dualProtectMapper) {
		this.dualProtectMapper = dualProtectMapper;
	}
	
	/**
	 * 通过网元ID查询
	 * @param siteId 网元ID
	 * @return
	 */
	public List<DualProtect> selectBySite(int siteId) {
		TunnelMapper tunnelMapper = null;
		DualProtect dualProtectSel = null;
		dualProtectSel = new DualProtect();
		dualProtectSel.setSiteId(siteId);
		List<DualProtect> dualProtectList = null;
		Tunnel BreakoverTunnel;
		Tunnel relevanceTunnel;
		List<Tunnel> relevanceTunnelList;
		DualRelevance dualRelevanceSel;
		List<DualRelevance> dualRelevanceList;
		List<Tunnel> relevanceTunnelListSel;
		DualRelevanceService_MB dualRelevanceServiceMB = null;
		try {
			tunnelMapper = sqlSession.getMapper(TunnelMapper.class);
			dualRelevanceServiceMB = (DualRelevanceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALRELEVANCE, this.sqlSession);
			relevanceTunnelList = new ArrayList<Tunnel>();
			BreakoverTunnel = new Tunnel();
			relevanceTunnel = new Tunnel();
			dualProtectList =  this.dualProtectMapper.queryByCondition(dualProtectSel);
			//添加没一个 穿通 和 关联Tunnel
			for(DualProtect dualProtect:dualProtectList){
				dualRelevanceSel = new DualRelevance();
				dualRelevanceSel.setSiteId(dualProtect.getSiteId());
				dualRelevanceSel.setObjId(dualProtect.getId());
				dualRelevanceSel.setDualGroupId(dualProtect.getDualRelevanceGroupId());
				dualRelevanceList = dualRelevanceServiceMB.queryByCondition(dualRelevanceSel);
				for(DualRelevance dualRelevance:dualRelevanceList){
					//如果是穿通tunnel
					if(EDualProtectType.BREAKOVER.getValue()==dualRelevance.getObjType()){
						BreakoverTunnel = tunnelMapper.queryBySiteIdAndTunnelId(dualRelevance.getSiteId(), dualRelevance.getTunnelId());
						dualProtect.setBreakoverTunnel(BreakoverTunnel);
					}else{//如果是关联tunnel
						relevanceTunnel.setTunnelId(dualRelevance.getTunnelId());
						relevanceTunnelListSel = tunnelMapper.queryByCondition_nojoin(relevanceTunnel);
						if(null!=relevanceTunnelListSel&&relevanceTunnelListSel.size()==1){
							relevanceTunnelList.add(relevanceTunnelListSel.get(0));
						}
					}
				}
				dualProtect.setRelevanceTunnelList(relevanceTunnelList);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
//			UiUtil.closeService(dualRelevanceService);
		}
		return dualProtectList;
	}
	
	/**
	 * 通过条件查询
	 * @param siteId 网元ID
	 * @return
	 */
	public List<DualProtect> queryByCondition(DualProtect condition) {
		TunnelMapper tunnelMapper = null;
		DualProtect dualProtectSel = condition;
		List<DualProtect> dualProtectList = null;
		Tunnel BreakoverTunnel;
		Tunnel relevanceTunnel;
		DualRelevance dualRelevanceSel;
		List<Tunnel> relevanceTunnelList;
		List<DualRelevance> dualRelevanceList;
		List<Tunnel> relevanceTunnelListSel;
		DualRelevanceService_MB dualRelevanceService = null;
		try {
			dualRelevanceService = (DualRelevanceService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALRELEVANCE, this.sqlSession);
			relevanceTunnelList = new ArrayList<Tunnel>();
			BreakoverTunnel = new Tunnel();
			relevanceTunnel = new Tunnel();
			dualProtectList =  this.dualProtectMapper.queryByCondition(dualProtectSel);
			tunnelMapper = sqlSession.getMapper(TunnelMapper.class);
			//添加没一个 穿通 和 关联Tunnel
			for(DualProtect dualProtect:dualProtectList){
				dualRelevanceSel = new DualRelevance();
				dualRelevanceSel.setSiteId(dualProtect.getSiteId());
				dualRelevanceSel.setObjId(dualProtect.getId());
				dualRelevanceSel.setDualGroupId(dualProtect.getDualRelevanceGroupId());
				dualRelevanceList = dualRelevanceService.queryByCondition(dualRelevanceSel);
				for(DualRelevance dualRelevance:dualRelevanceList){
					//如果是穿通tunnel
					if(EDualProtectType.BREAKOVER.getValue()==dualRelevance.getObjType()){
						BreakoverTunnel = tunnelMapper.queryBySiteIdAndServiceId(dualRelevance.getSiteId(), dualRelevance.getTunnelId());
						dualProtect.setBreakoverTunnel(BreakoverTunnel);
					}
					//如果是关联tunnel
					else{
						relevanceTunnel.setTunnelId(dualRelevance.getTunnelId());
						relevanceTunnelListSel = tunnelMapper.queryByCondition_nojoin(relevanceTunnel);
						if(null!=relevanceTunnelListSel&&relevanceTunnelListSel.size()==1){
							relevanceTunnelList.add(relevanceTunnelListSel.get(0));
						}
					}
				}
				dualProtect.setRelevanceTunnelList(relevanceTunnelList);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
//			UiUtil.closeService(dualRelevanceService);
		}
		return dualProtectList;
	}
	
}
