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
package basicMenusForApp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import addObjectMenus.ObjectAddingMenu;
import exportMenus.EPSQuickExport;
import exportMenus.FlatCreator;
import exportMenus.PDFQuickExport;
import exportMenus.PNGQuickExport;
import exportMenus.PNGSequenceQuickExport;
import exportMenus.PPTQuickExport;
import exportMenus.SVGQuickExport;
import exportMenus.TiffQuickExport;
import figureFormat.TemplateUserMenuAction;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.CurrentSetInformer;
import graphicActionToolbar.QuickFigureMaker;
import imageMenu.CombineImages;
import imageMenu.UndoRedoMenuItem;
import imageMenu.CanvasAutoResize;
import imageMenu.CanvasAutoTrim;
import imageMenu.CanvasDialogResize;
import imageMenu.ZoomFit;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import selectedItemMenus.SelectionOperationsMenu;
import uiForAnimations.TimeLineAction;

/**The main menu bar that appears for every image (and some toolbars) 
 
 @see MenuItemForObj
 It will appear above windows ( @see GraphicSetDisplayWindow).
 */
public class MenuBarForApp extends JMenuBar implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CurrentSetInformer currentImageInformer=new CurrentFigureSet();
	
	/**A hashmap with menu commands and the objects that will execute them*/
	HashMap<String, MenuItemForObj> isntalledMenuItems=new HashMap<String, MenuItemForObj>();
	
	static ArrayList<MenuBarItemInstaller> installers=new ArrayList<MenuBarItemInstaller>();
	
	public static void addMenuBarItemInstaller(MenuBarItemInstaller mbi) {
		installers.add(mbi);
	}
	
	public JMenu imageMenu=setupImageMenu();
	
	
	public MenuBarForApp() {
		installItem(new NewCanvasDialog());
		installItems(QuickFigureMaker.getMenuBarItems());
		installItem(new GraphicSetSaver());
		installItem(new TemplateUserMenuAction(true, false));
		
		
	
		this.add(imageMenu);//makes sure the image menu is the second menu
		
		
	
		installItem(new UndoRedoMenuItem(true));
		installItem(new UndoRedoMenuItem(false));
		installItem(new TimeLineAction(true));
		
		installItem(new  GraphicSetOpener());
		installItem(new SVGOpener());
		
		installItem(new TreeShower());
		
		installItem(new CanvasDialogResize());
		installItem(new CanvasAutoResize() );
		installItem(new CanvasAutoTrim());
		installItem(new CanvasAutoResize(CanvasAutoResize.slide) );
		installItem(new CanvasAutoResize(CanvasAutoResize.page) );
		
		installItem(new ZoomFit());
		installItem(new ZoomFit(ZoomFit.OUT));
		installItem(new ZoomFit(ZoomFit.IN));
		installItem(new ZoomFit(ZoomFit.USER_SET));
		
		
		for(int i=0; i<ShowToolBar.names.length; i++)
			installItem(new ShowToolBar(i));
		
		
		installItem(new FlatCreator());
		installItem(new CombineImages());
		addExportMenus();
		installItem(new GraphicSetCloser2());
		
		CurrentSetLayerSelector ls = new CurrentSetLayerSelector();
		add(SelectionOperationsMenu.getStandardMenu(ls));
		add(ObjectAddingMenu.getStandardAddingMenu(ls));
		
		//installItem(new XMLloadItem());
		installItem(new DebugMenuItems());
		installItem(new DebugMenuItems(false));
		installItem(new WindowDebugMenuItem());
		
	String figFormatPath="Image<Figure Format<";
	ArrayList<TemplateUserMenuAction> templateMenu = TemplateUserMenuAction.createSeveral(figFormatPath);
	for(TemplateUserMenuAction i: templateMenu) {
		installItem(i);
	}
		
		
		for(MenuBarItemInstaller installer: installers) {
			if (installer==null) continue;
			installer.addToMenuBar(this);
		}
	}
	
	
	/**Installs the export menu items*/
	void addExportMenus() {
		
			
			installItem(new PNGQuickExport(false));
			installItem(new TiffQuickExport(false));
			installItem(new PNGSequenceQuickExport(false));
			
			
			try {
				installItem(new PPTQuickExport(true));} 
			catch (Throwable t) {	
			//if there is any problem with installation the menu item will not be added
		}
			
			installBatikExportItems();
			
			
			
	
	}


	/**
	 * 
	 */
	public void installBatikExportItems() {
		try {
			SVGQuickExport obj = new SVGQuickExport();
			if (obj.isBatikInstalled())
				{
				installItem(obj);
			
				EPSQuickExport eps=new EPSQuickExport(false);
				installItem(eps);
				
				PDFQuickExport ep=new PDFQuickExport(false);
				installItem(ep);
			}
			
			
			} 
		catch (Throwable t) {	
			//if there is any problem with installation the menu item will not be added
}
	}
	
	
	public JMenu setupImageMenu() {
		JMenu output = new SmartJMenu("Image");
		
		return output;
	}
	
	public void installItems(MenuItemForObj... o) {
		for(MenuItemForObj obj: o) {
			installItem(obj);
		}
	}
	
	public void installItem(MenuItemForObj obj) {
		if(obj==null) return;
		try{
		
		JMenuItem ji=new JMenuItem(obj.getNameText());
		ji.setIcon(obj.getIcon());
		ji.setActionCommand(obj.getCommand());
		ji.addActionListener(this);
		isntalledMenuItems.put(obj.getCommand(), obj);
		
		String menuPath=obj.getMenuPath();
		String delimiter="<";
		
		JMenu men2 = getOrCreateSubmenuOfPath(this, menuPath, delimiter);
		
		men2.add(ji);
		
		} catch (Throwable t) {
			IssueLog.log("Problem installing items");
			IssueLog.logT(t);
		}
	}
	
	/**returns the submenu with the given menu path. if that submenu does not exist, creates the submenu*/
	public static JMenu getOrCreateSubmenuOfPath(JMenuBar th, String menuPath, String delimiter) {
		String[] array = menuPath.split(delimiter);
		
		JMenu men2 = SmartJMenu.getSubmenu(th, array[0]);
		if (men2==null) {
			men2=new SmartJMenu(array[0]);
			th.add(men2);
			};
			
		for(int i=1; i<array.length; i++) {
			JMenu men3 = men2;
			 men2 = SmartJMenu.getSubmenuOfJMenu(men3, array[i]);
			 if (men2==null) {
					men2=new SmartJMenu(array[i]);
					men3.add(men2);
					};
		}
		
		return men2;
		
	}
	

	public static void main(String[] args) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
		
		MenuItemForObj item = isntalledMenuItems.get(arg0.getActionCommand());
		
		
		item.performActionDisplayedImageWrapper(currentImageInformer.getCurrentlyActiveDisplay());
		} catch (Throwable t) {
			IssueLog.logT(t);;
		}
		}
	
	
}
