package com.nms.ui.ptn.business.testoam;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.testoam.dialog.TestOamMainDialog;

public class TestOamBusinessController extends AbstractController {
	private TestOamMainDialog view;

	public TestOamBusinessController(TestOamMainDialog dialog) {
		this.view = dialog;
	}

	public TestOamBusinessController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void refresh() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 新建或修改按需oam
	 * @param type 类型:段层/tunnel层/pw层
	 * @param s segment/tunnel/pw
	 */
	public void openTestOamConfig(String type, Object s) {
		List<OamInfo> oamList = null;
		TestOamMainDialog mainDialog = null;
		boolean flag = false;
		if(type != null && type.equals(EServiceType.SECTION.toString())){
			//段层按需OAM,只支持10条按需OAM
			if(s != null){
				flag = this.checkTestOam(s, "SECTION_TEST");
				if(!flag){
					Segment segment = (Segment) s;
					oamList = segment.getOamList();
					 //先要判断该段上有主动OAM,才能配置按需OAM
					if(oamList != null && oamList.size() > 1){
						mainDialog = new TestOamMainDialog(oamList, s, type);
						if(ResourceUtil.language.equals("zh_CN")){
							UiUtil.showWindow(mainDialog, 500, 480);
						}else{
							UiUtil.showWindow(mainDialog, 780, 480);
						}
					}
				}else{
					//提示用户超出限制
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_OAM_LIMIT_10));
				}
			}
		}
		if(type != null && type.equals(EServiceType.TUNNEL.toString())){
			//Tunnel层按需OAM
			if(s != null){
				flag = this.checkTestOam(s, "TUNNEL_TEST");
				if(!flag){
					Tunnel tunnel = (Tunnel) s;
					oamList = tunnel.getOamList();
					//先要判断该tunnel上有主动OAM,才能配置按需OAM
					if(oamList != null && oamList.size() > 1){
						mainDialog = new TestOamMainDialog(oamList, s, type);
						if(ResourceUtil.language.equals("zh_CN")){
							UiUtil.showWindow(mainDialog, 730, 500);
						}else{
							UiUtil.showWindow(mainDialog, 920, 500);
						}
					}
				}else{
					//提示用户超出限制
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_OAM_LIMIT_10));
				}
			}
		}
		if(type != null && type.equals(EServiceType.PW.toString())){
			//Pw层按需OAM
			if(s != null){
				flag = this.checkTestOam(s, "PW_TEST");
				if(!flag){
					PwInfo pw = (PwInfo) s;
					oamList = pw.getOamList();
					//先要判断该pw上有主动OAM,才能配置按需OAM
					if(oamList != null && oamList.size() > 1){
						mainDialog = new TestOamMainDialog(oamList, s, type);
						if(ResourceUtil.language.equals("zh_CN")){
							UiUtil.showWindow(mainDialog, 730, 500);
						}else{
							UiUtil.showWindow(mainDialog, 800, 500);
						}
					}
				}else{
					//提示用户超出限制
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_OAM_LIMIT_10));
				}
			}
		}
		
	}

	/**
	 * 判断按需OAM是否超过十条
	 * @param s
	 * @param type 
	 * @return true 可新建 false 不可新建
	 */
	private boolean checkTestOam(Object obj, String type) {
		OamInfoService_MB oamService = null;
		List<OamInfo> oamList = null;
		int aSiteId = 0;
		int zSiteId = 0;
		int aPortId = 0;
		int zPortId = 0;
		OamInfo oam = null;
		OamMepInfo mep = null;
		boolean aFlag = true;
		boolean zFlag = true;
		Segment s = null;
		Tunnel t = null;
		PwInfo pw = null;
		try {
			oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			if(type.equals("SECTION_TEST")){
				s = (Segment) obj;
				aSiteId = s.getASITEID();
				zSiteId = s.getZSITEID();
				aPortId = s.getAPORTID();
				zPortId = s.getZPORTID();
			}
			if(type.equals("TUNNEL_TEST")){
				t = (Tunnel) obj;
				aSiteId = t.getASiteId();
				zSiteId = t.getZSiteId();
				aPortId = t.getTunnelId();
				zPortId = t.getTunnelId();
			}
			if(type.equals("PW_TEST")){
				pw = (PwInfo) obj;
				aSiteId = pw.getASiteId();
				zSiteId = pw.getZSiteId();
				aPortId = pw.getPwId();
				zPortId = pw.getPwId();
			}
			oam = new OamInfo();
			mep = new OamMepInfo();
			mep.setSiteId(aSiteId);
			mep.setObjType(type);
			oam.setOamMep(mep);
			oamList = oamService.queryBySiteIdAndType(oam);
			//判断A端网元
			if(oamList.size() > 9){
				for (OamInfo oamInfo : oamList) {
					if(oamInfo.getOamMep() != null && oamInfo.getOamMep().getObjId() == aPortId){
						aFlag = false;
					}
				}
			}else{
				aFlag = false;
			}
			
			//判断Z端网元
			mep.setSiteId(zSiteId);
			oamList = oamService.queryByServiceForNode(oam);
			if(oamList.size() > 9){
				for (OamInfo oamInfo : oamList) {
					if(oamInfo.getOamMep() != null && oamInfo.getOamMep().getObjId() == zPortId){
						zFlag = false;
					}
				}
			}else{
				zFlag = false;
			}
			
			if(!aFlag && !zFlag){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamService);
		}
		return true;
		
	}

	/**
	 * 删除按需oam
	 * @param type 类型:段层/tunnel层/pw层
	 * @param s segment/tunnel/pw
	 */
	public String deleteTestOamConfig(String type, Object s) {
		List<OamInfo> oamList = null;
		DispatchUtil tmsDispatch = null;
		DispatchUtil tmpDispatch = null;
		DispatchUtil tmcDispatch = null;
		boolean onlineFlag = false;
		List<Integer> allSiteIds =null;
		List<Integer> siteIds =null;
		//判断是否有在线网元托管，存在不允许删除
		SiteUtil siteUtil = new SiteUtil();
		allSiteIds = new ArrayList<Integer>();
		siteIds = new ArrayList<Integer>();	
		String message = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_TEST_OAM);
		if(type != null && type.equals(EServiceType.SECTION.toString())){
			//段层按需OAM
			if(s != null){
				Segment segment = (Segment) s;
				oamList = this.getTestOam(segment.getASITEID(), segment.getAPORTID(),
						segment.getZSITEID(), segment.getZPORTID(), "SECTION_TEST");
				if(oamList.size() > 0){
					try {
						//判断有没有在线脱管网元
						for (OamInfo oam : oamList) {
						OamMepInfo mep = oam.getOamMep();
						if(mep != null){
							allSiteIds.add(mep.getSiteId());
						}
					}
						for(int i=0;i<allSiteIds.size();i++){
					    if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
					    	siteIds.add(allSiteIds.get(i));				    
					    }
					}
					if(siteIds !=null && siteIds.size()!=0){
					   onlineFlag = true;
					}		
					if(onlineFlag){
						WhImplUtil wu = new WhImplUtil();
						String str=wu.getNeNames(siteIds);						
						message = ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE);
					}else{
						tmsDispatch = new DispatchUtil(RmiKeys.RMI_TMSOAMCONFIG);
						message = tmsDispatch.excuteDelete(oamList);
					}
					} catch (RemoteException e) {
						ExceptionManage.dispose(e, this.getClass());
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		}
		if(type != null && type.equals(EServiceType.TUNNEL.toString())){
			//Tunnel层按需OAM
			if(s != null){
				Tunnel tunnel = (Tunnel) s;
				oamList = this.getTestOam(tunnel.getASiteId(), tunnel.getTunnelId(),
						tunnel.getZSiteId(), tunnel.getTunnelId(), "TUNNEL_TEST");
				if(oamList.size() > 0){
					try {
						//判断有没有在线脱管网元
						for (OamInfo oam : oamList) {
						OamMepInfo mep = oam.getOamMep();
						if(mep != null){
							allSiteIds.add(mep.getSiteId());
						}
					}
						for(int i=0;i<allSiteIds.size();i++){
					    if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
					    	siteIds.add(allSiteIds.get(i));				    
					    }
					}
					if(siteIds !=null && siteIds.size()!=0){
					   onlineFlag = true;
					}		
					if(onlineFlag){
						WhImplUtil wu = new WhImplUtil();
						String str=wu.getNeNames(siteIds);						
						message = ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE);
					}else{
						tmpDispatch = new DispatchUtil(RmiKeys.RMI_TMPOAMCONFIG);
						message = tmpDispatch.excuteDelete(oamList);
					}
					} catch (RemoteException e) {
						ExceptionManage.dispose(e, this.getClass());
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		}
		if(type != null && type.equals(EServiceType.PW.toString())){
			//Pw层按需OAM
			if(s != null){
				PwInfo pw = (PwInfo) s;
				oamList = this.getTestOam(pw.getASiteId(), pw.getPwId(),
						pw.getZSiteId(), pw.getPwId(), "PW_TEST");
				if(oamList.size() > 0){
					try {
						//判断有没有在线脱管网元
						for (OamInfo oam : oamList) {
						OamMepInfo mep = oam.getOamMep();
						if(mep != null){
							allSiteIds.add(mep.getSiteId());
						}
					}
						for(int i=0;i<allSiteIds.size();i++){
					    if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
					    	siteIds.add(allSiteIds.get(i));				    
					    }
					}
					if(siteIds !=null && siteIds.size()!=0){
					   onlineFlag = true;
					}		
					if(onlineFlag){
						WhImplUtil wu = new WhImplUtil();
						String str=wu.getNeNames(siteIds);						
						message = ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE);
					}else{
						tmcDispatch = new DispatchUtil(RmiKeys.RMI_TMCOAMCONFIG);
						message = tmcDispatch.excuteDelete(oamList);
					}
					} catch (RemoteException e) {
						ExceptionManage.dispose(e, this.getClass());
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		}
		return message;
	}

	private List<OamInfo> getTestOam(int aSiteId, int aObjectId, int zSiteId, int zObjectId, String type) {
		OamInfoService_MB oamInfoService = null;
		OamInfo oamInfoA = null;
		OamMepInfo oamMepInfoA = null;
		OamInfo oamInfoZ = null;
		OamMepInfo oamMepInfoZ = null;
		List<OamInfo> oamList = new ArrayList<OamInfo>();
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamInfoA = new OamInfo();
			oamMepInfoA = new OamMepInfo();
			oamMepInfoA.setSiteId(aSiteId);
			oamMepInfoA.setObjId(aObjectId);
			oamMepInfoA.setObjType(type);
			oamInfoA.setOamMep(oamMepInfoA);
			oamInfoA = oamInfoService.queryByCondition(oamInfoA, OamTypeEnum.AMEP);
			if(oamInfoA.getOamMep() != null && oamInfoA.getOamMep().getId() > 0){
				oamList.add(oamInfoA);
			}
			
			oamInfoZ = new OamInfo();
			oamMepInfoZ = new OamMepInfo();
			oamMepInfoZ.setSiteId(zSiteId);
			oamMepInfoZ.setObjId(zObjectId);
			oamMepInfoZ.setObjType(type);
			oamInfoZ.setOamMep(oamMepInfoZ);
			oamInfoZ = oamInfoService.queryByCondition(oamInfoZ, OamTypeEnum.AMEP);
			if(oamInfoZ.getOamMep() != null && oamInfoZ.getOamMep().getId() > 0){
				oamList.add(oamInfoZ);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			oamInfoA = null;
			oamMepInfoA = null;
			oamInfoZ = null;
			oamMepInfoZ = null;
		}
		return oamList;
	}

}
