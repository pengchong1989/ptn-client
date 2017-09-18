package com.nms.ui.ptn.ne.acl.view;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import com.nms.db.bean.ptn.AclInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.ptn.ne.acl.controller.AclController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class AclPanel extends ContentView<AclInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6509038043636415696L;

	public AclPanel() {
		super("AclTable",RootFactory.CORE_MANAGE);
		init();
	}

	private void init(){
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_ACL_MANAGE)));
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		panelLayout.rowHeights = new int[] {400, 10};
		panelLayout.rowWeights = new double[] {0, 0};
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(this.getContentPanel(), c);
		this.add(this.getContentPanel());
	}
	
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(200, ConstantUtil.INT_WIDTH_THREE);
	}
	
	@Override
	public void setController() {
		controller = new AclController(this);
	}
	

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getRefreshButton());
		needRemoveButtons.add(getExportButton());
		return needRemoveButtons;
	}

	public List<JButton> setAddButtons() {
		
		List<JButton> needAddButtons = new ArrayList<JButton>();
		needAddButtons.add(getAddButton());
		needAddButtons.add(getUpdateButton());
		needAddButtons.add(getDeleteButton());
		needAddButtons.add(getRefreshButton());
		needAddButtons.add(getSynchroButton());
		return needAddButtons;
	}
}
