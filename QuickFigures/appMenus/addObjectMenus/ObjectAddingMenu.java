/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: October, 2021
 * Version: 2021.2
 */
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
import genericTools.ToolBit;
import graphicTools.RectGraphicTool;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.QuickFigureIcon;
import includedToolbars.ObjectToolset1;
import layersGUI.GraphicTreeUI;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import objectCartoon.LobeMaker;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MirrorObjects;
import selectedItemMenus.SVG_GraphicAdder2;
import standardDialog.graphics.DisplaysGraphicalObject;
import standardDialog.graphics.GraphicDisplayComponent;
import standardDialog.graphics.GraphicJMenuItem;
import textObjectProperties.TextPattern;

/**The menu used to add graphics to graphic layers*/
public class ObjectAddingMenu extends SmartJMenu implements KeyListener {
	
	
	static ArrayList <GraphicAdder> adders= new ArrayList <GraphicAdder>(); 
	static ArrayList <GraphicAdder> layoutadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> imageadders= new ArrayList <GraphicAdder>();
	static ArrayList <GraphicAdder> figureAdders= new ArrayList <GraphicAdder>();
	
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
			
			
			
			//adders.add(new layoutAdder());
			adders.add(new GroupAdder());
			imageadders.add(new FileImageAdder(false));
			imageadders.add(new ClipboardAdder(false));
			
			if (CurrentAppContext.getMultichannelContext()!=null) {
					figureAdders.add(new FigureAdder(false));
					figureAdders.add(new FigureAdder(true));
					figureAdders.add(new BlotFigureAdder(false));
					figureAdders.add(new BlotFigureAdder(true));
			}
			
		
			//adders.add(new PasteItem());
			cartoonadders.add(new CartoonPolygonAdder(1));
			cartoonadders.add(new CartoonPolygonAdder(0));
			cartoonadders.add(new CartoonPolygonAdder(2));
			cartoonadders.add(new CartoonPolygonAdder(3));
			cartoonadders.add(new CartoonPolygonAdder(4));
			cartoonadders.add(new CartoonPolygonAdder(5));
			cartoonadders.add(new CartoonPolygonAdder(6));
			cartoonadders.add(new CartoonPolygonAdder(7));
			cartoonadders.add(new CartoonPolygonAdder(8));
			cartoonadders.add(new CentriolePairCartoonAdder());
			cartoonadders.add(new ShapeMakerBasedAdder());
			cartoonadders.add(new ShapeMakerBasedAdder("Golgi", new LobeMaker(), Color.magenta));
			
			layoutadders.add(new LayoutAdder());
			layoutadders.add(new ImagePanelLayoutAdder());
			layoutadders.add(new PlasticLayoutAdder());
			layoutadders.add(new DividedLayoutAdder());
			adders.add(new TextItemAdder(true)); 
			
			adders.add(new TextItemAdder(false)); 
			
			TextPattern a = new TextPattern(TextPattern.PatternType.ABC, false);
			adders.add(new TextItemAdder(true, a, "", true));
			
			adders.add(new MirrorObjects());
		
			
			
			adders.add(new LaneLabelAdder()); 
			if (new SVGQuickExport().isBatikInstalled())
				adders.add(new SVG_GraphicAdder2());
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
	private LayerSelectionSystem selector;

	public ObjectAddingMenu(String name, LayerSelectionSystem selection, ArrayList<GraphicAdder> adders) {
		super(name);
		this.useradders=adders;
		this.selector=selection;
		for (GraphicAdder ad: adders) {
			addMenuItemForAdder(ad);
		}
	}
	
	public void addMenuItemForAdder(GraphicAdder ad) {
		if (selector!=null &&!ad.canUseObjects(selector))
				return;
		JMenuItem jmi=new AddingMenuItem(ad);
		
		/**certain items appear as images in the menu and not as menu items*/
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
			selector.getWorksheet().updateDisplay();
			
		}}
	
	 void setupAdderAndRun(GraphicAdder ad) {
		
				ad.setSelector(selector);
				ad.run();
				}
	

	
	
	public static ObjectAddingMenu getStandardAddingMenu(LayerSelectionSystem selection) {
		ObjectAddingMenu output = new ObjectAddingMenu("Add",selection, adders);
		
		ObjectAddingMenu lad = new ObjectAddingMenu("Empty Layout", selection, layoutadders);
		lad.setIcon(PanelLayoutGraphic.createImageIcon());
		ObjectAddingMenu iad = new ObjectAddingMenu("Raw Image Panel", selection, imageadders);
		iad.setIcon(ImagePanelGraphic.createImageIcon());
		
		ObjectAddingMenu cad = new ObjectAddingMenu("Cell Cartoons", selection,  cartoonadders);
		
		if (selection instanceof GraphicTreeUI) output.insert( lad, 2);
		
		if (CurrentAppContext.getMultichannelContext()!=null) {
				ObjectAddingMenu iad2 = new ObjectAddingMenu("Figure ", selection, figureAdders);
				iad2.setIcon(new QuickFigureIcon().getMenuVersion());
				output.insert( iad2, 2);
				}
		
		
		
		output.insert( iad, 2);
		output.insert( cad, 4);
		
		return output;
	}
	
	static ObjectAddingMenu getStandardKeyMenu(LayerSelectionSystem selection) {
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
