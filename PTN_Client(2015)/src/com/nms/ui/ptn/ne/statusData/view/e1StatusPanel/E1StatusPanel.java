package com.nms.ui.ptn.ne.statusData.view.e1StatusPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.E1LegStatus;
import com.nms.db.bean.ptn.SiteStatusInfo;
import com.nms.db.enums.EOperationLogType;
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
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class E1StatusPanel extends ContentView<E1LegStatus>{
	private static final long serialVersionUID = -716140915615484612L;
	private JButton jButton;
	private E1StatusPanel view;
	public E1StatusPanel() {
		super("e1LegStatus",RootFactory.CORE_MANAGE);
		view = this;
		this.init();
		this.addActionListener();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder("E1仿真状态查询"));
		setLayout();
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		jButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT));
		needRemoveButtons.add(jButton);
		return needRemoveButtons;
	}
	
	public void addActionListener(){
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteService = null;
				SiteInst siteInst = null;
				DispatchUtil siteDispatch = null;
				SiteStatusInfo result = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					siteInst = siteService.select(ConstantUtil.siteId);
					siteInst.setStatusMark(30);
					siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
					result = siteDispatch.siteStatus(siteInst);
					if(null!=result && result.getE1LegStatusList() != null){
						DialogBoxUtil.succeedDialog(null, ResultString.CONFIG_SELECT);
						this.insertOpeLog(EOperationLogType.E1STATUSFIND.getValue(),  ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null,null);	
						view.updateUI();
						view.initData(result.getE1LegStatusList());
					}else{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_FIND_DETAIL_FAIL));
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}
			
			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
				  AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.PANEL_E1_STATUS),"");				
			}
		});
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
