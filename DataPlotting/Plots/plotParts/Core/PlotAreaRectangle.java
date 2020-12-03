/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package plotParts.Core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import fLexibleUIKit.MenuItemExecuter;
import graphicalObjectHandles.LockedItemHandle;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.RectangleGraphicOptionsDialog;
import plotParts.DataShowingParts.PlotLabel;
import undo.AbstractUndoableEdit2;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.TakesLockedItems;

public class PlotAreaRectangle extends RectangularGraphic implements HasUniquePopupMenu, TakesLockedItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PlotArea plotArea;
	LockedItemList list=new LockedItemList(this);
	{hideCenterAndRotationHandle=true;}
	
	
	public PlotAreaRectangle( PlotArea plotArea, Rectangle r) {
		 this.plotArea=plotArea;
		 this.setRectangle(r);
	}
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		//mapPanelLocations();
		 graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		snapLockedItems();
		super.draw(graphics, cords);	
	}
	
	
	public int isUserLocked() {
		return 1;
	}
	
	public Rectangle2D.Double getRectangle() {
		//if (plotArea!=null) {this.setRectangle(plotArea.getPlotArea());}
		return super.getRectangle();
	}
	
	public PopupMenuSupplier getMenuSupplier(){
		GraphicLayer par = this.getParentLayer();
		if (par instanceof HasUniquePopupMenu) {
			PopupMenuSupplier out = ((HasUniquePopupMenu) par).getMenuSupplier();
			if (out instanceof MenuItemExecuter) {
				((MenuItemExecuter) out).setPartner(new MenuItemExecuter(this));
			}
			return out;
			}
		return null;
		}
	
	public void showOptionsDialog() {
		new SpecialOptionDialog(this).showDialog();
		
	}
	
	@Override
	public void handleSmartMove(int handlenum, Point p1, Point p2) {
		if (handlenum==10)return;
		super.handleSmartMove(handlenum, p1, p2);
		
		
		if (handlenum==RectangleEdges.CENTER) {
			this.setLocationType(RectangleEdges.CENTER);
			Point2D l = this.getLocation();
			double dx = p2.getX()-l.getX();
			double dy = p2.getY()-l.getY();
			
			boolean moveWorkd = plotArea.moveEntirePlot(dx, dy);
			
			if (!moveWorkd)this.setLocation(p2);
		}
		
		plotArea.onAxisUpdate();
		
		//plotArea.fullPlotUpdate();
	}
	
	class SpecialOptionDialog extends RectangleGraphicOptionsDialog {

		public SpecialOptionDialog(RectangularGraphic s) {
			super(s, false);
			// TODO Auto-generated constructor stub
		}

		protected void setItemsToDiaog() {
			super.setItemsToDiaog();
		//	plotArea.setAreaDims(getNumber("width"), getNumber("height"));
			plotArea.onAxisUpdate();
			
		}
		private static final long serialVersionUID = 1L;}

	@Override
	public void addLockedItem(LocatedObject2D l) {
		list.add(l);
		getSmartHandleList().add(new LockedItemHandle(this, l, 400+list.size()));
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
	public LockedItemList getLockedItems() {
		return list;
	}
	
	/**And edit is requested */
	public AbstractUndoableEdit2 provideDragEdit() {
		
		return null;
		
	}
	
	/**draws the handles*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		super.drawHandesSelection(g2d, cords);
		/**if (selected) {
		Point2D c = RectangleEdges.getLocation(CENTER, getRectangle());
		Point2D c2 = cords.getAfflineTransform().transform(c, null);
		
		Rectangle r = new Rectangle(0, 0, 25, 25);
		 RectangleEdges.setLocation(r, CENTER, c2.getX(), c2.getY());
		 g2d.setColor(Color.white);
		 g2d.fill(r);
		 g2d.setColor(Color.black);
		 g2d.draw(r);
		 super.handleBoxes.set(CENTER, new HandleRect(r));
		}*/
	}
	
	protected SmartHandleList createSmartHandleList() { 
		SmartHandleList list2 = super.createSmartHandleList();
		SmartHandle h2 = this.createSmartHandle(RectangleEdges.CENTER);
		h2.setHandleColor(Color.black);
		return list2;
	}

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

}
