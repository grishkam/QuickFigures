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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package handles.miniToolbars;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import actionToolbarItems.EditManyObjects;
import actionToolbarItems.EditScaleBars;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.BarGraphic;
import iconGraphicalObjects.DialogIcon;
import selectedItemMenus.BarOptionsSyncer;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.AttachmentPositionAdjuster;

/**A mini toolbar designed for scale bars. each handle is relvant to scale bars*/
public class ScaleBarActionHandleList extends ActionButtonHandleList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BarGraphic theBar;
	
	public ScaleBarActionHandleList(BarGraphic t) {
	
		this.theBar=t;
		addBarColorButton(t);
	
		addProjectionButton(t);
		
		
		addBarLengthHandle(t);
		
		//createGeneralButton(new EditScaleBars(EditScaleBars.TYPE_HIDE_TEXT, 0, t));
		
		
		add(new BarOptionsDialogHandle(1120));
		
		createGeneralButton(new SelectAllButton(t));
		
		add(new GeneralActionHandle(new AttachmentPositionAdjuster(true, theBar), 741905));
		
	}



	/**
	 * @param t
	 */
	private void addBarColorButton(BarGraphic t) {
		EditManyObjects itemForIcon2 = new EditManyObjects(false, t.getFillColor());
		itemForIcon2.setModelItem(t);
		GeneralActionListHandle hf = addOperationList(itemForIcon2, new EditManyObjects[] {});
		hf.setAlternativePopup(new ColoringButton(itemForIcon2, 78341));
	}



	/**adds a button that control the length of the scale bars
	 * @param t
	 */
	private void addBarLengthHandle(BarGraphic t) {
		EditScaleBars itemForIcon;
		itemForIcon = new EditScaleBars(EditScaleBars.TYPE_BAR_THICKNESS_WIDTH, 4);
		itemForIcon.setModelItem(t);
		addOperationList(itemForIcon, EditScaleBars.getUnitLengthList(t.getScaleInfo().getUnits()));
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
	
	
	public class BarOptionsDialogHandle extends GeneralActionHandle {

		public  BarOptionsDialogHandle( int num) {
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
