package includedToolbars;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import actionToolbarItems.EditAndColorizeMultipleItems;
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
		
		installOperator(EditAndColorizeMultipleItems.getForColors(false, standardColor));
		installOperator(EditAndColorizeMultipleItems.getForColors(true, standardColor));
		installOperator(getTextColors());
		
		installOperator( getDashesAndStrokes());
		installOperator( getCapsAndJions()
				);
		
		installOperator( new EditAndColorizeMultipleItems("up"));
		installOperator( getTextOperations()
				);
		installOperator(new SuperTextButton(0));
		installOperator(new SuperTextButton(1));
		installOperator(new SuperTextButton(2));
		installOperator(new SuperTextButton(3));
		installOperator(new SuperTextButton(4));
		installOperator(new SuperTextButton(5));
		installOperator(SuperTextButton.getForDims(new TextGraphic("", Color.white)));
		
	}



	public static SuperTextButton[] getTextColors() {
		return SuperTextButton.getForColors(true, standardColor);
	}



	public static EditAndColorizeMultipleItems[] getCapsAndJions() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_BEVEL, null),
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_MITER, null),
				new EditAndColorizeMultipleItems(BasicStroke.JOIN_ROUND, null),
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_BUTT),
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_ROUND),
				new EditAndColorizeMultipleItems(null, BasicStroke.CAP_SQUARE)
				
				};
	}



	public static MultiSelectionOperator[] getTextOperations() {
		return new MultiSelectionOperator[] {
				
				new EditAndColorizeMultipleItems("down"),
				new EditAndColorizeMultipleItems(Font.BOLD),
				new EditAndColorizeMultipleItems(Font.PLAIN),
				
				new EditAndColorizeMultipleItems(Font.ITALIC),
				new EditAndColorizeMultipleItems(Font.BOLD+Font.ITALIC),
				
				new TextOptionsSyncer(),
				new TextBackGroundOptionsSyncer()
				
				};
	}



	public static EditAndColorizeMultipleItems[] getDashesAndStrokes() {
		return new EditAndColorizeMultipleItems[] {
				new EditAndColorizeMultipleItems(true, new float[] {2,2}),
				new EditAndColorizeMultipleItems(true, new float[] {}),
				
				new EditAndColorizeMultipleItems(true, new float[] {4,4}),
				new EditAndColorizeMultipleItems(true, new float[] {8,8}),
				new EditAndColorizeMultipleItems(true, new float[] {8,16}),
				new EditAndColorizeMultipleItems(true, 1),
				new EditAndColorizeMultipleItems(true, 2),
				new EditAndColorizeMultipleItems(true, 4),
				new EditAndColorizeMultipleItems(true, 8),
				new EditAndColorizeMultipleItems(true, 16),
				new EditAndColorizeMultipleItems(true, 30)};
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
