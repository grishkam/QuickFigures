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
 * Version: 2023.1
 */
package selectedItemMenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import actionToolbarItems.AlignItem;
import actionToolbarItems.AlignSize;
import actionToolbarItems.DistributeItems;
import addObjectMenus.AddLink;
import figureFormat.MassTemplateApplication;
import figureFormat.TemplateUserMenuAction;
import fileListOpps.CombineSavedFigures;
import fileListOpps.LoadFileLists;
import genericMontageLayoutToolKit.FitLayout;
import imageDisplayApp.UserPreferences;
import journalCriteria.JournalCriteriaMenuOption;
import journalCriteria.PPIOption;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.SmartJMenu;

/**Builds a selected item menu with many operatiuons
 * @see MultiSelectionOperator for the objects that define the menu elements*/
public class SelectionOperationsMenu extends SmartJMenu implements
		ActionListener {

	/**
	 * 
	 */
	static boolean operatorsMade=false;
	static ArrayList <MultiSelectionOperator> operators  =new ArrayList <MultiSelectionOperator>(); 
	static ArrayList <MultiSelectionOperator> operatorsOp=new ArrayList <MultiSelectionOperator>(); 
	static ArrayList <MultiSelectionOperator> operatorsSc=new ArrayList <MultiSelectionOperator>(); 
	
	static ArrayList <MultiSelectionOperator> operatorsTemplates=new ArrayList <MultiSelectionOperator>(); 
	
	public static void addNewOperator(MultiSelectionOperator m) {
		operators.add(m);
	}
	
	{
		
		if (!operatorsMade) {
		operators.add(new ItemRemover());
		operatorsOp.add(new TextOptionsSyncer());
		operatorsOp.add(new BarOptionsSyncer());
		operatorsOp.add(new ImageGraphicOptionsSyncer());
		operatorsOp.add(new AttachmentPositionAdjuster(null));
		operatorsOp.add(new CroppingSyncer());
		
		operators.add(new AlignItem(RectangleEdges.RIGHT));
		operators.add(new AlignItem(RectangleEdges.LEFT));
		operators.add(new AlignItem(RectangleEdges.BOTTOM));
		operators.add(new AlignItem(RectangleEdges.TOP));
	
		
		
		
		operators.add(new AlignItem(RectangleEdges.CENTER));
		operators.add(new AlignItem(RectangleEdges.CENTER+1));
		
		operators.add(new AlignItem(100));
		operators.add(new AlignItem(101));
		operators.add(new AlignItem(102));
		operators.add(new AlignItem(103));
		operators.add(new DistributeItems(false));
		operators.add(new DistributeItems(true));
		
		
		
		operators.add(new HideItem());
		operators.add(new UnHideItem());
		operators.add(new SelectOrDeselectItem(false));
		operators.add(new SelectOrDeselectItem(true));
		
		
		operators.add(new DuplicateItem());
		
		if(UserPreferences.current.testNew)
			operators.add(new AddLink());
		
		ArrayList<TemplateUserMenuAction> many = TemplateUserMenuAction.createSeveral("Figure Format");
		operators.addAll(many);
		
		operatorsOp.add(new TextBackGroundOptionsSyncer());
		operatorsOp.add(new ShapeOptionsSyncer());
		operatorsOp.add(new InsetOptionsSyncer());
		operatorsOp.add(new JournalCriteriaMenuOption());
		operatorsOp.add(new PPIOption());

		operators.add(new MassTemplateApplication());
		operators.add(new CombineSavedFigures());
		
		operatorsSc.add(new IllustratorMimic(true));
		operatorsSc.add(new IllustratorMimic(false));
		

		

		operators.add(new FitLayout(FitLayout.ALIGN_GRID));
		operators.add(new FitLayout(false));
		operators.add(new FitLayout(true));
		operators.addAll(AlignSize.getAllPossibleAligns());
		
		operators.add(new ScalingSyncer());
		operators.add(new ScalingSyncerFigures());
		
		
		operators.add(new LoadFileLists(0));
		operators.add(new LoadFileLists(1));
		operators.add(new LoadFileLists(2));
		
		if (IssueLog.isDebugMode()) operators.add(new ListItems());
		operators.add(new FillPaintSetter());
		operators.add(new LocationSetter());
		
	
		
		operatorsMade=true;
		}
		}
	
	private LayerSelectionSystem selector;
	private ArrayList<MultiSelectionOperator> useroperators;
	private ArrayList<SelectionOperationsMenu> subordinateMenus=new ArrayList<SelectionOperationsMenu> ();
	
	public static SelectionOperationsMenu getStandardMenu(LayerSelectionSystem selection) {
			SelectionOperationsMenu out = new  SelectionOperationsMenu("Selected Item(s)", selection, operators);
			SelectionOperationsMenu o2 = new SelectionOperationsMenu("Options Dialogs", selection, operatorsOp);
			out.subordinateMenus.add(o2);
			out.insert(o2, 3);
			 o2 = new SelectionOperationsMenu("Application Scripts", selection, operatorsSc);
			 out.insert(o2, 4);
			 out.subordinateMenus.add(o2);
			 
			return out;
	}
	
	public static SelectionOperationsMenu getPrunedMenu(LayerSelectionSystem selection) {
		SelectionOperationsMenu output = getStandardMenu(selection);
		
		return output;
	}
	
	public void setSelector(LayerSelectionSystem selection) {
		for(MultiSelectionOperator i: useroperators) 
					{i.setSelector(selection);}
		for(SelectionOperationsMenu i: subordinateMenus) 
					{i.setSelector(selection);}
	}
	
	private static final long serialVersionUID = 1L;
	

	public SelectionOperationsMenu(String name, LayerSelectionSystem selection, ArrayList<MultiSelectionOperator > adders) {
		super(name);
		this.useroperators=adders;
		this.selector=selection;
		for (MultiSelectionOperator ad: adders) {
			if (ad.isValidForLayerSelector(selection)) addMenuItemForOperator(ad);
		}
	}
	
	private void addMenuItemForOperator(MenuItemInstall o) {
		JMenuItem jb = new JMenuItem(o.getMenuCommand(), o.getIcon());
		jb.addActionListener(this);
		jb.setActionCommand(o.getMenuCommand());
		if (o.getMenuPath()==null)
				add(jb);
		else {
			JMenu men =this;
			men = getOrCreateSubmenuFromPath(o, men);
			men.add(jb);
		}
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		for(MultiSelectionOperator ad: useroperators) {
			if (ad.getMenuCommand().equals(arg0.getActionCommand())) try {
			
				ad.setSelector( selector);
				ad.setSelection(selector.getSelecteditems());
				ad.run();
				
			} catch (Throwable t) {IssueLog.logT(t);}
			
		}
		
		if(selector.getWorksheet()!=null)
			selector.getWorksheet().updateDisplay();
	}

}
