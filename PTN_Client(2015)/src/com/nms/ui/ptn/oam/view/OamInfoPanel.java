package com.nms.ui.ptn.oam.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;

/**
 * OAM配置页面
 * @author dzy
 */
public class OamInfoPanel extends PtnPanel {
	private static final long serialVersionUID = 8094259435277719184L;
	private OamBasicPanel oamBasicPanel;	//基础信息
	private OamInfo aOamInfo; 	//OAM BEAN
	private OamInfo zOamInfo; 	//OAM BEAN
	private int width = 0; 	//根据业务控制高度
	private String busiType; 	//业务类型
	private List<OamInfo> oamInfoList; 
	private Object obj; //业务对象
	private int aSiteId;  //a网元ID
	private int zSiteId;  //z网元ID	
	private int aManufacturer; //a网元厂商
	private int zManufacturer; //b网元厂商
	private int isSingle; //业务侧类型  1=单网元 0=网络层
	private OamInfoDialog oamInfoDialog;  //主对话框
	private int height; //主面板高度
	private int nePanelHeight; //网元信息高度
	private String workType; //工作类型 “工作”还是“保护”
	/**
	 * 创建一个新的实例
	 * 
	 * @param oamInfo bean对象。 当为null时 为新建操作
	 * @param busiType 业务类型
	 * @param btnSave 
	 * @param  
	 * 
	 */
	public OamInfoPanel(OamInfoDialog oamInfoDialog, String busiType, String workType, int isSingle){
		try {
			this.busiType = busiType;
			this.oamInfoDialog = oamInfoDialog;
			this.obj = oamInfoDialog.getObj();
			this.isSingle = isSingle;
			this.workType = workType;
			this.initData();
			this.initComponent();
			this.setLayout();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
	}
	
	/**
	 * 初始化az端OAM、aSiteId、zSiteId、this.oamInfoList
	 * @param oamList
	 * @throws Exception 
	 */
	private void initData() throws Exception {
		this.aOamInfo = new OamInfo();
		this.zOamInfo = new OamInfo();
		SiteService_MB siteService = null;
		try {
			aOamInfo.setOamMep(new OamMepInfo());
			zOamInfo.setOamMep(new OamMepInfo());
			if(this.obj instanceof Segment){  //Segment
				this.aSiteId = ((Segment)this.obj).getASITEID();
				this.zSiteId = ((Segment)this.obj).getZSITEID();
				this.oamInfoList = ((Segment)this.obj).getOamList();
				if(null!=this.oamInfoList&&this.oamInfoList.size()>0){
					for (OamInfo oamInfo : ((Segment)this.obj).getOamList()) {
						if(oamInfo.getOamMep().getSiteId()==((Segment)this.obj).getASITEID()||"a".equals(oamInfo.getNeDirect())){
							this.aOamInfo = oamInfo; //a端OAM
						}else if(oamInfo.getOamMep().getSiteId()==((Segment)this.obj).getZSITEID()||"z".equals(oamInfo.getNeDirect())){
							this.zOamInfo = oamInfo; //z端OAM
						}
					}
				}
			}else if(this.obj instanceof Tunnel){//Tunnel
				this.aSiteId = ((Tunnel)this.obj).getASiteId();
				this.zSiteId = ((Tunnel)this.obj).getZSiteId();
				if(this.workType.equals("work")){
					this.oamInfoList = ((Tunnel)this.obj).getOamList();
				}else if(this.workType.equals("protect")){
					if(((Tunnel)this.obj).getProtectTunnel() != null){
						this.oamInfoList = ((Tunnel)this.obj).getProtectTunnel().getOamList();
					}else{
						this.oamInfoList = ((Tunnel)this.obj).getOamList();
					}
				}
				if(null!=this.oamInfoList&&this.oamInfoList.size()>0){
					for (OamInfo oamInfo : oamInfoList) {
						if(null!=oamInfo.getOamMep()&&oamInfo.getOamMep().getSiteId()==((Tunnel)this.obj).getASiteId()||"a".equals(oamInfo.getNeDirect())){
							this.aOamInfo = oamInfo; //a端OAM
						}else if(null!=oamInfo.getOamMep()&&oamInfo.getOamMep().getSiteId()==((Tunnel)this.obj).getZSiteId()||"a".equals(oamInfo.getNeDirect())){
							this.zOamInfo = oamInfo; //z端OAM
						}
					}
				}
			}else if(this.obj instanceof PwInfo){//PW
				this.aSiteId = ((PwInfo)this.obj).getASiteId();
				this.zSiteId = ((PwInfo)this.obj).getZSiteId();
				this.oamInfoList = ((PwInfo)this.obj).getOamList();
				if(null!=this.oamInfoList&&this.oamInfoList.size()>0){
					for (OamInfo oamInfo : ((PwInfo)this.obj).getOamList()) {
						if(oamInfo.getOamMep().getSiteId()==((PwInfo)this.obj).getASiteId()||"a".equals(oamInfo.getNeDirect())){
							this.aOamInfo = oamInfo; //a端OAM
						}else{
							this.zOamInfo = oamInfo; //z端OAM
						}
					}
				}
			}else if(this.obj instanceof ElineInfo){//eline
				this.aSiteId = ((ElineInfo)this.obj).getaSiteId();
				this.zSiteId = ((ElineInfo)this.obj).getzSiteId();
				this.oamInfoList = ((ElineInfo)this.obj).getOamList();
				if(null!=this.oamInfoList&&this.oamInfoList.size()>0){
					for (OamInfo oamInfo : ((ElineInfo)this.obj).getOamList()) {
						if(oamInfo.getOamMep().getSiteId()==((ElineInfo)this.obj).getaSiteId()||"a".equals(oamInfo.getNeDirect())){
							this.aOamInfo = oamInfo; //a端OAM
						}else{
							this.zOamInfo = oamInfo; //z端OAM
						}
					}
				}
			}else if(this.obj instanceof OamInfo){//单网元侧
				this.oamInfoList  =  new ArrayList<OamInfo>();
				oamInfoList.add(aOamInfo);
				aOamInfo = (OamInfo) this.obj;
			}
			//a端面板
			if(0==this.aSiteId){
				this.aSiteId = ConstantUtil.siteId;
			}
		    siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			aManufacturer = siteService.getManufacturer(this.aSiteId);
			if(0==this.isSingle){
				zManufacturer = siteService.getManufacturer(this.zSiteId);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 初始化控件
	 * 
	 * @throws Exception
	 */
	private void initComponent() throws Exception {
		this.oamBasicPanel = new OamBasicPanel(this.oamInfoDialog, this.oamInfoList);
		if (aManufacturer == EManufacturer.WUHAN.getValue()) {
			this.aNEPanel = new OamInfoWHPanel(this.oamInfoDialog, busiType, "a", isSingle);
		} else {
			this.aNEPanel = new OamInfoCXPanel(this.oamInfoDialog, busiType, "a", isSingle);
		}
		//动态加载高度
		nePanelHeight = this.aNEPanel.getHeight();
		this.aNEPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_A_NE_INFO)));
		if(1 == this.isSingle)
		this.aNEPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysObj.STRING_OAM_CONFIG)));
		//z端面板
		if(0 != this.zSiteId){
			if (zManufacturer == EManufacturer.WUHAN.getValue()) {
				this.zNEPanel = new OamInfoWHPanel(this.oamInfoDialog, busiType, "z", isSingle);
			} else {
				this.zNEPanel = new OamInfoCXPanel(this.oamInfoDialog, busiType, "z", isSingle);
			}
			if(nePanelHeight<this.zNEPanel.getHeight())
				nePanelHeight = this.zNEPanel.getHeight();
			this.zNEPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_Z_NE_INFO)));
		}
		mipTable = new JTable();
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_OAM_MIDDLE_POINT)));
		scrollPane.setViewportView(mipTable);
		setTableModel();
		mipTable.setModel(mipTableModel);
		setIdColumnAttribute(mipTable);
		//动态加载高度
		height = nePanelHeight+180;
		if((EServiceType.TUNNEL.toString().equals(this.busiType)|| EServiceType.PW.toString().equals(this.busiType))&&0 == this.isSingle){
			scrollPane.setMinimumSize(new Dimension(200,height-10));
			width = 200;
		}
	}
	
	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {200,200,width };
		componentLayout.columnWeights = new double[] { 0, 0.1 };
		componentLayout.rowHeights = new int[] {150,nePanelHeight+20 };
		componentLayout.rowWeights = new double[] { 0.0, 0.1, 0.0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 5, 0, 5);

				
		// 第1行 基本信息
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.oamBasicPanel, c);
		this.add(this.oamBasicPanel);
		
		// 第2行 A端信息
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1+this.isSingle;
		componentLayout.setConstraints(this.aNEPanel, c);
		this.add(this.aNEPanel);
		
		if(0 == this.zSiteId)
			return;
					
		// 第3行 Z端信息
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.zNEPanel, c);
		this.add(this.zNEPanel);
		
		// 第4列 MIP
		if(!(EServiceType.TUNNEL.toString().equals(this.busiType) || EServiceType.PW.toString().equals(this.busiType)))
			return;
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight=2;
		componentLayout.setConstraints(this.scrollPane, c);
		this.add(this.scrollPane);
	}
	
	
	
	/**
	 * 保存按钮事件
	 */ 
	public List<OamInfo> getOam() {
		this.oamInfoList = new ArrayList<OamInfo>();
		//获取A端网元信息
		if (this.aManufacturer == EManufacturer.WUHAN.getValue()) {
			((OamInfoWHPanel)this.aNEPanel).getWHOam(this.aOamInfo);
		} else {
			((OamInfoCXPanel)this.aNEPanel).getCXOam(this.aOamInfo);
		}
		this.aOamInfo.getOamMep().setSiteId(this.aSiteId);
		//单网元侧不获取Z端网元信息
		if(0 == this.isSingle){
			if (this.zManufacturer == EManufacturer.WUHAN.getValue()) {
				((OamInfoWHPanel)this.zNEPanel).getWHOam(this.zOamInfo);
			} else {
				((OamInfoCXPanel)this.zNEPanel).getCXOam(this.zOamInfo);
			}
			this.zOamInfo.getOamMep().setSiteId(this.zSiteId);
			this.oamInfoList.add(this.aOamInfo);
			this.oamInfoList.add(this.zOamInfo);
		}else{
			this.oamInfoList.add(this.aOamInfo);
		}
		
		this.oamBasicPanel.getBasicOam(this.oamInfoList);
		if(0==this.isSingle){
			//网络侧添加邻端网元
			if(null!=this.oamInfoList&&this.oamInfoList.size()==2){
				this.oamInfoList.get(0).getOamMep().setRemoteMepId(this.oamInfoList.get(1).getOamMep().getLocalMepId());
				this.oamInfoList.get(1).getOamMep().setRemoteMepId(this.oamInfoList.get(0).getOamMep().getLocalMepId());
			}
		}
		if(this.obj instanceof Segment){
			((Segment)this.obj).setOamList(this.oamInfoList);
		}else if(this.obj instanceof Tunnel){
			if(this.workType.equals("work")){
				((Tunnel)this.obj).setOamList(this.oamInfoList);
			}else if(this.workType.equals("protect")){
				((Tunnel)this.obj).getProtectTunnel().setOamList(this.oamInfoList);
			}
		}else if(this.obj instanceof PwInfo){
			((PwInfo)this.obj).setOamList(this.oamInfoList);
		}else if(this.obj instanceof ElineInfo){
			((ElineInfo)this.obj).setOamList(this.oamInfoList);
		}
		return this.oamInfoList;
	}
	
	/**
	 * 设置mip的表模型
	 */
	private void setTableModel() {
		mipTableModel = new DefaultTableModel(null, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), ResourceUtil.srcStr(StringKeysObj.NET_BASE), "MIP ID","TC" }) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4945416832440363809L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 1) {
					return false;
				}
				return true;
			}
		};
	}
	
	/**
	 * 设置 mip 属性
	 * @param table
	 */
	@SuppressWarnings("serial")
	public void setIdColumnAttribute(JTable table) {
		table.getTableHeader().setReorderingAllowed(false);
		TableColumn idColumn = table.getColumn(ResourceUtil.srcStr(StringKeysObj.ORDER_NUM));
		idColumn.setResizable(false);
		idColumn.setMaxWidth(30);
		idColumn.setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public java.awt.Dimension getSize() {
				return new Dimension(30, 20);
			};

			@Override
			public int getHorizontalAlignment() {
				return SwingConstants.CENTER;
			};

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
				this.setForeground(Color.BLUE);
				this.setBackground(UIManager.getColor("Panel.background"));
				return this;
			};
		});
	}
	
	public DefaultTableModel mipTableModel; //mip表模型
	public JTable mipTable; //mip表
	public JScrollPane scrollPane; //mip面板
	public DefaultTableModel ptableModel; // //保护侧mip表模型
	private JPanel aNEPanel; 	//A端网元属性面板
	private JPanel zNEPanel; 	//Z端网元属性面板
	
	
	public OamBasicPanel getOamBasicPanel() {
		return oamBasicPanel;
	}

	public JPanel getaNEPanel() {
		return aNEPanel;
	}

	public JPanel getzNEPanel() {
		return zNEPanel;
	}

	public OamInfo getaOamInfo() {
		return aOamInfo;
	}

	public DefaultTableModel getMipTableModel() {
		return mipTableModel;
	}

	public void setMipTableModel(DefaultTableModel mipTableModel) {
		this.mipTableModel = mipTableModel;
	}
	
	public int getHeight() {
		return height;
	}

	public int getaManufacturer() {
		return aManufacturer;
	}

	public int getzManufacturer() {
		return zManufacturer;
	}

	public JTable getMipTable() {
		return mipTable;
	}

	public void setMipTable(JTable mipTable) {
		this.mipTable = mipTable;
	}
	
}
