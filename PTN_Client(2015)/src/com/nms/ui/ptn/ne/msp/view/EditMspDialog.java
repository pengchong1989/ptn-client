package com.nms.ui.ptn.ne.msp.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortStm;
import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.path.protect.MspProtect;
import com.nms.db.enums.EActiveStatus;
import com.nms.model.equipment.port.PortStmService_MB;
import com.nms.model.ptn.SiteRoateService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.BusinessIdException;
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
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 * 编辑MSP保护对话框
 * 
 * @author kk
 * 
 */
public class EditMspDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6690249450160422054L;
	/**
	 * 每行的行高
	 */
	private final int LINEHEIGHT = 40;
	/**
	 * 主界面传入的bean
	 */
	private MspProtect mspProtect = null;

	/**
	 * 主界面传入的对象
	 */
	private MspPanel mspPanel = null;

	/**
	 * 创建一个新的实例
	 * 
	 * @param mspProtect
	 *            bean对象。 当为null时 为新建操作
	 * @param mspPanel
	 *            主界面。关闭窗口前 做刷新用
	 */
	public EditMspDialog(MspProtect mspProtect, MspPanel mspPanel) {

		try {
			this.mspProtect = mspProtect;
			this.mspPanel = mspPanel;
			this.initComponent();
			this.setLayout();
			this.initData();
			this.bindingData();
			this.addListener();
			UiUtil.showWindow(this, 400, 500);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 修改时，绑定页面数据
	 * 
	 * @throws Exception
	 */
	private void bindingData() throws Exception {

		if (null != this.mspProtect) {
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, this.mspProtect.getProtectType() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbModel, this.mspProtect.getRecoveryMode() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbWorkPort, this.mspProtect.getWorkPortId() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbProtectPort, this.mspProtect.getProtectPortId() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbSFPriority, this.mspProtect.getSfPriority() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbSDPriority, this.mspProtect.getSdPriority() + "");

			this.spinnerWaitTime.getTxt().setText(this.mspProtect.getWaitTime() + "");
			this.spinnerDelayTime.getTxt().setText(this.mspProtect.getDelayTime() + "");

			this.chkApsEnable.setSelected(this.mspProtect.getApsEnable() == 0 ? false : true);
			this.chkSDEnable.setSelected(this.mspProtect.getSdEnable() == 0 ? false : true);
		}

	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		// 取消按钮事件
		this.btnCanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 关闭窗口
				dispose();
			}

		});

		// 保存按钮事件
		this.btnSave.addActionListener(new MyActionListener() {

			/**
			 * 核对数据
			 */
			@Override
			public boolean checking() {
				return btnSaveChecking();
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSaveAction();
			}

		});
	}

	/**
	 * 初始化下拉列表数据
	 * 
	 * @throws Exception
	 */
	private void initData() throws Exception {
		try {
			super.getComboBoxDataUtil().comboBoxData(this.cmbType, "MSPPROTECTTYPE");
			super.getComboBoxDataUtil().comboBoxData(this.cmbModel, "RECOVERYMODE");
			super.getComboBoxDataUtil().comboBoxData(this.cmbSFPriority, "HIGHLOW");
			super.getComboBoxDataUtil().comboBoxData(this.cmbSDPriority, "HIGHLOW");
			this.initPortData(this.cmbWorkPort);
			this.initPortData(this.cmbProtectPort);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 绑定端口数据
	 * 
	 * @throws Exception
	 */
	private void initPortData(JComboBox comboBox) throws Exception {
		PortStmService_MB portStmService = null;
		List<PortStm> portStmList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			// 查询此网元下的所有stm口
			portStmService = (PortStmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTSTM);
			portStmList = portStmService.queryBySiteid(ConstantUtil.siteId);

			defaultComboBoxModel = new DefaultComboBoxModel();
			if (null != portStmList && portStmList.size() > 0) {
				// 遍历所有stm口。 加到model中
				for (PortStm portStm : portStmList) {
					defaultComboBoxModel.addElement(new ControlKeyValue(portStm.getPortid() + "", portStm.getName(), portStm));
				}
			}

			// 把model添加到下拉列表中
			comboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portStmService);
			portStmList = null;
			defaultComboBoxModel = null;
		}
	}

	/**
	 * 初始化控件
	 * 
	 * @throws Exception
	 */
	private void initComponent() throws Exception {
		if (null != mspProtect) {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_MSP));
		} else {
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_MSP));
		}
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT_TYPE));
		this.lblModel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RECOVERY_MODE));
		this.lblWorkPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WORK_PORT));
		this.lblProtectPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT_PORT));
		this.lblWaitTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.lblDelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));
		this.lblSFPriority = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SFPRIORITY));
		this.lblSDPriority = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SDPRIORITY));
		this.lblApsEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.lblSDEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SDALARM_ROTATE));

		this.cmbType = new JComboBox();
		this.cmbModel = new JComboBox();
		this.cmbWorkPort = new JComboBox();
		this.cmbProtectPort = new JComboBox();
		this.spinnerWaitTime = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
		this.spinnerDelayTime = new PtnSpinner(PtnSpinner.TYPE_DELAYTIME);
		this.cmbSFPriority = new JComboBox();
		this.cmbSDPriority = new JComboBox();
		this.chkApsEnable = new JCheckBox();
		this.chkSDEnable = new JCheckBox();

		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), true);
		this.btnCanel = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL), false);

		this.panelButton = new JPanel();
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setButtonLayout();

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 150, 250 };
		componentLayout.columnWeights = new double[] { 0, 0.1 };
		componentLayout.rowHeights = new int[] { LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT, LINEHEIGHT };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 10, 0, 10);

		// 第一行 保护类型
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.lblType, c);
		this.add(this.lblType);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbType, c);
		this.add(this.cmbType);

		// 第二行 恢复模式
		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.lblModel, c);
		this.add(this.lblModel);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbModel, c);
		this.add(this.cmbModel);

		// 第三行 工作端口
		c.gridx = 0;
		c.gridy = 2;
		componentLayout.setConstraints(this.lblWorkPort, c);
		this.add(this.lblWorkPort);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbWorkPort, c);
		this.add(this.cmbWorkPort);

		// 第四行 保护端口
		c.gridx = 0;
		c.gridy = 3;
		componentLayout.setConstraints(this.lblProtectPort, c);
		this.add(this.lblProtectPort);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbProtectPort, c);
		this.add(this.cmbProtectPort);

		// 第五行 恢复等待时间
		c.gridx = 0;
		c.gridy = 4;
		componentLayout.setConstraints(this.lblWaitTime, c);
		this.add(this.lblWaitTime);
		c.gridx = 1;
		componentLayout.setConstraints(this.spinnerWaitTime, c);
		this.add(this.spinnerWaitTime);

		// 第六行 延迟时间
		c.gridx = 0;
		c.gridy = 5;
		componentLayout.setConstraints(this.lblDelayTime, c);
		this.add(this.lblDelayTime);
		c.gridx = 1;
		componentLayout.setConstraints(this.spinnerDelayTime, c);
		this.add(this.spinnerDelayTime);

		// 第七行 SF优先级
		c.gridx = 0;
		c.gridy = 6;
		componentLayout.setConstraints(this.lblSFPriority, c);
		this.add(this.lblSFPriority);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbSFPriority, c);
		this.add(this.cmbSFPriority);

		// 第八行 SD优先级
		c.gridx = 0;
		c.gridy = 7;
		componentLayout.setConstraints(this.lblSDPriority, c);
		this.add(this.lblSDPriority);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbSDPriority, c);
		this.add(this.cmbSDPriority);

		// 第九行 APS使能
		c.gridx = 0;
		c.gridy = 8;
		componentLayout.setConstraints(this.lblApsEnable, c);
		this.add(this.lblApsEnable);
		c.gridx = 1;
		componentLayout.setConstraints(this.chkApsEnable, c);
		this.add(this.chkApsEnable);

		// 第十行 使能SD告警触发倒换
		c.gridx = 0;
		c.gridy = 9;
		componentLayout.setConstraints(this.lblSDEnable, c);
		this.add(this.lblSDEnable);
		c.gridx = 1;
		componentLayout.setConstraints(this.chkSDEnable, c);
		this.add(this.chkSDEnable);

		// 第十行 使能SD告警触发倒换
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.panelButton, c);
		this.add(this.panelButton);
	}

	/**
	 * 设置按钮panel布局
	 */
	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 260, 70, 70 };
		componentLayout.columnWeights = new double[] { 0.1, 0, 0 };
		componentLayout.rowHeights = new int[] { LINEHEIGHT };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelButton.setLayout(componentLayout);

		// 按钮布局
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		componentLayout.setConstraints(this.btnSave, c);
		this.panelButton.add(this.btnSave);
		c.gridx = 2;
		componentLayout.setConstraints(this.btnCanel, c);
		this.panelButton.add(this.btnCanel);

	}

	/**
	 * 保存按钮事件
	 */
	private void btnSaveAction() {
		String result = null;
		DispatchUtil mspDispatch = null;
		SiteRoate siteRoate =null;
		SiteRoateService_MB siteRoateService=null;
		try {
			
			// 给对象赋值
			this.setMspProtect();
			//给msp倒换对象赋值
			siteRoate=new SiteRoate();
			siteRoateService=(SiteRoateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITEROATE);
			siteRoate.setType("msp");
			siteRoate.setSiteId(this.mspProtect.getSiteId());
			siteRoate.setTypeId(this.mspProtect.getId());
			
			mspDispatch=new DispatchUtil(RmiKeys.RMI_MSPPROTECT);
			if(0==this.mspProtect.getId()){
				result = mspDispatch.excuteInsert(this.mspProtect);
//				//添加倒换信息记录
//				
//				siteRoateService.insert(siteRoate);
			}else{
				result = mspDispatch.excuteUpdate(this.mspProtect);
			}
			DialogBoxUtil.succeedDialog(this.mspPanel, result);
			this.mspPanel.getController().refresh();
			this.dispose();
		} catch (BusinessIdException e) {
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_BUSINESSID_NULL));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteRoateService);
		}

	}

	/**
	 * 保存前的验证
	 * 
	 * @throws Exception
	 */
	private boolean btnSaveChecking() {
		ControlKeyValue controlKeyValue_work = null;
		ControlKeyValue controlKeyValue_protect = null;
		boolean flag = true;
		try {
			// 验证是否选择了工作、保护端口
			if (this.cmbWorkPort.getSelectedIndex() == -1 || this.cmbProtectPort.getSelectedIndex() == -1) {
				DialogBoxUtil.errorDialog(this.mspPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PORT));
				flag = false;
			} else {
				// 验证工作、保护端口是否相同
				controlKeyValue_work = (ControlKeyValue) this.cmbWorkPort.getSelectedItem();
				controlKeyValue_protect = (ControlKeyValue) this.cmbProtectPort.getSelectedItem();
				if (controlKeyValue_work.getId().equals(controlKeyValue_protect.getId())) {
					DialogBoxUtil.errorDialog(this.mspPanel, ResourceUtil.srcStr(StringKeysTip.TIP_JOB_PROJECT_PORT));
					flag = false;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			controlKeyValue_work = null;
			controlKeyValue_protect = null;
		}
		return flag;
	}

	/**
	 * 把界面上的值添加到mspProtect对象中
	 * 
	 * @throws Exception
	 */
	private void setMspProtect() throws Exception {
		ControlKeyValue controlKeyValue_work = null;
		ControlKeyValue controlKeyValue_protect = null;
		ControlKeyValue controlKeyValue_type = null;
		ControlKeyValue controlKeyValue_model = null;
		ControlKeyValue controlKeyValue_sf = null;
		ControlKeyValue controlKeyValue_sd = null;
		try {
		
			// 各个下拉列表选中的对象
			controlKeyValue_work = (ControlKeyValue) this.cmbWorkPort.getSelectedItem();
			controlKeyValue_protect = (ControlKeyValue) this.cmbProtectPort.getSelectedItem();
			controlKeyValue_type = (ControlKeyValue) this.cmbType.getSelectedItem();
			controlKeyValue_model = (ControlKeyValue) this.cmbModel.getSelectedItem();
			controlKeyValue_sf = (ControlKeyValue) this.cmbSFPriority.getSelectedItem();
			controlKeyValue_sd = (ControlKeyValue) this.cmbSDPriority.getSelectedItem();
			if(controlKeyValue_work.getId()==controlKeyValue_protect.getId()){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_JOB_PROJECT_PORT));
				return;
			}
			// 如果msp对象为null 新创建一个， 当新增操作时，会为null
			if (null == this.mspProtect) {
				this.mspProtect = new MspProtect();
				this.mspProtect.setMspStatus(EActiveStatus.ACTIVITY.getValue());
			}
			this.mspProtect.setProtectType(Integer.parseInt(controlKeyValue_type.getId()));
			this.mspProtect.setRecoveryMode(Integer.parseInt(controlKeyValue_model.getId()));
			this.mspProtect.setWorkPortId(Integer.parseInt(controlKeyValue_work.getId()));
			this.mspProtect.setProtectPortId(Integer.parseInt(controlKeyValue_protect.getId()));
			this.mspProtect.setWaitTime(Integer.parseInt(this.spinnerWaitTime.getTxtData()));
			this.mspProtect.setDelayTime(Integer.parseInt(this.spinnerDelayTime.getTxtData()));
			this.mspProtect.setSfPriority(Integer.parseInt(controlKeyValue_sf.getId()));
			this.mspProtect.setSdPriority(Integer.parseInt(controlKeyValue_sd.getId()));
			this.mspProtect.setApsEnable(this.chkApsEnable.isSelected() ? 1 : 0);
			this.mspProtect.setSdEnable(this.chkSDEnable.isSelected() ? 1 : 0);
			this.mspProtect.setProtectStatus("");
			this.mspProtect.setNowWorkPortId(this.mspProtect.getWorkPortId());
			this.mspProtect.setSiteId(ConstantUtil.siteId);
		} catch (Exception e) {
			throw e;
		} finally {
			controlKeyValue_work = null;
			controlKeyValue_protect = null;
			controlKeyValue_type = null;
			controlKeyValue_model = null;
			controlKeyValue_sf = null;
			controlKeyValue_sd = null;
		}
	}

	private JLabel lblType; // 保护类型label
	private JComboBox cmbType; // 保护类型
	private JLabel lblModel; // 恢复模式label
	private JComboBox cmbModel; // 恢复类型
	private JLabel lblWorkPort; // 工作端口label
	private JComboBox cmbWorkPort; // 工作端口
	private JLabel lblProtectPort; // 保护端口label
	private JComboBox cmbProtectPort; // 保护端口
	private JLabel lblWaitTime; // 恢复等待时间label
	private PtnSpinner spinnerWaitTime; // 恢复等待时间控件
	private JLabel lblDelayTime; // 延迟时间label
	private PtnSpinner spinnerDelayTime;// 延迟时间控件
	private JLabel lblSFPriority; // SF优先级label
	private JComboBox cmbSFPriority; // SF优先级
	private JLabel lblSDPriority; // SD优先级label
	private JComboBox cmbSDPriority; // SD优先级
	private JLabel lblApsEnable; // APS使能label
	private JCheckBox chkApsEnable; // APS使能
	private JLabel lblSDEnable; // SD使能label
	private JCheckBox chkSDEnable; // SD使能
	private PtnButton btnSave; // 保存按钮
	private PtnButton btnCanel; // 取消按钮
	private JPanel panelButton; // button的面板
}
