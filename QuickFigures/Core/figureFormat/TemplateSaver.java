package figureFormat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import ultilInputOutput.FileChoiceUtil;

public class TemplateSaver extends BasicMultiSelectionOperator implements MenuItemForObj{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static DirectoryHandler handler=new DirectoryHandler(); static {handler.makeAllNeededDirsIfAbsent();}
	boolean save=true;
	boolean useDefaultpath=false;//set to true if the default path for figure templates is to be used
	String menuPath="File<Save<";
	boolean delete=false;
	
	public TemplateSaver(boolean save, boolean defpath)  {
			this.save=save;
		useDefaultpath=defpath;
	}
	
	
	public TemplateSaver(boolean save, boolean defpath, String menuPath)  {
			this.save=save;
		useDefaultpath=defpath;
		this.menuPath=menuPath;
	}
	
	
	/**returns a file for saving. the default for the file choser*/
	public static File  getSaveFile() {
		String path=handler.fullPathofDefaultTemplate();
		///JFileChooser jd= new JFileChooser(path); jd.setFileSelectionMode(JFileChooser.FILES_ONLY ); jd.setDialogTitle("Save Template");  jd.showSaveDialog(null); 
		  File files;//=jd.getSelectedFile();
		  files=FileChoiceUtil.getSaveFile(path, " template 2");
		  return files;
	}
	
	public static File  getFileToOpen() {
		String path=handler.fullPathofDefaultTemplate();
		//JFileChooser jd= new JFileChooser(path); jd.setFileSelectionMode(JFileChooser.FILES_ONLY ); jd.setDialogTitle("Choose Template");  jd.showOpenDialog(null); 
		  File files=FileChoiceUtil.getOpenFile(path);//jd.getSelectedFile();
		  return files;
	}
	
	public synchronized void saveTemplate(FigureTemplate figure, String path) {
		if (path==null) {
			File file = getSaveFile();
			if (file==null) return;
			path=file.getAbsolutePath();
		}
		
		try {
			writeObjectToFile(figure, path);
		} catch (Exception e) {
			IssueLog.log(e);
		}
	}
	
	

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
	
	
	public static synchronized void writeObjectToFile(Object o, String path) throws IOException {
		FileOutputStream os = new FileOutputStream(new File(path));
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(o);
			
			oos.flush();
			os.flush();
			//os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//writeToOS(os, this.getItemToBeEncoded());
	}



	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		/**FigureTemplate tp=new FigureTemplate();
		TemplateChooserDialog dd = new TemplateChooserDialog(tp, diw.getImageAsWrapper());
		dd.showDialog();
		saveTemplate(tp, getUserPath());*/
		this.operateOnContainer(diw.getImageAsWrapper());
	}



	@Override
	public String getCommand() {
		return "MENUCMD"+getMenuCommand();
		// TODO Auto-generated method stub
//	if (!save) return "openAndApplyTemplate";
	//	return "saveATemplate";
	}



	@Override
	public String getNameText() {
		return getMenuCommand();
	}


	

	@Override
	public String getMenuPath() {
		return menuPath;
	}

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
		
		if (itemsSel.size()==0) {
			GraphicSetDisplayContainer graphicDisplayContainer = selector.getGraphicDisplayContainer();
			
			operateOnContainer(graphicDisplayContainer);
		} else if (itemsSel.size()==1 && itemsSel.get(0) instanceof GraphicLayer) {
			operateOnLayer((GraphicLayer) itemsSel.get(0));
		}
		else {
			operateOnList(selector);
		}
	}


	public void operateOnContainer(GraphicSetDisplayContainer graphicDisplayContainer) {
		
		GraphicLayer graphicLayerSet = graphicDisplayContainer.getAsWrapper().getGraphicLayerSet();
		
		operateOnLayer(graphicLayerSet);
	}

	/**will apply the figure template to the layer and items within the layer*/
	public void operateOnLayer(GraphicLayer graphicLayerSet) {
		if (delete) {
			deleteTemplateFile();
			return;
		}
		
		FigureTemplate tp=new FigureTemplate();
		if (save) {
			TemplateChooserDialog dd = new TemplateChooserDialog(tp, graphicLayerSet);
			dd.showDialog();
		
			saveTemplate(tp, getUserPath());
		} else 
		{
			FigureTemplate temp = loadTemplate( getUserPath());
			if (temp!=null)
			temp.applyProperties(graphicLayerSet);
			temp.fixupLabelSpaces(graphicLayerSet);
			
		}
	}

	
	/**will apply the figure template to the layer and items within the layer*/
	public void operateOnList(LayerSelector itemsSel ) {
		if (delete) {
			deleteTemplateFile();
			return;
		}
		
		FigureTemplate tp=new FigureTemplate();
		if (save) {
			TemplateChooserDialog dd = new TemplateChooserDialog(tp, itemsSel.getSelectedLayer());
			dd.showDialog();
		
			saveTemplate(tp, getUserPath());
		} else 
		{
			FigureTemplate temp = loadTemplate( getUserPath());
			if (temp!=null)
			temp.applyProperties( itemsSel.getSelecteditems() );
			temp.fixupLabelSpaces( itemsSel.getSelecteditems() );
			
		}
	}


	public void deleteTemplateFile() {
		new File(getUserPath()).delete();
	}
/**returns the default template file path or returns null if the user is meant to chose one*/
	public String getUserPath() {
		String path=null;
		if (useDefaultpath) {path=handler.fullPathofDefaultTemplate();}
		return path;
	}
	
	public FigureTemplate loadDefaultTemplate() {
		String path=handler.fullPathofDefaultTemplate();
		return loadTemplate(path);
		
	}
	
	public void saveDefaultTemplate(FigureTemplate temp) {
		String path=handler.fullPathofDefaultTemplate();
		this.saveTemplate(temp, path);
		
	}
	
	public static ArrayList<TemplateSaver> createSeveral(String figFormatPath) {
		ArrayList<TemplateSaver> t=new ArrayList<TemplateSaver>();
		t.add(new TemplateSaver(true, true, figFormatPath));
		t.add(new TemplateSaver(false, true, figFormatPath));
		t.add(new TemplateSaver(true, false, figFormatPath));
		t.add(new TemplateSaver(false, false, figFormatPath));
		t.add(createTemplateDeleter(figFormatPath));
		return t;
	}


	public static TemplateSaver createTemplateDeleter(String figFormatPath) {
		TemplateSaver delTemp = new TemplateSaver(true, true, figFormatPath);
		delTemp.delete=true;
		return delTemp;
	}
	
	/**Returns a menu that the user can use to format figures in layer l*/
	public static JMenu createFormatMenu(GraphicLayer l) {
		JMenu output=new JMenu("Format Figure");
		ArrayList<TemplateSaver> list = createSeveral("");
		for(TemplateSaver item: list) {
			output.add(new TemplateSaverAction(item,l));
		}
		return output;
	}
	
	static class TemplateSaverAction extends JMenuItem implements ActionListener{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private TemplateSaver saver;
		private GraphicLayer layer;
		public TemplateSaverAction(TemplateSaver t, GraphicLayer l) {
			this.saver=t;
			this.layer=l;
			this.addActionListener(this);
			this.setText(saver.getMenuCommand());
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			saver.operateOnLayer(layer);
		}
	}
	
}
