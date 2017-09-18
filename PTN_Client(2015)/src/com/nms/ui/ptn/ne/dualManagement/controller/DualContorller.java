package com.nms.ui.ptn.ne.dualManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nms.db.bean.ptn.ARPInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.ARPInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.dialog.dualpath.UpdateDualDialog;
import com.nms.ui.ptn.ne.dualManagement.view.DualPanel;
import com.nms.ui.ptn.ne.dualManagement.view.DualViewDialog;
/**
 * <p>文件名称:DualContorller.java</p>
 * <p>文件描述:单站侧Dual界面控制器</p>
 * <p>版权所有:版权所有(c)2013-2015</p>
 * <p>公司:北京建博信通软件技术有限公司</p>
 * <p>内容摘要:</p>
 * <p>其他说明:</p>
 * <p>完成时间:2015-3-5</p>
 * <p>修改记录:</p>
 * <pre>
 *   修改日期:
 *   版本号:
 *   修改人:
 *   修改内容:
 * </pre>
 * @version 1.0
 * @author zhangkun
 *
 */
public class DualContorller extends AbstractController {


	private DualPanel view;
	private Map<Integer,List<DualInfo>> dualInfoMap;
	
	/************代码段 :构造函数***********************/
	
//	@Override
	public DualContorller(DualPanel dualPanel)
	{
	this.view = dualPanel;	
	try
	{
		refresh();
	} catch (Exception e) 
	{
		ExceptionManage.dispose(e, getClass());
	}
	}
	
	/************刷新************/
	@Override
	public void refresh() throws Exception 
	{
		DualInfoService_MB dualInfoServiceMB = null;
		List<DualInfo> dualInfoList = null;
		try 
		{
			dualInfoServiceMB = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			dualInfoMap = dualInfoServiceMB.select(1,ConstantUtil.siteId);
			dualInfoList = new ArrayList<DualInfo>();
			if(dualInfoMap != null && dualInfoMap.size() >0)
			{
				for (Map.Entry<Integer, List<DualInfo>> entrySet : dualInfoMap.entrySet()) 
				{
					if(entrySet.getValue().size() >1)
					{
						for(DualInfo dualInfo : entrySet.getValue())
						{
							if(dualInfo.getPwProtect() != null)
							{
								dualInfoList.add(dualInfo);
							}
						}
					}else
					{
						dualInfoList.add(entrySet.getValue().get(0));	
					}
				}
			}
			this.view.clear();
			if(this.view.getAcElinePanel() != null)
			{
				this.view.getAcElinePanel().clear();
			}
			if(this.view.getPwElinePanel() != null)
			{
				this.view.getPwElinePanel().clear();
			}
			this.view.initData(dualInfoList);
			this.view.updateUI();
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(dualInfoServiceMB);
			dualInfoList = null;
		}
		
	}
	
	/************新建************/
	@Override
	public void openCreateDialog() throws Exception
	{
		DualViewDialog DualViewDialog = new DualViewDialog(null,this);
	}
	
	/************修改************/
	@Override
	public void openUpdateDialog() throws Exception
	{
		List<DualInfo> dualInfoList = null;
		DualInfo dualInfo= null;
		UpdateDualDialog updateDualDialog= null;
		try 
		{
			dualInfo = this.view.getSelect();
			//只能修改单网元
			if(dualInfo.getIsSingle() == 0){
				DialogBoxUtil.succeedDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_NODE));
				return;
			}
			dualInfoList = new ArrayList<DualInfo>();
			dualInfoList.addAll(dualInfoMap.get(dualInfo.getServiceId()));
			updateDualDialog = new UpdateDualDialog(dualInfoList,this.view);
			UiUtil.showWindow(updateDualDialog, 350, 350);
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e,this.getClass());
		}finally
		{
			dualInfo = null;
			dualInfoList = null;
			updateDualDialog= null;
		}
	}
	
	/************删除************/
	@Override
	public void delete() throws Exception
	{
		List<DualInfo> dualInfoList = null;
		DispatchUtil dualDisPatch = null;
		String result = "";
		try 
		{
			dualInfoList = new ArrayList<DualInfo>();
			
			
			
			for(DualInfo dualInfo:this.view.getAllSelect())
			{
				if(hasARPInfo(dualInfo)){
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_ARP));
					return;
				}
				dualInfoList.addAll(dualInfoMap.get(dualInfo.getServiceId()));
			}
			dualDisPatch = new DispatchUtil(RmiKeys.RMI_DUAL);
			result = dualDisPatch.excuteDelete(dualInfoList);
			//添加日志记录
			for (DualInfo dualInfo : dualInfoList) {
				AddOperateLog.insertOperLog(null, EOperationLogType.DUALPROTECTDELETE.getValue(), result,
						null, null, ConstantUtil.siteId, dualInfo.getName(), null);
			}
			DialogBoxUtil.succeedDialog(this.view, result);	
			this.refresh();
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e,this.getClass());
		}finally
		{
			dualInfoList = null;
			dualDisPatch = null;
			result = null;
		}
		
	}
	
	/**
	 * 存在arp/不存在 true/false
	 * @param dualInfo
	 * @return
	 */
	private boolean hasARPInfo(DualInfo dualInfo){
		ARPInfoService_MB service = null;
		try {
			if(dualInfo.getPwProtect() != null){
				service = (ARPInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ARP);
				List<ARPInfo> arpList = service.queryBySiteId(dualInfo.getPwProtect().getSiteId());
				if(arpList != null && arpList.size() > 0){
					for (ARPInfo arpInfo : arpList) {
						if(arpInfo.getPwProtectId() == dualInfo.getPwProtect().getBusinessId()){
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return false;
	}
	
	@Override
	public boolean deleteChecking() {
		List<DualInfo> dualInfoList = null;
		boolean flag = false;
		List<Integer> siteIds = null;
		try {
			dualInfoList = this.view.getAllSelect();

			for (DualInfo dualInfo : dualInfoList) {
				if (dualInfo.getIsSingle() == 0) {
					flag = true;
					break;
				}
			}
			if (flag) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_NODE));
				return false;
			}else{
				SiteUtil siteUtil = new SiteUtil();
				if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
					WhImplUtil wu = new WhImplUtil();
					siteIds = new ArrayList<Integer>();
					siteIds.add(ConstantUtil.siteId);
					String str=wu.getNeNames(siteIds);
					DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
					return false;  		    		
					}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			siteIds = null;
			dualInfoList =null;
		}
		return flag;
	}
	
	/************初始化AC和PW列表************/
	@Override
	public void initDetailInfo()
	{
		DualInfo dualInfo = this.view.getSelect();
		//初始化PW数据值
		initPwData(dualInfo);
		//初始化AC数据值
		initAcData(dualInfo);
		dualInfo = null;
	}
	
	private void initPwData(DualInfo dualInfo) {
		PwInfoService_MB pwInfoServiceMB = null;
		List<PwInfo> pwInfoList = null;
		List<PwInfo> pwInstList = null;
		List<Integer> pwIds = null;
		try 
		{
			pwInfoServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfoList = new ArrayList<PwInfo>();
			pwIds = new ArrayList<Integer>();
			pwInstList = new ArrayList<PwInfo>();
			if(dualInfo != null && dualInfo.getPwProtect() != null)
			{
				pwIds.add(dualInfo.getPwProtect().getMainPwId());
				pwIds.add(dualInfo.getPwProtect().getStandPwId());
				pwInfoList = pwInfoServiceMB.findPwByPWIds(pwIds);
				if(pwInfoList != null && pwInfoList.size() >0)
				{
					for(PwInfo pwInfo :pwInfoList)
					{
						if(pwInfo.getASiteId() == ConstantUtil.siteId ||pwInfo.getZSiteId() == ConstantUtil.siteId)
						{
							pwInstList.add(pwInfo);
						}
					}
				}
			}else if(dualInfo != null)
			{
				pwIds.add(dualInfo.getPwId());
				pwInfoList = pwInfoServiceMB.findPwByPWIds(pwIds);
				pwInstList.addAll(pwInfoList);
			}
			this.view.getPwElinePanel().initData(pwInstList);
			this.view.updateUI();
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(pwInfoServiceMB);
			pwInfoList = null;
			pwIds = null;
			pwInstList = null;
		}
		
	}

	private void initAcData(DualInfo dualInfo) 
	{
		AcPortInfoService_MB acInfoServiceMB = null;
		AcPortInfo acPortInfo = null;
		List<AcPortInfo> acportInfoList = null;
		try 
		{
			acInfoServiceMB = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acPortInfo = new AcPortInfo();
			if(dualInfo.getRootSite() == ConstantUtil.siteId)
			{
				acPortInfo.setId(dualInfo.getaAcId());
			}else
			{
				acPortInfo.setId(dualInfo.getzAcId());
			}
			acportInfoList = acInfoServiceMB.queryByAcPortInfo(acPortInfo);

			this.view.getAcElinePanel().initData(acportInfoList);
			this.view.updateUI();
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		} finally 
		{
			UiUtil.closeService_MB(acInfoServiceMB);
			acPortInfo = null;
			acportInfoList = null;
		}
		
	}

	/************同步设备数据与网管保持一致************/
	@Override
	public void synchro() throws Exception
	{
		DispatchUtil dualDisPatch = null;
		String result = "";
		try {
			dualDisPatch = new DispatchUtil(RmiKeys.RMI_ELINE);
			result = dualDisPatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(null, result);
			//添加日志记录
			AddOperateLog.insertOperLog(null, EOperationLogType.DUALPROTECTDELETE.getValue(), result,
					null, null, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysTab.TAB_DUALINFO), null);
			this.refresh();
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			
		}
		
	}
	
}
