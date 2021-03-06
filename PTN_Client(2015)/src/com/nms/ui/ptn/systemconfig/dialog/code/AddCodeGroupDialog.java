/*
 * AddCodeGroupDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.systemconfig.dialog.code;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nms.db.bean.system.code.CodeGroup;
import com.nms.model.system.code.CodeGroupService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.Verification;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.CodePanel;

/**
 * 
 * @author __USER__
 */
@SuppressWarnings("serial")
public class AddCodeGroupDialog extends javax.swing.JDialog {

	private CodePanel codePanel = null;
	private CodeGroup codeGroup = null;

	/** Creates new form AddCodeGroupDialog */
	public AddCodeGroupDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public AddCodeGroupDialog(JPanel jPanel, boolean modal, String codeGroupId) {
		this.setModal(modal);
		initComponents();

		jTextField1.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("Code组名是必填项,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}

				jLabel5.setText("");

				jButton1.setEnabled(true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("Code组名是必填项,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}

				jLabel5.setText("");

				jButton1.setEnabled(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (Verification.jtextfieldNull(jTextField1)) {
					jLabel5.setText("Code组名是必填项,或长度小于5");
					jButton1.setEnabled(false);
					return;
				}

				jLabel5.setText("");

				jButton1.setEnabled(true);
			}
		});

		try {
			this.codePanel = (CodePanel) jPanel;

			if (null != codeGroupId && !"".equals(codeGroupId)) {

				this.getCodeGroup(codeGroupId);

				if (null != codeGroup) {
					jTextField1.setText(codeGroup.getCodeGroupName());
					jTextField2.setText(codeGroup.getCodeIdentily());
					jTextArea1.setText(codeGroup.getCodeDesc());
				}
				this.jLabel1.setText("修改Code组");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void getCodeGroup(String codeGroupId) throws Exception {
		CodeGroupService_MB codeGroupService = null;
		List<CodeGroup> codeGroupList = null;
		try {
			codeGroup = new CodeGroup();
			codeGroup.setId(Integer.parseInt(codeGroupId));
			codeGroupService = (CodeGroupService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.CodeGroup);
			codeGroupList = codeGroupService.select(codeGroup);

			if (null != codeGroupList && codeGroupList.size() > 0) {
				codeGroup = codeGroupList.get(0);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(codeGroupService);
		}

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jLabel4 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		jLabel1.setFont(new java.awt.Font("黑体", 0, 18));
		jLabel1.setText("\u521b\u5efaCode\u7ec4");

		jLabel5.setFont(new java.awt.Font("黑体", 0, 14));
		jLabel5.setForeground(java.awt.Color.red);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jLabel5,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																233,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel1))
										.addContainerGap(205, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel5)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		jButton1.setText("\u786e\u5b9a");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("\u53d6\u6d88");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel2Layout.createSequentialGroup().addContainerGap(290,
						Short.MAX_VALUE).addComponent(jButton1).addGap(18, 18,
						18).addComponent(jButton2).addGap(26, 26, 26)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel2Layout.createSequentialGroup().addContainerGap(16,
						Short.MAX_VALUE).addGroup(
						jPanel2Layout.createParallelGroup(
								javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButton1).addComponent(jButton2))
						.addContainerGap()));

		jLabel2.setText("Code\u7ec4\u540d");

		jLabel3.setText("Code\u7ec4\u6807\u8bc6");

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		jLabel4.setText("\u8bf4\u660e");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jPanel1,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(jPanel2,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGap(34, 34, 34)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel2)
														.addComponent(jLabel3)
														.addComponent(jLabel4))
										.addGap(32, 32, 32)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(
																jScrollPane1,
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jTextField1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																258,
																Short.MAX_VALUE)
														.addComponent(
																jTextField2,
																javax.swing.GroupLayout.Alignment.LEADING))
										.addContainerGap(68, Short.MAX_VALUE)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(37, 37, 37)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																jTextField1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(18, 18, 18)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel3)
														.addComponent(
																jTextField2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				jScrollPane1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGap(
																				62,
																				62,
																				62)
																		.addComponent(
																				jLabel4)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												50, Short.MAX_VALUE)
										.addComponent(
												jPanel2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		CodeGroupService_MB codeGroupService = null;
		boolean isExist = false;
		boolean isUpdate = false;
		CodeGroup codeGroupSelect = null;
		List<CodeGroup> codeGroupList = null;
		try {

			if (Verification.jtextfieldNull(jTextField1)) {
				jLabel5.setText("Code组名是必填项,或长度小于5");
				jButton1.setEnabled(false);
				return;
			}

			jLabel5.setText("");

			jButton1.setEnabled(true);

			if (Verification.jtextfieldNull(jTextField2)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				return;
			}

			codeGroupService = (CodeGroupService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.CodeGroup);

			codeGroupSelect = new CodeGroup();
			codeGroupSelect.setCodeIdentily(jTextField2.getText());
			codeGroupList = codeGroupService.select(codeGroupSelect);
			if (codeGroupList != null) {
				if (codeGroupList.size() > 0) {
					isExist = true;
				}
			}

			if (null == codeGroup) {

				if (isExist) {
					DialogBoxUtil.errorDialog(this,
							ResourceUtil.srcStr(StringKeysTip.TIP_CODE_IDENTITY_EXIST));
					return;
				}

				codeGroup = new CodeGroup();
			} else {
				if (!codeGroup.getCodeIdentily().equals(jTextField2.getText())) {
					isUpdate = true;
				}

				if (isExist && isUpdate) {
					DialogBoxUtil.errorDialog(this,
							ResourceUtil.srcStr(StringKeysTip.TIP_CODE_IDENTITY_EXIST));
					return;
				}

			}

			codeGroup.setCodeIdentily(jTextField2.getText());
			codeGroup.setCodeGroupName(jTextField1.getText());
			codeGroup.setCodeDesc(jTextArea1.getText());

			codeGroupService.saveOrUpdate(codeGroup);

			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
			this.dispose();

			if (this.codePanel != null) {
				codePanel.tableGroupData();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(codeGroupService);
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				AddCodeGroupDialog dialog = new AddCodeGroupDialog(
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
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	// End of variables declaration//GEN-END:variables

}