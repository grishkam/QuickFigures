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
 * Date Modified: May 10, 2023
 * Version: 2023.2
 */
package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import actionToolbarItems.EditScaleBars;
import addObjectMenus.BarGraphicAdder;
import channelMerging.ImageDisplayLayer;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicTools.ArrowGraphicTool;
import graphicTools.BarGraphicTool;
import graphicTools.RectGraphicTool;
import graphicTools.ShapeGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.PanelMirror;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.CropAreaHandle;
import handles.ImagePanelHandleList;
import handles.SmartHandle;
import iconGraphicalObjects.CropIconGraphic;
import icons.InsetToolIcon;
import icons.SourceImageTreeIcon;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import messages.ShowMessage;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.InsetTool;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoAddOrRemoveAttachedItem;
import utilityClasses1.ArraySorter;

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
			
			mc.add(createCropModeMenuItem(c));

		}
		
	
		/**Creates a menu option for adding a scale bar*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public CombinedEdit performAction() {
				return addScaleBar();
				}	
	}.createJMenuItem("Add Scale Bar", EditScaleBars.getExampleIcon()));
		
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

	
	
		add(new OverlaySubmenu(c));
	
	
	
		super.setLockedItem(c);
		super.addLockedItemMenus();
	}



	/**
	 * @param c
	 * @return
	 */
	public static JMenuItem createCropModeMenuItem(ImagePanelGraphic c) {
		return new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				c.select();
				CropAreaHandle.addCropHandles(c, true);
				ShowMessage.showOptionalMessage("Crop handle instructions", true, "Drag Right, Left, Top and Bottom Handles to adjust crop area for this set of images", "Drag the lower right corner to scale the crop area", "If you have multiple sets of images selected, you can align their crop areas", "Panel handles will return to normal if panel is deselected", "this feature is still new!");
				;return null;}

			
		}.createJMenuItem("Crop by dragging handles", CropIconGraphic.createsCropIcon());
	}



	/**Creates a submenu for adding shapes, user should be able to add*/
	public void addShapeSubmenu() {
		SmartJMenu s = new SmartJMenu("Add Shape ");
		Color color = imagePanel.getFigureType().getForeGroundDrawColor();
		s.add(new PanelShapeAdder(new ArrowGraphicTool(), imagePanel, imagePanel.getParentLayer(), color));
		s.add(new PanelShapeAdder(new RectGraphicTool(), imagePanel, imagePanel.getParentLayer(), color));
		s.add(new PanelShapeAdder(new ShapeGraphicTool(new CircularGraphic(null)), imagePanel, imagePanel.getParentLayer(), color));
		if(imagePanel.getParentLayer() instanceof ImageDisplayLayer) {
					/**Creates a menu option for add inset panels*/
					s.add(new ObjectAction<ImagePanelGraphic>(imagePanel) {
						@Override
						public CombinedEdit performAction() { return addInset(true, imagePanel, null, null);}
								}.createJMenuItem("Add Split Channel Inset Panels", new InsetToolIcon(0).getMenuVersion()));
					
					s.add(new ObjectAction<ImagePanelGraphic>(imagePanel) {
						@Override
						public CombinedEdit performAction() { return addInset(false, imagePanel,null,null);}
								}.createJMenuItem("Add Single Inset Panel", new InsetToolIcon(0).getMenuVersion()));
					
					
					
						
					
					JMenuItem insetSeries = new ObjectAction<ImagePanelGraphic>(imagePanel) {
						@Override
						public CombinedEdit performAction() { return addInsetSeries((ImageDisplayLayer) imagePanel.getParentLayer(), imagePanel);}
								}.createJMenuItem("Add insets to all channel panels", new InsetToolIcon(0).getMenuVersion());
					
							SmartJMenu menuItem = new SmartJMenu("More");
							menuItem.add(insetSeries);
							s.add(menuItem);	
							
		}
		this.add(s);
	}


	/**adds a label to the image panel
	 * @return */
	protected CombinedEdit addText() {
		CombinedEdit undo=new CombinedEdit();
		
		
		
		ImagePanelGraphic imagePanel2 = imagePanel;
		
		addTextToImagePanel(undo, imagePanel2, "Panel");
		
		 try {
			addTextToSelectedPanels(undo);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		return undo;
	}



	/**
	 * @param undo
	 * @param imagePanel2
	 */
	public void addTextToImagePanel(CombinedEdit undo, ImagePanelGraphic imagePanel2, String name) {
		
		ComplexTextGraphic text = new ComplexTextGraphic(name);
		text.setTextColor(imagePanel2.getFigureType().getForeGroundDrawColor());
		Text_GraphicTool.lockAndSnap(imagePanel2, text, (int)imagePanel2.getLocationUpperLeft().getX(), (int) imagePanel2.getLocationUpperLeft().getY());
		
		undo.addEditToList(new UndoAddOrRemoveAttachedItem(imagePanel2, text, false));
		GraphicLayer parentLayer = imagePanel2.getParentLayer();
		undo.addEditToList(Edit.addItem(parentLayer, text));
	}
	
	
	/**Adds scale bars to every other panel
	 * @param undo
	 */
	public void addTextToSelectedPanels(CombinedEdit undo) {
	
			ArrayList<ZoomableGraphic> items = getOtherImagePanels();
			if(!items.isEmpty()) {
							boolean go = ShowMessage.showOptionalMessage("many images selected", false, "Do you want to add to every selected image panel?");
							if(go) {
								int count =1;
								for(ZoomableGraphic item: items) {
									if(item!=imagePanel) {
										addTextToImagePanel(undo, (ImagePanelGraphic) item, "Panel "+count);
										count++;
									}
								}
							}
		}
			
			
	}


	/**Adds a scale bar to the image panel
	 * @return */
	public CombinedEdit addScaleBar() {
		CombinedEdit undo = new CombinedEdit();
		
		createScaleBar(undo, imagePanel);
		
		try {
			addScaleBarToSelectedPanels(undo);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		return undo;
	}



	/**Adds scale bars to every other panel
	 * @param undo
	 */
	public void addScaleBarToSelectedPanels(CombinedEdit undo) {
	
			ArrayList<ZoomableGraphic> items = getOtherImagePanels();
			if(!items.isEmpty()) {
							boolean go = ShowMessage.showOptionalMessage("many images selected", false, "Do you want to add to every selected image panel?");
							if(go) {
								for(ZoomableGraphic item: items) {
									if(item!=imagePanel) {
										createScaleBar(undo, (ImagePanelGraphic) item);
									}
								}
							}
		}
			
			
	}



	/**returns all other selected image panels besides the main one
	 * @return
	 */
	public ArrayList<ZoomableGraphic> getOtherImagePanels() {
		if(imagePanel.getParentLayer()==null)
			return new ArrayList<ZoomableGraphic>();
		GraphicLayer topLevelParentLayer = imagePanel.getParentLayer().getTopLevelParentLayer();
		ArrayList<ZoomableGraphic> items = topLevelParentLayer.getAllGraphics();
		ArraySorter.removeNonSelectionItems(items);
		ArraySorter.removeThoseNotOfClass(items, ImagePanelGraphic.class);
		items.remove(imagePanel);
		return items;
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
	
	
	/**Creates an inset at every sister panel*/
	private CombinedEdit addInsetSeries(ImageDisplayLayer img, ImagePanelGraphic primaryImage) {
		CombinedEdit output = new CombinedEdit();
		 PanelMirror mirror=null;
		AttachmentPosition position = AttachmentPosition.defaultInternalPanel();
		position.setLocationTypeInternal(RectangleEdges.LOWER_RIGHT);
		InsetTool iTool = new InsetTool();
			iTool.setInsetPosition(position);
			iTool.addToExisting=false;
			iTool.arrangement=InsetTool.ATTACH_TO_PARENT_PANEL;
			
		ArrayList<ImagePanelGraphic> panels = img.getPanelManager().getPanelList().getPanelGraphics();
		if(panels.size()==1) {
			ShowMessage.showOptionalMessage("This option can only be used if multiple parent panels are available",true,"This option can only be used if multiple parent panels are available" );
			return null;
		}
		ArrayList<PanelGraphicInsetDefiner> listofAddedItems=new ArrayList<PanelGraphicInsetDefiner>();
		for(ImagePanelGraphic panel:panels) {
			CombinedEdit undo = addInset(false, panel,  iTool, listofAddedItems);
			output.addEditToList(undo);
			PanelGraphicInsetDefiner copy = listofAddedItems.get(listofAddedItems.size()-1);
			if(mirror==null) {
				mirror=new PanelMirror(copy, new PanelMirror.ImagePanelAddress(panel));
				output.addEditToList(Edit.addItem(primaryImage.getParentLayer(), mirror));
			} else {
				mirror.addReflection(copy, new PanelMirror.ImagePanelAddress(panel));
				for(ImagePanelGraphic panel3: copy.getPanelManager().getPanelList().getPanelGraphics()) {
					panel3.getScaleBar().getParentLayer().remove(panel3.getScaleBar());
				}
			}
			copy.getChannelLabelManager().eliminateChanLabels();
			Edit.removeItem(copy.getPanelManager().getGridLayout());
			copy.getPanelManager().getChannelUseInstructions().channelColorMode=img.getPanelManager().getChannelUseInstructions().channelColorMode;
		}
		
		position = AttachmentPosition.defaultInternalPanel();
		position.setLocationTypeInternal(RectangleEdges.LOWER_LEFT);
		for(PanelGraphicInsetDefiner inset: listofAddedItems) {
			ImagePanelGraphic insetPanel = inset.getPanelManager().getPanelList().getPanelGraphics().get(0);
			inset.getSourcePanel().addLockedItem(insetPanel);
			 insetPanel.setAttachmentPosition(position);
		}
		mirror.updateAllReflectionLocations();
		return output;
	}
	
	
	/**Creates an inset
	 * @param b set to true if inset should create split channel*/
	private CombinedEdit addInset(boolean b, ImagePanelGraphic imagePanel, InsetTool il, ArrayList<PanelGraphicInsetDefiner> listofAddedItems) {
		ArrayList<PanelGraphicInsetDefiner> old =PanelGraphicInsetDefiner.getInsetDefinersFromLayer(imagePanel.getParentLayer());
		RectangularGraphic sisterInset=null;
		if(old.size()>0)
			sisterInset=old.get(0);
		
		CombinedEdit output = new CombinedEdit();
		InsetTool iTool = new InsetTool();
		if(il!=null)
			iTool=il;
		iTool.createMultiChannel=b?1:0;
		iTool.setupToolForImagePanel(imagePanel);
		iTool.undo=output;
		
		
		Point2D c = imagePanel.getCenterOfRotation();
		Point2D d = imagePanel.getLocationUpperLeft();
		d=ShapeGraphic.midPoint(c, d);
	
		Rectangle2D newRect=new Rectangle2D.Double(d.getX(), d.getY(), c.getX()-d.getX(), c.getY()-d.getY());
		Rectangle newRect2 = newRect.getBounds();
		
		if(sisterInset!=null) {
			
			newRect2.width=(int) sisterInset.getRectangle().width;
			newRect2.height=(int) sisterInset.getRectangle().height;
			if(!imagePanel.getBounds().contains(newRect2))
				newRect2 = newRect.getBounds();
		}
		
		
		Point clickPoint = getMemoryOfMouseEvent().getCoordinatePoint();
		
		if(imagePanel.getBounds().contains(clickPoint )) {
			RectangleEdges.setLocation(newRect2, RectangleEdges.CENTER, clickPoint.x, clickPoint.y);
			if(imagePanel.getBounds().contains(newRect2))
				newRect=newRect2;
			c=RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, newRect);
			d=RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, newRect);
		}
		
		PanelGraphicInsetDefiner currentInset = iTool.refreshInsetOnMouseDrag(c, d);
		
		
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			output.addEditToList(
					new CanvasAutoResize(false).performUndoableAction( getMemoryOfMouseEvent().getAsDisplay())
			);
		
		//prompts the user to change the position of the inset panels if they are obscured by objects in front
		if(isObscured(currentInset.getPanelManager().getPanelList())) {
			ShowMessage.showOptionalMessage("Panels are behind another object", true, "Panels created at right, but another object is in the way", "you will be prompted to change their position", "drag mouse over red sqaures in the dialog that will appear on the left");
			new InsetMenu(currentInset).showRedoInsetLayoutDialog("Edit until new panels are no longer behind another object");
		}
		if(listofAddedItems!=null) {
			listofAddedItems.add(currentInset);
		}
		return output;
	}



	/**returns true if there are other objects obscuring the panels in the panel list
	 * @param panelList
	 * @return
	 */
	private static boolean isObscured(PanelList panelList) {
		for(ImagePanelGraphic panel: panelList.getPanelGraphics()) {
			GraphicLayer top = panel.getParentLayer().getTopLevelParentLayer();
			boolean reachedPanel = false;
			for(ZoomableGraphic object: top.getObjectsAndSubLayers()) {
				if(object instanceof PanelLayoutGraphic) {
					continue;
				}
				if((object instanceof ImagePanelGraphic)&&reachedPanel) {
					ImagePanelGraphic panelInfront=(ImagePanelGraphic) object;
					if(panelInfront.doesIntersect(panel.getBounds()))
						return true;
				}
				
				if(object==panel)
					reachedPanel=true;
			}
		}
		return false;
	}	
}
