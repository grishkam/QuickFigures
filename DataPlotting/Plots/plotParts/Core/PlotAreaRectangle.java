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
 * Date Modified: Nov 12, 2021
 * Version: 2023.1
 */
package plotParts.Core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import fLexibleUIKit.MenuItemExecuter;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.RectangularGraphic;
import handles.AttachmentPositionHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.LocatedObject2D;
import locatedObject.AttachedItemList;
import locatedObject.RectangleEdges;
import locatedObject.TakesAttachedItems;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.RectangleGraphicOptionsDialog;
import plotParts.DataShowingParts.PlotLabel;
import undo.AbstractUndoableEdit2;

/**A special rectangle that defines and draws the plot area*/
public class PlotAreaRectangle extends RectangularGraphic implements HasUniquePopupMenu, TakesAttachedItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PlotArea plotArea;
	AttachedItemList list=new AttachedItemList(this);
	{hideCenterAndRotationHandle=true;}
	
	
	public PlotAreaRectangle( PlotArea plotArea, Rectangle r) {
		 this.plotArea=plotArea;
		 this.setRectangle(r);
	}
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		//mapPanelLocations();
		 graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		snapLockedItems();
		super.draw(graphics, cords);	
	}
	
	/**A user cannot drag a plot area directly, returns locked*/
	public int isUserLocked() {
		return LOCKED;
	}
	
	public Rectangle2D.Double getRectangle() {
		//if (plotArea!=null) {this.setRectangle(plotArea.getPlotArea());}
		return super.getRectangle();
	}
	
	/**returns a menu for the plot area*/
	public PopupMenuSupplier getMenuSupplier(){
		GraphicLayer par = this.getParentLayer();
		if (par instanceof HasUniquePopupMenu) {
			PopupMenuSupplier out = ((HasUniquePopupMenu) par).getMenuSupplier();
			if (out instanceof MenuItemExecuter) {
				((MenuItemExecuter) out).setPartner(new MenuItemExecuter(this, true));
			}
			return out;
			}
		return null;
		}
	
	
	public void showOptionsDialog() {
		new SpecialOptionDialog(this).showDialog();
		
	}
	
	

	/**
	 * @param handleNumber
	 * @param p1
	 * @param p2
	 */
	@Override
	public void afterHandleMove(int handleNumber, Point2D p1, Point2D p2) {
		if (handleNumber==RectangleEdges.CENTER) {
			this.setLocationType(RectangleEdges.CENTER);
			Point2D l = this.getLocation();
			double dx = p2.getX()-l.getX();
			double dy = p2.getY()-l.getY();
			
			boolean moveWorkd = plotArea.moveEntirePlot(dx, dy);
			
			if (!moveWorkd)this.setLocation(p2);
		}
		
		plotArea.onAxisUpdate();
		
	}
	
	/**An options dialog for a plot area*/
	class SpecialOptionDialog extends RectangleGraphicOptionsDialog {

		public SpecialOptionDialog(RectangularGraphic s) {
			super(s, false);
			
		}

		/**updates the plot after items are set*/
		protected void setItemsToDiaog() {
			super.setItemsToDiaog();
		//	plotArea.setAreaDims(getNumber("width"), getNumber("height"));
			plotArea.onAxisUpdate();
			
		}
		private static final long serialVersionUID = 1L;}

	@Override
	public void addLockedItem(LocatedObject2D l) {
		list.add(l);
		getSmartHandleList().add(new AttachmentPositionHandle(this, l, 400+list.size()));
	}

	@Override
	public void removeLockedItem(LocatedObject2D l) {
		list.remove(l);
		getSmartHandleList().removeLockedItemHandle(l);
	}

	@Override
	public void snapLockedItems() {
		for(LocatedObject2D l: list) {
			snapLockedItem(l);
		}
		
	}

	@Override
	public void snapLockedItem(LocatedObject2D l) {
		if (l instanceof PlotLabel) {}
		else {
			l.getAttachmentPosition().snapObjectToRectangle(l, this.getRectangle().getBounds());
		}
	}

	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return list.contains(l);
	}

	@Override
	public AttachedItemList getLockedItems() {
		return list;
	}
	
	/**And edit is requested */
	public AbstractUndoableEdit2 provideDragEdit() {
		
		return null;
		
	}
	
	/**draws the handles*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		super.drawHandesSelection(g2d, cords);
		
	}
	
	/**returns the handles*/
	@Override
	protected SmartHandleList createSmartHandleList() { 
		SmartHandleList list2 = super.createSmartHandleList();
		SmartHandle h2 = this.createSmartHandle(RectangleEdges.CENTER);
		h2.setHandleColor(Color.black);
		list2.add(h2);
		return list2;
	}

	/**when scaling the object*/
	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		plotArea.fullPlotUpdate();
	}
	
	@Override
	public Rectangle getContainerForBounds(LocatedObject2D l) {
		return getBounds();
	}

	
	@Override
	public ArrayList<LocatedObject2D> getNonLockedItems() {
		return new ArrayList<LocatedObject2D>();
	}
	
	/**override superclass*/
	public boolean doesSetAngle() {
			
			return false;
		}

	/**
	 * @return the plot area
	 * 
	 */
	public PlotArea getPlotArea() {
		return plotArea;
		
	}

}
