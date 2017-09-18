package com.nms.ui.ptn.ne.discardFlow.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.ne.discardFlow.controller.DiscardFlowController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.siteInitialise.SiteInitialiseConfig;

public class DiscardFlowPanel extends SiteInitialiseConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1491420517550067607L;
	private JMenuItem deleteVlan;
	private JMenuItem containValn;

	public DiscardFlowPanel() throws Exception {
		super();
		containValn = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_CONTAIN_VLAN));
		deleteVlan = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_PORT_DELETE_VLAN));
		this.getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.DISCARDFLOWMANAGE)));
		super.controller.refresh();
		addListeners();
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needButtons = new ArrayList<JButton>();
		needButtons.add(getSearchButton());
		needButtons.add(getRefreshButton());
		needButtons.add(getSynchroButton());
		needButtons.add(getUpdateButton());
		return needButtons;
	}

	@Override
	public List<JButton> setAddButtons() {
		return null;
	}

	@Override
	public void setController() {
		super.controller = new DiscardFlowController(this);
	}

	private void addListeners() {
		deleteVlan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setValue(1,EOperationLogType.DELETEPORTVLAN.getValue());
			}
		});

		containValn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setValue(0,EOperationLogType.CONTAINPORTVLAN.getValue());
			}
		});

	}

	private void setValue(int value,int type) {
		try {
			SiteInst siteInst = getSelect();
			DispatchUtil siteDispath = new DispatchUtil(RmiKeys.RMI_SITE);
			siteInst.setIsCreateDiscardFlow(value);
			siteInst.setControlType("portDiscardFlow");
			String result = siteDispath.createOrDeleteDiscardFlow(siteInst);
			DialogBoxUtil.succeedDialog(null, result);
			this.insertOpeLog(type, result, null,null);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){		
	   AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac,ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysPanel.DISCARDFLOWMANAGE),"");			
	}
	
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					SiteService_MB siteService = null;
					try {
						siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
						if (siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()) {
							int count = ttable.getSelectedRows().length;
							if (count == 1) {
								JPopupMenu menu = new JPopupMenu();
								menu.add(deleteVlan);
								menu.add(containValn);
								checkRoot(deleteVlan, RootFactory.CORE_MANAGE);
								checkRoot(containValn, RootFactory.CORE_MANAGE);
								menu.show(evt.getComponent(), evt.getX(), evt.getY());
								return menu;
							}
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					} finally {
						UiUtil.closeService_MB(siteService);
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}
}
