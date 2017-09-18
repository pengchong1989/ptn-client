/*
 * AddUdaAttrDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.systemconfig.dialog.uda;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nms.db.bean.system.UdaAttr;
import com.nms.db.bean.system.code.CodeGroup;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.UdaAttrService_MB;
import com.nms.model.system.code.CodeGroupService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.Verification;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.UdaGroupPanel;

/**
 * 
 * @author __USER__
 */
@SuppressWarnings("serial")
public class AddUdaAttrDialog extends PtnDialog {
	private UdaAttr udaattr;
	private UdaGroupPanel udagrouppanel = null;

	/** Creates new form AddUdaAttrDialog */
	public AddUdaAttrDialog(java.awt.Frame parent, boolean modal) {
		initComponents();
	}

	public AddUdaAttrDialog(JPanel jPanel, boolean modal, String udaAttrId,
			String groupId) {
		this.setModal(modal);
		initComponents();

		jTextField1.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel8.setText("属性名称是必填项,或长度小于5");

					jButton1.setEnabled(false);
					return;
				}

				jLabel8.setText("");

				jButton1.setEnabled(true);
			}

			public void insertUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel8.setText("属性名称是必填项,或长度小于5");

					jButton1.setEnabled(false);
					return;
				}

				jLabel8.setText("");

				jButton1.setEnabled(true);

			}

			public void changedUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel8.setText("属性名称是必填项,或长度小于5");

					jButton1.setEnabled(false);
					return;
				}

				jLabel8.setText("");

				jButton1.setEnabled(true);
			}
		});

		try {
			this.hideuda();
			this.udaAttrGroupData();

			super.getComboBoxDataUtil().comboBoxData(jComboBox1, "UDAATTRTYPE");
			this.udagrouppanel = (UdaGroupPanel) jPanel;

			if (null != udaAttrId && !"".equals(udaAttrId)) {
				this.jLabel1.setText("修改UDA属性");
				this.getUdaAttr(udaAttrId);

				if (udaattr != null) {
					super.getComboBoxDataUtil().comboBoxSelect(jComboBox1, this.udaattr
							.getAttrType());
					super.getComboBoxDataUtil().comboBoxSelect(jComboBox2, this.udaattr
							.getCodeGroupId());
					this.jTextField1.setText(this.udaattr.getAttrName());
					this.jTextField2.setText(this.udaattr.getWidth() + "");
					this.jTextField3.setText(this.udaattr.getHeight() + "");
					this.jTextField4.setText(this.udaattr.getDefaultValue());
					this.jCheckBox1.setSelected(this.udaattr.getIsTableShow()
							.equals("true") ? true : false);
					this.jCheckBox2.setSelected(this.udaattr.getIsNeedText()
							.equals("true") ? true : false);
					this.jCheckBox3.setSelected(this.udaattr.getIsMustFill()
							.equals("true") ? true : false);
				}

			} else {
				this.udaattr = new UdaAttr();
				this.udaattr.setGroupId(Integer.parseInt(groupId));
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void getUdaAttr(String udaAttrId) throws Exception {

		UdaAttrService_MB udaattrservice = null;
		List<UdaAttr> udaattrList = null;
		try {
			udaattr = new UdaAttr();
			udaattr.setId(Integer.parseInt(udaAttrId));
			udaattrservice = (UdaAttrService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.UDAATTR);
			udaattrList = udaattrservice.select(udaattr);

			if (null != udaattrList && udaattrList.size() > 0) {
				udaattr = udaattrList.get(0);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(udaattrservice);
		}
	}

	private void udaAttrGroupData() throws Exception {
		CodeGroupService_MB codegroupservice = null;
		try {
			 codegroupservice = (CodeGroupService_MB) ConstantUtil.serviceFactory
			.newService_MB(Services.CodeGroup);
			List<CodeGroup> codegroupList = null;
			codegroupList = codegroupservice.select();
			DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) jComboBox2
			.getModel();
			defaultComboBoxModel.addElement(new ControlKeyValue("0", ""));
			for (int i = 0; i < codegroupList.size(); i++) {
				defaultComboBoxModel.addElement(new ControlKeyValue(codegroupList
						   .get(i).getCodeIdentily(), codegroupList.get(i)
						   .getCodeGroupName(), null));
			}
			jComboBox2.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(codegroupservice);
		}
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

jPanel1 = new javax.swing.JPanel();
jLabel1 = new javax.swing.JLabel();
jLabel8 = new javax.swing.JLabel();
jPanel2 = new javax.swing.JPanel();
jLabel2 = new javax.swing.JLabel();
jTextField1 = new javax.swing.JTextField();
jLabel3 = new javax.swing.JLabel();
jComboBox1 = new javax.swing.JComboBox();
jCheckBox1 = new javax.swing.JCheckBox();
jCheckBox3 = new javax.swing.JCheckBox();
jPanel3 = new javax.swing.JPanel();
jLabel4 = new javax.swing.JLabel();
jComboBox2 = new javax.swing.JComboBox();
jCheckBox2 = new javax.swing.JCheckBox();
jLabel5 = new javax.swing.JLabel();
jTextField2 = new javax.swing.JTextField();
jLabel6 = new javax.swing.JLabel();
jTextField3 = new javax.swing.JTextField();
jLabel7 = new javax.swing.JLabel();
jTextField4 = new javax.swing.JTextField();
jPanel4 = new javax.swing.JPanel();
jButton1 = new javax.swing.JButton();
jButton2 = new javax.swing.JButton();

setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

jLabel1.setFont(new java.awt.Font("黑体", 0, 18));
jLabel1.setText("\u521b\u5efaUDA\u5c5e\u6027");

jLabel8.setFont(new java.awt.Font("黑体", 0, 14));
jLabel8.setForeground(java.awt.Color.red);

javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
jPanel1.setLayout(jPanel1Layout);
jPanel1Layout.setHorizontalGroup(
jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel1Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
.addComponent(jLabel1))
.addContainerGap(389, Short.MAX_VALUE))
);
jPanel1Layout.setVerticalGroup(
jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel1Layout.createSequentialGroup()
.addContainerGap()
.addComponent(jLabel1)
.addGap(20, 20, 20)
.addComponent(jLabel8)
.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
);

jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("\u57fa\u672c\u4fe1\u606f"));

jLabel2.setText("\u5c5e\u6027\u540d\u79f0");

jLabel3.setText("\u5c5e\u6027\u7c7b\u578b");


jComboBox1.addItemListener(new java.awt.event.ItemListener() {
public void itemStateChanged(java.awt.event.ItemEvent evt) {
jComboBox1ItemStateChanged(evt);
}
});
jComboBox1.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
}
});

jCheckBox1.setText("\u662f\u5426\u5728\u5217\u8868\u4e2d\u663e\u793a");

jCheckBox3.setText("\u662f\u5426\u5fc5\u586b");

javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
jPanel2.setLayout(jPanel2Layout);
jPanel2Layout.setHorizontalGroup(
jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel2Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel2Layout.createSequentialGroup()
.addComponent(jCheckBox1)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
.addComponent(jCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGroup(jPanel2Layout.createSequentialGroup()
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jLabel2)
.addComponent(jLabel3))
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jComboBox1, 0, 217, Short.MAX_VALUE)
.addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))))
.addContainerGap())
);
jPanel2Layout.setVerticalGroup(
jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel2Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel2)
.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel3)
.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGap(30, 30, 30)
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jCheckBox1)
.addComponent(jCheckBox3))
.addGap(20, 20, 20))
);

jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("\u53ef\u9009\u4fe1\u606f"));

jLabel4.setText("\u679a\u4e3e\u7ec4");

jCheckBox2.setText("\u662f\u5426\u9700\u8981\u6587\u672c");

jLabel5.setText("\u957f");

jLabel6.setText("\u9ad8");

jLabel7.setText("\u9ed8\u8ba4\u503c");

javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
jPanel3.setLayout(jPanel3Layout);
jPanel3Layout.setHorizontalGroup(
jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel3Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jCheckBox2)
.addGroup(jPanel3Layout.createSequentialGroup()
.addComponent(jLabel4)
.addGap(18, 18, 18)
.addComponent(jComboBox2, 0, 211, Short.MAX_VALUE))
.addGroup(jPanel3Layout.createSequentialGroup()
.addComponent(jLabel5)
.addGap(19, 19, 19)
.addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
.addGap(71, 71, 71)
.addComponent(jLabel6)
.addGap(18, 18, 18)
.addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGroup(jPanel3Layout.createSequentialGroup()
.addComponent(jLabel7)
.addGap(18, 18, 18)
.addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)))
.addContainerGap())
);
jPanel3Layout.setVerticalGroup(
jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel3Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel4)
.addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGap(18, 18, 18)
.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel7)
.addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGap(18, 18, 18)
.addComponent(jCheckBox2)
.addGap(16, 16, 16)
.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addComponent(jLabel5)
.addComponent(jLabel6)
.addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addContainerGap(16, Short.MAX_VALUE))
);

jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

jButton1.setText("\u786e\u5b9a");
jButton1.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
jButton1ActionPerformed(evt);
}
});

jButton2.setText("\u53d6\u6d88");
jButton2.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
jButton2ActionPerformed(evt);
}
});

javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
jPanel4.setLayout(jPanel4Layout);
jPanel4Layout.setHorizontalGroup(
jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
.addContainerGap(498, Short.MAX_VALUE)
.addComponent(jButton1)
.addGap(18, 18, 18)
.addComponent(jButton2)
.addGap(36, 36, 36))
);
jPanel4Layout.setVerticalGroup(
jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
.addContainerGap(19, Short.MAX_VALUE)
.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jButton2)
.addComponent(jButton1))
.addContainerGap())
);

javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addGroup(layout.createSequentialGroup()
.addContainerGap()
.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addGap(44, 44, 44)
.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addContainerGap())
.addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
);
layout.setVerticalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup()
.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
);

pack();
}// </editor-fold>

	//GEN-END:initComponents

	private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {

		ControlKeyValue controlKeyValue = null;
		try {
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				this.clearuda();
				controlKeyValue = (ControlKeyValue) jComboBox1
						.getSelectedItem();
				if (controlKeyValue.getName().equals("")) {
					this.hideuda();
				} else if (controlKeyValue.getName().equals("ComboBox")) {
					jComboBox2.setEnabled(true);
					jTextField4.setEnabled(false);
					jCheckBox2.setEnabled(false);
				} else if (controlKeyValue.getName().equals("CheckBox")) {
					jCheckBox2.setEnabled(true);
					jComboBox2.setEnabled(false);
					jTextField4.setEnabled(false);
				} else {
					jTextField4.setEnabled(true);
					jComboBox2.setEnabled(false);
					jCheckBox2.setEnabled(false);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			controlKeyValue = null;
		}

	}

	private void clearuda() {
		jComboBox2.setSelectedIndex(0);
		jTextField4.setText("");
		jCheckBox2.setSelected(false);
	}

	private void hideuda() {
		jComboBox2.setEnabled(false);
		jCheckBox2.setEnabled(false);
		jTextField4.setEnabled(false);
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		UdaAttrService_MB udaattrservice = null;
		ControlKeyValue selectType = null;
		ControlKeyValue selectUdaattrGroup = null;
		try {
			selectType = (ControlKeyValue) jComboBox1.getSelectedItem();
			selectUdaattrGroup = (ControlKeyValue) jComboBox2.getSelectedItem();

			if (Verification.jtextfieldNull(jTextField1)) {
				jLabel8.setText("属性名称是必填项,或长度小于5");

				jButton1.setEnabled(false);
				return;
			}

			jLabel8.setText("");

			jButton1.setEnabled(true);

			if (Verification.jComboBoxNull(jComboBox1)) {
				jComboBox1.setBorder(new LineBorder(Color.RED));
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				UiUtil.insertOperationLog(EOperationLogType.UDAERROR.getValue());
				return;
			}

			udaattrservice = (UdaAttrService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.UDAATTR);

			this.udaattr.setAttrName(jTextField1.getText());
			this.udaattr.setAttrType(selectType.getId());
			this.udaattr.setIsTableShow(jCheckBox1.isSelected() + "");
			this.udaattr.setDefaultValue(jTextField4.getText());
			this.udaattr.setIsNeedText(jCheckBox2.isSelected() + "");
			this.udaattr.setIsMustFill(jCheckBox3.isSelected() + "");
			if (jTextField2.getText().trim().length() > 0) {
				this.udaattr.setWidth(Integer.parseInt(jTextField2.getText()));
			}
			if (jTextField3.getText().trim().length() > 0) {
				this.udaattr.setHeight(Integer.parseInt(jTextField3.getText()));
			}
			this.udaattr.setCodeGroupId(selectUdaattrGroup.getId());

			udaattrservice.saveOrUpdate(udaattr);

			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
			this.dispose();

			if (null != this.udagrouppanel) {
				udagrouppanel.udaGroupData();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(udaattrservice);
		}

	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				AddUdaAttrDialog dialog = new AddUdaAttrDialog(
						new javax.swing.JFrame(), true);
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

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JCheckBox jCheckBox1;
	private javax.swing.JCheckBox jCheckBox2;
	private javax.swing.JCheckBox jCheckBox3;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JComboBox jComboBox2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField3;
	private javax.swing.JTextField jTextField4;
	// End of variables declaration//GEN-END:variables

}