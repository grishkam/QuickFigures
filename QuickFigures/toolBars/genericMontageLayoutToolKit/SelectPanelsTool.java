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
 * Version: 2023.2
 */
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;

import imageDisplayApp.OverlayObjectManager;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.BasicLayoutEditor;
import layout.basicFigure.LayoutEditorDialogs;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import utilityClasses1.ArraySorter;

/**The panel selector tool allows one to select regions of interest based on the the MontageLayout available*/
public class SelectPanelsTool extends GeneralLayoutEditorTool {
	
	public static final int SELECT_PANEL=0, SWAP_TWO_PANEL=1, MOVE_PANEL=2;
	public int actionType=SELECT_PANEL;
	
	public int targetType=LayoutSpaces.PANELS;
	public int panelSelectionMod=ONLY_THIS_ONE;
	
	public LocatedObject2D lastRoi=null;
	
	public void setpanelSelectionOption(int i) {
		targetType=i;
	};
	
	public void showPanelSelectorOptions() {
		StandardDialog gd=new StandardDialog("Panel Selector Options", true);
		gd.add("Select what type of space",new ChoiceInputPanel("Select what type of space", stringDescriptors, targetType%100));
		gd.add("Mod", new ChoiceInputPanel(" ", stringDescriptorsOfModifiers, panelSelectionMod));
		String[] option2=new String[] {"Select", "Swap", "Move"};
		gd.add("What to do ",new ChoiceInputPanel("What to do ", option2,actionType));

		gd.showDialog();
		if (gd.wasOKed()) {	
			targetType=gd.getChoiceIndex("Select what type of space");
			panelSelectionMod=gd.getChoiceIndex("Mod");
			targetType+=panelSelectionMod*100;
			actionType=gd.getChoiceIndex("What to do ");

		}
	}
	
	/**based on two points, determines which targeted areas are at those points and swaps the locations of the objects inside of them*/
	 private void swapPanels(BasicLayout ml,  BasicLayoutEditor me, int xc1, int yc1, int xc2, int yc2, int type){
		if (ml==null) {IssueLog.log("one is attempting to swap panels in a null layout"); return;}
		 Rectangle r1=(ml.getSelectedSpace(xc1, yc1, targetType)).getBounds();
		 Rectangle r2=(ml.getSelectedSpace( xc2, yc2, targetType)).getBounds();
		me.swapMontagePanels(ml.getVirtualWorksheet(), r1, r2);
	}

	

		
	 	/**moves the layout panel at the first location to the second*/
		private void moveLayoutPanel(BasicLayout basicMontageLayout, BasicLayoutEditor me, int xc1, int yc1, int xc2, int yc2, int type){
			checkExpansion(basicMontageLayout, me, xc1, yc1, xc2, yc2);
			basicMontageLayout=basicMontageLayout.makeAltered(type);
			me.moveMontagePanels(basicMontageLayout, basicMontageLayout.getPanelIndex(xc1, yc1), basicMontageLayout.getPanelIndex(xc2, yc2), type);
		
		}
		
		/**determines whether than panel movement demands a layout edit*/
		private void checkExpansion(BasicLayout basicMontageLayout, BasicLayoutEditor me, int xc1, int yc1, int xc2, int yc2) {
			try{
			if (yc1>basicMontageLayout.layoutHeight-basicMontageLayout.specialSpaceWidthBottom || yc2>basicMontageLayout.layoutHeight-basicMontageLayout.specialSpaceWidthBottom ) {getLayoutEditor().addRows(basicMontageLayout, 1); return;}
			if (xc1>basicMontageLayout.layoutWidth-basicMontageLayout.specialSpaceWidthRight || xc2>basicMontageLayout.layoutWidth-basicMontageLayout.specialSpaceWidthRight) {getLayoutEditor().addCols(basicMontageLayout, 1); return;}
			if (xc1<0 || xc2<0) {
				me.addLeftLabelSpace(basicMontageLayout, basicMontageLayout.getPanelWidthOfColumn(1)/8); 
				return;}
			if (yc1<0 || yc2<0) {
				me.addTopLabelSpace(basicMontageLayout,basicMontageLayout.getPanelHeightOfRow(1)/8); 
				return;
				}
			} catch (Exception e) {IssueLog.logT(e );}	
		}
		

		public void mousePressed() {
			findClickedLayout(true);
			
			if (!hasALayoutBeenClicked()) {getSelManOfClcikedImage().removeObjectSelections();return;}
		
			
				performPressEdit();
			
		}
		
		protected OverlayObjectManager getSelManOfClcikedImage() {
			return getImageClicked().getOverlaySelectionManagger();
		}
		
		protected void performPressEdit() {
			
			if (this.getCurrentLayout()==null
					||!getCurrentLayout().getBoundry().contains(this.getClickedCordinateX(), this.getClickedCordinateY())
					) {
				getImageClicked().getOverlaySelectionManagger().removeObjectSelections();return;
			}
			getSelManOfClcikedImage().select(getSelectedRoi(getCurrentLayout(), this.getClickedCordinateX(), this.getClickedCordinateY(), targetType), 4, 0);
			
			
		}
		
		public void mouseDragged() {
			if (this.getCurrentLayout()==null)
				return;
			
			/**selects several panels if one has dragged from one to another*/
				if (this.actionType==SELECT_PANEL){
				
					Shape selArea = getCurrentLayout().rangeRoi(getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), targetType);
					this.getSelManOfClcikedImage().select(selArea, 2, 0);
					if(selArea!=null)
							try {
						ArraySorter.selectItems(getObjecthandler().getContainedObjects(selArea.getBounds(), this.getImageClicked()));
							} catch (Throwable t) {IssueLog.log(t);}
					return;
				}
				
			OverlayObjectManager man = this.getImageClicked().getOverlaySelectionManagger();
			man.select(getSelectedRoi(getCurrentLayout(), this.getDragCordinateX(), this.getDragCordinateY(), targetType), 4, 1);
		}
		
		/**returns the selected area*/
		private Shape getSelectedRoi(BasicLayout ml, int x, int y, int type) {
			 	ml=ml.makeAltered(type);//Makes a copy of this MontageLayout with alterations depending on the type.
			 int index=ml.getPanelIndex(x,y);
				  
			  Shape s=ml.getSelectedSpace(index, type); 
			  
			return s;
		
		}

		
		protected void performDragEdit() {
			
		}
		
		public void mouseReleased() {
			
			
			this.getImageClicked().getOverlaySelectionManagger().removeObjectSelections();
			
			if ((actionType==SWAP_TWO_PANEL || ( (shiftDown() && actionType==SELECT_PANEL)) ) ) {
				swapPanels(this.getCurrentLayout(), getLayoutEditor(), getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), targetType );
				
			}
			if ((actionType==MOVE_PANEL || ( (altKeyDown() && actionType==SELECT_PANEL)) ) ) {
				moveLayoutPanel(this.getCurrentLayout(),  getLayoutEditor(), getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), targetType);
				
			}
			
			updateClickedDisplay();
			
		}
		
		public void mouseClicked() {
			if (this.clickCount()>1)
			 new LayoutEditorDialogs().showMontageLayoutEditingDialog(getCurrentLayout());
			
		}
		
		public void mouseMoved() {}
		
		public void mouseExited() {}
		
		@Override
		public String getToolSubMenuName() {
			return "Expert Tools";
		}


		public void showOptionsDialog() {
			showPanelSelectorOptions();
		}
		
		@Override
		public String getToolTip() {
				
				return "Select Panels";
			}
		@Override
		public String getToolName() {
				
				return "Select Panels";
			}
		
		
		{this.setIconSet(new  PanelSelectionToolIcon(0).generateIconSet());}
		
		/**the icon for the panel selection tool*/
		class PanelSelectionToolIcon extends GeneralLayoutToolIcon {

			/**
			 * @param type
			 */
			public PanelSelectionToolIcon(int type) {
				super(type);
				super.paintBoundry=false;
				super.panelColor=new Color[] {BLUE_TONE, Color.white, Color.white, Color.WHITE};
				
				if(type!=NORMAL_ICON_TYPE) {
					super.panelColor=new Color[] { Color.white,  Color.WHITE, BLUE_TONE,Color.white};
					
				}
			}
			
			/**
			creates a layout for drawing and icon
			 */
			protected PanelLayout createSimpleIconLayout( int type) {
				BasicLayout layout = new BasicLayout(2, 2, 7, 7, 2,2, true);
				layout.setLabelSpaces(2, 2,2,2);
				layout.move(2,2);
				
				return layout;
			}
			
			/**
			 */
			protected GeneralLayoutToolIcon generateAnother(int type) {
				return new PanelSelectionToolIcon(type);
			}
			
			public GeneralLayoutToolIcon copy(int type) {
				GeneralLayoutToolIcon another = generateAnother(type);
				return another;
			}
		}

}
