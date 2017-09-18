package com.nms.ui.ptn.business.dual;

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
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnMenuItem;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.dialog.dualpath.DualBusinessRotateDialog;
import com.nms.ui.ptn.business.eline.PortNetworkTablePanel;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.client.view.ClientInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.Schematize;

public class DualBusinessPanel extends ContentView<DualInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1827073066007450379L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	// 激活右键菜单
	private PtnMenuItem activeItem;
	// 去激活右键菜单
	private PtnMenuItem unActiveItem;
	private TopoPanel topoPanel;
	private PortNetworkTablePanel portNetworkTablePanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private ClientInfoPanel clientInfoPanel;
	
	public DualBusinessPanel() {
		super("dualInfo", RootFactory.CORE_MANAGE);
		init();
	}

	@Override
	public void setController() {
		controller = new DualBusinessController(this);
		
	}
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_DUALINFO)));
		initComponent();
		setLayout();
		setActionListention();
	}

	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					topoPanel.clear();
					portNetworkTablePanel.clear();
					schematize_panel.clear();
					clientInfoPanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});

		// 激活处理事件
		activeItem.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((DualBusinessController) getController()).doActive();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});

		// 去激活处理事件
		unActiveItem.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((DualBusinessController) getController()).doUnActive();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getSearchButton());
		return needRemoveButtons;
	}

	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		needAddButtons.add(this.getRotateButton());		
		return needAddButtons;
	}
	
	
	/**
	 * 设置倒换按钮
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private JButton getRotateButton() {
		JButton jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ROTATE),RootFactory.CORE_MANAGE);

		// 新建按钮事件
		jButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					rotateButtonListener();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		return jButton;
	}

	private void rotateButtonListener() throws NumberFormatException, Exception {

		if (this.getAllSelect().size() != 1) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			return;
		}

		DualInfo dualInfo = this.getSelect();		
		new DualBusinessRotateDialog(true, dualInfo);

	}

	
	private void initComponent() {
		portNetworkTablePanel=new PortNetworkTablePanel();
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		activeItem = new PtnMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_ACTIVATION), true);
		unActiveItem = new PtnMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_GO_ACTIVATION), true);
		topoPanel = new TopoPanel();
		schematize_panel=new Schematize();
		clientInfoPanel = new ClientInfoPanel();
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), topoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_AC_INFO), portNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel);
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
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable table, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					int count = table.getSelectedRows().length;
					if (count > 0) {
						JPopupMenu menu = new JPopupMenu();
						menu.add(activeItem);
						menu.add(unActiveItem);
						menu.show(evt.getComponent(), evt.getX(), evt.getY());
						return menu;
					}
				}
				return null;
			}

		};
		super.setMenuFactory(menuFactory);
	}

	public TopoPanel getTopoPanel() {
		return topoPanel;
	}

	public void setTopoPanel(TopoPanel topoPanel) {
		this.topoPanel = topoPanel;
	}
	public PortNetworkTablePanel getPortNetworkTablePanel() {
		return portNetworkTablePanel;
	}
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public ClientInfoPanel getClientInfoPanel() {
		return clientInfoPanel;
	}
}
