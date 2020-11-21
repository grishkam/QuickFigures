package basicMenusForApp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import addObjectMenus.ObjectAddingMenu;
import exportMenus.FlatCreator;
import exportMenus.PNGQuickExport;
import exportMenus.PNGSequenceQuickExport;
import exportMenus.PPTQuickExport;
import exportMenus.SVGQuickExport;
import exportMenus.TiffQuickExport;
import figureFormat.TemplateSaver;
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
		installItem(new TemplateSaver(true, false));
		
		
	//	intallItem(new TemplateSaver(false));
		this.add(imageMenu);
		
		
	
		installItem(new UndoRedoMenuItem(true));
		installItem(new UndoRedoMenuItem(false));
		installItem(new TimeLineAction(true));
		
		installItem(new  GraphicSetOpener());
		installItem(new SVGOpener());
		
		installItem(new TreeShower());
		installItem(new CanvasAutoTrim());
		installItem(new CanvasDialogResize());
		installItem(new CanvasDialogResize(CanvasDialogResize.Inch));
		installItem(new CanvasAutoResize() );
		installItem(new CanvasAutoResize(CanvasAutoResize.slide) );
		installItem(new CanvasAutoResize(CanvasAutoResize.page) );
		
		installItem(new ZoomFit());
		installItem(new ZoomFit(ZoomFit.OUT));
		installItem(new ZoomFit(ZoomFit.IN));
		installItem(new ZoomFit(ZoomFit.USER_SET));
		
		for(int i=0; i<5; i++)
			installItem(new ShowToolBar(i));
		
		
		installItem(new FlatCreator());
		installItem(new CombineImages());
		addOfficeToolsToMenu();
		installItem(new GraphicSetCloser2());
		
		CurrentSetLayerSelector ls = new CurrentSetLayerSelector();
		add(SelectionOperationsMenu.getStandardMenu(ls));
		add(ObjectAddingMenu.getStandardAddingMenu(ls));
		
		//installItem(new XMLloadItem());
		installItem(new DebugMenuItems());
		installItem(new DebugMenuItems(false));
		
	String figFormatPath="Image<Figure Format<";
	ArrayList<TemplateSaver> templateMenu = TemplateSaver.createSeveral(figFormatPath);
	for(TemplateSaver i: templateMenu) {
		installItem(i);
	}
		
		
		for(MenuBarItemInstaller installer: installers) {
			if (installer==null) continue;
			installer.addToMenuBar(this);
		}
	}
	
	
	void addOfficeToolsToMenu() {
		
			
			installItem(new PNGQuickExport());
			installItem(new TiffQuickExport());
			installItem(new PNGSequenceQuickExport());
			
			
			try {installItem(new PPTQuickExport());} 
			catch (Throwable t) {	
			//if there is any problem with installation the menu item will not be added
		}
			
			try {
				SVGQuickExport obj = new SVGQuickExport();
				if (obj.isBatikInstalled())
					installItem(obj);
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
