package addObjectMenus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import appContext.CurrentAppContext;
import exportMenus.SVGQuickExport;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.SVG_GraphicAdder2;
import standardDialog.GraphicDisplayComponent;
import standardDialog.GraphicJMenuItem;
import standardDialog.DisplaysGraphicalObject;
import undo.UndoAddItem;
import utilityClassesForObjects.LobeMaker;

/**The menus used to add graphics to graphic layers*/
public class ObjectAddingMenu extends SmartJMenu implements ActionListener, KeyListener {
	
	
	static ArrayList <GraphicAdder> adders= new ArrayList <GraphicAdder>(); 
	static ArrayList <GraphicAdder> layoutadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> imageadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> imagePlusAdders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> kayadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> cartoonadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> objectAdders= new ArrayList <GraphicAdder>();
	
	static ArrayList <AddingMenuInstaller> bonusAdders=new ArrayList<AddingMenuInstaller>();
	
	static boolean addersMade=false;
	//{setIconTextGap(-5);}
	
	public static void addItemInstaller(AddingMenuInstaller n) {
		n.installOntoMenu(null);
	}
	
	
	{
		if (!addersMade) {
			
			//adders.add(new layerSetAdder());
			objectAdders.add(new RectangleAdder());
			objectAdders.add(new OvalGraphicAdder());
			objectAdders.add(new TextItemAdder(true)); 
			objectAdders.add(new TextItemAdder(false)); 
			adders.add(new LayerAdder());
		
			objectAdders.add(new ArrowGraphicAdder());
			
			
			objectAdders.add(new BarGraphicAdder());
			//adders.add(new SavedGraphicAdder());
			if (new SVGQuickExport().isBatikInstalled())
				adders.add(new SVG_GraphicAdder2());
			
			
			//adders.add(new layoutAdder());
			objectAdders.add(new GroupAdder());
			imageadders.add(new FileImageAdder(false));
			imageadders.add(new ClipboardAdder(false));
			
			if (CurrentAppContext.getMultichannelContext()!=null) {
					//imagePlusAdders.add(new ImagePlusAdder());
					imagePlusAdders.add(new ImageAndlayerAdder(false));
					imagePlusAdders.add(new ImageAndlayerAdder(true));
					imagePlusAdders.add(new ImageAndlayerAdder(true, true));
			}
			
		
			//adders.add(new PasteItem());
			cartoonadders.add(new PolygonAdder(1));
			cartoonadders.add(new PolygonAdder(0));
			cartoonadders.add(new PolygonAdder(2));
			cartoonadders.add(new PolygonAdder(3));
			cartoonadders.add(new PolygonAdder(4));
			cartoonadders.add(new PolygonAdder(5));
			cartoonadders.add(new PolygonAdder(6));
			cartoonadders.add(new PolygonAdder(7));
			cartoonadders.add(new PolygonAdder(8));
			cartoonadders.add(new CentriolePairCartoonAdder());
			cartoonadders.add(new ShapeMakerBasedAdder());
			cartoonadders.add(new ShapeMakerBasedAdder("Golgi", new LobeMaker(), Color.magenta));
			
			layoutadders.add(new LayoutAdder());
			layoutadders.add(new ImagePanelLayoutAdder());
			layoutadders.add(new PlasticLayoutAdder());
			layoutadders.add(new DividedLayoutAdder());
			for(AddingMenuInstaller bonus:  bonusAdders) try {bonus.installOntoMenu(this);} catch (Throwable t) {IssueLog.log(t);}
			addersMade=true;
			}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<GraphicAdder> useradders;
	private LayerSelector selector;

	public ObjectAddingMenu(String name, LayerSelector selection, ArrayList<GraphicAdder> adders) {
		super(name);
		this.useradders=adders;
		this.selector=selection;
		for (GraphicAdder ad: adders) {
			addMenuItemForAdder(ad);
		}
	}
	
	public void addMenuItemForAdder(GraphicAdder ad) {
	
		JMenuItem jmi=new JMenuItem(ad.getMessage(), ad.getIcon());
		
		if (ad instanceof DisplaysGraphicalObject) {
			GraphicJMenuItem jmi2 = new GraphicJMenuItem(ad.getMessage());
			GraphicDisplayComponent g2 = jmi2.getDisplayedGraphicalObject();
			g2.setCurrentDisplayObject(((DisplaysGraphicalObject)ad).getCurrentDisplayObject());
			jmi=jmi2;
		}
			jmi.setActionCommand(ad.getCommand());
			//jmi.setIconTextGap(-5);
			
			add(jmi);
			jmi.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		for (GraphicAdder ad:useradders){
			
			if (ad.getCommand().equals(arg0.getActionCommand())) try {
				
				setupAdderAndRun(ad);
				selector.getGraphicDisplayContainer().updateDisplay();
			} catch (Throwable t) {IssueLog.log(t);}
		
		}
		
	}
	
	 void setupAdderAndRun(GraphicAdder ad) {
		//ad.setDisplay(selector.getGraphicDisplayContainer());
				ad.setSelector(selector);
				ZoomableGraphic item = ad.add(selector.getSelectedLayer());
				
				selector.getGraphicDisplayContainer().getUndoManager().addEdit(new UndoAddItem(selector.getSelectedLayer(), item));
	}
	

	
	
	public static ObjectAddingMenu getStandardAddingMenu(LayerSelector selection) {
		ObjectAddingMenu output = new ObjectAddingMenu("Add",selection, adders);
		ObjectAddingMenu lad = new ObjectAddingMenu("Layout", selection, layoutadders);
		lad.setIcon(PanelLayoutGraphic.createImageIcon());
		ObjectAddingMenu iad = new ObjectAddingMenu("Image Panel", selection, imageadders);
		iad.setIcon(ImagePanelGraphic.createImageIcon());
		ObjectAddingMenu iadO = new ObjectAddingMenu("Shape", selection, objectAdders);
		
		ObjectAddingMenu cad = new ObjectAddingMenu("Cell Cartoons", selection,  cartoonadders);
		//lad.setIconTextGap(-5);
		output.insert( lad, 2);
		output.insert(iadO,2);
		
		if (CurrentAppContext.getMultichannelContext()!=null) {
				ObjectAddingMenu iad2 = new ObjectAddingMenu("Figure ", selection, imagePlusAdders);
				iad2.setIcon(ImagePanelGraphic.createImageIcon());
				output.insert( iad2, 3);
				}
		
		
		
		//iad.setIconTextGap(-5);
		output.insert( iad, 3);
		output.insert( cad, 5);
		
		return output;
	}
	
	static ObjectAddingMenu getStandardKeyMenu(LayerSelector selection) {
		return null;
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
for (GraphicAdder ad:useradders){
			
			if (ad.getKey()!=null&&ad.getKey()==arg0.getKeyChar()) try {
				
				setupAdderAndRun(ad);
			
			} catch (Throwable t) {IssueLog.log(t);}
		
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
