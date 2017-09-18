package com.nms.ui.ptn.ne.exp.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.ne.exp.controller.ExpMainContorller;
import com.nms.ui.ptn.safety.roleManage.RootFactory;


public class ExpMainPanel extends ContentView<QosMappingMode>{
	
	private static final long serialVersionUID = -7071818928958422496L;
	private JSplitPane splitPane;
	protected JTabbedPane tabbedPane;
	private ExpPanel updateExpDialog;
	private JScrollPane sllPanelTab_attr;
	@SuppressWarnings("unused")
	private JPanel panel;
	
	//lable  1： 用于标记是EXP  2 ：还是PRI
	public ExpMainPanel() throws Exception {
		super("expMapping",RootFactory.CORE_MANAGE);
		initComponent();
		init();
		setLayout();
		addListeners();
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTitle.TIT_EXP_MAPPING),sllPanelTab_attr);
	}

	private void addListeners() {
		getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
	
				if (SwingUtilities.isLeftMouseButton(evt) && getSelect() != null) {
					try {
						updateExpDialog=new ExpPanel(getSelect());
						sllPanelTab_attr.setViewportView(updateExpDialog);
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}
			}
		});
		
	}

	public void init() throws Exception{
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_EXP_MAPPING)));
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	private void initComponent() throws Exception {
		updateExpDialog=new ExpPanel(null);
		panel = new JPanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.setMaximumSize(new Dimension(1000,500));
		tabbedPane.add( updateExpDialog);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		this.sllPanelTab_attr=new JScrollPane();
		sllPanelTab_attr.setViewportView(updateExpDialog);
	}

	public void setLayout(){
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		
//		panelLayout.columnWidths = new int[] {800 };
//		panelLayout.columnWeights = new double[] { 0 };
//		panelLayout.rowHeights = new int[] { 400,400};
//		panelLayout.rowWeights = new double[] { 0,0,0};
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
		controller = new ExpMainContorller(this);
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needButtons = new ArrayList<JButton>();
		needButtons.add(getAddButton());
		needButtons.add(getDeleteButton());
		needButtons.add(getSearchButton());
		return needButtons;
	}

	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}
		
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}
	
	

	
}
