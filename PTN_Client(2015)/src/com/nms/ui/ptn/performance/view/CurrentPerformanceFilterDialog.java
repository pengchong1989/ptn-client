package com.nms.ui.ptn.performance.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.TDataBox;
import twaver.list.TList;

import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;

public class CurrentPerformanceFilterDialog extends PtnDialog {
	private static final long serialVersionUID = -654311209469442360L;
	private JButton confirm;
	private JButton cancel;
	private JButton clear;
	private JButton add;
	private JButton remove;
	private JLabel lbProtList;// 端口列表
	private JLabel lbPerofrType;// 性能类型
	private JLabel lbCycle;// 监控周期
	private ButtonGroup cycleButtonGroup;
	private JRadioButton minsRadioButton;// 15分钟
	private JRadioButton hoursRadioButton;// 24小时
	private JScrollPane portListScrollPane;
	private JScrollPane perforTypeScrollPane;
	private TDataBox perforTypeDataBox;
	private TList perforTypeList;// 性能类型集合
	private JPanel buttonPanel;// 按钮面板

	public CurrentPerformanceFilterDialog() {
		this.setModal(true);
		init();
	}

	private void init() {
		initComponents();
		setLayout();
		addListener();
	}

	private void initComponents() {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));

		perforTypeDataBox = new TDataBox("perforTypeDataBox");
		perforTypeList = new TList(perforTypeDataBox);
		perforTypeList.setTListSelectionMode(TList.CHECK_SELECTION);
		perforTypeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		perforTypeScrollPane = new JScrollPane(perforTypeList);
		perforTypeScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		perforTypeScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		perforTypeScrollPane.setViewportView(perforTypeList);

		minsRadioButton = new JRadioButton("15" + ResourceUtil.srcStr(StringKeysObj.MINUTES));
		hoursRadioButton = new JRadioButton("24" + ResourceUtil.srcStr(StringKeysObj.HOURS));
		cycleButtonGroup = new ButtonGroup();
		cycleButtonGroup.add(minsRadioButton);
		cycleButtonGroup.add(hoursRadioButton);

		lbProtList = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
		lbPerofrType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROPERTY_TYPE));
		lbCycle = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_PERIOD));

		add = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		remove = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE));
		clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));

		buttonPanel = new JPanel();

	}

	private void setLayout() {
		setButtonLayout();
		setListLayout();

	}

	private void setListLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 10, 10, 10, 10 };
		layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30, 30, 30, 30, 10, 30 };
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 5, 10);
		layout.setConstraints(lbProtList, c);
		this.add(lbProtList);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 10, 5, 10);
		layout.setConstraints(add, c);
		this.add(add);
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NORTH;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(remove, c);
		this.add(remove);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 5, 10);
		layout.setConstraints(portListScrollPane, c);
		this.add(portListScrollPane);
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(lbPerofrType, c);
		this.add(lbPerofrType);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(perforTypeScrollPane, c);
		this.add(perforTypeScrollPane);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(lbCycle, c);
		this.add(lbCycle);

		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(minsRadioButton, c);
		this.add(minsRadioButton);
		c.gridx = 2;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 10, 5, 10);
		layout.setConstraints(hoursRadioButton, c);
		this.add(hoursRadioButton);

		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(5, 10, 10, 10);
		layout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);

	}

	private void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 10, 10, 10, 10 };
		buttonLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
		buttonLayout.rowHeights = new int[] { 10 };
		buttonLayout.rowWeights = new double[] { 0.0 };
		buttonPanel.setLayout(buttonLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		buttonLayout.setConstraints(clear, c);
		buttonPanel.add(clear);
		c.gridx = 2;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(confirm, c);
		buttonPanel.add(confirm);
		c.gridx = 3;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(cancel, c);
		buttonPanel.add(cancel);

	}

	private void addListener() {
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CurrentPerformanceFilterDialog.this.dispose();

			}
		});
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CurrentPerformanceFilterDialog.this.clear();

			}
		});
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final PortSelectDialog dialog = new PortSelectDialog();
				dialog.setSize(new Dimension(300, 400));
				dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()),
						UiUtil.getWindowHeight(dialog.getHeight()));

//				dialog.addWindowListener(new WindowAdapter() {
//					@Override
//					public void windowClosed(WindowEvent e) {
//						dialog.dispose();
//					}
//				});
				dialog.setVisible(true);
			}
		});
	}

	private void clear() {
		perforTypeDataBox.getSelectionModel().clearSelection();
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				CurrentPerformanceFilterDialog dialog = new CurrentPerformanceFilterDialog();
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

}
