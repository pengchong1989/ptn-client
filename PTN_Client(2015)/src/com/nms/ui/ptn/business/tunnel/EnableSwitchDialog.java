package com.nms.ui.ptn.business.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
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
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * 使能选择对话框
 * @author guoqc
 *
 */
public class EnableSwitchDialog extends PtnDialog {
	private static final long serialVersionUID = -5904752858478590697L;
	private JRadioButton enableSwitchBtn;//使能
	private JRadioButton unEnableSwitchBtn;//不使能
	private ButtonGroup switchBtnGroup;
	private JRadioButton mainBtn;//主用
	private JRadioButton reserveBtn;//备用
	private PtnButton saveBtn;//确认
	private JButton cancelBtn;//取消
	private JPanel buttonPanel;
	private List<Tunnel> tunnelList = null;//被选中的tunnel
	private String type = "";//操作类型 aps/oam/cv/是否返回
	
	public EnableSwitchDialog(List<Tunnel> allSelect, String type) {
		this.tunnelList = allSelect;
		this.type = type;
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_BATCH_UPDATE_ATTR));
		this.initComponent();
		this.setLayout();
		this.addListener();
		if(this.type.equals("isReverse")){
			UiUtil.showWindow(this, 250, 200);
		}else{
			UiUtil.showWindow(this, 250, 230);
		}
	}

	private void initComponent() {
		this.enableSwitchBtn = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ENABLE));//使能
		this.enableSwitchBtn.setSelected(true);
		this.unEnableSwitchBtn = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_UNENABLE));//去使能
		this.switchBtnGroup = new ButtonGroup();
		this.switchBtnGroup.add(this.enableSwitchBtn);
		this.switchBtnGroup.add(this.unEnableSwitchBtn);
		this.mainBtn = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_MAINPATH));//主用
		this.mainBtn.setSelected(true);
		this.reserveBtn = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_RESERVEPATH));//备用
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.CORE_MANAGE);
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.buttonPanel = new JPanel();
	}

	private void setLayout() {
		this.setButtonLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		if(this.type.equals("isReverse")){
			componentLayout.rowHeights = new int[] {30, 30, 30, 30};
			componentLayout.columnWidths = new int[] { 50, 50, 50};
		}else{
			componentLayout.rowHeights = new int[] {30, 30, 30};
			componentLayout.columnWidths = new int[] { 50, 50, 50};
		}
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowWeights = new double[] { 0.1, 0, 0, 0 };
		this.setLayout(componentLayout);
	
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		//第一行
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(30, 10, 15, 10);
		componentLayout.setConstraints(this.enableSwitchBtn, c);
		this.add(this.enableSwitchBtn);
		c.gridx = 2;
		c.gridy = 0;
		componentLayout.setConstraints(this.unEnableSwitchBtn, c);
		this.add(this.unEnableSwitchBtn);
		if(!this.type.equals("isReverse")){
			// 第二行
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(10, 10, 15, 10);
			componentLayout.setConstraints(this.mainBtn, c);
			this.add(this.mainBtn);
			c.gridx = 2;
			c.gridy = 1;
			componentLayout.setConstraints(this.reserveBtn, c);
			this.add(this.reserveBtn);
		}
		//第三行
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		if(!this.type.equals("isReverse")){
			c.gridy = 3;
		}else{
			c.gridy = 2;
		}
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
	}
	
	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {50, 50, 50};
		componentLayout.columnWeights = new double[] {0.1, 0, 0};
		componentLayout.rowHeights = new int[] {30};
		componentLayout.rowWeights = new double[] {0};
		this.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.saveBtn, c);
		buttonPanel.add(this.saveBtn);
		c.gridx=2;
		componentLayout.setConstraints(this.cancelBtn, c);
		buttonPanel.add(this.cancelBtn);
	}

	private void addListener() {
		this.saveBtn.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}

			@Override
			public boolean checking() {
				return false;
			}
		});			
	
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	private void saveAction() {
		boolean enable = this.enableSwitchBtn.isSelected();
		boolean mainPath = this.mainBtn.isSelected();
		boolean reservePath = this.reserveBtn.isSelected();
		if(!mainPath && !reservePath){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_MAIN_OR_RESERVE));
			return;
		}
		try {
			String failResult = "";
			String result = null;
			DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			for (int i = 0; i < this.tunnelList.size(); i++) {
				Tunnel tunnel = this.tunnelList.get(i);
				List<OamInfo> oamList = tunnel.getOamList();
				if(this.type.equals("isReverse")){
					if(tunnel.getTunnelType().equals("186")){
						tunnel.setProtectBack(enable == true ? 0:1);
					}
				}else{
					this.setValue(oamList, mainPath, reservePath, this.type, enable, tunnel);
				}
				result = dispatchUtil.excuteUpdate(tunnel);
				if(result == null || !result.contains(ResultString.CONFIG_SUCCESS)){
					failResult += tunnel.getTunnelName();
					if(i < this.tunnelList.size()-1){
						failResult += ",";
					}
				}
			}
			if(failResult.equals("")){
				result = ResultString.CONFIG_SUCCESS;
			}else{
				result = failResult;
			}
			//添加日志记录
			CommonBean log = new CommonBean();
			log.setAttributeEnable(enable);
			if(!type.equals("isReverse")){
				log.setMainPathEnable(true);
				log.setReservePathEnable(true);
			}
			String operationObjName = "";
			if(type.equals("aps")){
				operationObjName = ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_APS);
			}else if(type.equals("oam")){
				operationObjName = ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ENABLE);
			}else if(type.equals("cv")){
				operationObjName = ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONNECT_TEST);
			}else{
				operationObjName = ResourceUtil.srcStr(StringKeysLbl.LBL_BACK);
			}
			for (Tunnel tunnel : tunnelList) {
				AddOperateLog.insertOperLog(saveBtn, EOperationLogType.TUNNELUPDATE.getValue(), result, 
						null, log, -1, tunnel.getTunnelName()+"_"+operationObjName, "tunnelUpdateOAM");
			}
			DialogBoxUtil.succeedDialog(this, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			this.dispose();		
		}
	}

	private void setValue(List<OamInfo> oamList, boolean mainPath, boolean reservePath, String type, boolean enable, Tunnel tunnel) {
		if(oamList != null && oamList.size() > 1 && mainPath){
			if(type.equals("aps")){
				oamList.get(0).getOamMep().setAps(enable);
				oamList.get(1).getOamMep().setAps(enable);
			}else if(type.equals("oam")){
				oamList.get(0).getOamMep().setOamEnable(enable);
				oamList.get(1).getOamMep().setOamEnable(enable);
			}else if(type.equals("cv")){
				oamList.get(0).getOamMep().setCv(enable);
				oamList.get(1).getOamMep().setCv(enable);
			}
		}
		if(tunnel.getProtectTunnel() != null){
			List<OamInfo> protectOamList = tunnel.getProtectTunnel().getOamList();
			if(protectOamList != null && protectOamList.size() > 1 && reservePath){
				if(type.equals("aps")){
					protectOamList.get(0).getOamMep().setAps(enable);
					protectOamList.get(1).getOamMep().setAps(enable);
				}else if(type.equals("oam")){
					protectOamList.get(0).getOamMep().setOamEnable(enable);
					protectOamList.get(1).getOamMep().setOamEnable(enable);
				}else if(type.equals("cv")){
					protectOamList.get(0).getOamMep().setCv(enable);
					protectOamList.get(1).getOamMep().setCv(enable);
				}
			}
		}
	}
}
