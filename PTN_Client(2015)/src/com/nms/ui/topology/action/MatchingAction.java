package com.nms.ui.topology.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import twaver.Element;
import twaver.Slot;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.topology.ShelfTopology;

public class MatchingAction implements Action {

	private ShelfTopology shelfTopology;

	public MatchingAction(ShelfTopology shelfTopology) {
		this.shelfTopology = shelfTopology;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	public Object getValue(String key) {
		return null;
	}

	public boolean isEnabled() {
		return false;
	}

	public void putValue(String key, Object value) {

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	public void setEnabled(boolean b) {

	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {

		List<Element> elemntList = null;
		SlotInst slotInst = null;
		String xmlPath = null;
		boolean flag = true;
		DispatchUtil cardDispatch = null;
		Map<Integer, String> map = null;
		CardService_MB cardService = null;
		CardInst cardInst = null;
		SiteService_MB  siteService=null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			if (this.shelfTopology != null) {
				siteService=(SiteService_MB ) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				cardService = (CardService_MB ) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
				elemntList = shelfTopology.getTDataBox().getAllElements();
				//通过网元主键从设备上同步出此网元所有板卡
				cardDispatch = new DispatchUtil(RmiKeys.RMI_CARD);
				map = cardDispatch.matchingCard(ConstantUtil.siteId);
				if (map == null) {
					DialogBoxUtil.errorDialog(this.shelfTopology, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_FAIL));
					return;
				}
				//遍历拓扑上所有元素
				for (Element element : elemntList) {
					//验证是否为槽位
					if (element instanceof Slot) {
						slotInst = (SlotInst) element.getUserObject();
						//验证此槽位下是否有板卡
						if (element.getChildren().size() == 0) {
							//如果没有板卡通过板卡名称_网元类型，去xml中读取配置
							xmlPath = equimentDataUtil.getXmlPathByCardName(map.get(slotInst.getNumber())+"_"+siteService.select(ConstantUtil.siteId).getCellType());
							if (xmlPath != null && !xmlPath.equals("")) {
								cardInst = equimentDataUtil.addCard(xmlPath, slotInst);
								cardService.saveOrUpdate(cardInst);
							}
						} else {
							flag = true;
						}
					}
				}
				if (flag) {
					this.shelfTopology.createTopo();
				}
			}
		} catch (Exception e2) {
			ExceptionManage.dispose(e2,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(cardService);
		}
	}

}
