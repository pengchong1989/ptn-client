/*
 * PwPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.pw.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.pw.PwQosQueuePanel;
import com.nms.ui.ptn.ne.pw.controller.PwNodeController;
import com.nms.ui.ptn.ne.pwnni.PwVlanMainDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 *
 * @author  __USER__
 */
public class PwPanel extends ContentView<PwInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PwQosQueuePanel qosPanel;
//	private static PwPanel pwPanel;
	private PwVlanTablePanel pwVlanTablePanel;
	
	/** Creates new form LspPanl */
	public PwPanel() {
		super("pwNodeTable",RootFactory.CORE_MANAGE);
		init();
		getRefreshButton().doClick();
		//PwPanel.pwPanel=this;
	}
	
	
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_PWINFO)));
		initComponent();
		setLayout();
		this.setActionListention();
	}
	
	/**
	 * 添加监听
	 */
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					qosPanel.clear();
					pwVlanTablePanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
		miUpdateQos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				PwInfo pwinfo = null;
				QosConfigController qoscontroller = null;
				try {
					pwinfo = getSelect();
					qoscontroller = new QosConfigController();
					qoscontroller.setNetwork(false);
					qoscontroller.openQosConfig(qoscontroller, "PW", pwinfo,pwinfo.getType(),PwPanel.this);

				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					 pwinfo = null;
					qoscontroller = null;
				}
			}

		});
	}
	
	private void initComponent() {
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		qosPanel = new PwQosQueuePanel();
		pwVlanTablePanel=new PwVlanTablePanel();
		miUpdateQos = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QOS_UPDATE));
	}
	
	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE), pwVlanTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
	}

	public void setLayout() {
		setTabbedPaneLayout();
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);
	}

	@Override
	public void setController() {
		controller = new PwNodeController(this);
	}
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		JButton jButton = new PtnButton(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE),RootFactory.CORE_MANAGE);
		jButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					if (getAllSelect().size() == 0 || getAllSelect().size() > 1) {
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
						return ;
					}
					PwInfo pwinfo = getAllSelect().get(0);
					if (pwinfo.getIsSingle() == 0) {
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_NODE));
						return;
					}
					if (pwinfo == null) {
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
					} else {
						if(pwinfo.getASiteId() >0 || pwinfo.getZSiteId() >0)
						{
							new PwVlanMainDialog(false,pwinfo);
						}
						else
						{
							DialogBoxUtil.errorDialog(null,ResourceUtil.srcStr(StringKeysTip.TIP_DOULEPW));
							return ;
						}
						
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}				
			}
		});
		
		needRemoveButtons.add(jButton);
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getConsistenceButton());
		return needRemoveButtons;
	}
	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					int count = ttable.getSelectedRows().length;
					if (count == 1) {
						JPopupMenu menu = new JPopupMenu();
						menu.add(miUpdateQos);
						checkRoot(miUpdateQos, RootFactory.CORE_MANAGE);
						menu.show(evt.getComponent(), evt.getX(), evt.getY());
						return menu;
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getExportButton());
		needRemoveButtons.add(getFiterZero());
		return needRemoveButtons;
	}
	
	public PwQosQueuePanel getQosPanel() {
		return qosPanel;
	}
	
	
	public PwVlanTablePanel getPwVlanTablePanel() {
		return pwVlanTablePanel;
	}
	private JMenuItem miUpdateQos;
}