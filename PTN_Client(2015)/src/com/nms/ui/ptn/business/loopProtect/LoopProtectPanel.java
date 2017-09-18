﻿package com.nms.ui.ptn.business.loopProtect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dialog.loopProtect.LoopProRotateDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class LoopProtectPanel extends ContentView<LoopProtectInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoopProtectTopoPanel loopProtectTopoPanel;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
//	private static LoopProtectPanel loopProtectPanel;
	private SegmentTablePanel segmentTablePanel;
	
	/**
	 * 创建一个实例
	 */
	public LoopProtectPanel() {
		super("loopProtect",RootFactory.CORE_MANAGE);
		init();
		//loopProtectPanel = this;
		
	}
	
//	public static LoopProtectPanel getLoopProtectPanel() {
//		return loopProtectPanel;
//	}
	
	/**
	 * 初始化方法
	 */
	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_LOOPPROTECT)));
		initComponent();
		setLayout();
		setActionListention();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		loopProtectTopoPanel = new LoopProtectTopoPanel();
		segmentTablePanel = new SegmentTablePanel();
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
	}
	
	/**
	 * 布局管理
	 */
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
	
	/**
	 * 添加标签
	 */
	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), loopProtectTopoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_CONNECT_CONFIG), segmentTablePanel);
	}
	
	/**
	 * 添加监听事件
	 */
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					loopProtectTopoPanel.clear();
					segmentTablePanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
	}
	
	/**
	 * 设置倒换按钮
	 * @Exception 异常对象
	 */
	private JButton getRotateButton() {
		JButton jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ROTATE),RootFactory.CORE_MANAGE);
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
	
	private void rotateButtonListener() {
		if (super.getAllSelect().size() != 1) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			return;
		}
		new LoopProRotateDialog(true, super.getSelect(), 0);
	}

	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		needAddButtons.add(this.getRotateButton());
		return needAddButtons;
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getSearchButton());
		return needRemoveButtons;
	}

	@Override
	public void setController() {
		controller = new LoopProtectController(this);
	}
	public LoopProtectTopoPanel getLoopProtectTopoPanel() {
		return loopProtectTopoPanel;
	}

	public void setLoopProtectTopoPanel(LoopProtectTopoPanel loopProtectTopoPanel) {
		this.loopProtectTopoPanel = loopProtectTopoPanel;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	public void setSplitPane(JSplitPane splitPane) {
		this.splitPane = splitPane;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

//	public void setLoopProtectPanel(LoopProtectPanel loopProtectPanel) {
//		this.loopProtectPanel = loopProtectPanel;
//	}

	public SegmentTablePanel getSegmentTablePanel() {
		return segmentTablePanel;
	}

	public void setSegmentTablePanel(SegmentTablePanel segmentTablePanel) {
		this.segmentTablePanel = segmentTablePanel;
	}
}
