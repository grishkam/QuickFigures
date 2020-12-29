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
package handles.miniToolbars;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import actionToolbarItems.EditManyShapes;
import actionToolbarItems.EditScaleBars;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.BarGraphic;
import objectDialogs.DialogIcon;
import selectedItemMenus.BarOptionsSyncer;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.SnappingSyncer;

public class ScaleBarActionHandleList extends ActionButtonHandleList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BarGraphic theBar;
	
	public ScaleBarActionHandleList(BarGraphic t) {
	
		this.theBar=t;
		EditManyShapes itemForIcon2 = new EditManyShapes(false, t.getFillColor());
		itemForIcon2.setModelItem(t);
		GeneralActionListHandle hf = addOperationList(itemForIcon2, new EditManyShapes[] {});
		hf.setAlternativePopup(new ColoringButton(itemForIcon2, 78341));
			
		
	
		
		EditScaleBars itemForIcon;
		addProjectionButton(t);
		
		
		itemForIcon = new EditScaleBars(EditScaleBars.TYPE_BAR_THICKNESS_WIDTH, 4);
		itemForIcon.setModelItem(t);
		addOperationList(itemForIcon, EditScaleBars.getUnitLengthList(t.getScaleInfo().getUnits()));
		
		add(new BarSyncHandle(1120));
		createGeneralButton(new SelectAllButton(t));
		add(new GeneralActionHandle(new SnappingSyncer(true, theBar), 741905));
		
	}



	protected void addProjectionButton(BarGraphic t) {
		EditScaleBars itemForIcon = new EditScaleBars(EditScaleBars.TYPE_CHANGE_PROJECTIONS, 0);
		itemForIcon.setModelItem(t);
		addOperationList(itemForIcon, EditScaleBars.getProjectionList());
	}


	
	public void updateLocation() {
		
		Rectangle bounds = theBar.getOutline().getBounds();
		super.setLocation(new Point2D.Double(bounds.getX()+5, bounds.getMaxY()+20));
	
	}
	public void updateHandleLocations(double magnify) {
		 
		super.updateHandleLocations(magnify);
	}

	public void draw(Graphics2D g, CordinateConverter cords) {
		
		
		super.draw(g, cords);
	}
	
	
	public class BarSyncHandle extends GeneralActionHandle {

		public  BarSyncHandle( int num) {
			super(new BarOptionsSyncer(), num);
		}
		
		public void updateIcon() {
			super.setIcon(DialogIcon.getIcon());
		}
		
		@Override
		public boolean isHidden() {
			
			return false;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	
	
}
