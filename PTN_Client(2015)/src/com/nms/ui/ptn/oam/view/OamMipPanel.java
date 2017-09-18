package com.nms.ui.ptn.oam.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMipInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EServiceType;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;
/**
 * 网元侧 MIP OAM 面板
 * @author dzy
 *
 */
public class OamMipPanel extends PtnPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2183731142851402538L;
	private OamInfo oamInfo; //oam
	private OamInfoDialog oamInfoDialog; //主面板
	/**
	 * 创建一个实例
	 * @param oamInfo oam对象
	 * @param str 
	 */
	public OamMipPanel(OamInfoDialog oamInfoDialog,OamInfo oamInfo) {
		try {
			this.oamInfoDialog = oamInfoDialog;
			this.oamInfo = oamInfo;
			initComponent();
			initCompenentLayput();
			initDate();
			initCombox();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 初始化控件
	 * @throws Exception 
	 */
	private void initComponent() throws Exception {
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysObj.STRING_MIP_OAM_CONFIG)));
		
		lblMessage = new JLabel();
		
		lblMipServices = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MIP_BUSINESS_NAME));
		cmbServicesCombo = new JComboBox();
		lblMipMegIdLabel = new JLabel("MEG Id ");
		lblMip = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_MAINTENANCE_POINT_ID));
		txtMip = new PtnTextField(true, PtnTextField.TYPE_INT, 4, this.lblMessage, oamInfoDialog.getBtnSave(), oamInfoDialog);
		this.txtMip.setText("1");
		this.txtMip.setCheckingMaxValue(true);
		this.txtMip.setMaxValue(8191);
		this.txtMip.setCheckingMinValue(true);
		this.txtMip.setMinValue(0);
		lblAMep = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TWO_MAINTENANCE_POINT_ID1));
		txtAMep = new PtnTextField(true, PtnTextField.TYPE_INT, 4, this.lblMessage, oamInfoDialog.getBtnSave(), oamInfoDialog);
		this.txtAMep.setText("2");
		this.txtAMep.setCheckingMaxValue(true);
		this.txtAMep.setMaxValue(8191);
		this.txtAMep.setCheckingMinValue(true);
		this.txtAMep.setMinValue(0);
		lblZMep = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_TWO_MAINTENANCE_POINT_ID2));
		txtZMep = new PtnTextField(true, PtnTextField.TYPE_INT, 4, this.lblMessage, oamInfoDialog.getBtnSave(), oamInfoDialog);
		this.txtZMep.setText("3");
		this.txtZMep.setCheckingMaxValue(true);
		this.txtZMep.setMaxValue(8191);
		this.txtZMep.setCheckingMinValue(true);
		this.txtZMep.setMinValue(0);
		this.txtMipMegId = new PtnTextField(false, PtnTextField.TYPE_INT, 4, this.lblMessage, oamInfoDialog.getBtnSave(), oamInfoDialog);
		this.txtMipMegId.setCheckingMaxValue(true);
		this.txtMipMegId.setMaxValue(8191);
		this.txtMipMegId.setCheckingMinValue(true);
		this.txtMipMegId.setMinValue(0);
		tcJLabel = new JLabel("TC");
		this.tcPtnTextField = new PtnTextField(false, PtnTextField.TYPE_INT, 4, this.lblMessage, oamInfoDialog.getBtnSave(), oamInfoDialog);
		this.tcPtnTextField.setCheckingMaxValue(true);
		this.tcPtnTextField.setMaxValue(7);
		this.tcPtnTextField.setCheckingMinValue(true);
		this.tcPtnTextField.setMinValue(0);
	}

	/**
	 * 布局
	 */
	private void initCompenentLayput() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 30, 100 };
		layout.columnWeights = new double[] { 1.0, 1.0 };
		layout.rowHeights = new int[] {30, 30, 30, 30, 30, 30};
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		
		//第1行 提示信息
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMessage, c);
		this.add(lblMessage);
		
		//第2行 mip下拉菜单 lbl
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMipServices, c);
		this.add(lblMipServices);
		
		//第2行 mip下拉菜单 
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(cmbServicesCombo, c);
		this.add(cmbServicesCombo);
		
		//第3行 MegId lbl
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMipMegIdLabel, c);
		this.add(lblMipMegIdLabel);
		
		//第3行 MegId 
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(txtMipMegId, c);
		this.add(txtMipMegId);
		
		//第4行 MIP lbl
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblMip, c);
		this.add(lblMip);
		
		//第4行 MIP 
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(txtMip, c);
		this.add(txtMip);
		
		//第5行 A端维护点 lbl
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblAMep, c);
		this.add(lblAMep);
		
		//第5行 A端维护点 
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(txtAMep, c);
		this.add(txtAMep);
		
		//第6行 Z端维护点 lbl
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblZMep, c);
		this.add(lblZMep);
		
		//第6行 Z端维护点
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(txtZMep, c);
		this.add(txtZMep);
		
		//第7行 tc
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(tcJLabel, c);
		this.add(tcJLabel);
		
		//第7行 tc
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(tcPtnTextField, c);
		this.add(tcPtnTextField);

	}


	/**
	 * 初始化
	 * @Exception 
	 */
	private void initDate() throws Exception {
		this.txtMipMegId.setText("1");
		this.txtMip.setText("1");
		this.txtAMep.setText("2");
		this.txtZMep.setText("3");
		this.tcPtnTextField.setText("7");
		if (null != this.oamInfo && null!=this.oamInfo.getOamMip()) {
			OamMipInfo oamMipInfo=this.oamInfo.getOamMip();
			super.getComboBoxDataUtil().comboBoxSelect(cmbServicesCombo, oamMipInfo.getServiceId() + "");
			this.txtMipMegId.setText(oamMipInfo.getMegId()+"");
			this.txtMip.setText(oamMipInfo.getMipId()+"");
			this.txtAMep.setText(oamMipInfo.getAMId()+"");
			this.txtZMep.setText(oamMipInfo.getZMId()+"");
			this.tcPtnTextField.setText(oamMipInfo.getTc()+"");
		}
	}

	/**
	 *  初始化下拉菜单
	 */
	private void initCombox() throws Exception {
		DefaultComboBoxModel boxModelMop;
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> tunnelList = null;
		Tunnel tunnel ;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			boxModelMop = (DefaultComboBoxModel) cmbServicesCombo.getModel();
			if(null==this.oamInfo || null==this.oamInfo.getOamMip()){
				tunnelList = tunnelServiceMB.selectNodesBySiteId(ConstantUtil.siteId);
				for (Tunnel info : tunnelList) {
					boolean hasOamMip = false;
					for(OamInfo oamInfo : info.getOamList()){
						if(oamInfo.getOamMip() != null &&  oamInfo.getOamMip().getSiteId() == ConstantUtil.siteId){
							hasOamMip = true;
							break;
						}
					}
					if(!hasOamMip&&info.getASiteId() != ConstantUtil.siteId && info.getZSiteId() != ConstantUtil.siteId){
								boxModelMop.addElement(new ControlKeyValue(info.getTunnelId() + "", info.getTunnelName(), info));
					}
				}
			}else{
				tunnel = new Tunnel();
				tunnel.setTunnelId(this.oamInfo.getOamMip().getServiceId());
				tunnelList = tunnelServiceMB.select_nojoin(tunnel);
				if(null==tunnelList || tunnelList.size()==0)
					return;
				boxModelMop.addElement(new ControlKeyValue(tunnelList.get(0).getTunnelId() + "", tunnelList.get(0).getTunnelName(), tunnelList.get(0)));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
			tunnelList = null;
		}
	}
	
	/**
	 * 获取MIP对象
	 * @return 返回 OamInfo
	 */
	public boolean getMip(OamInfo oamInfo){
		if(Integer.parseInt(this.txtMip.getText())==(Integer.parseInt(this.txtAMep.getText()))||
				Integer.parseInt(this.txtMip.getText())==(Integer.parseInt(this.txtZMep.getText()))||
				Integer.parseInt(this.txtAMep.getText())==(Integer.parseInt(this.txtZMep.getText()))){
			DialogBoxUtil.succeedDialog(oamInfoDialog, ResourceUtil.srcStr(StringKeysTip.TIP_MEPID_DIFFERENT));
			return false;
		}
		OamMipInfo oamMipInfo = new OamMipInfo();
		if(null!=this.oamInfo){
			oamInfo = this.oamInfo;
			oamMipInfo = oamInfo.getOamMip();
		}else{
			oamInfo.setOamMip(oamMipInfo);
		}
		Tunnel tunnel = (Tunnel) ((ControlKeyValue) (cmbServicesCombo.getSelectedItem())).getObject();
		oamMipInfo.setServiceId(tunnel.getTunnelId());
		for (Lsp lsp : tunnel.getLspParticularList()) {
			if (lsp.getASiteId() == ConstantUtil.siteId) {
				oamMipInfo.setObjId(lsp.getAtunnelbusinessid());
				break;
			}
			if (lsp.getZSiteId() == ConstantUtil.siteId) {
				oamMipInfo.setObjId(lsp.getZtunnelbusinessid());
				break;
			}
		}
		oamInfo.setOamObjType("TUNNEL");
		oamMipInfo.setSiteId(ConstantUtil.siteId);
		oamMipInfo.setObjType(EServiceType.TUNNEL.toString());
		oamMipInfo.setMegId(Integer.parseInt(this.txtMipMegId.getText()));
		oamMipInfo.setMipId(Integer.parseInt(this.txtMip.getText()));
		oamMipInfo.setAMId(Integer.parseInt(this.txtAMep.getText()));
		oamMipInfo.setZMId(Integer.parseInt(this.txtZMep.getText()));
		oamMipInfo.setServiceId(Integer.parseInt(((ControlKeyValue)this.cmbServicesCombo.getSelectedItem()).getId()));
		oamMipInfo.setTc(Integer.parseInt(this.tcPtnTextField.getText()));
		return true;
	}
	
	private JLabel lblMessage;
	private JLabel lblMipServices; //xc业务对象 lbl
	private JComboBox cmbServicesCombo;//xc业务对象 lbl 
	private JLabel lblMipMegIdLabel; // megid lbl
	private PtnTextField txtMipMegId;//megid
	private JLabel lblMip;// mipid lbl
	private PtnTextField txtMip;// mipid
	private JLabel lblAMep;// a mep lbl
	private PtnTextField txtAMep;//  a mep
	private JLabel lblZMep;// z mep lbl
	private PtnTextField txtZMep;//  a mep lbl
	private JLabel tcJLabel;
	private PtnTextField tcPtnTextField;
	public OamInfo getOamInfo() {
		return oamInfo;
	}

	public void setOamInfo(OamInfo oamInfo) {
		this.oamInfo = oamInfo;
	}

	public JTextField getTxtMipMegId() {
		return txtMipMegId;
	}

	public void setTxtMipMegId(PtnTextField txtMipMegId) {
		this.txtMipMegId =  txtMipMegId;
	}

	public PtnTextField getTxtMip() {
		return txtMip;
	}

	public void setTxtMip(PtnTextField txtMip) {
		this.txtMip = txtMip;
	}

	public PtnTextField getTxtAMep() {
		return txtAMep;
	}

	public void setTxtAMep(PtnTextField txtAMep) {
		this.txtAMep = txtAMep;
	}

	public PtnTextField getTxtZMep() {
		return txtZMep;
	}

	public void setTxtZMep(PtnTextField txtZMep) {
		this.txtZMep = txtZMep;
	}
	public JComboBox getCmbServicesCombo() {
		return cmbServicesCombo;
	}
	
}


