package com.nms.ui.ptn.statistics.performance;
	
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import twaver.Card;
import twaver.Element;
import twaver.Node;
import twaver.Port;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.VisibleFilter;
import twaver.tree.ElementNode;
import twaver.tree.TTree;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.system.Field;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.manager.xmlbean.EquipmentType;
	
	/**
	 *  端口   树  的 panel: 
	 *   
	 * @author sy
	 *
	 */
public class	PortTree  extends JScrollPane {
	private static final long serialVersionUID = -6767156953328928386L;
	private TDataBox box;  
	private TTree tree;
	
	public  PortTree(){
		init();
		addPortInit();
	}
	public void init() {
		initComponents();
		initData();
		addNodeListener();
	}	
	private void initData() {
		box.clear();
		initTree(box);
	}
	public Element getElement(TreeExpansionEvent e){
		TreePath path = e.getPath();
		  if (path != null) {
		    Object comp = path.getLastPathComponent();
		    if (comp instanceof ElementNode) {
		      ElementNode node = ((ElementNode) comp);
		      return node.getElement();
		    }
		  }
		  return null;
	}
	/**
	 *   树的 展开节点事件，（加以控制）
	 *  TreeExpansionListener  expoansionListener
	 */
	private  TreeExpansionListener expoansionListener=new TreeExpansionListener() {	
		@Override
		public void treeExpanded(TreeExpansionEvent event) {				
			Element element=getElement(event);
			if(element!=null){
				if(element instanceof Node){					
					if(element.getParent()!=null){					
					//   取出网元 节点，添加   板卡 （查 DB）
			if(element.getUserObject() instanceof SiteInst){					
				//遍历此网元的子节点
				if(element.getChildren()!=null&&element.getChildren().size()>0){
					for(int i=0;i<element.getChildren().size();i++){
										
						/**
						 * 删除 所有 子节点
						 * 并在 删除最后一个子节点时，添加 查询到的板块（通过网元） 
						 */
						if(i==element.getChildren().size()-1){
							Node node=(Node)element;
							addCard(node);
							Element e=(Element) element.getChildren().get(i);
							tree.getDataBox().removeElement(e);
							i--;
							break;
						}
						Element e=(Element) element.getChildren().get(i);
						tree.getDataBox().removeElement(e);
						i--;				
					}
				}
			}
			//取出 板卡 节点，添加   端口 
			else if(element.getUserObject() instanceof CardInst){
				//遍历此板卡的子节点
				if(element.getChildren()!=null&&element.getChildren().size()>0){
					for(int i=0;i<element.getChildren().size();i++){
						/**
						 * 删除 所有 子节点
						 * 并在 删除最后一个子节点时，添加 查询到的板块（通过网元） 
						 */
						if(i==element.getChildren().size()-1){
							Node node=(Node)element;
							addPort(node);
							Element e=(Element) element.getChildren().get(i);
							tree.getDataBox().removeElement(e);
							i--;
							break;
						}
						Element e=(Element) element.getChildren().get(i);
						tree.getDataBox().removeElement(e);
						i--;									
					}
				}
			}
			//取出端口节点，添加  性能类型
			else if(element.getUserObject() instanceof PortInst){
				//遍历此板卡的子节点
				if(element.getChildren()!=null&&element.getChildren().size()>0){
					for(int i=0;i<element.getChildren().size();i++){
						/**
						 * 删除 所有 子节点
						 * 并在 删除最后一个子节点时，添加 查询到的板块（通过网元） 
						 */
						if(i==element.getChildren().size()-1){
							Node node=(Node)element;
							addCapability(node);
							Element e=(Element) element.getChildren().get(i);
							tree.getDataBox().removeElement(e);
							i--;
							break;
						}
						Element e=(Element) element.getChildren().get(i);
						tree.getDataBox().removeElement(e);
						i--;									
					}
				}
			}
			// 选择  域时
			else if(element.getUserObject() instanceof Field){
				box.clear();
				initTree(box);
			}
					}
				}					
			}				
		}		
	//合并 时
		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
		}
	};
	/**
	 * 添加   节点事件
	 */
	
	private void addNodeListener(){
		this.tree.addTreeExpansionListener(expoansionListener);
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				try {
							ElementNode note=(ElementNode) ((TTree) e.getSource()).getLastSelectedPathComponent();
							if(note!=null){
								isSelect(note.getElement());
							}
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
		});
	}
	/**
	 * 添加  性能 类型
	 * @param node
	 * 		端口
	 */
	public void addCapability(Node node){
		CapabilityService_MB capabilityService=null;
		Capability capability=null;
		SiteInst site=null;
		PortInst port=null;
		List<Capability> capabilityList=new ArrayList<Capability>();
		try {
			capabilityService = (CapabilityService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.Capability);
			if(node!= null&&node.getUserObject() instanceof PortInst){
				if(node.getParent().getParent()!=null&&node.getParent().getParent().getUserObject() instanceof SiteInst){
					 site=(SiteInst)node.getParent().getParent().getUserObject();
				}
				 port=(PortInst)node.getUserObject();
				
			}
			/**
			 *选中的 端口 不为空
			 *    并且  其父的父节点（网元）不为空
			 */
			if(site!=null&&port!=null){
				/**
				 * 判定
				 * 	端口的类型
				 */
				capability=new Capability();
				if(site.getManufacturer()==1){
					//陈晓
					capability.setManufacturer(2);
				}else{
					//武汉
					capability.setManufacturer(1);
				}
				if(capability.getManufacturer()==2){
					if(port.getPortType().equals("NNI")||port.getPortType().equals("UNI")||port.getPortType().equals("NONE")||port.getPortType().equals("UNDE")){
						//属于 ETH 类型
						capability.setCapabilitytype("ETH");
					}else  if(port.getPortType().equals("STM1")){
						capability.setCapabilitytype("STM1");
					}else  if(port.getPortType().equals("e1")){
						capability.setCapabilitytype("PDH");
					}
				}else{
					capability.setCapabilitytype("PORT");
				}
				
				capabilityList = capabilityService.selectCapaName(capability);
				if(capabilityList!=null&&capabilityList.size()>0){
					for (Capability capab : capabilityList) {
						Node capaNode=new Node();
						capaNode.setSelected(false);
						capaNode.setVisible(true);
						capaNode.setUserObject(capab);
						capaNode.setParent(node);
						capaNode.setName(capab.getCapabilityname());
						box.addElement(capaNode);
					}
				}
			}			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(capabilityService);
		}		
	}
	/**
	 * 添加  端口
	 * @param node
	 */
	public void addPort(Node node){
		CardInst cardInst=(CardInst)node.getUserObject();
		PortService_MB portService=null;
		List<PortInst> portList=new ArrayList<PortInst>();
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.PORT);
			PortInst portInst=new PortInst();
			portInst.setCardId(cardInst.getId());
			portInst.setPortType("system");
			portList = portService.selectPortbyName(portInst);
			if(portList!=null&&portList.size()>0){
				for (PortInst port_inst : portList) {
					
					Port port=new Port();
					port.setSelected(false);
					port.setVisible(true);
					port.setUserObject(port_inst);
					port.setParent(node);
					port.setName(port_inst.getPortName());
					box.addElement(port);
					/**
					 * 端口 下  添加 站位 节点（性能类型）
					 */
					Node capabilityNode=new Node();
					capabilityNode.setParent(port);
					box.addElement(capabilityNode);					
				}
			}			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
		
	}
	/**
	 * 添加 板卡
	 * @param node
	 */
	public void addCard(Node node){
		SiteInst site = (SiteInst) node.getUserObject();
		CardService_MB cardService=null;
		List<CardInst> cardList = new ArrayList<CardInst>();
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.CARD);
			CardInst cardInst=new CardInst();
			cardInst.setSiteId(site.getSite_Inst_Id());
			cardList = cardService.select(cardInst);
			if(cardList!=null&&cardList.size()>0){
				for (CardInst card_inst : cardList) {
					if (!"FAN".equals(card_inst.getCardName())&&!"PSU".equals(card_inst.getCardName())) {
						Card card = new Card();
						card.setSelected(false);
						card.setVisible(true);
						card.setUserObject(card_inst);
						card.setParent(node);
						card.setName(card_inst.getCardName());
						box.addElement(card);
						//选择  端口时，,添加板卡同时，要添加  端口 站位
						Node portNode=new Node();
						portNode.setParent(card);
						portNode.setVisible(true);
						box.addElement(portNode);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(cardService);
		}
	}
	/**
	 * 显示不同的界面（并且 添加   子节点站位 ）
	 * @param flag
	 * 		 false  板卡，端口
	 * 			true 网元
	 * @throws Exception 
	 */
	public void addTestNode(String name) throws Exception{
		@SuppressWarnings("unchecked")
	List<Element> elementList=this.tree.getDataBox().getAllElements();
	if(elementList!=null&&elementList.size()>0){
		//遍历  tree中 的 元素
		for(Element element: elementList){
			if(element instanceof Node){					
				//  为  网元 或者 板卡
				if(element.getParent()!=null){
					if(ResourceUtil.srcStr(StringKeysObj.NET_BASE).equals(name)){
						//存在   板卡时
						if(element.getChildren()!=null&&element.getChildren().size()>0){
							//移除
							for(int j=0;j<element.getChildren().size();j++){
								element.removeChild((Element) element.getChildren().get(j));
							}
						}		
					}						
					else{
		
						/**
						 * 为每一个网元添加 板卡
						 * 板卡  添加后，且  选择端口时，端口才添加到板卡下
						 * 			即，端口添加在  （添加板卡里面）
						 */
							Node node=(Node)element;
							 addTest(node,box);
						}
					}else {
						if(ResourceUtil.srcStr(StringKeysObj.NET_BASE).equals(name)){
							box.clear();
							initTree(box);
						}
					}
				}					
			}
			
		}
	}
	/**
	 * 创建节点(站位，，展开事件可以使界面看不到这个子节点)
	 * 
	 * @param node
	 *            板卡父节点，即网元节点
	 * @param box
	 *            TDatabox
	 * @throws Exception
	 */
	private  Node addTest(Node node, TDataBox box) throws Exception {
		Node test=new Node();
		test.setVisible(true);
		test.setParent(node);
		box.addElement(test);
		return test;
	}
	/**
	 * 补充
	 * 		性能模块  
	 * 			： 因为没有 选择框 事件
	 * 		所以：
	 * 			树直接显示到  端口 （所在的子节点）
	 */
	public void addPortInit(){
		TopologyUtil topologyUtil=new TopologyUtil();
		//移除事件监听
	tree.removeTreeExpansionListener(expoansionListener);
	tree.setCheckableFilter(topologyUtil.PortAndCapabilityFilter);	
	tree.getDataBox().getSelectionModel().clearSelection();						
	PortTree.this.getVerticalScrollBar().setValue(0);
	// 合并
	tree.collapseAll();
	// 添加    展开事件监听
		addNodeListener();
	tree.addVisibleFilter(new VisibleFilter() {
		@Override
		public boolean isVisible(Element element) {
			if(element instanceof Card){
				if(element.getName().equals("710_LEFT")){
					return false;
				}
			}
			return true;
		}
	});
	}
	/**
	 * 验证输入数据的正确性
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean validateParams(){
		Iterator it = box.getSelectionModel().selection();
		if(!it.hasNext()){
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_ALARMOBJ));
			return false;
		}
		return true;
	}
	
	private void initComponents() {
		TopologyUtil topologyUtil=new TopologyUtil();
		box = new TDataBox(ResourceUtil.srcStr(StringKeysObj.PTN_MANAGE_SYSTEM));
		tree = new TTree(box);
		tree.setRootVisible(false);
		tree.setTTreeSelectionMode(TTree.CHECK_CHILDREN_SELECTION);
		tree.setCheckableFilter(topologyUtil.SiteInstFilter);
		tree.getDataBox().getSelectionModel().clearSelection();
		this.add(tree);
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(tree);
	}
	/**
	 * 创建过滤条件树
	 */
	public  void initTree(TDataBox box) {
		FieldService_MB fieldService = null;
		List<Field> fieldList = null;
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.Field);
			fieldList = fieldService.select();
			for (int i = 0; i < fieldList.size(); i++) {
				addDomain(box, fieldList.get(i), new Color(100, 100, 240));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
		}
	}
	
	/**
	 * 创建域节点，挂载在树上
	 * 
	 * @param box
	 * @param field
	 * @param color
	 */
	private  void addDomain(TDataBox box, Field field, Color color) {
		SubNetwork domain = null;
		try {
			domain = new SubNetwork(field.getFieldName());
			domain.setName(field.getFieldName());
			domain.setLocation(field.getFieldX(), field.getFieldY());
			domain.putLabelColor(color);
			domain.setUserObject(field);
			box.addElement(domain);
			addSite(domain, box);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			domain = null;
		}
	}
	
	/**
	 * 根据域，创建网元节点，添加到box中
	 * 
	 * @param domain
	 *            域节点
	 * @param box
	 *            TDatabox
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private  void addSite(SubNetwork domain, TDataBox box)
			throws Exception {
		Field field = null;
		Node node = null;
		
		String path = null;
		List<String> strList = null;
		List<Element> elementList = null;
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		EquipmentType equipmentType = null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			strList = new ArrayList<String>();
			elementList = domain.getChildren();
			for (int i = 0; i < elementList.size(); i++) {
				strList.add(elementList.get(i).getID().toString());
			}
	
			for (int i = 0; i < strList.size(); i++) {
				box.removeElementByID(strList.get(i));
			}
	
			field = (Field) domain.getUserObject();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.SITE);
			SiteInst siteInst = new SiteInst();
			siteInst.setFieldID(field.getId());
			siteInstList = siteService.select(siteInst);
			for (int i = 0; i < siteInstList.size(); i++) {
				node = new Node();
				node.setName(siteInstList.get(i).getCellId());
				node.setLocation(siteInstList.get(i).getSiteX(), siteInstList
						.get(i).getSiteY());
				node.setParent(domain);
				equipmentType = equimentDataUtil.getEquipmentType(
						siteInstList.get(i).getCellType());
	
				if (null != equipmentType) {
					path = equipmentType.getImagePath();
				}
				node.setImage(path + "ne_0.png");
				node.setUserObject(siteInstList.get(i));
				box.addElement(node);	
				// 如 没有 复选框，则 添加 站位
				
				Node cardNode=new Node();
				cardNode.setParent(node);
				cardNode.setVisible(true);
				box.addElement(cardNode);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}
	/**
	 * 树的选中事件
	 * 		长期性能） 只能选中一个
	 */
	@SuppressWarnings("unchecked")
	public void isSelect(Element node){
			if(node.getUserObject()!=null&& node.getUserObject() instanceof Capability){
				if(node.isSelected()){
					if(node.getParent()!=null&&node.getParent().getUserObject() instanceof PortInst){
						Element parentElement= (Element) node.getParent().getUserObject();
						parentElement.setSelected(true);						
					}
				}
				
			}
			else if(node.getUserObject()!=null&& node.getUserObject() instanceof PortInst){
				if(node.isSelected()){
					List<Element> chirElement=node.getChildren();
					if(chirElement!=null&&chirElement.size()>0){
						for(Element child:chirElement){
							child.setSelected(true);
						}
					}
				}
			
		}
		
//			List<Element> elementList=this.box.getAllElements();
//			try{
//				if(elementList!=null&&elementList.size()>0){
//					for(Element element:elementList){
//						if(element instanceof Node){
//							Node node=(Node)element;
//							//选中端口时
//							if(node instanceof Port){
//								//端口被选中，子节点 性能类型都选中
//								if(node.isSelected()){
//									if(node.getChildren()!=null&&node.getChildren().size()>0){
//										for(int i=0;i<node.getChildren().size();i++){
//											Element childElement=(Element) node.getChildren().get(i);
//											childElement.setSelected(true);
//										}
//									}
//								}
//							}
//							//选中 性能类型
//							else if(node.getUserObject() instanceof Capability){
//								//性能类型 被选中： 父节点，端口也要选中
//								if(node.isSelected()){
//									node.getParent().setSelected(true);
//								}
//							}
//						}
//					}
//				}
//			}catch (Exception e) {
//				ExceptionManage.dispose(e, this.getClass());
//			} finally {
//				elementList = null;
//			}
//		
	}
	/**
	 * 获取所有选中的菜单
	 * 
	 * @return 选中的菜单集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getSelectObject()  {
		List<Element> object = null;
		List<Element> elementList = null;
		try {
			object = new ArrayList<Element>();
			elementList = this.box.getAllElements();
			// 遍历box上面所有元素
			for (Element element : elementList) {
				// 如果元素为Node
					if (element instanceof Node) {
						// 如果是选中的，就放入结果集中。
						if (element.isSelected()) {
							object.add(element);
						}
					}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elementList = null;
		}
		return object;
	}
	public TDataBox getBox() {
		return box;
	}
	
	public void setBox(TDataBox box) {
		this.box = box;
	}
}
