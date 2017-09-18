﻿package com.nms.ui.ptn.ne.ac.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;

public class AddAcDialogStep3 extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel leftPanel;
	private JPanel rightPanel;

	private JPanel topPanel;
	private JPanel buttomPanel;

	private JLabel operJLabel; // 操作步骤
	private JSeparator js1;
	private JLabel createStep3JLabel; // 创建第三步

	private JLabel bufferTypeJLabel;

	private JComboBox bufferTypeJCB;

	private JTable simpleTable;
	private JTable detailTable;

	private JButton previousBtn;
	private PtnButton okBtn;

	private JButton addBtn;
	private JButton updateBtn;
	private PtnButton deleteBtn;
	private JScrollPane simpleTableScrollPanel;

	public AddAcDialogStep3(int siteId) {
		this.setModal(true);
		initComponents(siteId);
		setLayout();
		UiUtil.jTableColumsHide(detailTable, 1);
	}

	private void setLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		// leftPanel.setLayout(new GridBagLayout());
		// leftPanel.setBorder(BorderFactory.createTitledBorder(""));
		// addComponent(leftPanel, operJLabel, 0, 0, 0.1, 0, 1, 1,
		// GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),
		// GridBagConstraints.NORTHWEST, gbc);
		// addComponent(leftPanel, js1, 0, 1, 0.1, 0, 1, 1,
		// GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),
		// GridBagConstraints.NORTHWEST, gbc);
		// addComponent(leftPanel, createStep3JLabel, 0, 2, 0.1, 0.6, 1, 1,
		// GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
		// GridBagConstraints.NORTH, gbc);
		this.rightPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_TIP_THIRD)));
		JPanel simplePanel = new JPanel(new BorderLayout());
		JPanel detailPanel = new JPanel(new BorderLayout());

		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		// 简单qos
		JPanel bufferTypePanel = new JPanel();
		JPanel simpelTablePanel = new JPanel(new BorderLayout());
		simpleTableScrollPanel = new JScrollPane();
		simpelTablePanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_SIMPLENESS_QOS)));
		simpleTableScrollPanel.setViewportView(simpleTable);
		simpelTablePanel.add(simpleTableScrollPanel, BorderLayout.CENTER);

		bufferTypePanel.setLayout(flowLayout);
		bufferTypePanel.add(bufferTypeJLabel);
		bufferTypePanel.add(bufferTypeJCB);
		bufferTypeJCB.setPreferredSize(new Dimension(100, 20));

		simplePanel.add(bufferTypePanel, BorderLayout.NORTH);
		simplePanel.add(simpelTablePanel, BorderLayout.CENTER);
		simplePanel.setPreferredSize(new Dimension(300, 100));

		// 细分Qos
		detailPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_SUBDIVIDE_QOS)));
		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(addBtn);
		buttonPanel.add(updateBtn);
		buttonPanel.add(deleteBtn);

		JPanel detailTablePanel = new JPanel(new BorderLayout());
		JScrollPane detailTableScrollPanel = new JScrollPane();
		detailTableScrollPanel.setViewportView(detailTable);
		detailTablePanel.add(detailTableScrollPanel, BorderLayout.CENTER);

		detailPanel.add(buttonPanel, BorderLayout.NORTH);
		detailPanel.add(detailTablePanel, BorderLayout.CENTER);

		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(simplePanel, BorderLayout.NORTH);

		rightPanel.add(detailPanel, BorderLayout.CENTER);

		topPanel.setLayout(new BorderLayout());
		// leftPanel.setPreferredSize(new Dimension(150, 300));
		// /*
		// * addComponent(topPanel, leftPanel, 0, 0, 0.05, 0.8, 1, 1,
		// GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),
		// GridBagConstraints.NORTHWEST ,gbc); addComponent(topPanel,
		// rightPanel, 1, 0, 0.3, 0.8, 1, 1, GridBagConstraints.BOTH, new
		// Insets(5, 5, 5, 5), GridBagConstraints.NORTHWEST ,gbc);
		// */
		// topPanel.add(leftPanel, BorderLayout.WEST);
		topPanel.add(rightPanel, BorderLayout.CENTER);

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.RIGHT);
		buttomPanel.setLayout(fl);
		buttomPanel.add(previousBtn);
		buttomPanel.add(okBtn);

		JPanel jpanel = new JPanel(new BorderLayout());

		buttomPanel.setPreferredSize(new Dimension(680, 40));
		jpanel.add(topPanel, BorderLayout.CENTER);
		jpanel.add(buttomPanel, BorderLayout.SOUTH);
		this.add(jpanel);

	}

	private void initComponents(int siteId) {
		int actionSiteId;
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		topPanel = new JPanel();
		buttomPanel = new JPanel();

		operJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OPERATING_STEPS)); // 操作步骤
		js1 = new JSeparator();
		createStep3JLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_AC_THIRDLY)); // 创建第一步

		bufferTypeJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_STREAM_TYPE));

		bufferTypeJCB = new JComboBox();
		simpleTable = new JTable();
		

		detailTable = new JTable();
		SiteService_MB siteService = null;
		try {
			if(0==siteId){
				actionSiteId = ConstantUtil.siteId;
			}else{
				actionSiteId = siteId;
			}
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if (siteService.getManufacturer(actionSiteId) == EManufacturer.WUHAN.getValue()) {
				simpleTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { "1", 0, QosCosLevelEnum.EF.toString(), 0, 1, 0, 1,
					new Boolean(false), 0, 0 } }, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "SEQ", "COS", "CIR(kbps)",
					ResourceUtil.srcStr(StringKeysLbl.LBL_CBS), "EIR(kbps)", ResourceUtil.srcStr(StringKeysObj.EBS_BYTE),
					ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR_AWARE), "PIR(kbps)", ResourceUtil.srcStr(StringKeysLbl.LBL_PBS) }) {
				Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
						java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class,
						java.lang.Object.class };

				@Override
				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					if (columnIndex == 0)
						return false;
					return true;
				}
			});
				detailTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] { "id",
						ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "PHB", "CIR(kbps)", ResourceUtil.srcStr(StringKeysLbl.LBL_CBS),
						ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR_AWARE), "PIR(kbps)", ResourceUtil.srcStr(StringKeysLbl.LBL_PBS) }) {
					Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
							java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class };

					@Override
					public Class getColumnClass(int columnIndex) {
						return types[columnIndex];
					}

					@Override
					public boolean isCellEditable(int row, int col) {
						return false;
					}
				});
			}else{
				simpleTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { "1", 0, QosCosLevelEnum.EF.toString(), 0, 1, 0, 1,
					new Boolean(false), 0, 0 } }, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "SEQ", "COS", "CIR(kbps)",
					ResourceUtil.srcStr(StringKeysLbl.LBL_CBS), "EIR(kbps)", ResourceUtil.srcStr(StringKeysObj.EBS_BYTE),
					ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR_AWARE), "PIR(kbps)" }) {
				Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
						java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class };

				@Override
				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					if (columnIndex == 0)
						return false;
					return true;
				}
			});
				detailTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, new String[] { "id",
						ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), "SEQ","COS" ,"CIR(kbps)", ResourceUtil.srcStr(StringKeysLbl.LBL_CBS),
						"EIR(kbps)","EBS(字节)",ResourceUtil.srcStr(StringKeysLbl.LBL_COLOR_AWARE), "PIR(kbps)" }) {
					Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
							java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class };

					@Override
					public Class getColumnClass(int columnIndex) {
						return types[columnIndex];
					}

					@Override
					public boolean isCellEditable(int row, int col) {
						return false;
					}
				});
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		

		previousBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_BACK));
		okBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);

		addBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CREATE));
		updateBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_UPDATE));
		deleteBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_DELETE));
	}

	public JButton getPreviousBtn() {
		return previousBtn;
	}

	public PtnButton getOkBtn() {
		return okBtn;
	}

	public JButton getAddBtn() {
		return addBtn;
	}

	public JButton getUpdateBtn() {
		return updateBtn;
	}

	public PtnButton getDeleteBtn() {
		return deleteBtn;
	}

	public JTable getDetailTable() {
		return detailTable;
	}

	public JTable getSimpleTable() {
		return simpleTable;
	}

	public JComboBox getBufferTypeJCB() {
		return bufferTypeJCB;
	}

	public JScrollPane getSimpleTableScrollPanel() {
		return simpleTableScrollPanel;
	}

}
