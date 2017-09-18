package com.nms.ui.ptn.ne.statusData.view.portStatus.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.PortStatus;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
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
import com.nms.ui.ptn.ne.statusData.view.portStatus.controller.PortStatusController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class PortStatusPanel extends ContentView<PortStatus>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PtnButton jButton;
	private PortStatusPanel view;
	public PortStatusPanel() {
		super("portStatus",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_PORTSTATUS)));
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
					siteInst.setStatusMark(23);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					
					if(null!=result&&result.getPortStatusList() != null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);
						view.updateUI();
						view.initData(initValue(result.getPortStatusList()));
						this.insertOpeLog(EOperationLogType.PORTSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);					
					}else{
						this.insertOpeLog(EOperationLogType.PORTSTATUS.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);									
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){				
				AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_PORTSTATUS),"");				
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
	public List<PortStatus> initValue(List<PortStatus> portStatusList){
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortStatus> portStatusList2 = new ArrayList<PortStatus>();
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			for(PortStatus portStatus : portStatusList){
				portInst = new PortInst();
				portInst.setSiteId(ConstantUtil.siteId);
				portInst.setNumber(portStatus.getPortNumber());
				if(portService.select(portInst).size()>0){
					portInst = portService.select(portInst).get(0);
					portStatus.setPortName(portInst.getPortName());
					portStatusList2.add(portStatus);
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
		return portStatusList2;
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
		controller = new PortStatusController();
		
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
