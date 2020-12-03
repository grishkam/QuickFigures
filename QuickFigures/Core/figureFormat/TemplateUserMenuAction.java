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
package figureFormat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartJMenu;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import ultilInputOutput.FileChoiceUtil;
import undo.CombinedEdit;

/**This class creates menu items for saving/creating figure templates, loading saved templates, applying figure templates
  to few or many objects or deleting a figure template. It is required for the figure format menus to appear*/
public class TemplateUserMenuAction extends BasicMultiSelectionOperator implements MenuItemForObj{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static DirectoryHandler handler=new DirectoryHandler(); static {handler.makeAllNeededDirsIfAbsent();}
	
	/**set to true if this object allows user to create/save a template, false if it allows user to load/apply a template*/
	boolean save=true;
	/**set to true if this simply deletes a target template and nothing else. if this is true, it will neither save nor apply*/
	boolean delete=false;
	boolean useDefaultpath=false;//set to true if the default path for figure templates is to be used
	String menuPath="File<Save<";
	
	
	/**constructor, creates a menu action for either saving a new figure template or applying a saved one
	  the default template*/
	public TemplateUserMenuAction(boolean save, boolean willUseDefaultTemplate)  {
			this.save=save;
		useDefaultpath=willUseDefaultTemplate;
	}
	

	/**constructor, creates a menu action for a figure template with the pro*/
	public TemplateUserMenuAction(boolean save, boolean defpath, String menuPath)  {
			this.save=save;
		useDefaultpath=defpath;
		this.menuPath=menuPath;
	}
	
	
	/**displays a dialog for the user to select a file save path
	 * returns a file object for saving. the default for the file chooser*/
	public static File  getSaveFile() {
		String path=handler.fullPathofDefaultTemplate();
		File files;
		  files=FileChoiceUtil.getSaveFile(path, " template 2");
		  return files;
	}
	
	/**displays a dialog for the user to select a file
	 * and returns the file that the user has chosen to open*/
	public static File  getFileToOpen() {
		String path=handler.fullPathofDefaultTemplate();
		  File files=FileChoiceUtil.getOpenFile(path);//jd.getSelectedFile();
		  return files;
	}
	
	/**Saves a given figure template with the savepath.
	 * if the given save path is null, displays a dialog for the user to select a 
	 * save path.
	  */
	public synchronized void saveTemplate(FigureTemplate figure, String path) {
		if (path==null) {
			File file = getSaveFile();
			if (file==null) return;
			path=file.getAbsolutePath();
		}
		
		try {
			writeObjectToFile(figure, path);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	
	/**Loads a figure template from the save path
	 * if the given save path is null, displays a dialog for the user to select a 
	 * save path.
	  */
	public FigureTemplate loadTemplate(String path) {
		File f;
		if (path==null) {
			IssueLog.log("Problem: cannot load template");
			f=getFileToOpen() ;
		}
		else f=new File(path);
		
		Object ob = GraphicEncoder.readObjectFromFile(f);
		if (ob instanceof FigureTemplate) {
			return (FigureTemplate) ob;
		}
		return null;
	}
	
	/**serializes the object and stores it in the file path given*/
	public static synchronized void writeObjectToFile(Object o, String path) throws IOException {
		FileOutputStream os = new FileOutputStream(new File(path));
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(o);
			
			oos.flush();
			os.flush();
			//os.close();//TODO: determine if closing the output stream makes a difference
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}


	/**operates with the entire image given as its target for creating templates or applying a saved template*/
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		/**FigureTemplate tp=new FigureTemplate();
		TemplateChooserDialog dd = new TemplateChooserDialog(tp, diw.getImageAsWrapper());
		dd.showDialog();
		saveTemplate(tp, getUserPath());*/
		diw.getUndoManager().addEdit(
				this.operateOnContainer(diw.getImageAsWrapper()));
	}



	@Override
	public String getCommand() {
		return "MENUCMD"+getMenuCommand();
	}



	/**returns the text that the user will see in the menu*/
	@Override
	public String getNameText() {
		return getMenuCommand();
	}


	@Override
	public String getMenuPath() {
		return menuPath;
	}

	/**returns the menu text that acts as both the menu commands and the text of the menu item*/
	@Override
	public String getMenuCommand() {
		String output="";
		if (!save)  output+= "Apply";
			else if (delete) {
							 output+= "Delete";
						}
						else
					output+="Create";// Template ";
		
		if (this.useDefaultpath) output+=" Default";
		output+=" Template";
		return output;
	}

	/**Overrides the run command from the selection operations menu*/
	@Override
	public void run() {
		if (delete) {
			
			deleteTemplateFile();
			return;
		}
		
		
		if (super.selector.getGraphicDisplayContainer()==null) return;
		ArrayList<ZoomableGraphic> itemsSel = selector.getSelecteditems();
		CombinedEdit undo=null;
		
		if (itemsSel.size()==0) {
			FigureDisplayContainer graphicDisplayContainer = selector.getGraphicDisplayContainer();
					undo=operateOnContainer(graphicDisplayContainer);
		} else if (itemsSel.size()==1 && itemsSel.get(0) instanceof GraphicLayer) {
					undo=operateOnLayer((GraphicLayer) itemsSel.get(0));
		}
		else {
					undo=operateOnList(selector);
		}
		
		selector.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
		
	}

	/**Called when the target of this template saver is the given figure container.
	 * @return 
	 */
	public CombinedEdit operateOnContainer(FigureDisplayContainer graphicDisplayContainer) {
		
		GraphicLayer graphicLayerSet = graphicDisplayContainer.getAsWrapper().getGraphicLayerSet();
		
		return operateOnLayer(graphicLayerSet);
	}

	/**will apply the figure template to the layer and items within the layer
	 either creates+saves a template, deletes a saved template or applies a saved template
	 see constructors
	 * @return 
	 */
	public CombinedEdit operateOnLayer(GraphicLayer graphicLayerSet) {
		if (delete) {
			deleteTemplateFile();
			return null;
		}
		
		FigureTemplate tp=new FigureTemplate();
		
		/***/
		if (save) {
			TemplateChooserDialog dd = new TemplateChooserDialog(tp, graphicLayerSet);
			dd.showDialog();
		
			saveTemplate(tp, getUserPath());
			return null;
		} else 
			
		{
			FigureTemplate temp = loadTemplate( getUserPath());
			if (temp!=null) {
					CombinedEdit undo = new CombinedEdit();
					undo.addEditToList(
								temp.applyTemplateToLayer(graphicLayerSet));
					undo.addEditToList(
								temp.fixupLabelSpaces(graphicLayerSet));
					return undo;
			}
			
		}
		return null;
	}

	
	/**will apply the figure template to a set of selected items
	 * for example, the targets may be the layer and items within the layer
	 * @return */
	public CombinedEdit operateOnList(LayerSelector itemsSel ) {
		if (delete) {
			deleteTemplateFile();
			return null;
		}
		
		FigureTemplate tp=new FigureTemplate();
		if (save) {
			TemplateChooserDialog dd = new TemplateChooserDialog(tp, itemsSel.getSelectedLayer());
			dd.showDialog();
		
			saveTemplate(tp, getUserPath());
		} else 
		{
			FigureTemplate temp = loadTemplate( getUserPath());
			if (temp!=null) {
				CombinedEdit undo = new CombinedEdit();
				undo.addEditToList(
				temp.applyTemplateToList( itemsSel.getSelecteditems() ));
				undo.addEditToList(
				temp.fixupLabelSpaces( itemsSel.getSelecteditems() ));
		
				return undo;
			}
			
		}
		return null;
	}


	/**deletes the template file that this template saver targets*/
	public void deleteTemplateFile() {
		new File(getUserPath()).delete();
	}
	
	
/**returns the default template file path or returns null if the user is meant to chose one*/
	public String getUserPath() {
		String path=null;
		if (useDefaultpath) {path=handler.fullPathofDefaultTemplate();}
		return path;
	}
	
	/**returns the default template*/
	public FigureTemplate loadDefaultTemplate() {
		String path=handler.fullPathofDefaultTemplate();
		return loadTemplate(path);
		
	}
	
	/**Saves the given template as the new default template*/
	public void saveDefaultTemplate(FigureTemplate temp) {
		String path=handler.fullPathofDefaultTemplate();
		this.saveTemplate(temp, path);
		
	}
	
	/**Creates a series of items that will appear in the same menu*/
	public static ArrayList<TemplateUserMenuAction> createSeveral(String figFormatPath) {
		ArrayList<TemplateUserMenuAction> t=new ArrayList<TemplateUserMenuAction>();
		t.add(new TemplateUserMenuAction(true, true, figFormatPath));
		t.add(new TemplateUserMenuAction(false, true, figFormatPath));
		t.add(new TemplateUserMenuAction(true, false, figFormatPath));
		t.add(new TemplateUserMenuAction(false, false, figFormatPath));
		t.add(createTemplateDeleter(figFormatPath));
		return t;
	}

	/**creates a version that deletes templates*/
	public static TemplateUserMenuAction createTemplateDeleter(String figFormatPath) {
		TemplateUserMenuAction delTemp = new TemplateUserMenuAction(true, true, figFormatPath);
		delTemp.delete=true;
		return delTemp;
	}
	
	/**Returns a menu that the user can use to format figures in a layer */
	public static SmartJMenu createFormatMenu(GraphicLayer l) {
		SmartJMenu output=new SmartJMenu("Format Figure");
		ArrayList<TemplateUserMenuAction> list = createSeveral("");
		for(TemplateUserMenuAction item: list) {
			output.add(new TemplateSaverAction(item,l));
		}
		return output;
	}
	
	/**a j menu item for a popup menu that targets a particular layer*/
	static class TemplateSaverAction extends BasicSmartMenuItem implements ActionListener{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private TemplateUserMenuAction saver;
		private GraphicLayer layer;
		public TemplateSaverAction(TemplateUserMenuAction t, GraphicLayer l) {
			this.saver=t;
			this.layer=l;
			this.addActionListener(this);
			this.setText(saver.getMenuCommand());
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			CombinedEdit undo = saver.operateOnLayer(layer);
			if (this.undoManager!=null) this.undoManager.addEdit(undo);
		}
	}
	
}
