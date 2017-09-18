package com.nms.ui.ptn.upgradeManage.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import com.nms.db.bean.equipment.manager.UpgradeManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.upgradeManage.controller.UpgradeVersionsDialogController;

public class UpgradeVersionsDialog extends PtnDialog implements Runnable{

	private static final long serialVersionUID = 1320343121147368431L;
	private JLabel chooseFileLabel;
	private JLabel schemeTypeLabel;
	private JLabel ftpAddressLabel;
	private JLabel isForceLabel;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JTextField chooseFileTextField;
	private JComboBox schemeTypeComboBox;
	private JTextField ftpAddressTextField;
	private JCheckBox isForce;
	private JTextField userNameTextField;
	private JTextField passwordField;
	private JButton open;
	private PtnButton confirm;
	private JButton cancel;
	private JLabel tipLabel;
	public JProgressBar  jpb;
	private UpgradeVersionsDialogController controller;
	private JFileChooser fileChooser;
	public  int total = 100;
	public  int current = 0;
	private List<UpgradeManage> upgradeManages;
	private UpgradeManagePanel upgradeManagePanel;
	private boolean isRun = true;
	
	public UpgradeVersionsDialog(List<UpgradeManage> upgradeManages,UpgradeManagePanel upgradeManagePanel) {
		this.upgradeManages = upgradeManages;
		this.upgradeManagePanel = upgradeManagePanel;
		this.setModal(true);
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_VERSIONS_UPGRADE));
		initComponent();
		setLayout();
		controller = new UpgradeVersionsDialogController(this);
		addActionListener();
	}

	private void addActionListener() {
		open.addActionListener(controller);
//		cancel.addActionListener(controller);
		confirm.addActionListener(controller);
	}

	private void initComponent() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		chooseFileLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_FILE));
		schemeTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_SCHEME));
		ftpAddressLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FTP_ADDRESS));
		isForceLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_FORCE));
		userNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UPGRADE_FILE));
		passwordLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.FIEL_NAME));
		chooseFileTextField = new JTextField(50);
		chooseFileTextField.setEditable(false);
		schemeTypeComboBox = new JComboBox();
		schemeTypeComboBox.setEnabled(false);
		ftpAddressTextField = new JTextField(50);
		ftpAddressTextField.setEnabled(false);
		isForce = new JCheckBox();
		userNameTextField = new JTextField(50);
		userNameTextField.setEnabled(false);
		passwordField = new JTextField(50);
		passwordField.setEnabled(false);
		tipLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UPGRADE));
		
		jpb = new JProgressBar();  
		jpb.setOrientation(JProgressBar.HORIZONTAL);  
        jpb.setMaximum(100);  
        jpb.setMinimum(0);  
        jpb.setValue(0);  
        jpb.setStringPainted(true);
        Dimension d = jpb.getSize();
		Rectangle rect = new Rectangle(0,0, d.width, d.height);
		jpb.paintImmediately(rect);
        jpb.setEnabled(true);
		
		open = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_OPEN));
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));

		fileChooser = new JFileChooser();
	}

	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 20 };
		layout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20 ,20};
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0 ,0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(25, 10, 10, 10);
		layout.setConstraints(chooseFileLabel, c);
		this.add(chooseFileLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(25, 10, 10, 10);
		layout.setConstraints(chooseFileTextField, c);
		this.add(chooseFileTextField);
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(25, 0, 10, 10);
		layout.setConstraints(open, c);
		this.add(open);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(schemeTypeLabel, c);
		this.add(schemeTypeLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(schemeTypeComboBox, c);
		this.add(schemeTypeComboBox);
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(ftpAddressLabel, c);
		this.add(ftpAddressLabel);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridheight = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(ftpAddressTextField, c);
		this.add(ftpAddressTextField);
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(isForceLabel, c);
		this.add(isForceLabel);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(isForce, c);
		this.add(isForce);
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(userNameLabel, c);
		this.add(userNameLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(userNameTextField, c);
		this.add(userNameTextField);
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 30, 10);
		layout.setConstraints(passwordLabel, c);
		this.add(passwordLabel);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 30, 10);
		layout.setConstraints(passwordField, c);
		this.add(passwordField);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 30, 10);
		layout.setConstraints(tipLabel, c);
		this.add(tipLabel);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 30, 10);
		layout.setConstraints(jpb, c);
		this.add(jpb);
		
		
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(confirm, c);
		this.add(confirm);
		c.gridx = 2;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(cancel, c);
		this.add(cancel);
	}

	public JTextField getChooseFileTextField() {
		return chooseFileTextField;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}

//	@Override
	public void run()
	{
		while(isRun)
		{
			jpb.setValue(current);
			jpb.setMaximum(total);
		}		
	}

	public JLabel getTipLabel() {
		return tipLabel;
	}

	public void setTipLabel(JLabel tipLabel) {
		this.tipLabel = tipLabel;
	}

	public List<UpgradeManage> getUpgradeManages() {
		return upgradeManages;
	}

	public void setUpgradeManages(List<UpgradeManage> upgradeManages) {
		this.upgradeManages = upgradeManages;
	}

	public JTextField getUserNameTextField() {
		return userNameTextField;
	}

	public void setUserNameTextField(JTextField userNameTextField) {
		this.userNameTextField = userNameTextField;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	public JButton getOpen() {
		return open;
	}

	public void setOpen(JButton open) {
		this.open = open;
	}

	public UpgradeManagePanel getUpgradeManagePanel() {
		return upgradeManagePanel;
	}

	public void setUpgradeManagePanel(UpgradeManagePanel upgradeManagePanel) {
		this.upgradeManagePanel = upgradeManagePanel;
	}

	public JTextField getPasswordField() {
		return passwordField;
	}

	public void setPasswordField(JTextField passwordField) {
		this.passwordField = passwordField;
	}

	public JCheckBox getIsForce() {
		return isForce;
	}

	public void setIsForce(JCheckBox isForce) {
		this.isForce = isForce;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	
}