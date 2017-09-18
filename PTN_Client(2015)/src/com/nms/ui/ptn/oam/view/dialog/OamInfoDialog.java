package com.nms.ui.ptn.oam.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.enums.EServiceType;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.oam.controller.OamController;
import com.nms.ui.ptn.oam.view.OamInfoPanel;

/**
 * OAM配置页面
 * @author dzy
 *
 */
public class OamInfoDialog extends PtnDialog {
	private static final long serialVersionUID = 8094259435277719184L;
	private int DialogWidth = 500; 	//根据业务控制高度
	private String busiType; 	//业务类型
	private Object obj; 	//业务对象
	private OamController controller; //控制器
	private int isSingle; //业务区分  0=网络侧，1=单网络侧
	private int DialogHeight; //高度
	
	/**
	 * 创建一个新的实例 单网元
	 * @param oamInfo
	 * 				oam对象
	 * @param busiType
	 * 				业务类型
	 * @param modal
	 * 
	 * @param isSingle
	 * 				 业务区分  0=网络侧，1=单网络侧
	
	 */
	public OamInfoDialog(OamInfo oamInfo, String busiType, boolean modal, JDialog dialog) {
		try {
			this.isSingle =1 ;
			this.setModal(modal);
			this.obj = oamInfo;
			this.busiType = busiType;
			this.initComponent(dialog);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 创建一个新的实例 网络层
	 * @param obj oamInfo	对象。 当为null时 为新建操作
	 * @param busiType	业务类型
	 * @param isSingle 业务区分  0=网络侧，1=单网络侧
	 * @param modal
	 */
	public OamInfoDialog(Object obj, String busiType, int isSingle, boolean modal, JDialog dialog){
		try {
			this.setModal(modal);
			this.busiType = busiType;
			this.isSingle = 0;
			this.obj = obj;
			this.initComponent(dialog);
			this.setLayout();
			this.getController().init(dialog);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 初始化控件
	 * @throws Exception
	 */
	public void initComponent(JDialog dialog) throws Exception {
		this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONFIGOAM));
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), true);
		this.btnCanel = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL), false);
		this.panelButton = new JPanel();
		this.oamPanel = new OamInfoPanel(this, this.busiType, "work", isSingle);
		mainPanel = new JTabbedPane();
		mainPanel.add(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_SERVICE_PATH), this.oamPanel);
		//网络侧TUNNEL 添加保护面板
		if(0 == this.isSingle && EServiceType.TUNNEL.toString().equals(this.busiType)){
			this.oamProtectPanel = new OamInfoPanel(this, this.busiType,"protect",0);
			mainPanel.add(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_PROTECT_PATH), this.oamProtectPanel);
		}
		this.DialogHeight = this.getOamPanel().getHeight()+20;
		if(1 == this.isSingle)
			this.DialogHeight+=20;
		this.controller = new OamController(this,dialog);
	}
	
	/**
	 * 设置布局
	 */
	public void setLayout() {
		this.setButtonLayout();

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { DialogWidth };
		componentLayout.columnWeights = new double[] { 0, 0.1 };
		componentLayout.rowHeights = new int[] {this.getOamPanel().getHeight(),50 };
		componentLayout.rowWeights = new double[] { 0.0, 0.1, 0.0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 5, 0, 5);

		// 第1行 保护类型
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.mainPanel, c);
		this.add(this.mainPanel);
		
		// 第2行 按钮面板
		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.panelButton, c);
		this.add(this.panelButton);
		
	}
	
	/**
	 * 设置按钮panel布局
	 */
	public void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 260, 70, 70 };
		componentLayout.columnWeights = new double[] { 0.1, 0, 0 };
		componentLayout.rowHeights = new int[] { 50 };
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
	
	public JPanel panelButton;	// button的面板
	public PtnButton btnSave;	// 保存按钮
	public JButton btnCanel; // 取消按钮
	public OamInfoPanel oamPanel;	//OAM信息面板
	private OamInfoPanel oamProtectPanel;	//OAM保护侧面板
	public DefaultTableModel mipTableModel;	//mip table模板
	public JTable mipTable;	//mip table
	public JScrollPane scrollPane; //mip 面板
	private JTabbedPane mainPanel; //主面板

	public PtnButton getBtnSave() {
		return btnSave;
	}

	public OamInfoPanel getOamPanel() {
		return oamPanel;
	}

	public OamInfoPanel getOamProtectPanel() {
		return oamProtectPanel;
	}

	public Object getObj() {
		return obj;
	}
	
	public JTabbedPane getMainPanel() {
		return mainPanel;
	}
	
	public OamController getController() {
		return controller;
	}
	
	public String getBusiType() {
		return busiType;
	}

	public int getIsSingle() {
		return isSingle;
	}
	
	public JButton getBtnCanel() {
		return btnCanel;
	}
	
	public int getDialogHeight() {
		return DialogHeight;
	}
	
	public int getDialogWidth() {
		return DialogWidth;
	}
	
}
