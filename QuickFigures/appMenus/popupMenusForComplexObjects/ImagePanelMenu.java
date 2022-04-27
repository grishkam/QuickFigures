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
 * Date Modified: April 25, 2022
 * Version: 2022.0
 */
package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicTools.ArrowGraphicTool;
import graphicTools.BarGraphicTool;
import graphicTools.ShapeGraphicTool;
import graphicTools.RectGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.SourceImageTreeIcon;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import locatedObject.RectangleEdges;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.InsetTool;
import undo.AbstractUndoableEdit2;
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
			JMenu mc=new SmartJMenu("This Image", new SourceImageTreeIcon());
			MultiChannelImageDisplayPopup everyMenu = new MultiChannelImageDisplayPopup(m, m.getPanelList(), imagePanel);
			everyMenu.addMenus(mc);
			this.add(mc);
		}
		
	
		/**Creates a menu option for adding a scale bar*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public CombinedEdit performAction() {
				return addScaleBar();
				}	
	}.createJMenuItem("Add Scale Bar"));
		
		/**Creates a menu option for add a label*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public CombinedEdit performAction() { return addText();}	
	}.createJMenuItem("Add Text", ComplexTextGraphic.createImageIcon()));
		
	
		
	addShapeSubmenu();
	
	SmartJMenu expert = new SmartJMenu("Expert Options");
	add(expert);
	expert.add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public AbstractUndoableEdit2 performAction() {item.showCroppingDialog();return null;}	
}.createJMenuItem("Crop Only This Panel"));


		super.setLockedItem(c);
		super.addLockedItemMenus();
	}



	/**Creates a submenu for adding shapes, user should be able to add*/
	public void addShapeSubmenu() {
		SmartJMenu s = new SmartJMenu("Add Shape ");
		Color color = imagePanel.getFigureType().getForeGroundDrawColor();
		s.add(new PanelShapeAdder(new ArrowGraphicTool(), imagePanel, imagePanel.getParentLayer(), color));
		s.add(new PanelShapeAdder(new RectGraphicTool(), imagePanel, imagePanel.getParentLayer(), color));
		s.add(new PanelShapeAdder(new ShapeGraphicTool(new CircularGraphic(null)), imagePanel, imagePanel.getParentLayer(), color));
		
		/**Creates a menu option for add a label*/
		s.add(new ObjectAction<ImagePanelGraphic>(imagePanel) {
			@Override
			public CombinedEdit performAction() { return addInset();}

			
	}.createJMenuItem("Add ROI and Inset Panels"));
		this.add(s);
	}


	/**adds a label to the image panel
	 * @return */
	protected CombinedEdit addText() {
		CombinedEdit undo=new CombinedEdit();
		String name=null;
	
			name="Panel";
		
		ComplexTextGraphic text = new ComplexTextGraphic(name);
		text.setTextColor(imagePanel.getFigureType().getForeGroundDrawColor());
		Text_GraphicTool.lockAndSnap(imagePanel, text, (int)imagePanel.getLocationUpperLeft().getX(), (int) imagePanel.getLocationUpperLeft().getY());
		
		undo.addEditToList(new UndoAddOrRemoveAttachedItem(imagePanel, text, false));
		GraphicLayer parentLayer = imagePanel.getParentLayer();
		undo.addEditToList(Edit.addItem(parentLayer, text));
		
		
		return undo;
	}
	
	


	/**Adds a scale bar tot he image panel
	 * @return */
	public CombinedEdit addScaleBar() {
		CombinedEdit undo = new CombinedEdit();
		createScaleBar(undo, imagePanel);
		
		return undo;
	}



	/**Creates a scale bar and adds it to the image panel
	 * @param undo
	 * @param imagePanel 
	 * @return 
	 */
	public static BarGraphic createScaleBar(CombinedEdit undo, ImagePanelGraphic imagePanel) {
		GraphicLayer parentLayer = imagePanel.getParentLayer();
		if(parentLayer instanceof MultichannelDisplayLayer) {
			parentLayer=parentLayer.getParentLayer();
		}
		BarGraphicTool.getCurrentBarTool().addBarGraphic(imagePanel, parentLayer, undo, (int)imagePanel.getBounds().getMaxX(),(int)imagePanel.getBounds().getMaxY());
		
		BarGraphic scaleBar = imagePanel.getScaleBar();
		Color c = imagePanel.getFigureType().getForeGroundDrawColor();
		scaleBar.setFillColor(c);
		scaleBar.getBarText().setTextColor(c);
		return scaleBar;
	}
	
	/**Creates an inset*/
	private CombinedEdit addInset() {
		ArrayList<PanelGraphicInsetDefiner> old =PanelGraphicInsetDefiner.getInsetDefinersFromLayer(imagePanel.getParentLayer());
		RectangularGraphic s=null;
		if(old.size()>0)
			s=old.get(0);
		
		CombinedEdit output = new CombinedEdit();
		InsetTool iTool = new InsetTool();
		iTool.setupToolForImagePanel(imagePanel);
		iTool.undo=output;
		
		
		Point2D c = imagePanel.getCenterOfRotation();
		Point2D d = imagePanel.getLocationUpperLeft();
		d=ShapeGraphic.midPoint(c, d);
	
		Rectangle2D newRect=new Rectangle2D.Double(d.getX(), d.getY(), c.getX()-d.getX(), c.getY()-d.getY());
		Rectangle newRect2 = newRect.getBounds();
		
		if(s!=null) {
			
			newRect2.width=(int) s.getRectangle().width;
			newRect2.height=(int) s.getRectangle().height;
			if(!imagePanel.getBounds().contains(newRect2))
				newRect2 = newRect.getBounds();
		}
		
		
		Point clickPoint = getMemoryOfMouseEvent().getCoordinatePoint();
		RectangleEdges.setLocation(newRect2, RectangleEdges.CENTER, clickPoint.x, clickPoint.y);
		if(imagePanel.getBounds().contains(newRect2))
			newRect=newRect2;
		c=RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, newRect);
		d=RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, newRect);
		
		iTool.refreshInsetOnMouseDrag(c, d);
		
		
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			output.addEditToList(
					new CanvasAutoResize(false).performUndoableAction( getMemoryOfMouseEvent().getAsDisplay())
			);
		
		return output;
	}	
}
