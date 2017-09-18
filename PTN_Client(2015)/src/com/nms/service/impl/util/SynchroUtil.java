﻿/**   
 * 文件名：SynchroUtil.java   
 * 创建人：kk   
 * 创建时间：2013-5-13 下午01:25:15 
 *   
 */
package com.nms.service.impl.util;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.oam.OamEthernetInfo;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.oam.OamMipInfo;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.OamTypeEnum;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.oam.OamEthreNetService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;

/**
 * 
 * 项目名称：WuHanPTN2012 类名称：SynchroUtil 类描述：同步帮助类 创建人：kk 创建时间：2013-5-13 下午01:25:15 修改人：kk 修改时间：2013-5-13 下午01:25:15 修改备注：
 * 
 * @version
 * 
 */
public class SynchroUtil {

	/**
	 * tunnel对象与数据库同步
	 * 
	 * @author kk
	 * 
	 * @param tunnel
	 *            设备上查询出的tunnel对象
	 * @param role
	 *            类型。 "ingress"=A端 "egress"=z端 "xc"=中间节点 ""=武汉不确定是a还是z 所以同时比较az端
	 * @param siteId
	 *            网元ID
	 * @param siteRoate
	 *            设备上查出的倒换对象        
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void tunnelSynchro(Tunnel tunnel, String role, int siteId) throws Exception {

		LspInfoService_MB lspService = null;
		List<Lsp> lspList = null;
		TunnelService_MB tunnelService = null;
		SiteRoate siteRoate=null;
		OamInfoService_MB oamInfoService = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			lspList = lspService.select_synchro(role, siteId, tunnel);

//			if (lspList.size() == 0) {
//				throw new Exception("同步tunnel时 查询LSP出错");
//			}

			switch (lspList.size()) {
			case 0:
				// 插入tunnel
				tunnelService.save(tunnel);
//				/**
//				 * 插入tunnel 时，判断是否为保护，
//				 * 保护则添加  site_roate表 （倒换
//				 */
//				if("2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())){
//					siteRoate.setTypeName(tunnel.getTunnelName());
//					siteRoateService.insert(siteRoate);
//				}
				
				break;
			case 1:
				if (siteService.getManufacturer(siteId) == EManufacturer.valueOf("CHENXIAO").getValue()) {
					// 判断是不是1:1保护，如果是。修改1:1保护
					if ("2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
						int projectId = updateProtectTunnel(tunnel.getProtectTunnel(), tunnelService, lspList, lspService, role);
						if (projectId != 0) {
							tunnel.setProtectTunnelId(projectId);
						}
					}
				}else{//武汉设备需同步oam
					OamInfo oamInfo = new OamInfo();
					if(tunnel.getaSiteId() == siteId){
						oamInfo.setOamMep(tunnel.getOamList().get(0).getOamMep());
						oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.AMEP);
					}else if(tunnel.getzSiteId() == siteId){
						oamInfo.setOamMep(tunnel.getOamList().get(0).getOamMep());
						oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.ZMEP);
					}else{
						oamInfo.setOamMip(tunnel.getOamList().get(0).getOamMip());
						oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.MIP);
					}
					if(oamInfo.getOamMep() != null && oamInfo.getOamMep().getId()>0){
						tunnel.getOamList().get(0).getOamMep().setId(oamInfo.getOamMep().getId());
					}
					if(oamInfo.getOamMip() != null && oamInfo.getOamMip().getId()>0){
						tunnel.getOamList().get(0).getOamMip().setId(oamInfo.getOamMip().getId());
					}
				}

				// 修改A Z端
				updateTunnel(lspList, tunnel, lspService,tunnelService);
				/**
				 * 更新tunnel 时，判断是否为保护，
				 * 保护则更新  site_roate表 （倒换
				 */
				if(!tunnel.getTunnelType().equals("0") && "2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())){
					siteRoate=new SiteRoate();
					siteRoate.setTypeId(tunnel.getTunnelId());
					siteRoate.setType("tunnel");
					siteRoate.setSiteId(siteId);
				//	siteRoate.
				//	siteRoateService.update(siteRoate);
				}
				break;
			case 2:
				// 修改xc
				if (siteService.getManufacturer(siteId) == EManufacturer.valueOf("WUHAN").getValue()) {
					OamInfo oamInfo = new OamInfo();
					OamMipInfo oamMipInfo = new OamMipInfo();
					oamMipInfo.setSiteId(siteId);
					oamMipInfo.setServiceId(lspList.get(0).getTunnelId());
					oamMipInfo.setObjType("TUNNEL");
					oamInfo.setOamMip(oamMipInfo);
					oamInfo = oamInfoService.queryByCondition(oamInfo, OamTypeEnum.MIP);
					if(oamInfo.getOamMip().getId()>0){
						tunnel.getOamList().get(0).getOamMip().setId(oamInfo.getOamMip().getId());
						
					}
				}
				updateTunnel(lspList, tunnel, lspService,tunnelService);
				break;
			default:
				throw new Exception("同步tunnel时 查询LSP出错");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(lspService);
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 修改LSP数据库
	 * 
	 * updateDB(这里用一句话描述这个方法的作用)
	 * 
	 * @author kk
	 * 
	 * @param lspList
	 *            从数据库查询出来的lsp结果
	 * @param tunnel
	 *            要修改的tunnel对象
	 * @param lspService
	 *            lspservice对象
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void updateTunnel(List<Lsp> lspList, Tunnel tunnel, LspInfoService_MB lspService,TunnelService_MB tunnelService) throws Exception {

		OamInfoService_MB oamInfoService = null;
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			if (tunnel.getLspParticularList().get(0).getASiteId() != 0 && siteService.getManufacturer(tunnel.getLspParticularList().get(0).getASiteId()) == EManufacturer.WUHAN.getValue()) {
				for (int i = 0; i < lspList.size(); i++) {
					// 判断是哪A端还是Z端 取不同的端口做修改
					if (lspList.get(i).getASiteId() == tunnel.getLspParticularList().get(i).getASiteId() && lspList.get(i).getAtunnelbusinessid() == tunnel.getLspParticularList().get(i).getAtunnelbusinessid()) {
						lspList.get(i).setAPortId(tunnel.getLspParticularList().get(i).getAPortId());
						// 把设备读上来的标签给要修改的对象
						lspList.get(i).setFrontLabelValue(tunnel.getLspParticularList().get(i).getFrontLabelValue());
						lspList.get(i).setBackLabelValue(tunnel.getLspParticularList().get(i).getBackLabelValue());
					} else {
						lspList.get(i).setZPortId(tunnel.getLspParticularList().get(i).getAPortId());
						// 把设备读上来的标签给要修改的对象
						lspList.get(i).setFrontLabelValue(tunnel.getLspParticularList().get(i).getBackLabelValue());
						lspList.get(i).setBackLabelValue(tunnel.getLspParticularList().get(i).getFrontLabelValue());
					}
					lspList.get(i).setSourceMac(tunnel.getLspParticularList().get(i).getSourceMac());
					lspList.get(i).setTargetMac(tunnel.getLspParticularList().get(i).getTargetMac());
					// 修改
					lspService.saveOrUpdate(lspList.get(i));
					tunnel.setTunnelId(lspList.get(i).getTunnelId());
				}
			} else {
				for (int i = 0; i < lspList.size(); i++) {
					// 把设备读上来的标签给要修改的对象
					lspList.get(i).setFrontLabelValue(tunnel.getLspParticularList().get(i).getFrontLabelValue());
					lspList.get(i).setBackLabelValue(tunnel.getLspParticularList().get(i).getBackLabelValue());

					lspList.get(i).setAoppositeId(tunnel.getLspParticularList().get(i).getAoppositeId());
					lspList.get(i).setZoppositeId(tunnel.getLspParticularList().get(i).getZoppositeId());

					// 判断是哪A端还是Z端 取不同的端口做修改
					if (lspList.get(i).getASiteId() == tunnel.getLspParticularList().get(i).getASiteId() && lspList.get(i).getAtunnelbusinessid() == tunnel.getLspParticularList().get(i).getAtunnelbusinessid()) {
						lspList.get(i).setAPortId(tunnel.getLspParticularList().get(i).getAPortId());
					} else {
						lspList.get(i).setZPortId(tunnel.getLspParticularList().get(i).getZPortId());
					}
					// 修改
					lspService.saveOrUpdate(lspList.get(i));
					tunnel.setTunnelId(lspList.get(i).getTunnelId());
				}
			}
			tunnelService.update_synchro(tunnel);

			if (tunnel.getOamList().size() > 0) {
				for (OamInfo oamInfo : tunnel.getOamList()) {
					if (oamInfo.getOamType() == OamTypeEnum.AMEP) {
						oamInfo.getOamMep().setServiceId(tunnel.getTunnelId());
						oamInfo.getOamMep().setObjId(tunnel.getLspParticularList().get(0).getAtunnelbusinessid());
						oamInfo.setOamType(OamTypeEnum.AMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.ZMEP) {
						oamInfo.getOamMep().setServiceId(tunnel.getTunnelId());
						oamInfo.getOamMep().setObjId(tunnel.getLspParticularList().get(0).getZtunnelbusinessid());
						oamInfo.setOamType(OamTypeEnum.ZMEP);
					} else if (oamInfo.getOamType() == OamTypeEnum.MEP) {
						oamInfo.getOamMep().setServiceId(tunnel.getTunnelId());
					} else if (oamInfo.getOamType() == OamTypeEnum.MIP) {
						oamInfo.getOamMip().setServiceId(tunnel.getTunnelId());
					}
					oamInfoService.saveOrUpdate(oamInfo);
				}
			}

			// updateOam(tunnel.getOamList());
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 
	 * 同步保护tunnel
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private int updateProtectTunnel(Tunnel protectTunnel, TunnelService_MB tunnelService, List<Lsp> lspList, LspInfoService_MB lspService, String role) throws Exception {

		int tunnelId = 0;
		int result = 0;
		Tunnel tunnel = null;
		List<Lsp> lspList_protect = new ArrayList<Lsp>();
		try {

			tunnelId = lspList.get(0).getTunnelId();
			tunnel = new Tunnel();
			tunnel.setTunnelId(tunnelId);
			tunnel = tunnelService.select_nojoin(tunnel).get(0);

			if (null == tunnel.getProtectTunnel()) {
				result = tunnelService.savaProtect(protectTunnel);
			} else {
				result = tunnel.getProtectTunnel().getTunnelId();

				for (Lsp lsp : tunnel.getProtectTunnel().getLspParticularList()) {
					if ("ingress".equals(role)) {
						if (lsp.getASiteId() == tunnel.getProtectTunnel().getASiteId()) {
							lspList_protect.add(lsp);
							break;
						}
					} else if ("egress".equals(role)) {
						if (lsp.getZSiteId() == tunnel.getProtectTunnel().getZSiteId()) {
							lspList_protect.add(lsp);
							break;
						}
					}
				}

				updateTunnel(lspList_protect, protectTunnel, lspService,tunnelService);
			}

		} catch (Exception e) {
			throw e;
		}

		return result;
	}

	/**
	 * 修改OAM
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void updateOam(List<OamInfo> oamInfoList) throws Exception {
		OamInfoService_MB oamInfoService = null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);

			for (OamInfo oamInfo : oamInfoList) {
				oamInfoService.saveOrUpdate(oamInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * 同步以太网OAM
	 * @author kk
	 * 
	 * @param
	 * 
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public static void ethOamSynchro(OamEthernetInfo oamInfo) throws Exception {
		OamEthreNetService_MB oamInfoService = null;
		try {
			oamInfoService = (OamEthreNetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OAMETHERNET);
			oamInfoService.saveOrUpdate(oamInfo);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
	}
	/**
	 * pw 对象与数据库信息同步
	 * 
	 * @author kk
	 * 
	 * @param pwinfo
	 *            数据库pw对象
	 * @param siteid
	 *            网元id
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void pwInfoSynchro(PwInfo pwinfo, int siteid) throws Exception {
		PwInfoService_MB pwInfoService = null;
		List<PwInfo> pwInfoList = null;
		int serviceid = 0;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			if (pwinfo.getApwServiceId() == 0) {
				serviceid = pwinfo.getZpwServiceId();
			} else {
				serviceid = pwinfo.getApwServiceId();
			}
			pwInfoList = pwInfoService.select_synchro(siteid, serviceid, pwinfo.getType().getValue());

			if (null == pwInfoList) {
				throw new Exception("同步pw时 查询pw出错");
			}

			switch (pwInfoList.size()) {
			case 0:
				// 插入pw
				pwInfoService.save(pwinfo);
				break;
			case 1:
				// 修改PW
				if(pwInfoList.get(0).getTunnelId() > 0){
					updatePwinfo(pwInfoList, pwinfo, pwInfoService);
				}else{
					List<Integer> pwIdList = new ArrayList<Integer>();
					pwIdList.add(pwInfoList.get(0).getPwId());
					pwInfoService.updateActiveStatus(pwIdList, 1);
				};
				break;

			default:
				throw new Exception("同步pw时 查询pw出错");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
		}
	}

	/**
	 * 修改pw数据库
	 * 
	 * @author kk
	 * 
	 * @param pwInfoList
	 *            数据库中的pw对象
	 * @param pwinfo
	 *            设备中的pw对象
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void updatePwinfo(List<PwInfo> pwInfoList, PwInfo pwinfo, PwInfoService_MB pwInfoService) throws Exception {

		if (null == pwInfoList || pwInfoList.size() != 1) {
			throw new Exception("pwInfoList is null");
		}
		if (null == pwinfo) {
			throw new Exception("pwinfo is null");
		}
		if (null == pwInfoService) {
			throw new Exception("pwInfoService is null");
		}

		PwInfo pwInfo_db = null;
		SiteService_MB siteService = null;
		OamInfoService_MB oamInfoService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			pwInfo_db = pwInfoList.get(0);
			if (pwinfo.getASiteId() != 0 && siteService.getManufacturer(pwinfo.getASiteId()) == EManufacturer.WUHAN.getValue()) {
				pwInfo_db.setPwStatus(EActiveStatus.ACTIVITY.getValue());
				pwInfo_db.setTunnelId(pwinfo.getTunnelId());
				if (pwInfo_db.getASiteId() == pwinfo.getASiteId()) {
					pwInfo_db.setInlabelValue(pwinfo.getInlabelValue());
					pwInfo_db.setOutlabelValue(pwinfo.getOutlabelValue());
				} else {
					pwInfo_db.setInlabelValue(pwinfo.getOutlabelValue());
					pwInfo_db.setOutlabelValue(pwinfo.getInlabelValue());
				}
				pwInfo_db.setJobStatus(pwinfo.getJobStatus());
				pwInfo_db.setaOutVlanValue(pwinfo.getaOutVlanValue());
				pwInfo_db.setaVlanEnable(pwinfo.getaVlanEnable());
				pwInfo_db.setaSourceMac(pwinfo.getaSourceMac());
				pwInfo_db.setAtargetMac(pwinfo.getAtargetMac());
			} else {
				pwInfo_db.setPwStatus(EActiveStatus.ACTIVITY.getValue());
				pwInfo_db.setTunnelId(pwinfo.getTunnelId());
				pwInfo_db.setInlabelValue(pwinfo.getInlabelValue());
				pwInfo_db.setOutlabelValue(pwinfo.getOutlabelValue());
				pwInfo_db.setQosList(pwinfo.getQosList());
				pwInfo_db.setAoppositeId(pwinfo.getAoppositeId());
				pwInfo_db.setZoppositeId(pwinfo.getZoppositeId());
				pwInfo_db.setJobStatus(pwinfo.getJobStatus());
				pwInfo_db.setzOutVlanValue(pwinfo.getaOutVlanValue());
				pwInfo_db.setzVlanEnable(pwinfo.getaVlanEnable());
				pwInfo_db.setzSourceMac(pwinfo.getaSourceMac());
				pwInfo_db.setZtargetMac(pwinfo.getAtargetMac());
			}

			// pwInfo_db.setPwName(pwinfo.getPwName());
			if(pwinfo.getOamList() != null && pwinfo.getOamList().size()>0){//武汉设备需同步pw的oam
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				for(OamInfo oamInfo : pwinfo.getOamList()){
					if(oamInfo.getOamMep() != null){
						List<OamMepInfo> oamMepInfos = oamInfoService.selectByOamMepInfo(oamInfo.getOamMep());
						if(oamMepInfos.size()>0){
							oamInfo.getOamMep().setId(oamMepInfos.get(0).getId());
						}
					}
				}
				pwInfo_db.setOamList(pwinfo.getOamList());
			}
			if(pwinfo.getQosList() != null && pwinfo.getQosList().size()>0){
				
				pwInfo_db.setQosList(pwinfo.getQosList());
			}
			pwInfoService.update(pwInfo_db);

			// updateQos(tunnel.getQosList());

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(oamInfoService);
		}
	}

	/**
	 * eline 对象与数据库信息同步
	 * 
	 * @author kk
	 * 
	 * @param eline
	 *            数据库eline对象
	 * @param siteid
	 *            网元id
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void elineSynchro(ElineInfo elineInfo, int siteid) throws Exception {
		ElineInfoService_MB elineService = null;
		List<ElineInfo> elineInfoList = null;
		try {
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			if(elineInfo.getaSiteId() == siteid){
				elineInfoList = elineService.select_synchro(siteid, elineInfo.getaXcId());
			}else if(elineInfo.getzSiteId() == siteid){
				elineInfoList = elineService.select_synchro(siteid, elineInfo.getzXcId());
			}
			if (null == elineInfoList) {
				throw new Exception("同步eline时 查询eline出错");
			}

			switch (elineInfoList.size()) {
			case 0:
				// 插入eline
				elineInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				elineService.save(elineInfo);
				break;
			case 1:
				// 修改eline
				updateEline(elineInfoList, elineInfo, elineService);
				break;

			default:
				throw new Exception("同步eline时 查询eline出错");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(elineService);
		}
	}
	/**
	 * 修改eline数据库
	 * 
	 * @author kk
	 * 
	 * @param elineInfoList
	 *            数据库查询出来的eline对象
	 * @param elineInfo
	 *            设备上的eline对象
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void updateEline(List<ElineInfo> elineInfoList, ElineInfo elineInfo, ElineInfoService_MB elineService) throws Exception {

		if (null == elineInfoList || elineInfoList.size() != 1) {
			throw new Exception("elineInfoList is null");
		}
		if (null == elineInfo) {
			throw new Exception("elineInfo is null");
		}
		if (null == elineService) {
			throw new Exception("elineService is null");
		}

		ElineInfo elineInfo_db = null;
		try {
			elineInfo_db = elineInfoList.get(0);
			elineInfo_db.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
			elineInfo_db.setPwId(elineInfo.getPwId());
			elineInfo_db.setJobStatus(elineInfo.getJobStatus());
			if (elineInfo_db.getaSiteId() == elineInfo.getaSiteId()) {
				elineInfo_db.setaAcId(elineInfo.getaAcId());
			} else {
				elineInfo_db.setzAcId(elineInfo.getzAcId());
			}

			elineService.update(elineInfo_db);

		} catch (Exception e) {
			throw e;
		} finally {
			elineInfo_db = null;
		}
	}

	/**
	 * dualInfo 对象与数据库信息同步
	 * 
	 * @author kk
	 * 
	 * @param dualInfo
	 *            数据库eline对象
	 * @param siteid
	 *            网元id
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void dualSynchro(DualInfo dualInfo, int siteId) throws Exception {
		List<DualInfo> dualInfoListDB = null;
		DualInfoService_MB dualInfoService = null;
		List<DualInfo> dualInfoList = null;
		DualInfo dualInfoDb = null;
		try {
			dualInfoService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			dualInfoListDB = new ArrayList<DualInfo>();
			if(dualInfo.getaXcId()>0){
				dualInfoDb = dualInfoService.selectBySiteIdAndBusinessId(siteId,dualInfo.getaXcId());
			}else if(dualInfo.getzXcId() >0){
				dualInfoDb = dualInfoService.selectBySiteIdAndBusinessId(siteId,dualInfo.getzXcId());
			}
			
			if(dualInfoDb != null)
			{
				dualInfoListDB.add(dualInfoDb);
			}
			if (null == dualInfoListDB) {
               throw new Exception("同步dual时 查询dual出错");
			}

			switch (dualInfoListDB.size()) {
			case 0:
				dualInfoList = new ArrayList<DualInfo>();
				dualInfoList.add(dualInfo);
				// 插入dual
				dualInfoService.insert(dualInfoList);
				break;
			case 1:
				// 修改eline
				updateDual(dualInfoListDB, dualInfo, dualInfoService);
				break;

			default:
				throw new Exception("同步dual时 查询dual出错");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(dualInfoService);
			dualInfoListDB = null;
			dualInfoList = null;
			dualInfoDb= null;
		}
	}
	
	private void updateDual(List<DualInfo> dualInfoListDB, DualInfo dualInfo,
			DualInfoService_MB dualInfoService) throws Exception{
		
		if (null == dualInfoListDB || dualInfoListDB.size() != 1) {
			throw new Exception("dualInfoListDB is null");
		}
		if (null == dualInfo) {
			throw new Exception("dualInfo is null");
		}
		if (null == dualInfoService) {
			throw new Exception("dualInfoService is null");
		}
		try 
		{
			dualInfoListDB.get(0).setActiveStatus(EActiveStatus.ACTIVITY.getValue());
			dualInfoListDB.get(0).setPwId(dualInfo.getPwId());
			if(dualInfo.getaSiteId()>0){
				dualInfoListDB.get(0).setaAcId(dualInfo.getaAcId());
			}else {
				dualInfoListDB.get(0).setzAcId(dualInfo.getzAcId());
			}
			

			dualInfoService.update(dualInfoListDB);
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		
	}

	/**
	 * Ces 对象与数据库信息同步
	 * 
	 * @author kk
	 * 
	 * @param cesinfo
	 *            数据库ces对象
	 * @param siteid
	 *            网元id
	 * 
	 * @return
	 * @throws Exception
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void CesSynchro(CesInfo cesInfo, int siteId) throws Exception {
		CesInfoService_MB cesInfoService = null;
		List<CesInfo> cesInfoList = null;
		try {
			cesInfoService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			if(cesInfo.getaSiteId() == siteId){
				cesInfoList = cesInfoService.select_synchro(siteId, cesInfo.getAxcId());
			}else if(cesInfo.getzSiteId() == siteId){
				cesInfoList = cesInfoService.select_synchro(siteId, cesInfo.getZxcId());
			}
			if (null == cesInfoList) {
				throw new Exception("同步ces时查询ces出错");
			}

			switch (cesInfoList.size()) {
			case 0:
				// 插入eline
				cesInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				cesInfoService.save(cesInfo);
				break;
			case 1:
				// 修改eline
				updateCes(cesInfoList, cesInfo, cesInfoService);
				break;

			default:
				throw new Exception("同步ces时查询ces出错");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(cesInfoService);
		}
	}

	/**
	 * 修改eline数据库
	 * 
	 * @author kk
	 * 
	 * @param elineInfoList
	 *            数据库查询出来的eline对象
	 * @param elineInfo
	 *            设备上的eline对象
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void updateCes(List<CesInfo> cesInfoList, CesInfo cesInfo, CesInfoService_MB cesInfoService) throws Exception {

		if (null == cesInfoList || cesInfoList.size() != 1) {
			throw new Exception("cesInfoList is null");
		}
		if (null == cesInfo) {
			throw new Exception("cesInfo is null");
		}
		if (null == cesInfoService) {
			throw new Exception("cesInfoService is null");
		}

		try {
			cesInfoList.get(0).setActiveStatus(EActiveStatus.ACTIVITY.getValue());
			cesInfoList.get(0).setPwId(cesInfo.getPwId());
			if (cesInfoList.get(0).getaSiteId() == cesInfo.getaSiteId()) {
				cesInfoList.get(0).setaAcId(cesInfo.getaAcId());
			} else {
				cesInfoList.get(0).setzAcId(cesInfo.getzAcId());
			}

			cesInfoService.update(cesInfoList);

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	/**
	 * ac 对象与数据库信息同步
	 * 
	 * @author wangwf
	 * 
	 * @param acPortInfo
	 *            数据库acPortInfo对象
	 * @param siteid
	 *            网元id
	 * 
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 * 
	 * @return acId AC的数据库主键ID
	 */
	public int acPortInfoSynchro(AcPortInfo acPortInfo, int siteid) throws Exception {
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acPortInfoList = null;
		SiteService_MB siteService = null;
		int acId = 0;
		PortService_MB portService = null;
		PortLagService_MB lagService = null;
		try {
			/*by guoqc*****************************************************/
			int[] acRelevanceArr = new int[6];//ac关联规则的规则集合，元素为1表示关联，0表示不关联
			int vlanRelevance = 0;
			int eightIpRelevance = 0;
			int sourMacRelevance = 0;
			int endMacRelevance = 0;
			int sourIPRelevance = 0;
			int endIPRelevance = 0;
			if(acPortInfo.getPortId() > 0){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				PortInst port = new PortInst();
				port.setPortId(acPortInfo.getPortId());
				List<PortInst> portList = portService.select(port);
				if(portList != null && !portList.isEmpty()){
					port = portList.get(0);
					vlanRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getVlanRelevance()).getCodeValue());
					eightIpRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getEightIpRelevance()).getCodeValue());
					sourMacRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getSourceMacRelevance()).getCodeValue());
					endMacRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getDestinationMacRelevance()).getCodeValue());
					sourIPRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getSourceIpRelevance()).getCodeValue());
					endIPRelevance = Integer.parseInt(UiUtil.getCodeById(port.getPortAttr().getPortUniAttr().getDestinationIpRelevance()).getCodeValue());
				}
			}else if(acPortInfo.getLagId() > 0){
				lagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
				PortLagInfo lag = new PortLagInfo();
				lag.setId(acPortInfo.getLagId());
				List<PortLagInfo> lagList = lagService.selectPortByCondition(lag);
				if(lagList != null && !lagList.isEmpty()){
					lag = lagList.get(0);
					vlanRelevance = lag.getVlanRelating();
				    eightIpRelevance = lag.getRelatingSet();
				    sourMacRelevance = lag.getFountainMAC();
				    endMacRelevance = lag.getAimMAC();
				    sourIPRelevance = lag.getFountainIP();
				    endIPRelevance = lag.getAimIP();
				}
			}
			acRelevanceArr[0] = vlanRelevance;
			acRelevanceArr[1] = eightIpRelevance;
			acRelevanceArr[2] = sourMacRelevance;
			acRelevanceArr[3] = endMacRelevance;
			acRelevanceArr[4] = sourIPRelevance;
			acRelevanceArr[5] = endIPRelevance;
			
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			if (siteService.getManufacturer(siteid) == EManufacturer.CHENXIAO.getValue()) {
				acPortInfoList = acInfoService.selectByCondition_synchro(acPortInfo);
			} else {
				acPortInfoList = acInfoService.select_vlan(acPortInfo, acRelevanceArr);
			}
			/*end**************************************************************/
			if (null == acPortInfoList) {
				throw new Exception("同步ac时 查询ac出错");
			}

			switch (acPortInfoList.size()) {
			case 0:
				// 插入ac
				acId = acInfoService.saveOrUpdate(acPortInfo.getBufferList(), acPortInfo);
				break;
			case 1:
				// 修改ac
				acId = updateAcPortInfo(acPortInfoList, acPortInfo, acInfoService);
				break;

			default:
				throw new Exception("同步ac时 查询ac出错");
			}
        
		} catch (Exception e) {
			//将插入的AC在数据库删除
			if(acPortInfoList.size() == 0 && acId >0){
				acInfoService.delete(acId);
			}else{
				//恢复原来的数据
				if(acPortInfoList != null && acPortInfoList.size() >0){
					acInfoService.update(acPortInfoList.get(0));
				}
			}
			throw e;
		} finally {
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(lagService);
		}
		return acId;
	}

	/**
	 * 修改ac数据库
	 * 
	 * @author wangwf
	 * 
	 * @param acPortInfoList
	 *            数据库查询出来的ac对象
	 * @param acPortInfo
	 *            设备上的acPortInfo对象
	 * @param acInfoService
	 *            acInfoService
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 * 
	 * @return result 返回的是ac的数据库主键ID
	 */
	private int  updateAcPortInfo(List<AcPortInfo> acPortInfoList, AcPortInfo acPortInfo, AcPortInfoService_MB acInfoService) throws Exception {

		int result = 0;
		if (null == acPortInfoList || acPortInfoList.size() != 1) {
			throw new Exception("acPortInfoList is null");
		}
		if (null == acPortInfo) {
			throw new Exception("acPortInfo is null");
		}
		if (null == acInfoService) {
			throw new Exception("acInfoService is null");
		}
		OamInfoService_MB oamInfoService = null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			AcPortInfo acPortInfoNew = acPortInfoList.get(0);
			acPortInfoNew.setAcBusinessId(acPortInfo.getAcBusinessId());
			acPortInfoNew.setVlanId(acPortInfo.getVlanId());
			acPortInfoNew.setPortModel(acPortInfo.getPortModel());
			acPortInfoNew.setSimpleQos(acPortInfo.getSimpleQos());
			acPortInfoNew.setBufType(acPortInfo.getBufType());
			acPortInfoNew.setJobStatus(acPortInfo.getJobStatus());
			acPortInfoNew.setManagerEnable(acPortInfo.getManagerEnable());
			if (acPortInfo.getExitRule() > 0) {
				acPortInfoNew.setExitRule(acPortInfo.getExitRule());
			}
			acPortInfoNew.setBufferList(acPortInfo.getBufferList());
			acPortInfoNew.setVlancri(acPortInfo.getVlancri());
			acPortInfoNew.setVlanpri(acPortInfo.getVlanpri());
			acPortInfoNew.setHorizontalDivision(acPortInfo.getHorizontalDivision());
			acPortInfoNew.setMacAddressLearn(acPortInfo.getMacAddressLearn());
			acPortInfoNew.setTagAction(acPortInfo.getTagAction());
//			acPortInfoNew.setIsUser(acPortInfo.getIsUser());
			acPortInfo.setId(acPortInfoNew.getId());
			acPortInfoNew.setAcStatus(acPortInfo.getAcStatus());
			acPortInfoNew.setLanId(acPortInfo.getLanId());
			acInfoService.update(acPortInfoNew);

			result = acPortInfoNew.getId();
			// if (acPortInfo.getOamList() != null && acPortInfo.getOamList().size() > 0) {
			// List<OamInfo> oamList = acPortInfo.getOamList();
			// for (OamInfo oamInfo : oamList) {
			// if (oamInfo.getOamType() == OamTypeEnum.AMEP || oamInfo.getOamType() == OamTypeEnum.ZMEP) {
			// oamInfo.getOamMep().setObjId(acPortInfo.getAcBusinessId());
			// } else if (oamInfo.getOamType() == OamTypeEnum.MEP) {
			//
			// } else if (oamInfo.getOamType() == OamTypeEnum.MIP) {
			//
			// }
			// oamInfoService.saveOrUpdate(oamInfo);
			// }
			// }
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}
		return result;

	}

	/**
	 * 修改数据库端口对象
	 * 
	 * @author kk
	 * 
	 * @param portInst
	 *            端口对象
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	public void updatePort(PortInst portInst) throws Exception {

		PortService_MB portService = null;
		PortInst portInst_db = null;
		List<PortInst> portInstList = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);

			portInst_db = new PortInst();
			portInst_db.setSiteId(portInst.getSiteId());
			// 根据设备厂商不同，查询端口的条件不同
			if (siteService.getManufacturer(portInst.getSiteId()) == EManufacturer.WUHAN.getValue()) {
				portInst_db.setNumber(portInst.getNumber());
			} else {
				portInst_db.setPortName(portInst.getPortName());
			}

			portInstList = portService.select(portInst_db);

			if (null == portInstList || portInstList.size() != 1) {
				ExceptionManage.logDebug("同步ETHPORT时没有找到" + portInst_db.getPortName() + "端口",SynchroUtil.class);
				return;
			}
			portInst_db = portInstList.get(0);
			portInst_db.setPortType(portInst.getPortType());
			portInst_db.setIsEnabled_code(portInst.getIsEnabled_code());
			portInst_db.setIsEnabled_QinQ(portInst.getIsEnabled_QinQ());
			portInst_db.setIsEnabledLaser(portInst.getIsEnabledLaser());
			if(portInst_db.getQosQueues() != null && portInst_db.getQosQueues().size()>0){
				for (int i = 0; i < portInst_db.getQosQueues().size(); i++) {
					int id = portInst_db.getQosQueues().get(i).getId();
					CoderUtils.copy(portInst.getQosQueues().get(i),portInst_db.getQosQueues().get(i));
					portInst_db.getQosQueues().get(i).setId(id);
				}
			}
			// 从驱动对象取值，进行修改
			convertPort(portInst_db, portInst);
			
			portService.update_synchro(portInst_db);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(siteService);
		}

	}

	/**
	 * 驱动数据与数据库数据同步
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void convertPort(PortInst portInst_db, PortInst portInst_drive) {

		if (isNull(portInst_drive.getJobStatus())) { // 工作状态
			portInst_db.setJobStatus(portInst_drive.getJobStatus());
		}
		if (null != portInst_drive.getOamInfo()) { // oam
			portInst_db.setOamInfo(portInst_drive.getOamInfo());
		}
		if (isNull(portInst_drive.getMacAddress())) { // mac地址
			portInst_db.setMacAddress(portInst_drive.getMacAddress());
		}
		if (null != portInst_drive.getPortAttr()) {
			if (portInst_drive.getPortAttr().getPortSpeed() > 0) { // 端口速率
				portInst_db.getPortAttr().setPortSpeed(portInst_drive.getPortAttr().getPortSpeed());
			}
			if (isNull(portInst_drive.getPortAttr().getActualSpeed())) { // 实际速率
				portInst_db.getPortAttr().setActualSpeed(portInst_drive.getPortAttr().getActualSpeed());
			}
			if (portInst_drive.getPortAttr().getWorkModel() > 0) { // 工作模式，对应code表主键
				portInst_db.getPortAttr().setWorkModel(portInst_drive.getPortAttr().getWorkModel());
			}
			if (isNull(portInst_drive.getPortAttr().getMaxFrameSize())) { // 最大帧长=武汉MTU
				portInst_db.getPortAttr().setMaxFrameSize(portInst_drive.getPortAttr().getMaxFrameSize());
			}
			if (portInst_drive.getPortAttr().getFluidControl() > 0) { // 流控=武汉802.3流控
				portInst_db.getPortAttr().setFluidControl(portInst_drive.getPortAttr().getFluidControl());
			}
			if (isNull(portInst_drive.getPortAttr().getExitLimit())) { // 出口限速
				portInst_db.getPortAttr().setMaxFrameSize(portInst_drive.getPortAttr().getMaxFrameSize());
			}
			if (isNull(portInst_drive.getPortAttr().getEntranceLimit())) { // 入口限速
				portInst_db.getPortAttr().setEntranceLimit(portInst_drive.getPortAttr().getEntranceLimit());
			}
			if (portInst_drive.getPortAttr().getSlowAgreement() == 0 || portInst_drive.getPortAttr().getSlowAgreement() == 1) { // 启动慢协议
				portInst_db.getPortAttr().setSlowAgreement(portInst_drive.getPortAttr().getSlowAgreement());
			}
			if (portInst_drive.getPortAttr().getTenWan() == 0 || portInst_drive.getPortAttr().getTenWan() == 1) { // 启动10G WAN
				portInst_db.getPortAttr().setTenWan(portInst_drive.getPortAttr().getTenWan());
			}
			if (portInst_drive.getPortAttr().getMessageLoopback() == 0 || portInst_drive.getPortAttr().getMessageLoopback() == 1) { // 是否启动报文环回
				portInst_db.getPortAttr().setMessageLoopback(portInst_drive.getPortAttr().getMessageLoopback());
			}
			if (portInst_drive.getPortAttr().getSfpExpectType() > 0) { // SFP期望类型
				portInst_db.getPortAttr().setSfpExpectType(portInst_drive.getPortAttr().getSfpExpectType());
			}
			if (isNull(portInst_drive.getPortAttr().getSfpActual())) { // SFP实际类型
				portInst_db.getPortAttr().setSfpActual(portInst_drive.getPortAttr().getSfpActual());
			}
			if (isNull(portInst_drive.getPortAttr().getWorkWavelength())) { // 工作波长
				portInst_db.getPortAttr().setWorkWavelength(portInst_drive.getPortAttr().getWorkWavelength());
			}
			if (isNull(portInst_drive.getPortAttr().getSfpVender())) { // sfp厂家信息
				portInst_db.getPortAttr().setSfpVender(portInst_drive.getPortAttr().getSfpVender());
			}

			// uni信息
			if (null != portInst_drive.getPortAttr().getPortUniAttr()) {
				if (portInst_drive.getPortAttr().getPortUniAttr().getEthernetPackaging() > 0) { // 以太网封装 对应code表主键
					portInst_db.getPortAttr().getPortUniAttr().setEthernetPackaging(portInst_drive.getPortAttr().getPortUniAttr().getEthernetPackaging());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getVlanTpId() > 0) { // 运营商vlantpid 对应code表主键
					portInst_db.getPortAttr().getPortUniAttr().setVlanTpId(portInst_drive.getPortAttr().getPortUniAttr().getVlanTpId());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getOuterVlanTpId() > 0) { // outer vlan tp id
					portInst_db.getPortAttr().getPortUniAttr().setOuterVlanTpId(portInst_drive.getPortAttr().getPortUniAttr().getOuterVlanTpId());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getVlanMode() > 0) { // 以太网vlan模式
					portInst_db.getPortAttr().getPortUniAttr().setVlanMode(portInst_drive.getPortAttr().getPortUniAttr().getVlanMode());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getIsBroadcast() > 0) { // 广播包抑制开关
					portInst_db.getPortAttr().getPortUniAttr().setIsBroadcast(portInst_drive.getPortAttr().getPortUniAttr().getIsBroadcast());
				}
				if (isNull(portInst_drive.getPortAttr().getPortUniAttr().getBroadcast())) { // 广播报文抑制=武汉 广播包抑制
					portInst_db.getPortAttr().getPortUniAttr().setBroadcast(portInst_drive.getPortAttr().getPortUniAttr().getBroadcast());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getIsUnicast() > 0) { // 洪泛包抑制开关
					portInst_db.getPortAttr().getPortUniAttr().setIsUnicast(portInst_drive.getPortAttr().getPortUniAttr().getIsUnicast());
				}
				if (isNull(portInst_drive.getPortAttr().getPortUniAttr().getUnicast())) { // 单播报文抑制=洪泛包抑制
					portInst_db.getPortAttr().getPortUniAttr().setUnicast(portInst_drive.getPortAttr().getPortUniAttr().getUnicast());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getIsMulticast() > 0) { // 组播包抑制开关
					portInst_db.getPortAttr().getPortUniAttr().setIsMulticast(portInst_drive.getPortAttr().getPortUniAttr().getIsMulticast());
				}
				if (isNull(portInst_drive.getPortAttr().getPortUniAttr().getMulticast())) { // 组播报文抑制=组播包抑制
					portInst_db.getPortAttr().getPortUniAttr().setMulticast(portInst_drive.getPortAttr().getPortUniAttr().getMulticast());
				}
				if (isNull(portInst_drive.getPortAttr().getPortUniAttr().getVlanId())) { // 缺省vlanid
					portInst_db.getPortAttr().getPortUniAttr().setVlanId(portInst_drive.getPortAttr().getPortUniAttr().getVlanId());
				}
				if (isNull(portInst_drive.getPortAttr().getPortUniAttr().getVlanPri())) { // 缺省vlan优先级
					portInst_db.getPortAttr().getPortUniAttr().setVlanPri(portInst_drive.getPortAttr().getPortUniAttr().getVlanPri());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getVlanRelevance() > 0) { // VLAN关联设置
					portInst_db.getPortAttr().getPortUniAttr().setVlanRelevance(portInst_drive.getPortAttr().getPortUniAttr().getVlanRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getEightIpRelevance() > 0) { // 802.iP关联设置
					portInst_db.getPortAttr().getPortUniAttr().setEightIpRelevance(portInst_drive.getPortAttr().getPortUniAttr().getEightIpRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getSourceMacRelevance() > 0) { // 源MAC地址关联设置
					portInst_db.getPortAttr().getPortUniAttr().setSourceMacRelevance(portInst_drive.getPortAttr().getPortUniAttr().getSourceMacRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getDestinationMacRelevance() > 0) { // 目的MAC地址关联设置
					portInst_db.getPortAttr().getPortUniAttr().setDestinationMacRelevance(portInst_drive.getPortAttr().getPortUniAttr().getDestinationMacRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getSourceIpRelevance() > 0) { // 源IP地址关联设置
					portInst_db.getPortAttr().getPortUniAttr().setSourceIpRelevance(portInst_drive.getPortAttr().getPortUniAttr().getSourceIpRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getDestinationIpRelevance() > 0) { // 目的IP地址关联设置
					portInst_db.getPortAttr().getPortUniAttr().setDestinationIpRelevance(portInst_drive.getPortAttr().getPortUniAttr().getDestinationIpRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getPwRelevance() > 0) { // pw关联设置
					portInst_db.getPortAttr().getPortUniAttr().setPwRelevance(portInst_drive.getPortAttr().getPortUniAttr().getPwRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getDscpRelevance() > 0) { // dscp关联设置
					portInst_db.getPortAttr().getPortUniAttr().setDscpRelevance(portInst_drive.getPortAttr().getPortUniAttr().getDscpRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getMappingEnable() > 0) { // pri映射
					portInst_db.getPortAttr().getPortUniAttr().setMappingEnable(portInst_drive.getPortAttr().getPortUniAttr().getMappingEnable());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getSourceTcpPortRelevance() > 0) { // 源tcp
					portInst_db.getPortAttr().getPortUniAttr().setSourceTcpPortRelevance(portInst_drive.getPortAttr().getPortUniAttr().getSourceTcpPortRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getEndTcpPortRelevance() > 0) { // 目的tcp
					portInst_db.getPortAttr().getPortUniAttr().setEndTcpPortRelevance(portInst_drive.getPortAttr().getPortUniAttr().getEndTcpPortRelevance());
				}
				if (portInst_drive.getPortAttr().getPortUniAttr().getTosRelevance() > 0) { // tos关联设置
					portInst_db.getPortAttr().getPortUniAttr().setTosRelevance(portInst_drive.getPortAttr().getPortUniAttr().getTosRelevance());
				}
			}
			// nni信息
			if (null != portInst_drive.getPortAttr().getPortNniAttr()) {
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getStaticMac())) { // 静态MAC地址
					portInst_db.getPortAttr().getPortNniAttr().setStaticMac(portInst_drive.getPortAttr().getPortNniAttr().getStaticMac());
				}
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getNeighbourId())) { // 邻居网元ID
					portInst_db.getPortAttr().getPortNniAttr().setNeighbourId(portInst_drive.getPortAttr().getPortNniAttr().getNeighbourId());
				}
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getOperMac())) { // 对端接口mac地址
					portInst_db.getPortAttr().getPortNniAttr().setOperMac(portInst_drive.getPortAttr().getPortNniAttr().getOperMac());
				}
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getOperId())) { // 对端接口ID
					portInst_db.getPortAttr().getPortNniAttr().setOperId(portInst_drive.getPortAttr().getPortNniAttr().getOperId());
				}
				if (portInst_drive.getPortAttr().getPortNniAttr().getNeighbourFind() > 0) { // 邻居发现状态 对应code表主键
					portInst_db.getPortAttr().getPortNniAttr().setNeighbourFind(portInst_drive.getPortAttr().getPortNniAttr().getNeighbourFind());
				}
				if (portInst_drive.getPortAttr().getPortNniAttr().getCcnEnable() == 0 || portInst_drive.getPortAttr().getPortNniAttr().getCcnEnable() == 1) { // ccn承载使能 0=false 1=true
					portInst_db.getPortAttr().getPortNniAttr().setCcnEnable(portInst_drive.getPortAttr().getPortNniAttr().getCcnEnable());
				}
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getNniVlanid())) { // 缺省vlanid
					portInst_db.getPortAttr().getPortNniAttr().setNniVlanid(portInst_drive.getPortAttr().getPortNniAttr().getNniVlanid());
				}
				if (isNull(portInst_drive.getPortAttr().getPortNniAttr().getNniVlanpri())) { // 缺省vlan优先级
					portInst_db.getPortAttr().getPortNniAttr().setNniVlanpri(portInst_drive.getPortAttr().getPortNniAttr().getNniVlanpri());
				}
				//NNI的是否连接段
				portInst_db.getPortAttr().getPortNniAttr().setStat(portInst_drive.getPortAttr().getPortNniAttr().getStat());
			}
		}

	}

	/**
	 * 判断字符串是否为null或者""
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return 如果不是返回true 否则false
	 * 
	 * @Exception 异常对象
	 */
	private boolean isNull(String text) {

		if (null != text && !"".equals(text)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 同步pwNniBuffer信息
	 * 
	 * @param pwNniInfo
	 * @param siteId
	 * @param isVPLS true/false
	 * 			     vpws业务不需要mac地址学习和水平分割两个属性
	 */
	public void pwNniBufferInfoSynchro(PwNniInfo pwNniInfo, int siteId, boolean isVPLS) {
		PwNniInfoService_MB pwNniBufferService = null;
		PwNniInfo info = null;
		List<PwNniInfo> pwNniInfos = null;
		try {
			pwNniBufferService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			info = new PwNniInfo();
			info.setSiteId(siteId);
			info.setPwId(pwNniInfo.getPwId());
			pwNniInfos = pwNniBufferService.select(info);
			if (pwNniInfos != null && pwNniInfos.size() > 0) {
				PwNniInfo pwNniInfo2 = pwNniInfos.get(0);
				pwNniInfo2.setPwId(pwNniInfo.getPwId());
				pwNniInfo2.setTagAction(pwNniInfo.getTagAction());
				pwNniInfo2.setExitRule(pwNniInfo.getExitRule());
				pwNniInfo2.setSvlan(pwNniInfo.getSvlan());
				pwNniInfo2.setVlanpri(pwNniInfo.getVlanpri());
				pwNniInfo2.setLanId(pwNniInfo.getLanId());
				if(isVPLS){
					pwNniInfo2.setMacAddressLearn(pwNniInfo.getMacAddressLearn());
					pwNniInfo2.setHorizontalDivision(pwNniInfo.getHorizontalDivision());
				}
				pwNniInfo2.setControlEnable(pwNniInfo.getControlEnable());
				pwNniBufferService.saveOrUpdate(pwNniInfo2);
			} else {
				pwNniBufferService.saveOrUpdate(pwNniInfo);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,SynchroUtil.class);
		} finally {
			UiUtil.closeService_MB(pwNniBufferService);
			info = null;
			pwNniInfos = null;
		}
	}

}
