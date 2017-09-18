package com.nms.ui.ptn.upgradeManage.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.equipment.slot.SlotService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTitle;

public class UpgradeTypePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6944142152573191579L;
	private JPanel jPanel;
	private JLabel typeJLabel;
	private JComboBox typeComboBox;
	private UpgradeManagePanel upgradeManagePanel;
	
	public UpgradeTypePanel() {
		init();
	}

	private void init() {
		initComponents();
		setComponentsLayout();
		addListener();
		typeCheck();
	}
	
	private void initComponents() {
		jPanel = new JPanel();
		jPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_INFO_LIST)));
		typeJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		typeComboBox = new JComboBox();
		typeComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.MCU_PANEL));
		typeComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.E1_PANEL));
		upgradeManagePanel = new UpgradeManagePanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		jPanel.setLayout(flowLayout);
		jPanel.add(typeJLabel);
		jPanel.add(typeComboBox);
	}

	private void setComponentsLayout() {
		setTypePanleLayout();
	}

	private void setTypePanleLayout() {
		GridBagLayout topPanelLayout = new GridBagLayout();
		topPanelLayout.rowHeights = new int[] { 50, 200 };
		topPanelLayout.rowWeights = new double[] { 0.0, 1.0 };
		this.setLayout(topPanelLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 8, 0, 0);
		topPanelLayout.setConstraints(jPanel, c);
		this.add(jPanel);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		topPanelLayout.setConstraints(upgradeManagePanel, c);
		this.add(upgradeManagePanel);
	}
	
	private void addListener(){
		typeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 2){
					if(typeComboBox.getSelectedIndex()==0){
						upgradeManagePanel.setType(1);
					}else{
						setType();
					}
					upgradeManagePanel.clear();
				}
			}
		});
	}
	
	private void setType(){
		CardService_MB cardService = null;
		SlotService_MB slotService = null;
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			slotService = (SlotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SLOT);
			CardInst cardinst = new CardInst();
			cardinst.setSiteId(ConstantUtil.siteId);
			cardinst.setCardName("E1T1");
			List<CardInst> cardInsts = cardService.select(cardinst);
			if(cardInsts != null && cardInsts.size() == 0){
				cardinst.setCardName("SP16");
				cardInsts = cardService.select(cardinst);
			}
			if(cardInsts != null && cardInsts.size()>0){
				SlotInst slotinst = new SlotInst();
				slotinst.setId(cardInsts.get(0).getSlotId());
				List<SlotInst> slotInsts = slotService.select(slotinst);
				if(slotInsts != null && slotInsts.size()>0){
					upgradeManagePanel.setType(slotInsts.get(0).getNumber());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(cardService);
			UiUtil.closeService_MB(slotService);
		}
	}
	private void typeCheck(){
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			SiteInst siteInst = siteService.select(ConstantUtil.siteId);
			if(siteInst != null && siteInst.getCellType().contains("703")){
				typeComboBox.setEnabled(false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}
}
