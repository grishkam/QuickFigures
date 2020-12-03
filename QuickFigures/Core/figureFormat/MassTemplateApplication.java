package figureFormat;

import java.io.File;
import java.util.ArrayList;

import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import layersGUI.GraphicTreeUI;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import ultilInputOutput.FileChoiceUtil;


/**A multi selection operator that applies a figure template to many files*/
public class MassTemplateApplication extends BasicMultiSelectionOperator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TemplateUserMenuAction templateSaver=new TemplateUserMenuAction(false, true);
	
	/**Will apply a single default template to a series of files that each contain serialized
  	figures*/
	public void perform(ArrayList<File> files) {
		 FigureTemplate template = templateSaver.loadDefaultTemplate();
		for(File f: files) {
			ImageWindowAndDisplaySet figure = ImageDisplayIO.showFile(f);
			GraphicLayer layer = figure.getImageAsWrapper().getGraphicLayerSet();
			ArrayList<GraphicLayer> allsublayers = layer.getSubLayers();
			
			for(GraphicLayer figLayer: allsublayers) {
				if (figLayer instanceof FigureOrganizingLayerPane) {
					template.applyTemplateToLayer(figLayer);
					
				}
			}
			
			
		}
		
		
	}
	

	@Override
	public String getMenuCommand() {
		return "Apply Default Template to File List";
	}
	
	public String getMenuPath() {
		return "File Lists";
	}

	@Override
	public void run() {
		ArrayList<File> files = super.getPointedFiles();
		 if (files.size()==0) {
			  files = FileChoiceUtil.getFileArray();
		 }
		perform(files);
		
	}
	
	/**this option is only available from the layers window (that window contains a means of storing a file list)*/
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
			return true;
		return false;
		}


}
