package com.nms.ui.ptn.ne.statusData.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.ne.statusData.FrequencyBasisJPanel;
import com.nms.ui.ptn.ne.statusData.FrequencyStatusPanel;
import com.nms.ui.ptn.ne.statusData.arpStatus.view.ARPStatusPanel;
import com.nms.ui.ptn.ne.statusData.bfdStatus.view.BfdStatusPanel;
import com.nms.ui.ptn.ne.statusData.protectStatus.view.ProtectStatusPanel;
import com.nms.ui.ptn.ne.statusData.pwProtectStatus.view.PwProtectStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.LagStatus.view.LagStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.ethOamPing.EthOamPingPanel;
import com.nms.ui.ptn.ne.statusData.view.ethOamTrace.EthOamTraceQueryPanel;
import com.nms.ui.ptn.ne.statusData.view.portStatus.view.PortStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.pwStatus.view.PwStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.tunnelStatus.TunnelStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.vpwsStatus.VpwsStatusPanel;
import com.nms.ui.ptn.ne.statusData.view.wrappingStatus.WrappingStatusPanel;
import com.nms.ui.ptn.ne.statusData.ptpBasicStatus.view.PtpBasicStatusPanel;
import com.nms.ui.ptn.ne.statusData.ptpPortStatus.view.PtpPortStatusPanel;

public class StatusDataPanel extends JPanel{

	


	private static final long serialVersionUID = 5960682360870159214L;
	
	private JTabbedPane tabbedPane = null;
	private GridBagLayout gridBagLayout = null;
	
	private ProtectStatusPanel protectStatusPanel = null;
	private FrequencyStatusPanel frequencyStatusPanel;
	private FrequencyBasisJPanel frequencyBasisJPanel;
	private PwStatusPanel pwStatusPanel = null;
	private PortStatusPanel portStatusPanel = null;
	private TunnelStatusPanel tunnelStatusPanel = null;
	private VpwsStatusPanel vpwsStatusPanel = null;
	private EthOamPingPanel ethOamPingPanel = null;
	private EthOamTraceQueryPanel ethOamTraceQueryPanel = null;
	private WrappingStatusPanel wrappingStatusPanel = null;
	private PwProtectStatusPanel pwProtectStatusPanel =null;
	private LagStatusPanel lagStatusPanel=null;
	private PtpPortStatusPanel ptpPortStatusPanel=null;
	private PtpBasicStatusPanel ptpBasicStatusPanel=null;
	private BfdStatusPanel bfdStatusPanel=null;
	private ARPStatusPanel arpStatusPanel = null;
	
	public StatusDataPanel() {
		try {
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void init() throws Exception {

		try {
			
			tabbedPane = new JTabbedPane();
			gridBagLayout = new GridBagLayout();
			protectStatusPanel = new ProtectStatusPanel();
			frequencyStatusPanel = new FrequencyStatusPanel();
			frequencyBasisJPanel = new FrequencyBasisJPanel();
			pwStatusPanel = new PwStatusPanel();
			portStatusPanel = new PortStatusPanel();
			tunnelStatusPanel = new TunnelStatusPanel();
			vpwsStatusPanel = new VpwsStatusPanel();
			ethOamPingPanel = new EthOamPingPanel();
			ethOamTraceQueryPanel = new EthOamTraceQueryPanel();
			wrappingStatusPanel = new WrappingStatusPanel();
			pwProtectStatusPanel = new PwProtectStatusPanel();
			lagStatusPanel=new LagStatusPanel();
			ptpPortStatusPanel = new PtpPortStatusPanel();
			ptpBasicStatusPanel= new PtpBasicStatusPanel();
			bfdStatusPanel= new BfdStatusPanel();
			arpStatusPanel = new ARPStatusPanel();
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PORTSTATUS), portStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TUNNEL_STATUS), tunnelStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_LSPPROTECT), protectStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PWSTATUS), pwStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PWPROTECT),pwProtectStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_VPWS_STATUS), vpwsStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_CLOCK_STATUS_BASIC), frequencyBasisJPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_CLOCK_STATUS_CHANGE), frequencyStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.OAM_TRACE), ethOamTraceQueryPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.OAM_PING), ethOamPingPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.WRAPPING_PROTECT), wrappingStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PORTLAG), lagStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PTPPORT), ptpPortStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PTPBASIC), ptpBasicStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_BFDSTATUS), bfdStatusPanel);
			tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_ARPSTATUS), arpStatusPanel);
			setGridBagLayout();/* 主页面布局 */
			this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_SITESTATUS)));
			this.setLayout(gridBagLayout);
			this.add(tabbedPane);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * <p>
	 * 主页面布局
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void setGridBagLayout() throws Exception {

		GridBagConstraints gridBagConstraints = null;
		try {

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagLayout.setConstraints(tabbedPane, gridBagConstraints);
		} catch (Exception e) {

			throw e;
		} finally {

			gridBagConstraints = null;
		}
	}

}
