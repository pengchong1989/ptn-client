package com.nms.ui.ptn.ne.msp.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.path.protect.MspProtect;
import com.nms.model.ptn.SiteRoateService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.ne.tunnel.view.CXRoatePanel;
/**
 * msp倒换页面
 * @author dzy
 *    
 *			修改：sy
 *            整理倒换界面，统一使用陈晓倒换界面 CXRoatePanel
 *             统一入库 site_roate
 *							
 */          
public class MspProtectRotateDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5753079965540205523L;
	private MspProtect mspProtect; 
	private MspPanel mspPanel;
	private JButton btnSave; //保存
	private JButton btnCanel;//取消
	private JPanel panelButton; //按钮面板
	private JPanel panel_a; //主面板
	private CXRoatePanel cxPanel;
	/**
	 * 
	 * 创建一个新的实例 
	 * 
	 * @param msp对象
	 *            倒换的msp对象
	 * @param mspPanel
	 *            msp面板
	 */
	public MspProtectRotateDialog(MspProtect mspProtect, MspPanel mspPanel) {
		try {
			this.mspProtect = mspProtect;
			this.mspPanel = mspPanel;
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER));
			this.initComponent();
			//setRainValue();
			this.setLayout();
			this.addListener();

			UiUtil.showWindow(this, 330, 300);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	/**
	 * 按钮添加事件
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void addListener() {
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		this.btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSaveListener();
			}
		});
	}

	/**
	 * 保存按钮监听
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void btnSaveListener() {
		String result = null;
		DispatchUtil mspDispatch = null;
		int rotateOrder  ;
		Enumeration<AbstractButton> elements = null;
		JRadioButton radioButton = null;
		SiteRoateService_MB siteRoateService=null;
		SiteRoate siteRoate=null;
		try {
			siteRoateService = (SiteRoateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITEROATE);
			
			// 遍历所有radiobutton 获取选中的button
			elements = cxPanel.getButtonGroup().getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					break;
				}
			}
			if (null != radioButton) {
				//倒换界面有选中倒换命令
				rotateOrder = Integer.parseInt(radioButton.getName());
				this.mspProtect.setRotateOrder(rotateOrder);
				mspDispatch=new DispatchUtil(RmiKeys.RMI_MSPPROTECT);
				result = mspDispatch.excuteUpdate(this.mspProtect);
				/**
				 * 更新设备倒换命令成功
				 *  则 更新数据库（倒换命令表）
				 */
				if(result.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
					siteRoate=new SiteRoate();
					siteRoate.setType("msp");
					siteRoate.setTypeId(this.mspProtect.getId());
					siteRoate.setSiteId(this.mspProtect.getSiteId());
					siteRoate.setRoate(rotateOrder);
					siteRoateService.update(siteRoate);
				}
				DialogBoxUtil.succeedDialog(this, result);
				this.mspPanel.getController().refresh();
				this.dispose();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteRoateService);
		}
	}

	/**
	 * 初始化控件
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void initComponent() { 
		cxPanel=new CXRoatePanel(this.mspProtect,true);;
		this.btnSave = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.panelButton = new JPanel();
		this.panel_a = new JPanel();

	}
	/**
	 * 设置主页面布局 a端
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void setLayout() {
		this.setLayout_main();
		this.setLayoutButton();

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {300};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 230, 30 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.panel_a, c);
		this.add(this.panel_a);

		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.panelButton, c);
		this.add(this.panelButton);

	}
	/**
	 * 设置主页面布局 a端
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void setLayout_main() {

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {100,200};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 200, 30 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0 };
		this.panel_a.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.cxPanel, c);
		this.panel_a.add(this.cxPanel);
	}
	/***
	 * 按钮布局
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void setLayoutButton() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 170, 70 ,70};
		componentLayout.columnWeights = new double[] { 0, 0 };
		componentLayout.rowHeights = new int[] { 30 };
		componentLayout.rowWeights = new double[] {};
		this.panelButton.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.btnSave, c);
		this.panelButton.add(this.btnSave);

		c.gridx = 2;
		componentLayout.setConstraints(this.btnCanel, c);
		this.panelButton.add(this.btnCanel);
	}


}