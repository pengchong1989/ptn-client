package com.nms.ui.ptn.ne.reset.controller;

import java.util.ArrayList;
import java.util.List;
import com.nms.db.bean.equipment.card.CardInst;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.ne.reset.view.ResetPanel;

public class ResetController extends AbstractController{

	private ResetPanel view;
	public ResetController (ResetPanel resetPanel){
		this.setView(resetPanel);
	}
	@Override
	public void refresh() throws Exception {
		searchAndrefreshdata();
	}
	private void searchAndrefreshdata(){
		
		CardService_MB cardService = null;
		List<CardInst> list = null;
		CardInst cardInst = null;
		List<CardInst> cardInsts = null;
		try{
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			cardInst = new CardInst();
			cardInst.setSiteId(ConstantUtil.siteId);
			list = cardService.select(cardInst);
			cardInsts = new ArrayList<CardInst>();
			for(CardInst inst : list){
				if(inst.getCardName().contains("MCU") ||inst.getCardName().contains("703") || inst.getCardName().contains("XCTS1")||inst.getCardName().contains("XCTO1")
						 ||"CSG T2000_CARD".equals(inst.getCardName())|| "SP16".equals(inst.getCardName())	
				){
					cardInsts.add(inst);
				}
			}
			view.clear();
			view.initData(cardInsts);
			view.updateUI();
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(cardService);
			list = null;
		}
	}
	public ResetPanel getView() {
		return view;
	}
	public void setView(ResetPanel view) {
		this.view = view;
	}
	
}
