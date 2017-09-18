package com.nms.ui.ptn.systemconfig.dialog.qos.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import twaver.table.editor.SpinnerNumberEditor;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;


public class QosConfigDialog extends QosCommonConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QosConfigDialog() {
		initComponents();
	}

	public QosConfigDialog(boolean modal, String title) {
		this.setModal(modal);
		this.initComponents();
		//qosConfigDialog = this;
	}

	@Override
	public void initComponents() {
		super.initComponents();
		setTableModel();
		qosTable.setModel(qosTableModel);
		GridBagConstraints gbc = new GridBagConstraints();
//		addComponent(mainPanel, titlePanel, 0, 0, 0.0, 0.005, 1, 1, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), GridBagConstraints.NORTH, gbc);
//		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		addComponent(mainPanel, namePanel, 0, 1, 0.0, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), GridBagConstraints.NORTHWEST, gbc);
		addComponent(mainPanel, tablePanel, 0, 2, 0.1, 0.1, 1, 1, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), GridBagConstraints.CENTER, gbc);
		addComponent(mainPanel, buttonPanel, 0, 3, 0.1, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), GridBagConstraints.SOUTH, gbc);

		namePanel.setLayout(new GridBagLayout());
		addComponent(namePanel, nameLabel, 0, 0, 0.002, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 20, 0, 0), GridBagConstraints.NORTHWEST, gbc);
		addComponent(namePanel, nameList, 1, 0, 0.015, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 2, 0, 10), GridBagConstraints.NORTHWEST, gbc);
		addComponent(namePanel, qosTypeLabel, 2, 0, 0.002, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 20, 0, 10), GridBagConstraints.NORTHWEST, gbc);
		addComponent(namePanel, qosTypeComboBox, 3, 0, 0.015, 0.0, 1, 1, GridBagConstraints.BOTH, new Insets(10, 2, 0, 20), GridBagConstraints.NORTHWEST, gbc);

		tablePanel.setLayout(new GridBagLayout());
		addComponent(tablePanel, tableScrollPane, 0, 0, 0.1, 0.1, 1, 1, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), GridBagConstraints.NORTHWEST, gbc);

		this.add(mainPanel);
		this.configSpinner(qosTable,"");
	}

	/*
	 * 对设置带宽的列的单元格添加Spinner
	 */
	@Override
	public void configSpinner(JTable qosTable,String type) {
		setIdColumnAttribute(qosTable);
		cosColumn = qosTable.getColumn("COS");
		cosColumn.setCellEditor(new DefaultCellEditor(comboBox));
		
//		if (CodeConfigItem.getInstance().getWuhan() == 1) {
//			cirColumn = qosTable.getColumn("CIR(Mbps)");
//			cirColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.QOS_CIR_MAX_10G/1000+"", "1"));
//			
//			eirColumn = qosTable.getColumn("EIR(Mbps)");
//			eirColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.QOS_CIR_MAX_10G/1000+"", "1"));
//			
//			pirColumn = qosTable.getColumn("PIR(Mbps)");
//			pirColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
//		}else{
			cirColumn = qosTable.getColumn("CIR(kbps)");
			cirColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.QOS_CIR_MAX_10G+"", "64"));
			
			eirColumn = qosTable.getColumn("EIR(kbps)");
			eirColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.QOS_CIR_MAX_10G+"", "64"));
			
			pirColumn = qosTable.getColumn("PIR(kbps)");
			pirColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
//		}

		

		cbsColumn = qosTable.getColumn(ResourceUtil.srcStr(StringKeysLbl.LBL_CBS));
		cbsColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.CBS_MAXVALUE+"", "1"));

		ebsColumn = qosTable.getColumn(ResourceUtil.srcStr(StringKeysObj.EBS_BYTE));
		ebsColumn.setCellEditor(new SpinnerNumberEditor("0", ConstantUtil.CBS_MAXVALUE+"", "1"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setTableModel() {
		columnName = new Vector();
		
		columnName.add(ResourceUtil.srcStr(StringKeysObj.ORDER_NUM));
		columnName.add("COS");
		columnName.add(ResourceUtil.srcStr(StringKeysLbl.LBL_DIR));
		
//		if (CodeConfigItem.getInstance().getWuhan() == 1) {
//			columnName.add("CIR(Mbps)");
//			columnName.add(ResourceUtil.srcStr(StringKeysLbl.LBL_CBS));
//			columnName.add("EIR(Mbps)");
//			columnName.add(ResourceUtil.srcStr(StringKeysObj.EBS_BYTE));
//			columnName.add("PIR(Mbps)");
//		}else{
			columnName.add("CIR(kbps)");
			columnName.add(ResourceUtil.srcStr(StringKeysLbl.LBL_CBS));
			columnName.add("EIR(kbps)");
			columnName.add(ResourceUtil.srcStr(StringKeysObj.EBS_BYTE));
			columnName.add("PIR(kbps)");
//		}
	
		qosTableModel = new DefaultTableModel(null, columnName.toArray()) {
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 2) {
					return false;
				}
				return true;
			}
		};
	}
}
