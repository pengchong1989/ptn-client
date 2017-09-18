package com.nms.ui.ptn.ne.ces.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.UiUtil;

public class CesInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442083213455101239L;
	private JScrollPane scrollPane_pw;
	private ViewDataTable<PwInfo> table_pw;
	private final String TABLENAME_PW = "cesInfoTable";

	public CesInfoPanel() {
		initComponents();
		setLayout();
	}

	public void clear() {
		table_pw.clear();
	}

	public void initData(CesInfo cesInfo) throws Exception {
		if (null != cesInfo) {

			PwInfoService_MB pwInfoService = null;
			PwInfo pwinfo = null;
			List<PwInfo> pwinfoList = null;
			CesInfoService_MB cesInfoService=null;
			TunnelService_MB tunnelServiceMB=null;
			try {
				cesInfoService=(CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
				tunnelServiceMB=(TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				pwinfo = new PwInfo();
				pwinfo.setPwId(cesInfo.getPwId());
				pwinfo = pwInfoService.selectBypwid_notjoin(pwinfo);

				pwinfo.putClientProperty("id", pwinfo.getPwId());
				pwinfo.putClientProperty("pwname", pwinfo.getPwName());
				pwinfo.putClientProperty("tunnelname", tunnelServiceMB.getTunnelName(pwinfo.getTunnelId()));

				if (pwinfo.getASiteId() == ConstantUtil.siteId) {
					pwinfo.putClientProperty("sitename", pwinfo.getAoppositeId());
					pwinfo.putClientProperty("inlabel", pwinfo.getInlabelValue());
					pwinfo.putClientProperty("outlabel", pwinfo.getOutlabelValue());
				} else {
					pwinfo.putClientProperty("sitename", pwinfo.getZoppositeId());
					pwinfo.putClientProperty("inlabel", pwinfo.getOutlabelValue());
					pwinfo.putClientProperty("outlabel", pwinfo.getInlabelValue());
				}
				if(cesInfo.getaSiteId() == ConstantUtil.siteId){
					pwinfo.putClientProperty("portname", cesInfoService.getCesPortName(cesInfo, "a"));
				}else{
					pwinfo.putClientProperty("portname",cesInfoService.getCesPortName(cesInfo, "z"));
				}
				pwinfoList = new ArrayList<PwInfo>();
				pwinfoList.add(pwinfo);

				this.table_pw.initData(pwinfoList);
			} catch (Exception e) {
				throw e;
			} finally {
				UiUtil.closeService_MB(cesInfoService);
				UiUtil.closeService_MB(tunnelServiceMB);
				UiUtil.closeService_MB(pwInfoService);
			}
		}
	}

	private void initComponents() {
		table_pw = new ViewDataTable<PwInfo>(TABLENAME_PW);
		table_pw.getTableHeader().setResizingAllowed(true);
		table_pw.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table_pw.setTableHeaderPopupMenuFactory(null);
		table_pw.setTableBodyPopupMenuFactory(null);
		scrollPane_pw = new JScrollPane();
		scrollPane_pw.setViewportView(table_pw);
		scrollPane_pw.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_pw.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	private void setLayout() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		GridBagLayout qosGridBagLayout = new GridBagLayout();
		this.setLayout(qosGridBagLayout);
		addComponent(this, scrollPane_pw, 0, 0, 0.5, 1.0, 0, 1, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
	}

	private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
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
}
