package addObjectMenus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import appContext.CurrentAppContext;
import exportMenus.SVGQuickExport;
import genericMontageUIKit.ToolBit;
import graphicTools.RectGraphicTool;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.SVG_GraphicAdder2;
import standardDialog.GraphicDisplayComponent;
import standardDialog.GraphicJMenuItem;
import standardDialog.DisplaysGraphicalObject;
import utilityClassesForObjects.LobeMaker;

/**The menus used to add graphics to graphic layers*/
public class ObjectAddingMenu extends SmartJMenu implements KeyListener {
	
	
	static ArrayList <GraphicAdder> adders= new ArrayList <GraphicAdder>(); 
	static ArrayList <GraphicAdder> layoutadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> imageadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> imagePlusAdders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> kayadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> cartoonadders= new ArrayList <GraphicAdder>();
	
	public static ArrayList <AddingMenuInstaller> bonusAdders=new ArrayList<AddingMenuInstaller>();
	
	static boolean addersMade=false;

	
	public static void addItemInstaller(AddingMenuInstaller n) {
		n.installOntoMenu(null);
	}
	
	
	{
		if (!addersMade) {
			
			//adders.add(new layerSetAdder());
			addShapeAdders();
			
			adders.add(new LayerAdder());
		
			adders.add(new ArrowGraphicAdder());
			
			
			adders.add(new BarGraphicAdder());
			//adders.add(new SavedGraphicAdder());
			if (new SVGQuickExport().isBatikInstalled())
				adders.add(new SVG_GraphicAdder2());
			
			
			//adders.add(new layoutAdder());
			adders.add(new GroupAdder());
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
			adders.add(new TextItemAdder(true)); 
			adders.add(new TextItemAdder(false)); 
			for(AddingMenuInstaller bonus:  bonusAdders) try {bonus.installOntoMenu(this);} catch (Throwable t) {IssueLog.logT(t);}
			addersMade=true;
			}
	}

	/**
	 Adds many items to the add shapes menu. content determined by object toolset1
	 */
	protected void addShapeAdders() {
		
		extractShapeAddersFrom(ObjectToolset1.getRectangularShapeGraphicBits(), "Rectangular");
		extractShapeAddersFrom(ObjectToolset1.getCircularShapeGraphicBits(), "Circular");
		extractShapeAddersFrom(ObjectToolset1.getRegularPolygonShapeTools(), "Polygonal");
		
	}

	/**
	 Takes the rectangle graphic tools from the list of tool bits and creates an adder from each of them
	 */
	protected void extractShapeAddersFrom(ArrayList<ToolBit> list, String sName) {
		for(ToolBit i: list) {
			if (i instanceof RectGraphicTool)
				adders.add(new RectangleAdder((RectGraphicTool) i, sName));
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
	
		JMenuItem jmi=new AddingMenuItem(ad);
		
		if (ad instanceof DisplaysGraphicalObject) {
			GraphicJMenuItem jmi2 = new GraphicJMenuItem(ad.getMenuCommand());
			GraphicDisplayComponent g2 = jmi2.getDisplayedGraphicalObject();
			g2.setCurrentDisplayObject(((DisplaysGraphicalObject)ad).getCurrentDisplayObject());
			jmi=jmi2;
		}
			JMenu menu1 = super.getOrCreateSubmenuFromPath(ad, this);
			menu1.add(jmi);
	}


	
	class AddingMenuItem extends JMenuItem implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private GraphicAdder ad;

		public AddingMenuItem(GraphicAdder ad) {
			super(ad.getMenuCommand(), ad.getIcon());
			this.ad=ad;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setupAdderAndRun(ad);
			selector.getGraphicDisplayContainer().updateDisplay();
			
		}}
	
	 void setupAdderAndRun(GraphicAdder ad) {
		
				ad.setSelector(selector);
				ad.run();
				}
	

	
	
	public static ObjectAddingMenu getStandardAddingMenu(LayerSelector selection) {
		ObjectAddingMenu output = new ObjectAddingMenu("Add",selection, adders);
		ObjectAddingMenu lad = new ObjectAddingMenu("Layout", selection, layoutadders);
		lad.setIcon(PanelLayoutGraphic.createImageIcon());
		ObjectAddingMenu iad = new ObjectAddingMenu("Image Panel", selection, imageadders);
		iad.setIcon(ImagePanelGraphic.createImageIcon());
		
		ObjectAddingMenu cad = new ObjectAddingMenu("Cell Cartoons", selection,  cartoonadders);
		//lad.setIconTextGap(-5);
		output.insert( lad, 2);
		
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
			
			} catch (Throwable t) {IssueLog.logT(t);}
		
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
