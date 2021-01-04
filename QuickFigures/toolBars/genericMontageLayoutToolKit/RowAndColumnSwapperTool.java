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
package genericMontageLayoutToolKit;
import figureOrganizer.FigureOrganizingLayerPane;
import icons.IconSet;
import layout.basicFigure.LayoutSpaces;

/**A tool for swapping rows and columns*/
public class RowAndColumnSwapperTool extends GeneralLayoutEditorTool implements LayoutSpaces{
	{resetClickPointOnDrag=false;}
	
	public RowAndColumnSwapperTool(int mode) {
		this.mode=mode;
		this.setIconSet(new RowSwapperToolIcon(0, mode).generateIconSet());
	}
	
	public int mode=LayoutSpaces.ROWS;

	 public int markerType() {
	    	return mode;
	    }
	
	public void performReleaseEdit(boolean b) {

		if (mode==LayoutSpaces.PANELS) {  
			getLayoutEditor().swapPanels(getCurrentLayout(), getIndexClick(), getPanelIndexDrag());
		
		}
		
		if (mode==LayoutSpaces.COLS) {
			 getLayoutEditor().swapColumn(getCurrentLayout(), getColIndexClick(), getColIndexDrag());
		
		}
		
		if (mode==LayoutSpaces.ROWS) {
			 getLayoutEditor().swapRow(getCurrentLayout(), getRowIndexClick(), getRowIndexDrag());
			
			
		}
		
		if (layoutGraphic!=null&&layoutGraphic.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane parentLayer = (FigureOrganizingLayerPane) layoutGraphic.getParentLayer();
			 parentLayer.updateChannelOrder(mode);
			
		}
		
		super.getImageClicked().updateDisplay();
		
	}

	/**
	 returns the clicked index
	 */
	public int getIndexClick() {
		if(mode==ROWS) {
			return this.getRowIndexClick();
		}
		if(mode==COLS)
			return this.getColIndexClick();
		return getPanelIndexClick();
	}
	

	
	IconSet columnSwapIcons=new RowSwapperToolIcon(0, LayoutSpaces.COLS).generateIconSet();//new IconSet("icons/ColumnSwapperIcon.jpg","icons/ColumnSwapperPressIcon.jpg","icons/ColumnSwapperRollIcon.jpg");
	IconSet rowSwapIcons=new RowSwapperToolIcon(0, LayoutSpaces.ROWS).generateIconSet();//new IconSet("icons/RowSwapperIcon.jpg","icons/RowSwapperPressIcon.jpg", "icons/RowSwapperRollIcon.jpg");
	IconSet panelSwapIcons=new RowSwapperToolIcon(0, LayoutSpaces.PANELS).generateIconSet();//new IconSet( "icons/PanelSwapperIcon.jpg",	"icons/PanelSwapperIconPressed.jpg","icons/PanelSwapperIconRollOver.jpg");
	
	
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
