package com.nms.ui.ptn.ne.CCCManagement.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.nms.db.bean.ptn.CccInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.CccService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.filter.impl.EthCCCFilterDialog;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.CCCManagement.view.CccEditDialog;
import com.nms.ui.ptn.ne.CCCManagement.view.CccPanel;
import com.nms.ui.ptn.ne.camporeData.CamporeDataDialog;


public class CccController extends AbstractController {

	private CccPanel cccPanel;	
	private CccInfo cccInfo=null;

	public CccController(CccPanel cccPanel) {
		this.setCccPanel(cccPanel);
	}

	@Override
	public void refresh() throws Exception {
		CccService_MB cccService = null;
		List<CccInfo> infos = null;
		try {
			if(null == this.cccInfo){
				this.cccInfo=new CccInfo();
			}
			infos = new ArrayList<CccInfo>();
			cccService = (CccService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CCCMANAGEMENT);
			this.cccInfo.setaSiteId(ConstantUtil.siteId);
			infos = cccService.filterSelect(this.cccInfo);
			
			this.getCccPanel().clear();		
			this.getCccPanel().getAcElinePanel().clear();
			this.getCccPanel().initData(infos);
			this.getCccPanel().updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			UiUtil.closeService_MB(cccService);
		}
	}
	
	

	/**
	 * 选中一条记录后，查看详细信息
	 * 
	 * @throws Exception
	 */
	@Override
	public void initDetailInfo() {
		CccInfo cccInfo = null;
		try {
			cccInfo = this.getCccPanel().getSelect();			
			this.initAcData(cccInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}



	/**
	 * 初始化ac数据
	 * 
	 * @param etreeInfo
	 * @throws Exception
	 */
	private void initAcData(CccInfo cccInfo) throws Exception {

		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acportInfoList = null;
		Set<Integer> acIdSet = null;
		UiUtil uiUtil = null;
		List<Integer> acIds = null;
		try {
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acIdSet = new HashSet<Integer>();
			uiUtil = new UiUtil();
			acIds = new ArrayList<Integer>();
			acportInfoList = new ArrayList<AcPortInfo>();
			if (cccInfo.getaSiteId() == ConstantUtil.siteId) {
				acIdSet.addAll(uiUtil.getAcIdSets(cccInfo.getAmostAcId()));
			}
			if(acIdSet.size() >0)
			{
				acIds.addAll(acIdSet);
				acportInfoList = acInfoService.select(acIds);
			}
			this.getCccPanel().getAcElinePanel().initData(acportInfoList);
			this.getCccPanel().updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(acInfoService);
			acportInfoList = null;
			acIdSet = null;
			uiUtil = null;
			acIds = null;
		}

	}

	@Override
	public void delete() throws Exception {
		List<CccInfo> etreeInfoList = null;
		DispatchUtil cccDispatch = null;
		String resultStr = null;
		try {
			etreeInfoList = this.getCccPanel().getAllSelect();
			cccDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			resultStr = cccDispatch.excuteDelete(etreeInfoList);
			DialogBoxUtil.succeedDialog(this.getCccPanel(), resultStr);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			resultStr = null;
			etreeInfoList = null;
			cccDispatch = null;
		}

	}

	@Override
	public boolean deleteChecking() {	
		boolean flag = true;
		List<Integer> siteIds = null;
		try {					
			SiteUtil siteUtil = new SiteUtil();
			if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
				WhImplUtil wu = new WhImplUtil();
				siteIds = new ArrayList<Integer>();
				siteIds.add(ConstantUtil.siteId);
				String str=wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.getCccPanel(), ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				return false;  		    		
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			siteIds = null;
			
		}
		return flag;
	}

	@Override
	public void synchro() {
		DispatchUtil cccDispatch = null;
		try {
			cccDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			String result = cccDispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			// 添加日志记录
			AddOperateLog.insertOperLog(null, EOperationLogType.SYNCCCC.getValue(), result, 
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO), null);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			cccDispatch = null;
		}
	}

	/**
	 * 激活处理事件
	 */
	public void doActive() {
		List<CccInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		try {
			dispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			infos = this.cccPanel.getAllSelect();
			if (infos != null && infos.size() > 0) {				
				for (CccInfo info : infos) {
					if(info.getActiveStatus()==EActiveStatus.ACTIVITY.getValue()){
						DialogBoxUtil.succeedDialog(this.cccPanel, ResourceUtil.srcStr(StringKeysObj.ACTIVITY_YES));
					}else{
					   info.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
					   result = dispatch.excuteUpdate(info);
					   //添加日志记录*************************/
					   AddOperateLog.insertOperLog(null, EOperationLogType.ACTIVECCC.getValue(), result, null, null, ConstantUtil.siteId, info.getName(), null);
					   //************************************/
					   DialogBoxUtil.succeedDialog(this.cccPanel, result);
					   this.refresh();
					}
					}			
			}								
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
		}
	}

	/**
	 * 去激活处理事件
	 */
	public void doUnActive() {
		List<CccInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		try {
			dispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			infos = this.cccPanel.getAllSelect();
			if (infos != null && infos.size() > 0) {				
				for (CccInfo info : infos) {
					if(info.getActiveStatus()==EActiveStatus.UNACTIVITY.getValue()){
						DialogBoxUtil.succeedDialog(this.cccPanel, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_UNACTIVATE));
					}else{
					   info.setActiveStatus(EActiveStatus.UNACTIVITY.getValue());
					   result = dispatch.excuteUpdate(info);
					 //添加日志记录*************************/
					   AddOperateLog.insertOperLog(null, EOperationLogType.UNACTIVECCC.getValue(), result, null, null, ConstantUtil.siteId, info.getName(), null);
					   //************************************/
					   DialogBoxUtil.succeedDialog(this.cccPanel, result);	
					   this.refresh();
					}
					}			
			}	
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
		}
	}

	@Override
	public void openCreateDialog() throws Exception {
		new CccEditDialog(null, this.getCccPanel());
	}

	// 修改
	@Override
	public void openUpdateDialog() throws Exception {		
		new CccEditDialog(this.cccPanel.getSelect(), this.getCccPanel());
	}
	
	@Override
	public void openFilterDialog() throws Exception {
		if(null==this.cccInfo){
			this.cccInfo=new CccInfo();
		}
		new EthCCCFilterDialog(this.cccInfo);
		this.refresh();
	}
	
	// 清除过滤
	public void clearFilter() throws Exception {
		this.cccInfo=null;
		this.refresh();
	}
	
	public CccPanel getCccPanel() {
		return cccPanel;
	}

	public void setCccPanel(CccPanel cccPanel) {
		this.cccPanel = cccPanel;
	}
	
	@SuppressWarnings("unchecked")
	public void consistence(){
		CccService_MB cccService = null;
		try {
			SiteUtil siteUtil=new SiteUtil();
			if (0 == siteUtil.SiteTypeUtil(ConstantUtil.siteId)) {
				DispatchUtil cccDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
				List<CccInfo> neList = new ArrayList<CccInfo>();
				try {
					neList = (List<CccInfo>) cccDispatch.consistence(ConstantUtil.siteId);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				cccService = (CccService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CCCMANAGEMENT);
				List<CccInfo> emsList = new ArrayList<CccInfo>();
				try {
					emsList = cccService.selectCccBySite(ConstantUtil.siteId);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}		
				if(emsList.size() > 0 && neList.size() > 0){
					CamporeDataDialog dialog = new CamporeDataDialog(ResourceUtil.srcStr(StringKeysTip.TIP_CCC),
							emsList, neList, this);
					UiUtil.showWindow(dialog, 700, 600);
				}else{
					DialogBoxUtil.errorDialog(this.cccPanel, ResourceUtil.srcStr(StringKeysTip.TIP_DATAISNULL));
				}
			}else{
				DialogBoxUtil.errorDialog(this.cccPanel, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(cccService);
		}
	}

	
	
}
