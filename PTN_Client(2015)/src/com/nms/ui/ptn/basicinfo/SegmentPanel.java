﻿package com.nms.ui.ptn.basicinfo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.ui.frame.ContentView;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.segment.AddSegment;
import com.nms.ui.ptn.basicinfo.dialog.segment.SegmentQosQueuePanel;
import com.nms.ui.ptn.basicinfo.dialog.segment.controller.SegmentPanelController;
import com.nms.ui.ptn.business.testoam.TestOamBusinessController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 段的 主界面
 * @author lepan
 */
public class SegmentPanel extends ContentView<Segment> {
	private static final long serialVersionUID = -8442026580706629773L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private JPanel qosPanel; // qos详细面板
	private SegmentQosQueuePanel aqosPanel;
	private SegmentQosQueuePanel zqosPanel;
	private JScrollPane oamScrollPane; // oam面板
	private ViewDataTable<OamInfo> oamTable;
	private JMenuItem miUpdateTestOam;//右键修改按需oam
	private JMenuItem miDeleteTestOam;//右键删除按需oam
	//private static SegmentPanel segmentPanel;
	private AddSegment dialog;
	
	
	public AddSegment getDialog() {
		return dialog;
	}

	public void setDialog(AddSegment dialog) {
		this.dialog = dialog;
	}

	public SegmentPanel() {
		super("segmentTable",RootFactory.TOPOLOGY_MANAGE);
		init();
	}

	private void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENT)));
		this.initComponents();
		setLayout();
		setActionListention();
		 this.getAddButton().setRootLabel(11);
		// segmentPanel = this;
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setActionListention() {
		//RoleRoot roleRoot=new RoleRoot();
		getTable().addElementClickedActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					aqosPanel.clear();
					zqosPanel.clear();
					getOamTable().clear();
					return;
				} else {
					((SegmentPanelController) getController()).initDetailInfo();
				}
			}
		});
		if(checkRoot(RootFactory.TOPOLOGY_MANAGE)){
			getTable().addElementDoubleClickedActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					try {
						if (getTable().getAllSelect().size() != 1) {
							return;
						} else {
							getController().openUpdateDialog();
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}
			});
			
			//新建或修改按需oam
			miUpdateTestOam.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					Segment s = null;
					TestOamBusinessController oamcontroller = null;
					try {
						s = getSelect();
						if(s != null && s.getOamList().size() > 1){
							oamcontroller = new TestOamBusinessController();
							oamcontroller.openTestOamConfig(EServiceType.SECTION.toString(), s);
						}else{
							DialogBoxUtil.succeedDialog(SegmentPanel.this, ResourceUtil.srcStr(StringKeysTip.TIP_OAM_CONFIG));
							return;
						}
						
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					} finally {
						s = null;
						oamcontroller = null;
					}
				}

			});
			
			//删除按需oam
			miDeleteTestOam.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					Segment s = null;
					TestOamBusinessController oamcontroller = null;
					try {
						s = getSelect();
						if(s != null){oamcontroller = new TestOamBusinessController();
						String message = oamcontroller.deleteTestOamConfig(EServiceType.SECTION.toString(), s);
						DialogBoxUtil.succeedDialog(SegmentPanel.this, message);
						UiUtil.insertOperationLog(EOperationLogType.TESTOAMSEGMENTDELETE.getValue(), message);
						}
						
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					} finally {
						s = null;
						oamcontroller = null;
					}
				}

			});
		}
		
	}

	private void initComponents() {
		
		tabbedPane = new JTabbedPane();
		qosPanel = new JPanel();
		aqosPanel = new SegmentQosQueuePanel(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT));
		zqosPanel = new SegmentQosQueuePanel(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT));
		oamTable = new ViewDataTable<OamInfo>("segmentOAMTable");
		oamTable.getTableHeader().setResizingAllowed(true);
		oamTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		oamTable.setTableHeaderPopupMenuFactory(null);
		oamTable.setTableBodyPopupMenuFactory(null);
		oamScrollPane = new JScrollPane();
		oamScrollPane.setViewportView(oamTable);
		oamScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		oamScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		tabbedPane.addTab(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
		tabbedPane.addTab(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_INFO), oamScrollPane);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		miUpdateTestOam = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_TEST_OAM_CONFIG));
		miDeleteTestOam = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_TEST_OAM_DELETE));
	}

	private void setLayout() {
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		GridBagLayout qosGridBagLayout = new GridBagLayout();
		qosGridBagLayout.columnWeights = new double[] { 0.5, 0.5 };
		qosPanel.setLayout(qosGridBagLayout);
		addComponent(qosPanel, aqosPanel, 0, 1, 0.5, 1.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
		addComponent(qosPanel, zqosPanel, 1, 1, 0.5, 1.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
		this.setLayout(new GridBagLayout());
		addComponent(this, splitPane, 0, 1, 1.0, 1.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
	}

	@Override
	public void setController() {
		controller = new SegmentPanelController(this);
	}
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSynchroButton());
		return needRemoveButtons;
	}
	
	@Override
	public List<JButton> setAddButtons() {
		List<JButton> needAddButtons = new ArrayList<JButton>();
		needAddButtons.add(this.getSearchButton());
		needAddButtons.add(this.getFilterButton());
		needAddButtons.add(this.getClearFilterButton());
		return needAddButtons;
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
						menu.add(miUpdateTestOam);
						menu.add(miDeleteTestOam);
						checkRoot(miUpdateTestOam, RootFactory.CORE_MANAGE);
						checkRoot(miDeleteTestOam, RootFactory.CORE_MANAGE);
						menu.show(evt.getComponent(), evt.getX(), evt.getY());
						return menu;
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame jf = new JFrame();
				SegmentPanel dialog = new SegmentPanel();
				jf.add(dialog);
				jf.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				jf.setSize(1000, 580);
				jf.setVisible(true);
			}
		});
	}

	public SegmentQosQueuePanel getAqosPanel() {
		return aqosPanel;
	}

	public SegmentQosQueuePanel getZqosPanel() {
		return zqosPanel;
	}

	public ViewDataTable<OamInfo> getOamTable() {
		return oamTable;
	}

}
