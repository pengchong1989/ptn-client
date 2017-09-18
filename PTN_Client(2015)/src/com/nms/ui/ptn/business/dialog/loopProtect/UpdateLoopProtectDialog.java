package com.nms.ui.ptn.business.dialog.loopProtect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.loopProtect.LoopProtectPanel;

/**
 * @author Administrator
 * 
 */
public class UpdateLoopProtectDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel namelabel;
	private PtnTextField nametext;
	private JLabel lblwaitTime;
	private JLabel lbldelayTime;
	private PtnSpinner txtWaitTime;
	private PtnSpinner txtDelayTime;
	private JCheckBox chkAps;
	private JCheckBox isBack;
	private JLabel lblMessage;
	private PtnButton confirmButton;//保存
	private JButton cancleButton;//取消
	private JPanel contentPanel;
	private JPanel buttonPanel;
	private JScrollPane scrollPanel;
	private JPanel titlePanel;
	private JLabel lblTitle;
	private final List<LoopProtectInfo> loopProtectInfoList;
	private final LoopProtectPanel loopProtectPanel;
	private LoopProtectInfo loopBefore = null;//log日志显示需要

	public UpdateLoopProtectDialog(List<LoopProtectInfo> loopProtectInfos,LoopProtectPanel view) throws Exception {
		super.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_LOOP));
		loopProtectPanel = view;
		loopProtectInfoList = loopProtectInfos;
		initComponents();
		setLayout();
		setValue(loopProtectInfoList);
	}

	private void initComponents() throws Exception {
		titlePanel = new JPanel();
		lblTitle = new JLabel();
		this.lblMessage = new JLabel();
		namelabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
//		nametext = new PtnTextField();
		this.lblwaitTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));
		
		this.txtDelayTime=new PtnSpinner(10000, 0, 100,ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));
		this.txtWaitTime=new PtnSpinner(12,1,1,ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.chkAps = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.isBack = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_LOOP)));
		scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(contentPanel);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		buttonPanel = new JPanel();
		cancleButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		cancleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		confirmButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		nametext = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.confirmButton, this);
		confirmButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 
				try {
					confrimAction();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
	}

	private void setLayout() {
		// title面板布局
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		layout.rowHeights = new int[] { 20 };
		layout.rowWeights = new double[] { 0 };
		layout.columnWidths = new int[] { 60, ConstantUtil.INT_WIDTH_THREE - 60 };
		layout.columnWeights = new double[] { 0, 1.0 };
		titlePanel.setLayout(layout);
		addComponent(titlePanel, lblTitle, 0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 20, 5, 5), GridBagConstraints.CENTER, c);
		// 主面板布局
		layout = new GridBagLayout();
		layout.rowHeights = new int[] { 60, 200, 60 };
		layout.rowWeights = new double[] { 0, 0, 0 };
		layout.columnWidths = new int[] { ConstantUtil.INT_WIDTH_THREE };
		layout.columnWeights = new double[] { 1 };
		this.setLayout(layout);
		addComponentJDialog(this, titlePanel, 0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponentJDialog(this, scrollPanel, 0, 1, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponentJDialog(this, buttonPanel, 0, 2, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);

		// content面板布局
		setValueLayout();;
		// button面板布局
		setButtonLayout();
	}

	private void setValueLayout() {
//		addComponent(contentPanel, lblMessage, 0, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, namelabel, 0, 1, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, nametext, 1, 1, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, lblwaitTime, 0, 2, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, txtWaitTime, 1, 2, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, lbldelayTime, 0, 3, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, txtDelayTime, 1, 3, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, chkAps, 0, 4, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
//		addComponent(contentPanel, isBack, 0, 5, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 180, 50 };
		layout.columnWeights = new double[] { 0, 0, 0 };
		layout.rowHeights = new int[] { 25, 25, 25, 25, 25, 25 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.contentPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		/** 第0行 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.contentPanel.add(this.lblMessage);

		/** 第一行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(namelabel, c);
		this.contentPanel.add(namelabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(nametext, c);
		this.contentPanel.add(nametext);

		/** 第三行 等待恢复时间 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lblwaitTime, c);
		this.contentPanel.add(this.lblwaitTime);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.txtWaitTime, c);
		this.contentPanel.add(this.txtWaitTime);
		/** 第四行 拖延时间 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lbldelayTime, c);
		this.contentPanel.add(this.lbldelayTime);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.txtDelayTime, c);
		this.contentPanel.add(this.txtDelayTime);
		/** 第五行 aps使能 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.chkAps, c);
		this.contentPanel.add(this.chkAps);
		/** 第六行 是否返回 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.isBack, c);
		this.contentPanel.add(this.isBack);
	}

	/**
	 * 保存，取消按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		buttonLayout.columnWidths = new int[] { 60, 60, 60, 6 };
		buttonLayout.columnWeights = new double[] { 1.0, 0, 0, 0 };
		buttonLayout.rowHeights = new int[] { 60 };
		buttonLayout.rowWeights = new double[] { 1 };
		buttonPanel.setLayout(buttonLayout);
		addComponent(buttonPanel, cancleButton, 2, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
		addComponent(buttonPanel, confirmButton, 1, 0, 0, 0, 1, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 20), GridBagConstraints.WEST, c);
	}
	
	@Override
	public void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
		
	}
	
	public void addComponentJDialog(JDialog panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
		
	}
	private void setValue(List<LoopProtectInfo> loopProtectInfos) {
		nametext.setText(loopProtectInfos.get(0).getName());
		txtWaitTime.getTxt().setText(loopProtectInfos.get(0).getWaittime()+"");
		txtDelayTime.getTxt().setText(loopProtectInfos.get(0).getDelaytime()+"");
		chkAps.setSelected(loopProtectInfos.get(0).getApsenable()== 1?true:false);
		isBack.setSelected(loopProtectInfos.get(0).getBackType()== 1?true:false);
		this.loopBefore = new LoopProtectInfo();
		this.loopBefore.setName(loopProtectInfos.get(0).getName());
		this.loopBefore.setWaittime(loopProtectInfos.get(0).getWaittime());
		this.loopBefore.setDelaytime(loopProtectInfos.get(0).getDelaytime());
		this.loopBefore.setApsenable(loopProtectInfos.get(0).getApsenable());
		this.loopBefore.setBackType(loopProtectInfos.get(0).getBackType());
		this.loopBefore.setActiveStatus(loopProtectInfos.get(0).getActiveStatus());
	}

	public void confrimAction() throws Exception {
		for (int i = 0; i < loopProtectInfoList.size(); i++) {
			loopProtectInfoList.get(i).setApsenable(chkAps.isSelected() ? 1 : 0);
			loopProtectInfoList.get(i).setBackType(isBack.isSelected()?1:0);
			
			// 验证名称是否存在
			String beforeName = "";
			if (loopProtectInfoList.get(i).getId() != 0) {
				beforeName = loopProtectInfoList.get(i).getName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.WRAPPINGPROTECT.getValue(), nametext.getText(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return ;
			}
			
			loopProtectInfoList.get(i).setName(nametext.getText());
			try {
				loopProtectInfoList.get(i).setWaittime(Integer.parseInt(txtWaitTime.getTxtData()));
				loopProtectInfoList.get(i).setDelaytime(Integer.parseInt(txtDelayTime.getTxtData()));
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			}
		}
		DispatchUtil wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
		String result = null;
		WrappingProtectService_MB service = null;
		try {
			result = wrappingDispatch.excuteUpdate(loopProtectInfoList);
			DialogBoxUtil.succeedDialog(this, result);
			//添加日志记录
			service = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			LoopProtectInfo condition = new LoopProtectInfo();
			condition.setLoopId(loopProtectInfoList.get(0).getLoopId());
			List<LoopProtectInfo> dbList = service.select(condition);
			for (LoopProtectInfo db : dbList) {
				AddOperateLog.insertOperLog(confirmButton, EOperationLogType.LOOPPROTECTUPDATE.getValue(), result, this.loopBefore, 
						loopProtectInfoList.get(0), db.getSiteId(), loopProtectInfoList.get(0).getName(), "wrapping");
			}
			this.loopProtectPanel.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	public JLabel getNamelabel() {
		return namelabel;
	}

	public void setNamelabel(JLabel namelabel) {
		this.namelabel = namelabel;
	}

	public PtnTextField getNametext() {
		return nametext;
	}

	public void setNametext(PtnTextField nametext) {
		this.nametext = nametext;
	}

	public JLabel getLblwaitTime() {
		return lblwaitTime;
	}

	public void setLblwaitTime(JLabel lblwaitTime) {
		this.lblwaitTime = lblwaitTime;
	}

	public JLabel getLbldelayTime() {
		return lbldelayTime;
	}

	public void setLbldelayTime(JLabel lbldelayTime) {
		this.lbldelayTime = lbldelayTime;
	}

//	public PtnTextField getTxtWaitTime() {
//		return txtWaitTime;
//	}
//
//	public void setTxtWaitTime(PtnTextField txtWaitTime) {
//		this.txtWaitTime = txtWaitTime;
//	}
//
//	public PtnTextField getTxtDelayTime() {
//		return txtDelayTime;
//	}
//
//	public void setTxtDelayTime(PtnTextField txtDelayTime) {
//		this.txtDelayTime = txtDelayTime;
//	}

	public JCheckBox getChkAps() {
		return chkAps;
	}

	public PtnSpinner getTxtWaitTime() {
		return txtWaitTime;
	}

	public void setTxtWaitTime(PtnSpinner txtWaitTime) {
		this.txtWaitTime = txtWaitTime;
	}

	public PtnSpinner getTxtDelayTime() {
		return txtDelayTime;
	}

	public void setTxtDelayTime(PtnSpinner txtDelayTime) {
		this.txtDelayTime = txtDelayTime;
	}

	public void setChkAps(JCheckBox chkAps) {
		this.chkAps = chkAps;
	}

	public JCheckBox getIsBack() {
		return isBack;
	}

	public void setIsBack(JCheckBox isBack) {
		this.isBack = isBack;
	}

}
