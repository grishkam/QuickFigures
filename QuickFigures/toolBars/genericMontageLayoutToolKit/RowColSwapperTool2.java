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
package genericMontageLayoutToolKit;
import externalToolBar.IconSet;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import gridLayout.LayoutSpaces;

/**A tool for swapping rows and columns*/
public class RowColSwapperTool2 extends GeneralLayoutEditorTool implements LayoutSpaces{
	{resetClickPointOnDrag=false;}
	
	public RowColSwapperTool2(int mode) {
		this.mode=mode;
	}
	
	public int mode=LayoutSpaces.ROWS;

	 public int markerType() {
	    	return mode;
	    }
	
	public void performReleaseEdit(boolean b) {

		if (mode==LayoutSpaces.PANELS) {  
			getLayoutEditor().swapPanels(getCurrentLayout(), getPanelIndexClick(), getPanelIndexDrag());
		
		}
		
		if (mode==LayoutSpaces.COLS) {
			 getLayoutEditor().swapColumn(getCurrentLayout(), getColIndexClick(), getColIndexDrag());
		
		}
		
		if (mode==LayoutSpaces.ROWS) {
			 getLayoutEditor().swapRow(getCurrentLayout(), getRowIndexClick(), getRowIndexDrag());
			
			
		}
		
		if (layoutGraphic.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane parentLayer = (FigureOrganizingLayerPane) layoutGraphic.getParentLayer();
			 parentLayer.updateChannelOrder(mode);
			
		}
		
		super.getImageClicked().updateDisplay();
		
	}
	
	{createIconSet("icons/ColumnSwapperIcon.jpg",
				"icons/ColumnSwapperPressIcon.jpg",
				"icons/ColumnSwapperRollIcon.jpg",
				"icons/RowSwapperIcon.jpg",
				"icons/RowSwapperPressIcon.jpg",
				 "icons/RowSwapperRollIcon.jpg", 
				 "icons/PanelSwapperIcon.jpg",
				"icons/PanelSwapperIconPressed.jpg",
				"icons/PanelSwapperIconRollOver.jpg"
				);
	}
	
	IconSet columnSwapIcons=new IconSet("icons/ColumnSwapperIcon.jpg",
			"icons/ColumnSwapperPressIcon.jpg",
			"icons/ColumnSwapperRollIcon.jpg");
	IconSet rowSwapIcons=new IconSet("icons/RowSwapperIcon.jpg",
			"icons/RowSwapperPressIcon.jpg",
			 "icons/RowSwapperRollIcon.jpg");
	IconSet panelSwapIcons=new IconSet( "icons/PanelSwapperIcon.jpg",
			"icons/PanelSwapperIconPressed.jpg",
			"icons/PanelSwapperIconRollOver.jpg");
	
	@Override
	public
	IconSet getIconSet() {
		if (mode==LayoutSpaces.PANELS) return panelSwapIcons;
		if (mode==LayoutSpaces.COLS) return columnSwapIcons;
		return rowSwapIcons;
	}
	
	 protected String getTextBase() {
		 if (mode==LayoutSpaces.ROWS) return "Row";
			if (mode==LayoutSpaces.COLS) return  "Column";
			 return "Panel";
	    
	}
	

	@Override
	public String getToolTip() {
			return "Swap "+getTextBase();
		}
	@Override
	public String getToolName() {
			return getToolTip();
		}

}
