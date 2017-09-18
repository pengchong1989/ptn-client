package com.nms.ui.ptn.ne.statusData.view.pwStatus.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.PwStatus;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
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
import com.nms.ui.ptn.ne.statusData.view.pwStatus.controller.PwStatusController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class PwStatusPanel extends ContentView<PwStatus>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -952149915266408726L;
	private PtnButton jButton;
	private PwStatusPanel view;
	public PwStatusPanel() {
		super("pwStatus",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_PWSTATUS)));
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT),true);
		needRemoveButtons.add(jButton);
		return needRemoveButtons;
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
					siteInst.setStatusMark(22);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null!=result&&result.getPwStatus() != null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);
						initValue(result.getPwStatus());
						view.updateUI();
						view.initData(result.getPwStatus());
						this.insertOpeLog(EOperationLogType.PWSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);								
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.PWSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);				
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){				
			   AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_PWSTATUS),"");				
			}
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
	public void initValue(List<PwStatus> pwStatusList){
		PwInfoService_MB pwInfoService = null;
		TunnelService_MB tunnelService = null;
		PwInfo pwInfo = null;
		Tunnel tunnel = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			for(PwStatus pwStatus : pwStatusList){
				pwInfo = pwInfoService.selectBysiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwStatus.getPwName())).get(0);
				pwStatus.setPwName(pwInfo.getPwName());
				tunnel = tunnelService.selectBySiteIdAndServiceId(ConstantUtil.siteId, Integer.parseInt(pwStatus.getTunnelName()));
				pwStatus.setTunnelName(tunnel.getTunnelName());
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwInfoService);
			pwInfo = null;
			tunnel = null;
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
		controller = new PwStatusController();
		
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
