package com.nms.ui.ptn.ne.eline.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.UiUtil;

public class PwElinePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442083213455101239L;
	private JScrollPane scrollPane_pw;
	private ViewDataTable<PwInfo> table_pw;
	private final String TABLENAME_PW = "elinePwTable";

	public PwElinePanel() {
		initComponents();
		setLayout();
	}
	
	public void clear() {
		table_pw.clear();
	}
	
	public void initData(List<PwInfo> pwInfoList) throws Exception {
		if (pwInfoList != null && pwInfoList.size() > 0) {
			TunnelService_MB tunnelServiceMB=null;
			try {
				tunnelServiceMB=(TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				
				for(PwInfo pwInfo : pwInfoList){
					pwInfo.putClientProperty("id", pwInfo.getPwId());
					pwInfo.putClientProperty("pwname", pwInfo.getPwName());
					pwInfo.putClientProperty("tunnelname", tunnelServiceMB.getTunnelName(pwInfo.getTunnelId()));
					
					if(pwInfo.getASiteId()==ConstantUtil.siteId){
						pwInfo.putClientProperty("inlabel", pwInfo.getInlabelValue());
						pwInfo.putClientProperty("outlabel", pwInfo.getOutlabelValue());
						pwInfo.putClientProperty("sitename",pwInfo.getAoppositeId());
					}else{
						pwInfo.putClientProperty("inlabel", pwInfo.getOutlabelValue());
						pwInfo.putClientProperty("outlabel", pwInfo.getInlabelValue());
						pwInfo.putClientProperty("sitename",pwInfo.getZoppositeId());
					}
				}
				
				table_pw.initData(pwInfoList);
			} catch (Exception e) {
				throw e;
			} finally {
				UiUtil.closeService_MB(tunnelServiceMB);
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
