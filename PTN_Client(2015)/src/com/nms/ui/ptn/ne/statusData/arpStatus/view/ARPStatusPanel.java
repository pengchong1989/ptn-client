package com.nms.ui.ptn.ne.statusData.arpStatus.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.ARPInfo;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.protect.PwProtect;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.ARPInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.protect.PwProtectService_MB;
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
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class ARPStatusPanel extends ContentView<ARPInfo> {
	private static final long serialVersionUID = -9162988438831957324L;
	private PtnButton jButton;
	private ARPStatusPanel view;
	public ARPStatusPanel() {
		super("ARPInfoTable",RootFactory.CORE_MANAGE);
		view = this;
		init();
		add();
	}
	
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ARPSTATUS)));
		setLayout();
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
					siteInst.setStatusMark(68);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null != result&& result.getArpStatusList()!= null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);						
						initValue(result.getArpStatusList());
						view.initData(result.getArpStatusList());
						view.updateUI();
						this.insertOpeLog(EOperationLogType.ARPSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
						this.insertOpeLog(EOperationLogType.ARPSELECT.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL), null,null);			
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
			   AddOperateLog.insertOperLog(jButton, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_ARPSTATUS),"");
			}
			@Override
			public boolean checking() {
				return true;
			}
		});
	}
	
	private void initValue(List<ARPInfo> arpStatusList) {
		PwProtectService_MB service = null;
		DualInfoService_MB dualService = null;
		ARPInfoService_MB arpService = null;
		try {
			service = (PwProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PWPROTECT);
			arpService = (ARPInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ARP);
			dualService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			for (ARPInfo arpStatus : arpStatusList) {
				try {
					PwProtect pwProtect = new PwProtect();
					pwProtect.setSiteId(ConstantUtil.siteId);
					pwProtect.setBusinessId(arpStatus.getPwProtectId());
					List<PwProtect> pwProtectList = service.select(pwProtect);
					if(pwProtectList != null && pwProtectList.size() > 0){
						for (PwProtect pwPro : pwProtectList) {
							DualInfo dual = dualService.queryById(pwPro.getServiceId());
							if(dual != null){
								arpStatus.setDualName(dual.getName());
								break;
							}
						}
					}
					List<ARPInfo> arpList = arpService.queryBySiteId(ConstantUtil.siteId);
					if(arpList != null && arpList.size() > 0){
						for (ARPInfo arpInfo : arpList) {
							if(arpInfo.getPwProtectId() == arpStatus.getPwProtectId()){
								arpStatus.setName(arpInfo.getName());
								break;
							}
						}
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(arpService);
			UiUtil.closeService_MB(dualService);
			UiUtil.closeService_MB(service);
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

	@Override
	public void setController() {
		
	}
}
