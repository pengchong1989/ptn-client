/*
 * AddUdaGroupDialog.java
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

import com.nms.db.bean.system.UdaGroup;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.UdaGroupService_MB;
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
 * @author  __USER__
 */
@SuppressWarnings("serial")
public class AddUdaGroupDialog extends PtnDialog{

	private UdaGroup udaGroup;
	private UdaGroupPanel udaGroupPanel;

	/** Creates new form AddUdaGroupDialog */
	public AddUdaGroupDialog(java.awt.Frame parent, boolean modal) {
		initComponents();
	}

	public AddUdaGroupDialog(JPanel jPanel, boolean modal, String udaGroupId) {
		this.setModal(modal);
		initComponents();
		this.jComboBox2.setVisible(false);
		this.jLabel4.setVisible(false);

		jTextField1.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("组名不能为空,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}
				jLabel5.setText("");
				jButton1.setEnabled(true);
			}
			
			public void insertUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("组名不能为空,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}
				jLabel5.setText("");
				jButton1.setEnabled(true);	
			}
			
			public void changedUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("组名不能为空,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}
				jLabel5.setText("");
				jButton1.setEnabled(true);
			}
		});
		
		try {
			super.getComboBoxDataUtil().comboBoxData(jComboBox1, "UDATYPE");
			//this.parentUdaGroupData();
			this.udaGroupPanel = (UdaGroupPanel) jPanel;
			if (null != udaGroupId && !"".equals(udaGroupId)) {
				this.jLabel1.setText("修改UDA组");
				this.getUdaGroup(udaGroupId);
				if (null != this.udaGroup) {
					this.jTextField1.setText(this.udaGroup.getGroupName());
					Code code = UiUtil.getCodeById(Integer.parseInt(udaGroup.getGroupType()));
					super.getComboBoxDataUtil().comboBoxSelect(jComboBox1, code.getId() + "");
					super.getComboBoxDataUtil().comboBoxSelect(jComboBox2, this.udaGroup.getParentId()+ "");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void getUdaGroup(String udaGroupId) throws Exception {

		UdaGroupService_MB udaGroupService = null;
		List<UdaGroup> udaGroupList = null;

		try {
			this.udaGroup = new UdaGroup();
			this.udaGroup.setId(Integer.parseInt(udaGroupId));
			udaGroupService = (UdaGroupService_MB) ConstantUtil.serviceFactory.newService_MB(Services.UDAGROUP);
			udaGroupList = udaGroupService.select(this.udaGroup);
			if (udaGroupList != null && udaGroupList.size() > 0) {
				this.udaGroup = udaGroupList.get(0);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(udaGroupService);
		}

	}

	@SuppressWarnings("unused")
	private void parentUdaGroupData() throws Exception {
		UdaGroupService_MB udaGroupService = null;
		List<UdaGroup> udaGroupList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;

		try {
			UdaGroup udaGroupSelect = new UdaGroup();
			udaGroupSelect.setParentId(-1);
			udaGroupService = (UdaGroupService_MB) ConstantUtil.serviceFactory.newService_MB(Services.UDAGROUP);
			udaGroupList = udaGroupService.select(udaGroupSelect);
			defaultComboBoxModel = (DefaultComboBoxModel) jComboBox2.getModel();
			defaultComboBoxModel.addElement(new ControlKeyValue("-1", "", null));
			if (udaGroupList != null && udaGroupList.size() > 0) {
				for (UdaGroup ug : udaGroupList) {
					defaultComboBoxModel.addElement(new ControlKeyValue(ug.getId()+ "", ug.getGroupName(), ug));
				}
			}
			jComboBox2.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(udaGroupService);
		}
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
private void initComponents() {

jPanel1 = new javax.swing.JPanel();
jLabel1 = new javax.swing.JLabel();
jLabel5 = new javax.swing.JLabel();
jLabel2 = new javax.swing.JLabel();
jTextField1 = new javax.swing.JTextField();
jLabel3 = new javax.swing.JLabel();
jComboBox1 = new javax.swing.JComboBox();
jPanel2 = new javax.swing.JPanel();
jButton1 = new javax.swing.JButton();
jButton2 = new javax.swing.JButton();
jLabel4 = new javax.swing.JLabel();
jComboBox2 = new javax.swing.JComboBox();

setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

jLabel1.setFont(new java.awt.Font("黑体", 0, 18));
jLabel1.setText("\u521b\u5efaUDA\u7ec4");//创建UDA

jLabel5.setFont(new java.awt.Font("黑体", 0, 14));
jLabel5.setForeground(java.awt.Color.red);

javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
jPanel1.setLayout(jPanel1Layout);
jPanel1Layout.setHorizontalGroup(
jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel1Layout.createSequentialGroup()
.addContainerGap()
.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
.addComponent(jLabel1))
.addContainerGap(197, Short.MAX_VALUE))
);
jPanel1Layout.setVerticalGroup(
jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(jPanel1Layout.createSequentialGroup()
.addContainerGap()
.addComponent(jLabel1)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
.addComponent(jLabel5)
.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
);

jLabel2.setText("\u7ec4\u540d");//组名

jLabel3.setText("\u7c7b\u578b");//类型

jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

jButton1.setText("\u786e\u5b9a");//确定
jButton1.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
jButton1ActionPerformed(evt);
}
});

jButton2.setText("\u53d6\u6d88");//取消
jButton2.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
jButton2ActionPerformed(evt);
}
});

javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
jPanel2.setLayout(jPanel2Layout);
jPanel2Layout.setHorizontalGroup(
jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
.addContainerGap(266, Short.MAX_VALUE)
.addComponent(jButton1)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
.addComponent(jButton2)
.addGap(30, 30, 30))
);
jPanel2Layout.setVerticalGroup(
jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
.addContainerGap(20, Short.MAX_VALUE)
.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jButton1)
.addComponent(jButton2))
.addContainerGap())
);

jLabel4.setText("\u4e0a\u5c42\u7ec4\u540d");//上层组名

jComboBox2.addItemListener(new java.awt.event.ItemListener() {
public void itemStateChanged(java.awt.event.ItemEvent evt) {
jComboBox2ItemStateChanged(evt);
}
});
jComboBox2.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
jComboBox2ActionPerformed(evt);
}
});

javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addGroup(layout.createSequentialGroup()
.addGap(46, 46, 46)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addComponent(jLabel4)
.addComponent(jLabel2)
.addComponent(jLabel3))
.addGap(28, 28, 28)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
.addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
.addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
.addContainerGap(46, Short.MAX_VALUE))
);
layout.setVerticalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup()
.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addGap(37, 37, 37)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel2)
.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGap(35, 35, 35)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel4)
.addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGap(34, 34, 34)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
.addComponent(jLabel3)
.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
);

pack();
}// </editor-fold>

	//GEN-END:initComponents

	private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {
	}
	private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			ControlKeyValue controlKeyValue = (ControlKeyValue) jComboBox2.getSelectedItem();
			if (controlKeyValue.getId().equals("-1")) {
				jComboBox1.setEnabled(true);
			} else {
				Code code = null;
				try {
					code = UiUtil.getCodeById(Integer.parseInt(((UdaGroup) controlKeyValue.getObject()).getGroupType()));
					super.getComboBoxDataUtil().comboBoxSelect(jComboBox1, code.getId() + "");
					jComboBox1.setEnabled(false);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		}
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		ControlKeyValue selectCode = (ControlKeyValue) jComboBox1.getSelectedItem();
		//		ControlKeyValue selectParent = (ControlKeyValue) jComboBox2.getSelectedItem();

		if (Verification.jtextfieldNull(jTextField1)) {
			jLabel5.setText("组名不能为空,或长度小于5");
			jButton1.setEnabled(false);
			return;
		}

		jLabel5.setText("");

		jButton1.setEnabled(true);

		if (Verification.jComboBoxNull(jComboBox1)) {
			jComboBox1.setBorder(new LineBorder(Color.RED));
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
			UiUtil.insertOperationLog(EOperationLogType.UDAERROR.getValue());
			return;
		}
		UdaGroupService_MB udaGroupService = null;

		try {
			udaGroupService = (UdaGroupService_MB) ConstantUtil.serviceFactory.newService_MB(Services.UDAGROUP);

			if (null == this.udaGroup) {
				this.udaGroup = new UdaGroup();
			}
			this.udaGroup.setGroupType(selectCode.getId());
			this.udaGroup.setGroupName(jTextField1.getText());
			//	this.udaGroup.setParentId(Integer.parseInt(selectParent.getId()));
			//	this.udaGroup.setParentName(selectParent.getName());
			udaGroupService.saveOrUpdate(udaGroup);
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
			this.dispose();

			if (null != this.udaGroupPanel) {
				udaGroupPanel.udaGroupData();
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(udaGroupService);
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				AddUdaGroupDialog dialog = new AddUdaGroupDialog(new javax.swing.JFrame(), true);
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
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JComboBox jComboBox2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JTextField jTextField1;
	// End of variables declaration//GEN-END:variables

}