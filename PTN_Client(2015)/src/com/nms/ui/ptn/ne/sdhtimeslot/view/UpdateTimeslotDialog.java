package com.nms.ui.ptn.ne.sdhtimeslot.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortStmTimeslot;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;

public class UpdateTimeslotDialog extends PtnDialog {
	/**   
	*   
	* @since Ver 1.1   
	*/   
	
	private static final long serialVersionUID = -8769245644491370326L;
	private PortStmTimeslot portStmTimeslot;
	private SDHTimeslotPanel panel;

	public UpdateTimeslotDialog(PortStmTimeslot portStmTimeslot, SDHTimeslotPanel panel) {
		this.setModal(true);
		try {
			this.portStmTimeslot = portStmTimeslot;
			this.panel = panel;
			initComponents();
			setLayout();
			this.comboBoxDate();
			this.initDate(portStmTimeslot);
			addListener();
			this.showWindow();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void initComponents() throws Exception {
		this.lblMessage=new JLabel();
		btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		lblmanage = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ENABLED_STATUS));
		lblexpectj2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_JTWO_EXPECT));
		lblsendj2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_JTWO_SEND));
		lblrealityj2 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_JTWO_REALITY));
		lbllptim = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LP_TIM));
		lblexpectv5 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VFIVE_EXPECT));
		lblsendv5 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VFIVE_SEND));
		lblrealityv5 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VFIVE_REALITY));
		lblcheck = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VFIVE_CHECK));
		cbbmanage = new JComboBox();
		txtexpectj2 = new PtnTextField(true,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
		txtsendj2 = new PtnTextField(true,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
		txtrealityj2 = new PtnTextField(false,PtnTextField.STRING_MAXLENGTH,this.lblMessage, this.btnSave,this);
		txtrealityj2.setEnabled(false);
		ckblptim = new JCheckBox();
		txtexpectv5 = new PtnTextField(true,PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave,this);
		txtexpectv5.setEnabled(false);
		cbbsendv5 = new JComboBox();
		txtrealityv5 = new PtnTextField(false,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave,this);
		txtrealityv5.setEnabled(false);
		ckbcheck = new JCheckBox();
	}

	private void setLayout() {

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 70, 120, 50 };
		layout.columnWeights = new double[] { 0, 0, 0 };
		layout.rowHeights = new int[] { 25, 35, 35, 35, 35, 35, 35, 35, 35, 35, 15, 35, 35 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);
		
		/** 第一行 管理状态 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 10, 5);
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(lblmanage, c);
		this.add(lblmanage);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(cbbmanage, c);
		this.add(cbbmanage);

		/** 第二行 期望j2 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblexpectj2, c);
		this.add(lblexpectj2);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtexpectj2, c);
		this.add(txtexpectj2);

		/** 第三行 发送j2 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblsendj2, c);
		this.add(lblsendj2);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtsendj2, c);
		this.add(txtsendj2);

		/** 第四行 实际j2 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblrealityj2, c);
		this.add(lblrealityj2);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtrealityj2, c);
		this.add(txtrealityj2);

		/** 第五行 LP-TIM */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lbllptim, c);
		this.add(lbllptim);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(ckblptim, c);
		this.add(ckblptim);

		/** 第六行 期望V5 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblexpectv5, c);
		this.add(lblexpectv5);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtexpectv5, c);
		this.add(txtexpectv5);

		/** 第七行 发送V5 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblsendv5, c);
		this.add(lblsendv5);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(cbbsendv5, c);
		this.add(cbbsendv5);

		/** 第八行 实际V5 */
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblrealityv5, c);
		this.add(lblrealityv5);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtrealityv5, c);
		this.add(txtrealityv5);

		/** 第九行 检查V5 */
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblcheck, c);
		this.add(lblcheck);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(ckbcheck, c);
		this.add(ckbcheck);

		/** 第十一行 按钮 */
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(btnSave, c);
		this.add(btnSave);
		c.gridx = 2;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(btnCanel, c);
		this.add(btnCanel);
	}

	private void showWindow() {
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_SDH_TIMESLOT));
		Dimension dimension = new Dimension(370, 430);
		this.setSize(dimension);
		this.setMinimumSize(dimension);
		this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
		this.setVisible(true);
	}

	private void comboBoxDate() throws Exception {
		try {
			super.getComboBoxDataUtil().comboBoxData(cbbmanage, "ENABLEDSTATUE");
			super.getComboBoxDataUtil().comboBoxData(cbbsendv5, "SENDV5");
		} catch (Exception e) {
			throw e;
		}

	}

	private void initDate(PortStmTimeslot portStmTimeslot) throws Exception {
		try {
			super.getComboBoxDataUtil().comboBoxSelectByValue(cbbmanage, portStmTimeslot.getManagerStatus() + "");

			//如果没有值给默认值
			if(null!=portStmTimeslot.getExpectjtwo() && !"".equals(portStmTimeslot.getExpectjtwo())){
				this.txtexpectj2.setText(portStmTimeslot.getExpectjtwo());
			}else{
				this.txtexpectj2.setText("ptn");
			}
			
			//如果没有值给默认值
			if(null!=portStmTimeslot.getSendjtwo() && !"".equals(portStmTimeslot.getSendjtwo())){
				this.txtsendj2.setText(portStmTimeslot.getSendjtwo());
			}else{
				this.txtsendj2.setText("ptn");
			}
			
			
			this.txtrealityj2.setText(portStmTimeslot.getRealityjtwo());
			if (portStmTimeslot.getLptim() == 0) {
				this.ckblptim.setSelected(false);
			} else {
				this.ckblptim.setSelected(true);
			}
			
			//如果没有值给默认值
			if(null!=portStmTimeslot.getExpectvfive() && !"".equals(portStmTimeslot.getExpectvfive())){
				this.txtexpectv5.setText(portStmTimeslot.getExpectvfive());
			}else{
				this.txtexpectv5.setText("Async");
			}
			
			
			super.getComboBoxDataUtil().comboBoxSelect(cbbsendv5,portStmTimeslot.getSendvfive());
			this.txtrealityv5.setText(portStmTimeslot.getRealityvfive());
			if (portStmTimeslot.getCheckvfive() == 0) {
				this.ckbcheck.setSelected(false);
			} else {
				this.ckbcheck.setSelected(true);
			}

		} catch (Exception e) {
			throw e;
		}

	}

	private void addListener() {
		btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UpdateTimeslotDialog.this.dispose();
			}
		});

		btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UpdateTimeslotDialog.this.savaPortStmTimeslot();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
	}

	private void savaPortStmTimeslot() {

		ControlKeyValue controlKeyValueManage = null;
		ControlKeyValue controlKeyValueSendv5 = null;
		DispatchUtil portStmTimeslotDispatch = null;
		String result = null;
		try {
			controlKeyValueManage = (ControlKeyValue) this.cbbmanage.getSelectedItem();
			controlKeyValueSendv5 = (ControlKeyValue) this.cbbsendv5.getSelectedItem();

			this.portStmTimeslot.setManagerStatus(Integer.parseInt(((Code) controlKeyValueManage.getObject()).getCodeValue()));
			this.portStmTimeslot.setExpectjtwo(this.txtexpectj2.getText());
			this.portStmTimeslot.setSendjtwo(this.txtsendj2.getText());
			this.portStmTimeslot.setRealityjtwo(this.txtrealityj2.getText());
			this.portStmTimeslot.setLptim(this.ckblptim.isSelected() ? 1 : 0);
			this.portStmTimeslot.setExpectvfive(this.txtexpectv5.getText());
			this.portStmTimeslot.setSendvfive(((Code) controlKeyValueSendv5.getObject()).getId()+"");
			this.portStmTimeslot.setRealityvfive(this.txtrealityv5.getText());
			this.portStmTimeslot.setCheckvfive(this.ckbcheck.isSelected() ? 1 : 0);

			portStmTimeslotDispatch = new DispatchUtil(RmiKeys.RMI_PORTSTMTIMESLOT);

			result = portStmTimeslotDispatch.excuteUpdate(this.portStmTimeslot);
			DialogBoxUtil.succeedDialog(this, result);
			this.panel.getController().refresh();
			this.dispose();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			controlKeyValueManage = null;
			controlKeyValueSendv5 = null;
			portStmTimeslotDispatch = null;
		}

	}

	private JLabel lblmanage;
	private JLabel lblexpectj2;
	private JLabel lblsendj2;
	private JLabel lblrealityj2;
	private JLabel lbllptim;
	private JLabel lblexpectv5;
	private JLabel lblsendv5;
	private JLabel lblrealityv5;
	private JLabel lblcheck;
	private JComboBox cbbmanage;
	private JTextField txtexpectj2;
	private JTextField txtsendj2;
	private JTextField txtrealityj2;
	private JCheckBox ckblptim;
	private JTextField txtexpectv5;
	private JComboBox cbbsendv5;
	private JTextField txtrealityv5;
	private JCheckBox ckbcheck;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblMessage;
}
