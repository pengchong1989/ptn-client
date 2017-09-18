package com.nms.ui.ptn.oam.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.OamTypeEnum;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;

/**
 * 武汉设备 OAM 配置面板
 * @author dzy
 *
 */
public class OamInfoWHPanel extends PtnPanel {
	private static final long serialVersionUID = -61622206476103381L;
	private final int LINEHEIGHT = 30; // 每行的行高
	private int[] LINEHEIGHTS = new int[13]; //高度控制数组
	private OamInfoDialog oamInfoDialog; //主窗口
	private String busiType; //业务类型
	private List<JCheckBox> componentList = new ArrayList<JCheckBox>();  //控件集合
	private String neDirect;//网元方向 区分是a、z端网元
	private int isSingle;  //业务侧类型  1=单网元 0=网络层
	private int height; //高度
	private int id; // Id
	private OamMepInfo oamMepInfo;
	/**
	 * 创建一个实例
	 * @param oamInfo oam对象 用于更新时加载数据
	 * @param busiType 业务类型
	 * @param oamInfoDialog 主窗口
	 * @param neSide 网元方向 区分是a、z端网元
	 */
	public OamInfoWHPanel(OamInfoDialog oamInfoDialog, String busiType, String neDirect, int isSingle){
		try {
			this.busiType = busiType;
			this.neDirect = neDirect;
			this.isSingle = isSingle;
			this.oamInfoDialog = oamInfoDialog;
			this.setOamInfo();
			this.initComponents();
			this.setLayOut();
			this.addActionListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * TMSOAM需要增加外层vlan和mac地址,需要给这些属性赋值
	 */
	private void setOamInfo() {
		if(busiType.equals(EServiceType.SECTION.toString())){
			Object obj = this.oamInfoDialog.getObj();
			//单站侧新建时对象为null
			if(obj == null){
				this.oamMepInfo = new OamMepInfo();
			}else{
				//修改时要判断对象是oamInfo对象,还是segment对象
				//单站侧传过来的是oamInfo
				if(this.isSingle == 1){
					if(obj instanceof OamInfo)
						this.oamMepInfo = ((OamInfo)obj).getOamMep();
				}else{
					//网络侧传过来的是segment对象
					if(obj instanceof Segment){
						Segment seg = (Segment) obj;
						List<OamInfo> oamList = seg.getOamList();
						if(oamList.size() == 0){
							this.oamMepInfo = new OamMepInfo();
						}else{
							int aSiteId = seg.getASITEID();
							int zSiteId = seg.getZSITEID();
							for (OamInfo oamInfo : oamList) {
								if(oamInfo.getOamMep().getSiteId() == aSiteId && this.neDirect.equals("a")){
									this.oamMepInfo = oamInfo.getOamMep();
								}else if(oamInfo.getOamMep().getSiteId() == zSiteId && this.neDirect.equals("z")){
									this.oamMepInfo = oamInfo.getOamMep();
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 初始化控件
	 * @throws Exception 
	 */
	public void initComponents() throws Exception {
		boolean flag = false;
		if(1 == this.isSingle)
			flag = true;
		int count;
		this.lblPromptInfo = new JLabel();
		this.lblMegICC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MEG_ICC));
		this.txtMegICC = new PtnTextField(false, PtnTextField.TYPE_STRING, 6, this.lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
		
		this.lblMegUMC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MEG_UMC));
		this.txtMegUMC = new PtnTextField(false, PtnTextField.TYPE_STRING, 6, this.lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
		
		this.lblMepId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MEPID));
		this.txtMepId = new PtnTextField();
		this.txtMepId= new PtnTextField(true, PtnTextField.TYPE_INT, 4, this.lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
		this.txtMepId.setText("2");
		this.txtMepId.setCheckingMaxValue(true);
		this.txtMepId.setMaxValue(8191);
		this.txtMepId.setCheckingMinValue(true);
		this.txtMepId.setMinValue(0);
//		if(this.isSingle == 0)
		this.lblRemoteMepId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_FAR_POINT_ID));
		this.txtRemoteMepId = new PtnTextField(flag, PtnTextField.TYPE_INT, 4, this.lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
		this.txtRemoteMepId.setText("2");
		this.txtRemoteMepId.setCheckingMaxValue(true);
		this.txtRemoteMepId.setMaxValue(8191);
		this.txtRemoteMepId.setCheckingMinValue(true);
		this.txtRemoteMepId.setMinValue(0);
		
		this.chbApsEnable= new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_APS));
		this.chbLMEnable = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_LM));
		
		this.lblTC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TC));
		this.cmbTC = new JComboBox();
		
		this.lblPWTC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_PWTC));
		this.cmbPWTC = new JComboBox();
		
		this.chbCSFEnable = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CSF));
		
		this.chbSSMEnable = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_SSM));
		
		this.chbSCCEnable = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_SCC));
		
		this.lblOutVlan = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
		this.btnOutVlan = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		
		this.oamEnable = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_ENABLE));
		//if(1 == this.isSingle){
			this.lblSourceMac = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC)); 
			this.txtSourceMac = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH,
								lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
			this.txtSourceMac.setText("00-00-00-00-00-01");
			this.lblEndMac = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PURPOSE_MAC));
			this.txtEndMac = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH,
								lblPromptInfo, this.oamInfoDialog.getBtnSave(), this.oamInfoDialog);
			this.txtEndMac.setText("00-00-00-00-00-01");
		//}
		
		//把控件放到LIST中  根据LIST 添加控件
		componentList.add(this.chbApsEnable);
		componentList.add(this.chbLMEnable);
		if(EServiceType.SECTION.toString().equals(busiType)){ //段层附加控件
			this.componentList.add(this.chbSSMEnable);
			this.componentList.add(this.chbSCCEnable);
		}else{
			this.componentList.add(this.oamEnable);
		}
		if(EServiceType.PW.toString().equals(busiType)){ //PW附加控件
			componentList.remove(this.chbApsEnable);
			componentList.add(this.chbCSFEnable);
		}
		//动态加载高度
		this.LINEHEIGHTS[0]=20;
		count = componentList.size()+5+this.isSingle;
		for (int i = 1; i < count; i++) {
			this.LINEHEIGHTS[i] = this.LINEHEIGHT;
		}
		
		this.height = 30 * count+20;
	}
	
	/**
	 * 页面布局
	 * @param BusiType
	 */
	public void setLayOut(){
		int count = 0 + this.isSingle;
		if(EServiceType.PW.toString().equals(this.busiType)){ //PW附加控件
			count = 1 +this.isSingle;
		}
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 100, 120   };
		componentLayout.columnWeights = new double[] { 0, 0.1 };
		componentLayout.rowHeights = LINEHEIGHTS;
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0 ,0, 0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 5, 0, 5);
		
		super.addComponent(this, lblPromptInfo, 0, 0, 0, 0, 2, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, lblMegICC, 0, 1, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, txtMegICC, 1, 1, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, lblMegUMC, 0, 2, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, txtMegUMC, 1, 2, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, lblMepId, 0, 3, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, txtMepId, 1, 3, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		if(1 == this.isSingle){
			super.addComponent(this, lblRemoteMepId, 0, 4, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			super.addComponent(this, txtRemoteMepId, 1, 4, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		}
		super.addComponent(this, lblTC, 0, 4+this.isSingle, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		super.addComponent(this, cmbTC, 1, 4+this.isSingle, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		
		//如果是PW业务添加PWTC 属性
		if(EServiceType.PW.toString().equals(this.busiType)){ //PW附加控件
			super.addComponent(this, lblPWTC, 0,4+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			super.addComponent(this, cmbPWTC, 1, 4+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			this.height += 20;
		}
		
		for (int i = 0; i < componentList.size(); i++) {
			super.addComponent(this, componentList.get(i), 0, i+5+count , 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
		}
		
		//如果是段层oam,添加外层vlan配置,源mac和目的mac属性
		if(EServiceType.SECTION.toString().equals(this.busiType)){ //段层附加控件
			c.insets = new Insets(5, 5, 5, 5);
			super.addComponent(this, this.lblOutVlan, 0,9+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			c.fill = GridBagConstraints.NONE;
			super.addComponent(this, this.btnOutVlan, 1,9+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			super.addComponent(this, this.lblSourceMac, 0,10+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			super.addComponent(this, this.txtSourceMac, 1,10+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			super.addComponent(this, this.lblEndMac, 0,11+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			super.addComponent(this, this.txtEndMac, 1,11+count, 0, 0, 1, 1,c.fill, c.insets,c.anchor, c);
			this.height += 80;
		}
	}
	
	/**
	 * 外层vlan配置按钮事件
	 */
	private void addActionListener() {
		this.btnOutVlan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SingleVlanConfig(oamMepInfo,oamInfoDialog);
			}
		});
	}
	
	/**
	 * 获取页面信息 
	 */
	public void getWHOam(OamInfo oamInfo){
		if(null == oamInfo.getOamMep())
		oamInfo.setOamMep(new OamMepInfo());
		oamInfo.getOamMep().setId(this.id);
		oamInfo.getOamMep().setMegIcc(this.txtMegICC.getText());
		oamInfo.getOamMep().setMegUmc(this.txtMegUMC.getText());
		oamInfo.getOamMep().setLocalMepId(Integer.parseInt(this.txtMepId.getText()));
		oamInfo.getOamMep().setRemoteMepId((Integer.parseInt(this.txtRemoteMepId.getText())));
		oamInfo.getOamMep().setAps(this.chbApsEnable.isSelected());
		oamInfo.getOamMep().setLm(this.chbLMEnable.isSelected());
		oamInfo.getOamMep().setCsfEnable(this.chbCSFEnable.isSelected());
		oamInfo.getOamMep().setLspTc(Integer.parseInt(((ControlKeyValue)this.cmbTC.getSelectedItem()).getId()));
		oamInfo.getOamMep().setSccTest(this.chbSCCEnable.isSelected());
		oamInfo.getOamMep().setSsm(this.chbSSMEnable.isSelected());
		oamInfo.getOamMep().setOamEnable(this.oamEnable.isSelected());
		if(EServiceType.PW.toString().equals(this.busiType)){ //PW附加控件
			oamInfo.getOamMep().setPwTc(Integer.parseInt(((ControlKeyValue)this.cmbPWTC.getSelectedItem()).getId()));
		}
		oamInfo.setNeDirect(neDirect);
		if("a".equals(this.neDirect)){
			oamInfo.setOamType(OamTypeEnum.AMEP);
		}else if("z".equals(this.neDirect)){
			oamInfo.setOamType(OamTypeEnum.ZMEP);
		}else{
			oamInfo.setOamType(OamTypeEnum.MEP);
		}
		if(EServiceType.SECTION.toString().equals(this.busiType)){
			//if(this.isSingle == 1){
				if(this.oamMepInfo.getOutVlanValue() == 0){
					this.oamMepInfo.setOutVlanValue(1);
					this.oamMepInfo.setVlanEnable(0);
				}
				oamInfo.getOamMep().setVlanEnable(this.oamMepInfo.getVlanEnable());
				oamInfo.getOamMep().setOutVlanValue(this.oamMepInfo.getOutVlanValue());
				oamInfo.getOamMep().setTpId(this.oamMepInfo.getTpId());
				oamInfo.getOamMep().setSourceMac(this.getTxtSourceMac().getText().trim());
				oamInfo.getOamMep().setEndMac(this.getTxtEndMac().getText().trim());
			//}
			oamInfo.getOamMep().setObjType(EServiceType.SECTION.toString());
		}else if(EServiceType.TUNNEL.toString().equals(this.busiType)){
			oamInfo.getOamMep().setObjType(EServiceType.TUNNEL.toString());
		}else if(EServiceType.PW.toString().equals(this.busiType)){
			oamInfo.getOamMep().setObjType(EServiceType.PW.toString());
		}else if(EServiceType.ELINE.toString().equals(this.busiType)){
			oamInfo.getOamMep().setObjType(EServiceType.ELINE.toString());
		}
	}
	
	private JLabel lblPromptInfo; // 提示信息
	private JLabel lblMegICC; 		// MegICC label
	private JLabel lblMegUMC; 		// MegUMC label
	private PtnTextField txtMegICC; 	// MegICC
	private PtnTextField txtMegUMC; 	// MegUMC
	private JLabel lblMepId;		// 维护点ID label
	private PtnTextField txtMepId;	//维护点ID
	private JCheckBox chbApsEnable;	// APS使能
	private JCheckBox chbLMEnable;	// LM使能
	private JLabel lblTC;			// TC label
	private JComboBox cmbTC;			//PWTC
	private JLabel lblPWTC;			// TC label
	private JComboBox cmbPWTC;		//PWTC
	private JCheckBox chbCSFEnable;	//CSF使能
	private JCheckBox chbSSMEnable;	//SSM使能
	private JCheckBox chbSCCEnable;	//SCC使能
	private JLabel lblRemoteMepId; // 对端维护点ID label
	private PtnTextField txtRemoteMepId; // 对端维护点ID
	private JLabel lblOutVlan;//外层vlan配置
	private JButton btnOutVlan; 
	private JLabel lblSourceMac;//源mac
	private JTextField txtSourceMac;
	private JLabel lblEndMac;//目的mac
	private JTextField txtEndMac;
	private JCheckBox oamEnable;//oam使能
	
	public PtnTextField getTxtMegICC() {
		return txtMegICC;
	}
	
	public void setTxtMegICC(PtnTextField txtMegICC) {
		this.txtMegICC = txtMegICC;
	}
	
	public PtnTextField getTxtMegUMC() {
		return txtMegUMC;
	}
	
	public void setTxtMegUMC(PtnTextField txtMegUMC) {
		this.txtMegUMC = txtMegUMC;
	}
	
	public PtnTextField getTxtMepId() {
		return txtMepId;
	}
	
	public void setTxtMepId(PtnTextField txtMepId) {
		this.txtMepId = txtMepId;
	}
	
	public JCheckBox getChbApsEnable() {
		return chbApsEnable;
	}
	
	public void setChbApsEnable(JCheckBox chbApsEnable) {
		this.chbApsEnable = chbApsEnable;
	}
	
	public JCheckBox getChbLMEnable() {
		return chbLMEnable;
	}
	
	public void setChbLMEnable(JCheckBox chbLMEnable) {
		this.chbLMEnable = chbLMEnable;
	}
	
	public JCheckBox getChbCSFEnable() {
		return chbCSFEnable;
	}
	
	public void setChbCSFEnable(JCheckBox chbCSFEnable) {
		this.chbCSFEnable = chbCSFEnable;
	}
	
	public JComboBox getCmbTC() {
		return cmbTC;
	}
	
	public void setCmbTC(JComboBox cmbTC) {
		this.cmbTC = cmbTC;
	}
	
	public JComboBox getCmbPWTC() {
		return cmbPWTC;
	}
	
	public void setCmbPWTC(JComboBox cmbPWTC) {
		this.cmbPWTC = cmbPWTC;
	}
	
	public PtnTextField getTxtRemoteMepId() {
		return txtRemoteMepId;
	}

	public void setTxtRemoteMepId(PtnTextField txtRemoteMepId) {
		this.txtRemoteMepId = txtRemoteMepId;
	}
	
	public JLabel getLblPromptInfo() {
		return lblPromptInfo;
	}

	public void setLblPromptInfo(JLabel lblPromptInfo) {
		this.lblPromptInfo = lblPromptInfo;
	}
	
	public int getHeight() {
		return height;
	}

	public JCheckBox getChbSSMEnable() {
		return chbSSMEnable;
	}

	public void setChbSSMEnable(JCheckBox chbSSMEnable) {
		this.chbSSMEnable = chbSSMEnable;
	}

	public JCheckBox getChbSCCEnable() {
		return chbSCCEnable;
	}

	public void setChbSCCEnable(JCheckBox chbSCCEnable) {
		this.chbSCCEnable = chbSCCEnable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JTextField getTxtSourceMac() {
		return txtSourceMac;
	}

	public void setTxtSourceMac(JTextField txtSourceMac) {
		this.txtSourceMac = txtSourceMac;
	}

	public JTextField getTxtEndMac() {
		return txtEndMac;
	}

	public void setTxtEndMac(JTextField txtEndMac) {
		this.txtEndMac = txtEndMac;
	}

	public JCheckBox getOamEnable() {
		return oamEnable;
	}

	public void setOamEnable(JCheckBox oamEnable) {
		this.oamEnable = oamEnable;
	}
	
}
