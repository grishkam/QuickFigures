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
package includedToolbars;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import actionToolbarItems.EditManyShapes;
import actionToolbarItems.SuperTextButton;
import basicMenusForApp.CurrentSetLayerSelector;
import basicMenusForApp.MenuItemForObj;
import basicMenusForApp.OpeningFileDropHandler;
import externalToolBar.IconSet;
import externalToolBar.IconWrappingToolIcon;
import genericMontageUIKit.BasicToolBit;
import genericMontageUIKit.GeneralTool;
import genericMontageUIKit.ToolBit;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.CurrentSetInformer;
import graphicalObjects_BasicShapes.TextGraphic;
import selectedItemMenus.MultiSelectionOperator;
import selectedItemMenus.TextBackGroundOptionsSyncer;
import selectedItemMenus.TextOptionsSyncer;

public class ActionToolset2 extends QuickFiguresToolBar{
	
	public static ActionToolset2 currentToolset;
	public CurrentSetInformer currentImageInformer=new CurrentFigureSet();
	private static Color[] standardColor=new Color[] { Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , Color.white, Color.black,new Color(0,0,0,0)};
	
	
	public ActionToolset2() {
		super.maxGridx=16;
		
		installOperator(EditManyShapes.getForColors(false, standardColor));
		installOperator(EditManyShapes.getForColors(true, standardColor));
		installOperator(getTextColors());
		
		installOperator( getDashesAndStrokes());
		installOperator( getCapsAndJions()
				);
		
		installOperator( new EditManyShapes("up"));
		installOperator( getTextOperations()
				);
		installOperator(new SuperTextButton(SuperTextButton.TO_SUPERSCRIPT));
		installOperator(new SuperTextButton(SuperTextButton.TO_SUBSCRIPT));
		installOperator(new SuperTextButton(SuperTextButton.BOLDENS));
		installOperator(new SuperTextButton(SuperTextButton.ITALICIZES));
		installOperator(new SuperTextButton(SuperTextButton.UNDERLINES));
		installOperator(new SuperTextButton(SuperTextButton.STRIKES));
		installOperator(SuperTextButton.getForDims(new TextGraphic("", Color.white)));
		
	}



	public static SuperTextButton[] getTextColors() {
		return SuperTextButton.getForColors(true, standardColor);
	}



	public static EditManyShapes[] getCapsAndJions() {
		return new EditManyShapes[] {
				new EditManyShapes(BasicStroke.JOIN_BEVEL, null),
				new EditManyShapes(BasicStroke.JOIN_MITER, null),
				new EditManyShapes(BasicStroke.JOIN_ROUND, null),
				new EditManyShapes(null, BasicStroke.CAP_BUTT),
				new EditManyShapes(null, BasicStroke.CAP_ROUND),
				new EditManyShapes(null, BasicStroke.CAP_SQUARE)
				
				};
	}



	public static MultiSelectionOperator[] getTextOperations() {
		return new MultiSelectionOperator[] {
				
				new EditManyShapes("down"),
				new EditManyShapes(Font.BOLD),
				new EditManyShapes(Font.PLAIN),
				
				new EditManyShapes(Font.ITALIC),
				new EditManyShapes(Font.BOLD+Font.ITALIC),
				
				new TextOptionsSyncer(),
				new TextBackGroundOptionsSyncer()
				
				};
	}



	public static EditManyShapes[] getDashesAndStrokes() {
		return new EditManyShapes[] {
				new EditManyShapes(true, new float[] {2,2}),
				new EditManyShapes(true, new float[] {}),
				
				new EditManyShapes(true, new float[] {4,4}),
				new EditManyShapes(true, new float[] {8,8}),
				new EditManyShapes(true, new float[] {8,16}),
				new EditManyShapes(true, 1),
				new EditManyShapes(true, 2),
				new EditManyShapes(true, 4),
				new EditManyShapes(true, 8),
				new EditManyShapes(true, 16),
				new EditManyShapes(true, 30)};
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
		
		this.getframe().setLocation(new Point(840, 200));
		
	}
	
	public void showFrame() {
		super.showFrame();
		getframe().setTitle("Change Shapes and Text");
		addToolKeyListeners();
		new DropTarget(getframe(), new OpeningFileDropHandler());
	}
	
	
	class SelectionDisplayActionTool extends BasicToolBit{

		
		CurrentSetLayerSelector selector=new CurrentSetLayerSelector();
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
	
	class MenuItemToActionTool extends BasicToolBit {

		
		CurrentSetLayerSelector selector=new CurrentSetLayerSelector();
		private MenuItemForObj ad;

		

		public  MenuItemToActionTool( MenuItemForObj iconpath) {
		this.ad=iconpath;
			setIconSet(new IconSet(ad.getIcon(),ad.getIcon(),ad.getIcon()));
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
			getframe().pack();//fix for an issue that made buttons invisible after tool switch
		}
		
		
		
		
	}
}
