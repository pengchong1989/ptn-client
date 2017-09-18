package com.nms.ui.ptn.ne.statusData.pwProtectStatus.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.PwProtectStatus;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.statusData.pwProtectStatus.controller.PwProtectStatusController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class PwProtectStatusPanel extends ContentView<PwProtectStatus>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6085820139836807260L;
	private PtnButton jButton;
	private PwProtectStatusPanel view;
	public PwProtectStatusPanel() {
		super("pwProtectStatus",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}
	
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_PWPROTECT_STATUS)));
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		needAddButtons.add(jButton);
		return needAddButtons;
	}
	
	public void add(){
		jButton.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				SiteStatusInfo result = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(33);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null!=result&&result.getPwProtectStatusList() != null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);						
						initValue(result.getPwProtectStatusList());
						view.updateUI();
						view.initData(result.getPwProtectStatusList());
						this.insertOpeLog(EOperationLogType.PWPROTECTSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);						
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.PWPROTECTSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);				
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}
			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){				
				AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_PWPROTECT_STATUS),"");				
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
	public void initValue(List<PwProtectStatus> pwProtectStatusList){
		TunnelService_MB tunnelService = null;
		Tunnel standTunnel = null;
		Tunnel mainTunnel = null;
		PwInfoService_MB pwInfoService = null;
		PwInfo mainPw = null;
		PwInfo standPw = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PwInfo> pwInfos = null;
		List<PwInfo> pwInfos1 = null;
		DualInfoService_MB dualService = null;
	try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			dualService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			if(pwProtectStatusList != null){
				for(PwProtectStatus pwProtectStatus : pwProtectStatusList){
					//在这里抓异常 可以防止每条pwprotect  状态信息 各不影响
					try {
						//端口	
						if(Integer.parseInt(pwProtectStatus.getMainPort())>0){
							portInst = new PortInst();
							portInst.setSiteId(ConstantUtil.siteId);
							portInst.setNumber(Integer.parseInt(pwProtectStatus.getMainPort()));
							portInst = portService.select(portInst).get(0);
							pwProtectStatus.setMainPort(portInst.getPortName());
						}else{
							pwProtectStatus.setMainPort("");
						}
						if(Integer.parseInt(pwProtectStatus.getStandPort())>0){
							portInst = new PortInst();
							portInst.setSiteId(ConstantUtil.siteId);
							portInst.setNumber(Integer.parseInt(pwProtectStatus.getStandPort()));
							portInst = portService.select(portInst).get(0);
							pwProtectStatus.setStandPort(portInst.getPortName());
						}else{
							pwProtectStatus.setStandPort("");
						}
										
										
						//lsp的名称
						standTunnel = new Tunnel();						
					    mainTunnel = new Tunnel();						
						standTunnel = tunnelService.selectBySiteIdAndServiceId(ConstantUtil.siteId,Integer.parseInt(pwProtectStatus.getStandLspName()));
						mainTunnel = tunnelService.selectBySiteIdAndServiceId(ConstantUtil.siteId,Integer.parseInt(pwProtectStatus.getMainLspName()));	
						if(null!=standTunnel ){							
						    pwProtectStatus.setStandLspName(standTunnel.getTunnelName());	
						}else{
						    pwProtectStatus.setStandLspName("");	
						}
												
						if(null!=mainTunnel){
							pwProtectStatus.setMainLspName(mainTunnel.getTunnelName());	
						}else{
						    pwProtectStatus.setMainLspName("");
						}
										
															
						//pw的名称
						standPw = new PwInfo();	
						mainPw = new PwInfo(); 
						pwInfos = pwInfoService.selectBysiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwProtectStatus.getStandPwName()));
						if(pwInfos.size()!=0){
				//		standPw= pwInfoService.selectBysiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwProtectStatus.getStandPwName())).get(0);
//							pwProtectStatus.setStandPwName(pwProtectStatus.getStandPwName());	
							pwProtectStatus.setStandPwName(pwInfos.get(0).getPwName());	
						}else{
							pwProtectStatus.setStandPwName("");
						}
						
						pwInfos1 = pwInfoService.selectBysiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwProtectStatus.getMainPwName()));				
						if(pwInfos1.size() != 0){
				//			mainPw= pwInfoService.selectBysiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwProtectStatus.getMainPwName())).get(0);	
//							pwProtectStatus.setMainPwName(pwProtectStatus.getMainPwName());
							pwProtectStatus.setMainPwName(pwInfos1.get(0).getPwName());
						}else{
							pwProtectStatus.setMainPwName("");
						}		
						//双规保护名称
						if(pwInfos1.size() != 0){
							pwProtectStatus.setDualName(dualService.queryNameByPwId(pwInfos.get(0).getPwId()));
						}else{
							pwProtectStatus.setDualName("");
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
		    	}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwInfoService);
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(dualService);
		}
	}
	
	private void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		panelLayout.rowHeights = new int[] {400, 10};
		panelLayout.rowWeights = new double[] {0, 0};
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(this.getContentPanel(), c);
		this.add(this.getContentPanel());
	}
	
	@Override
	public void setController() {
		controller = new PwProtectStatusController(this);
	} 
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getRefreshButton());
		return needRemoveButtons;
	}
	
}
