package com.nms.ui.ptn.ne.tunnel.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.path.protect.DualProtect;
import com.nms.db.bean.ptn.path.protect.MspProtect;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.ERotateType;
import com.nms.model.ptn.SiteRoateService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 *   陈晓的倒换界面（所有陈晓倒换都用此Panel）
 *     注意：  添加此panel 时
 *       一：新建某个类型（此类型有倒换功能）时，要新建倒换命令 ，及即：向site_roate表中插入相应数据 （默认到换命令-1 ）
 *       二： 调整倒换时，要及时更新倒换命令，即：更新site_roate表
 * @author sy
 *
 */
public class CXRoatePanel extends JPanel {

	private static final long serialVersionUID = -413313310276876404L;
	/**
	 * true A端（其他页面不受影响）
	 * false  Z端
	 */
	private boolean flag;
	private Object object;

	/**
	 * 
	 * 创建一个新的实例 RotateNodeDialog.
	 *    tunnel的 倒换
	 * @param flag
	 *         true    A 端 倒换界面(如msp等其他倒换页面不受此影响 ：即flag 任意传值)
	 *         false   Z 端倒换界面
	 */
	public CXRoatePanel(Object object,boolean flag) {
		try {
			this.object=object;
			this.flag=flag;
			this.initComponent();
			setRainValue();
			this.setLayout();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 设置 页面 的 倒换命令值
	 */
	private void setRainValue() {
		SiteRoateService_MB siteRoateServiceMB = null;
		SiteRoate siteRoate = null;
		int roate = 0;
		try {
			siteRoateServiceMB = (SiteRoateService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.SITEROATE);
			/**
			 * 将传人参数object转为  别的类型
			 */
			siteRoate=getObject();
			siteRoate = siteRoateServiceMB.select(siteRoate);
			//确认实例化陈晓Pnael 时传的参数能在数据库中查到
			if(siteRoate!=null){
				roate = siteRoate.getRoate();
				//倒换命令有值，则刷新倒换界面时，判断显示
				if(roate>-1){			
					if (roate == ERotateType.FORCESWORK.getValue()) {
						this.chkForceJob.setSelected(true);
					} else if (roate == ERotateType.FORCESPRO.getValue()) {
						this.chkForcePro.setSelected(true);
					} else if (roate == ERotateType.MANUALWORK.getValue()) {
						this.chkManpowerJob.setSelected(true);
					} else if (roate == ERotateType.MANUALPRO.getValue()) {
						chkManpowerPro.setSelected(true);
					} else if (roate == ERotateType.LOCK.getValue()) {
						this.chkShutting.setSelected(true);
						this.chkForceJob.setEnabled(false);
						this.chkForcePro.setEnabled(false);
						this.chkManpowerJob.setEnabled(false);
						this.chkManpowerPro.setEnabled(false);

					} else if (roate == ERotateType.CLEAR.getValue()) {
						this.buttonGroup.clearSelection();
						this.chkForceJob.setEnabled(true);
						this.chkForcePro.setEnabled(true);
						this.chkManpowerJob.setEnabled(true);
						this.chkManpowerPro.setEnabled(true);
					}
				}			
			}	
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteRoateServiceMB);
		}
	}
	/**
	 * 通过不同界面调用此Panel 
	 *   将传参 的部分属性 赋值给 倒换对象
	 * @return siteRoate
	 *      倒换对象
	 */
	private SiteRoate getObject(){
		SiteRoate siteRoate=null;
		Tunnel tunnel=null;
		MspProtect msp=null;
		DualProtect dual=null;
		try{
			siteRoate=new SiteRoate();
			if(this.object instanceof Tunnel){//  倒换Tunnel时，
				tunnel=(Tunnel) this.object;
				if (this.flag) {
					//A端
					siteRoate.setSiteId(tunnel.getASiteId());
				} else {
					//Z 端
					siteRoate.setSiteId(tunnel.getZSiteId());
				}
				siteRoate.setType("tunnel");
				siteRoate.setTypeId(tunnel.getProtectTunnelId());
			}else if(this.object instanceof MspProtect){//倒换 msp 
				msp=(MspProtect) this.object;
				siteRoate.setSiteId(msp.getSiteId());
				siteRoate.setType("msp");
				siteRoate.setTypeId(msp.getId());			
			}else if(this.object instanceof DualProtect){//倒换双规保护
				dual=(DualProtect) this.object;
				siteRoate.setSiteId(dual.getSiteId());
				siteRoate.setType("msp");
				siteRoate.setTypeId(dual.getId());			
			}			
		}catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			msp=null;
			tunnel=null;
			dual=null;
		}
		return siteRoate;
		
	}
	/**
	 * 初始化控件
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void initComponent() {
		this.chkForceJob = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_CHK_FORCEJOB));
		this.chkForcePro = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_CHK_FORCEPRO));
		this.chkManpowerJob = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_CHK_MANPOWERJOB));
		this.chkManpowerPro = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_CHK_MANPOWERPRO));
		this.chkShutting = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_CHK_SHUTTING));
		this.chkClear = new JRadioButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CLEAR));
		this.txtJoblsp = new PtnTextField();
		this.txtJoblsp.setEditable(false);
		this.panelOrder = new JPanel();
		this.panelOrder.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER)));
		this.buttonGroup = new ButtonGroup();
		this.buttonGroup.add(chkForceJob);
		this.buttonGroup.add(chkForcePro);
		this.buttonGroup.add(chkManpowerJob);
		this.buttonGroup.add(chkManpowerPro);		
		this.buttonGroup.add(chkShutting);
		this.buttonGroup.add(chkClear);
		//this.chkForceJob.setSelected(true);
		// 给单选按钮赋值，取值时用
		this.chkForceJob.setName(ERotateType.FORCESWORK.getValue() + "");
		this.chkForcePro.setName(ERotateType.FORCESPRO.getValue() + "");
		this.chkManpowerJob.setName(ERotateType.MANUALWORK.getValue() + "");
		this.chkManpowerPro.setName(ERotateType.MANUALPRO.getValue() + "");
		this.chkShutting.setName(ERotateType.LOCK.getValue() + "");
		this.chkClear.setName(ERotateType.CLEAR.getValue() + "");
	}

	/**
	 * 设置主页面布局
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void setLayout() {
		this.setLayoutPanel();
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 200 };
		componentLayout.columnWeights = new double[] { 0.1 };
		componentLayout.rowHeights = new int[] { 120};
		componentLayout.rowWeights = new double[] { 0.1 };
		this.setLayout(componentLayout);
		Insets s = new Insets(5, 5, 5, 5);
		GridBagConstraints c = new GridBagConstraints();
		addComponent(this, panelOrder, 0, 0, 0.1, 0.1, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
	}

	/**
	 * 设置PANEL布局
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void setLayoutPanel() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 100, 100 };
		componentLayout.columnWeights = new double[] { 0.1, 0.1};
		componentLayout.rowHeights = new int[] { 10,30, 30, 30, 10 };
		componentLayout.rowWeights = new double[] { 0.0, 0.1, 0.1, 0.1, 0.1 };
		this.panelOrder.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		Insets s = new Insets(5, 5, 5, 5);
		
		addComponent(panelOrder, chkForceJob, 0, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
		addComponent(panelOrder, chkForcePro, 1, 1, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
		addComponent(panelOrder, chkManpowerJob, 0, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
		addComponent(panelOrder, chkManpowerPro, 1, 2, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
		addComponent(panelOrder, chkClear, 0, 3, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
		addComponent(panelOrder, chkShutting, 1, 3, 0.1, 0, 1, 1, GridBagConstraints.BOTH, s, GridBagConstraints.CENTER, c);
	}
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

	public JPanel getPanelOrder() {
		return panelOrder;
	}

	public void setPanelOrder(JPanel panelOrder) {
		this.panelOrder = panelOrder;
	}

	public ButtonGroup getButtonGroup() {
		return buttonGroup;
	}

	public void setButtonGroup(ButtonGroup buttonGroup) {
		this.buttonGroup = buttonGroup;
	}

	private JRadioButton chkForceJob;
	private JRadioButton chkForcePro;
	private JRadioButton chkManpowerJob;
	private JRadioButton chkManpowerPro;
	private JRadioButton chkShutting;
	private JRadioButton chkClear;
	private JTextField txtJoblsp;
	private JPanel panelOrder;
	private ButtonGroup buttonGroup;

}
