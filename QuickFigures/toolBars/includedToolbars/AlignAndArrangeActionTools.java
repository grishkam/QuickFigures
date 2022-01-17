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
 * Version: 2022.0
 */
package includedToolbars;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import actionToolbarItems.AlignItem;
import actionToolbarItems.EditManyObjects;
import actionToolbarItems.DistributeItems;
import actionToolbarItems.SuperTextButton;
import basicMenusForApp.CurrentWorksheetLayerSelector;
import basicMenusForApp.MenuItemForObj;
import basicMenusForApp.OpeningFileDropHandler;
import genericMontageLayoutToolKit.FitLayout;
import genericTools.BasicToolBit;
import genericTools.GeneralTool;
import genericTools.ToolBit;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.CurrentSetInformer;
import icons.IconWrappingToolIcon;
import imageMenu.ZoomFit;
import locatedObject.RectangleEdges;
import selectedItemMenus.MultiSelectionOperator;
import selectedItemMenus.TextBackGroundOptionsSyncer;
import selectedItemMenus.TextOptionsSyncer;

/**A toolbar with button shortcuts for the align menu*/
public class AlignAndArrangeActionTools extends QuickFiguresToolBar{
	
	public static AlignAndArrangeActionTools currentToolset;
	public CurrentSetInformer currentImageInformer=new CurrentFigureSet();
	private static Color[] standardColor=new Color[] { Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , Color.white, Color.black,new Color(0,0,0,0)};
	
	
	public AlignAndArrangeActionTools() {
		super.maxGridx=16;
		installOperator(new AlignItem(RectangleEdges.LEFT));
		installOperator(new AlignItem(RectangleEdges.RIGHT));
		installOperator(new AlignItem(RectangleEdges.TOP));
		installOperator(new AlignItem(RectangleEdges.BOTTOM));
		installOperator(new AlignItem(RectangleEdges.CENTER));
		installOperator(new AlignItem(RectangleEdges.CENTER+1));
		installOperator(new DistributeItems(true));
		installOperator(new DistributeItems(false));
		installOperator(new FitLayout(FitLayout.ALIGN_GRID));
		installOperator(new AlignItem(100));
		installOperator(new AlignItem(101));
		installOperator(new AlignItem(102));
		installOperator(new AlignItem(103));
		
		
		installMenuAdapter( new ZoomFit(ZoomFit.IN));
		installMenuAdapter( new ZoomFit(ZoomFit.OUT));
		installMenuAdapter(  new ZoomFit());
		
	}



	public static SuperTextButton[] getTextColors() {
		return SuperTextButton.getForColors(true, standardColor);
	}



	public static EditManyObjects[] getCapsAndJoins() {
		return new EditManyObjects[] {
				new EditManyObjects(BasicStroke.JOIN_BEVEL, null),
				new EditManyObjects(BasicStroke.JOIN_MITER, null),
				new EditManyObjects(BasicStroke.JOIN_ROUND, null),
				new EditManyObjects(null, BasicStroke.CAP_BUTT),
				new EditManyObjects(null, BasicStroke.CAP_ROUND),
				new EditManyObjects(null, BasicStroke.CAP_SQUARE)
				
				};
	}

	


	public static MultiSelectionOperator[] getTextOperations() {
		return new MultiSelectionOperator[] {
				
				new EditManyObjects("down"),
				new EditManyObjects(Font.BOLD),
				new EditManyObjects(Font.PLAIN),
				
				new EditManyObjects(Font.ITALIC),
				new EditManyObjects(Font.BOLD+Font.ITALIC),
				
				new TextOptionsSyncer(),
				new TextBackGroundOptionsSyncer()
				
				};
	}



	public static EditManyObjects[] getDashesAndStrokes() {
		return new EditManyObjects[] {
				new EditManyObjects(true, new float[] {2,2}),
				new EditManyObjects(true, new float[] {}),
				
				new EditManyObjects(true, new float[] {4,4}),
				new EditManyObjects(true, new float[] {8,8}),
				new EditManyObjects(true, new float[] {8,16}),
				new EditManyObjects(true, 1),
				new EditManyObjects(true, 2),
				new EditManyObjects(true, 4),
				new EditManyObjects(true, 8),
				new EditManyObjects(true, 16),
				new EditManyObjects(true, 30)};
	}
	

	
	void installOperator(MultiSelectionOperator selectionOperator) {
		this.addTool(
				new GeneralTool(new SelectionDisplayActionTool(selectionOperator))
				);
	}
	
	void installOperator(MultiSelectionOperator... selectionOperator) {
		ArrayList<ToolBit> bits=new ArrayList<ToolBit>();
		for(MultiSelectionOperator bit1:  selectionOperator) bits.add(
				new SelectionDisplayActionTool(bit1)
				);
		this.addTool(new GeneralTool(bits));
	}
	
	void installMenuAdapter( MenuItemForObj... selectionOperator) {
		ArrayList<ToolBit> bits=new ArrayList<ToolBit>();
		for(MenuItemForObj bit1:  selectionOperator) bits.add(
				new MenuItemToActionTool(bit1)
				);
		this.addTool(new GeneralTool(bits));
	}
	
	public void start() {
		
	}

	

	
	
public void run(String s) {
		
	
		if (currentToolset!=null&&currentToolset!=this) currentToolset.getframe().setVisible(false);
		currentToolset=this;
		showFrame();
		
		this.getframe().setLocation(new Point(840, 100));
		
	}
	
	public void showFrame() {
		super.showFrame();
		getframe().setTitle("Align and Arrange");
		addToolKeyListeners();
		new DropTarget(getframe(), new OpeningFileDropHandler());
	}
	
	
	class SelectionDisplayActionTool extends BasicToolBit{

		
		CurrentWorksheetLayerSelector selector=new CurrentWorksheetLayerSelector();
		private MultiSelectionOperator ad;

		

		public SelectionDisplayActionTool(MultiSelectionOperator iconpath) {
		this.ad=iconpath;
			setIconSet(IconWrappingToolIcon.createIconSet(ad.getIcon()));
		}
		
		public String getToolTip() {
			return ad.getMenuCommand();
		}
		
		public String getToolName() {
			return ad.getMenuCommand();
		}

		
		
		public boolean isActionTool() {
			return true;
		}
		
		public void performLoadAction() {
			ad.setSelector( selector);
			ad.setSelection(selector.getSelecteditems());
			ad.run();
			CurrentFigureSet.updateActiveDisplayGroup();
		}
		
		
		
		
	}
	
	/**Used to include menu items */
	class MenuItemToActionTool extends BasicToolBit {

		
		CurrentWorksheetLayerSelector selector=new CurrentWorksheetLayerSelector();
		private MenuItemForObj ad;

		

		public  MenuItemToActionTool( MenuItemForObj iconpath) {
		this.ad=iconpath;
		
			setIconSet(new IconWrappingToolIcon(ad.getIcon(), 0).generateIconSet());
		}
		
		public String getToolTip() {
			return ad. getNameText();
		}
		
		public String getToolName() {
			return ad. getNameText();
		}

		
		
		public boolean isActionTool() {
			return true;
		}
		
		public void performLoadAction() {
			
			ad.performActionDisplayedImageWrapper(currentImageInformer.getCurrentlyActiveDisplay());
			CurrentFigureSet.updateActiveDisplayGroup();
			getframe().pack();
		}
		
		
		
		
	}
}
