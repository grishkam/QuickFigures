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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicTools.ArrowGraphicTool;
import graphicTools.BarGraphicTool;
import graphicTools.ShapeGraphicTool;
import graphicTools.RectGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoAddOrRemoveAttachedItem;

/**Menu for Image Panels. Displayed  when user right clicks on a panel*/
public class ImagePanelMenu extends AttachedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic imagePanel;

	
	public ImagePanelMenu(ImagePanelGraphic c) {
		this.imagePanel=c;
		
		/**If the panel is inside of a figure, adds a few submenus*/
		FigureOrganizingLayerPane folp = FigureOrganizingLayerPane.findFigureOrganizer(c);
		if (folp!=null) {
			
			JMenu channelMenu=new SmartJMenu("Channels"); 
			this.add(channelMenu);
			new ChannelPanelEditingMenu(c). addChannelRelevantMenuItems(channelMenu);
			
			JMenu extendFigure=new SmartJMenu("Figure");
			new FigureOrganizingSuplierForPopup(folp).addMenus(extendFigure);
			this.add(extendFigure);
			
		}
		
		
		/**If the panel is part of a MultichannelDisplayLayer, adds another submenu*/
		if (c.getParentLayer() instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) c.getParentLayer();
			JMenu mc=new SmartJMenu("This Image");
			MultiChannelImageDisplayPopup everyMenu = new MultiChannelImageDisplayPopup(m, m.getPanelList(), imagePanel);
			everyMenu.addMenus(mc);
			this.add(mc);
		}
		
	
		/**Creates a menu option for adding a scale bar*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addScaleBar();}	
	}.createJMenuItem("Add Scale Bar"));
		
		/**Creates a menu option for add a label*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addText();}	
	}.createJMenuItem("Add Text"));
		
		
		
	addShapeSubmenu();
	
	SmartJMenu expert = new SmartJMenu("Expert Options");
	add(expert);
	expert.add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public void actionPerformed(ActionEvent arg0) {item.showCroppingDialog();}	
}.createJMenuItem("Crop Only This Panel"));


super.setLockedItem(c);
super.addLockedItemMenus();
	}



	/**Creates a submenu for adding shapes, user should be able to add*/
	public void addShapeSubmenu() {
		SmartJMenu s = new SmartJMenu("Add Shape ");
		s.add(new PanelShapeAdder(new ArrowGraphicTool(), imagePanel, imagePanel.getParentLayer()));
		s.add(new PanelShapeAdder(new RectGraphicTool(), imagePanel, imagePanel.getParentLayer()));
		s.add(new PanelShapeAdder(new ShapeGraphicTool(new CircularGraphic(null)), imagePanel, imagePanel.getParentLayer()));
		this.add(s);
	}


	/**adds a label to the image panel*/
	protected void addText() {
		CombinedEdit undo=new CombinedEdit();
		String name=null;
	
			name="Panel";
		
		ComplexTextGraphic text = new ComplexTextGraphic(name);
		text.setTextColor(Color.white);
		Text_GraphicTool.lockAndSnap(imagePanel, text, (int)imagePanel.getLocationUpperLeft().getX(), (int) imagePanel.getLocationUpperLeft().getY());
		
		undo.addEditToList(new UndoAddOrRemoveAttachedItem(imagePanel, text, false));
		GraphicLayer parentLayer = imagePanel.getParentLayer();
		undo.addEditToList(Edit.addItem(parentLayer, text));
		
		
		imagePanel.getUndoManager().addEdit(undo);
	}


	/**Adds a scale bar tot he image panel*/
	public void addScaleBar() {
		CombinedEdit undo = new CombinedEdit();
		BarGraphicTool.getCurrentBarTool().addBarGraphic(imagePanel, imagePanel.getParentLayer(), undo, (int)imagePanel.getBounds().getMaxX(),(int)imagePanel.getBounds().getMaxY());
		imagePanel.getUndoManager().addEdit(undo);
	}
	
}
