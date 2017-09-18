/*
 * UpdatePortDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.ne.pdh.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.E1Info;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 * 修改PDH
 * @author __USER__
 */
public class UpdateE1Dialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3770415296255615906L;
	private PortInst portInst;
	private E1MainPanel panel;
	private E1Info e1Info;

	/** Creates new form UpdatePortDialog */
	public UpdateE1Dialog(java.awt.Frame parent, boolean modal) {
	}

	/**
	 * 通过端口和端口类型修改端口信息
	 * @param portinst2
	 * 				端口
	 * @param portType
	 * 				端口类型
	 * @throws Exception
	 */
	public UpdateE1Dialog(PortInst portinst2, String portType) throws Exception {
		portInst = new PortInst();
		portInst = portinst2;
	}

	/**
	 * 通过端口和端口类型修改端口信息
	 * @param e1Info
	 * 			端口
	 * @param panel
	 * 			主页面
	 */
	public UpdateE1Dialog(E1Info e1Info, E1MainPanel panel) {
		try {
			this.panel = panel;
			this.e1Info = e1Info;
			this.setModal(true);
			initComponents();
			setLayout();
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_PDH));
			addListeners();
			this.comboxData();
			refreshUIData(e1Info);
			this.showWindow();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 窗口显示参数
	 */
	private void showWindow(){
		Dimension dimension=new Dimension(540,235);
		this.setSize(dimension);
		this.setMinimumSize(dimension);
		this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
		this.setVisible(true);
	}
	
	/**
	 * 下拉菜单初始化
	 * @throws Exception
	 */
	private void comboxData() throws Exception {
		super.getComboBoxDataUtil().comboBoxData(manStatusCbox, "ENABLEDSTATUE");
		super.getComboBoxDataUtil().comboBoxData(this.EncodeCombox, "LINECODE");
	}

	
	public void refreshUIData(E1Info e1Info) {
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setPortId(e1Info.getPortId());
			portInst = portService.select(portInst).get(0);
			this.portNameField.setText(portInst.getPortName());
			// this.manStatusCbox.addItem(portInst.getManageStatus()==0?"不使能":"使能");
			this.jobStatusJTF.setText(e1Info.getPortInst().getJobStatus());
			this.modelJTF.setText(e1Info.getModel());
			super.getComboBoxDataUtil().comboBoxSelect(EncodeCombox, e1Info.getLinecoding());
			this.impedanceJTF.setText(e1Info.getImpedance() + "");
			super.getComboBoxDataUtil().comboBoxSelectByValue(manStatusCbox, portInst.getIsEnabled_code() + "");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
	}

	private void initComponents() {

		portNameLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		portNameField = new JTextField(20);
		portNameField.setEditable(false);

		manStatusLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ENABLED_STATUS));
		manStatusCbox = new javax.swing.JComboBox();
		manStatusCbox.setPreferredSize(new Dimension(125, 20));

		jobstatusLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_JOB_STATUS));
		jobStatusJTF = new javax.swing.JTextField(20);
		jobStatusJTF.setEditable(false);

		modelLable = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MODAL));
		modelJTF = new javax.swing.JTextField(20);
		modelJTF.setEditable(false);

		linecodingLable = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LINE_CODE));
		EncodeCombox = new javax.swing.JComboBox();
		EncodeCombox.setPreferredSize(new Dimension(125, 20));

		impedanceLable = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IMPEDANCE));
		impedanceJTF = new javax.swing.JTextField(20);
		impedanceJTF.setEditable(false);

		saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		cancelBtn = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));

		e1InfoJPanel = new JPanel();
		buttonJPanel = new JPanel();
	}

	public void setLayout() {
		setE1InfoPanelLayout();
		setButtonLayout();
		// this.add(BorderLayout.NORTH,e1InfoJPanel);
		// this.add(BorderLayout.SOUTH,b)
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		GridBagLayout contentLayout = new GridBagLayout();
		contentLayout.rowHeights = new int[] { 200, 180 };
		contentLayout.rowWeights = new double[] { 0.2, 0.4 };
		contentLayout.columnWeights = new double[] { 1.0 };
		this.setLayout(contentLayout);

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 0, 10);
		c.anchor = GridBagConstraints.WEST;
		contentLayout.setConstraints(e1InfoJPanel, c);
		this.add(e1InfoJPanel);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 0, 10);
		contentLayout.setConstraints(buttonJPanel, c);
		this.add(buttonJPanel);

	}

	public void setE1InfoPanelLayout() {
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		this.e1InfoJPanel.setLayout(layout);

		/* 第一行 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(portNameLabel, c);
		this.e1InfoJPanel.add(portNameLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 40);
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(portNameField, c);
		this.e1InfoJPanel.add(portNameField);

		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.manStatusLabel, c);
		this.e1InfoJPanel.add(this.manStatusLabel);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(this.manStatusCbox, c);
		this.e1InfoJPanel.add(this.manStatusCbox);

		/* 第二行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.jobstatusLabel, c);
		this.e1InfoJPanel.add(this.jobstatusLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 40);
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(this.jobStatusJTF, c);
		this.e1InfoJPanel.add(this.jobStatusJTF);

		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.modelLable, c);
		this.e1InfoJPanel.add(this.modelLable);
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.addLayoutComponent(this.modelJTF, c);
		this.e1InfoJPanel.add(this.modelJTF);

		/* 第三行 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.linecodingLable, c);
		this.e1InfoJPanel.add(this.linecodingLable);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 40);
		layout.addLayoutComponent(this.EncodeCombox, c);
		this.e1InfoJPanel.add(this.EncodeCombox);

		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 15, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.impedanceLable, c);
		this.e1InfoJPanel.add(this.impedanceLable);
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 15, 5);
		layout.addLayoutComponent(this.impedanceJTF, c);
		this.e1InfoJPanel.add(this.impedanceJTF);

	}

	public void setButtonLayout() {

		buttonJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonJPanel.add(saveBtn);
		buttonJPanel.add(cancelBtn);

	}

	private void addListeners() {
		saveBtn.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					UpdateE1Dialog.this.savaE1();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}

	public void savaE1() throws Exception {
		E1Info e1Info = collectData(portInst);
		DispatchUtil e1Dispatch = new DispatchUtil(RmiKeys.RMI_E1);
		List<E1Info> e1InfoList=null;
		try {
			e1InfoList=new ArrayList<E1Info>();
			e1InfoList.add(e1Info);
			String isSuccess = e1Dispatch.excuteUpdate(e1InfoList);
			DialogBoxUtil.succeedDialog(null, isSuccess);
			this.panel.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	protected E1Info collectData(PortInst portInst) throws Exception {
		Code code = new Code();
		ControlKeyValue controlKeyValue = (ControlKeyValue) this.manStatusCbox.getSelectedItem();
		code = (Code) controlKeyValue.getObject();
		e1Info.setPortName(portInst.getPortName());
//		e1Info.setId(portInst.getSiteId());   离线网元去id值不对
		e1Info.getPortInst().setIsEnabled_code(Integer.parseInt(code.getCodeValue()));
		e1Info.setModel(this.modelJTF.getText());
		e1Info.getPortInst().setJobStatus(this.jobStatusJTF.getText());
		ControlKeyValue Linecoding =(ControlKeyValue) this.EncodeCombox.getSelectedItem();
		e1Info.setLinecoding(((Code)Linecoding.getObject()).getId()+"");
		e1Info.setImpedance(Integer.parseInt(this.impedanceJTF.getText()));
		return e1Info;
	}

	private javax.swing.JPanel e1InfoJPanel;
	private javax.swing.JPanel buttonJPanel;
	private javax.swing.JComboBox EncodeCombox;
	private javax.swing.JButton cancelBtn;
	private javax.swing.JTextField impedanceJTF;
	private javax.swing.JLabel impedanceLable;
	private javax.swing.JLabel portNameLabel;
	private javax.swing.JLabel manStatusLabel;
	private javax.swing.JTextField portNameField;
	private javax.swing.JTextField jobStatusJTF;
	private javax.swing.JLabel jobstatusLabel;
	private javax.swing.JLabel linecodingLable;
	private javax.swing.JLabel modelLable;
	private javax.swing.JComboBox manStatusCbox;
	private javax.swing.JTextField modelJTF;
	private PtnButton saveBtn;
	// End of variables declaration//GEN-END:variables

}