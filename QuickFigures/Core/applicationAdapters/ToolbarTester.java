package applicationAdapters;

import java.io.File;

import javax.swing.JFrame;

import figureTemplates.DirectoryHandler;
import imageDisplayApp.ImageAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import includedToolbars.ActionToolset1;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;
import layersGUI.GraphicTreeUI;
import logging.IssueLog;

public class ToolbarTester {

	public static void main(String[] args) {
		IssueLog.sytemprint=true;
		startToolbars(true);
	}
	
	public static void startToolbars(boolean appclose) {
		
		ObjectToolset1 toolset = showInnitial();
		if (appclose) {
			toolset.getframe().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		}
	
	public static ObjectToolset1 showInnitial() {
		return  showToolSet();
	}
	
	/**shows both object and layout toolsets*/
	public static ObjectToolset1 showToolSet() {
		ObjectToolset1 ot = new ObjectToolset1();
		new LayoutToolSet().run("");
		new ActionToolset1().run("go");
		ot.run("hi");
		return ot;
	}

	
	
	public static ImageAndDisplaySet showExample(boolean appclose) {
		startToolbars(appclose);
		 
		
		File example=new File(new DirectoryHandler().getFigureFolderPath()+"/example");
		
		if (example.exists()) {
		 try {
			return setSavedExample(example);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		} 
		{
			 IssueLog.log("Example File not found "+'\n'+example.getAbsolutePath());
			 return ImageAndDisplaySet.createAndShowNew("Figure", 400,300);
		 }
		
		
		
		
		
	}

	private static ImageAndDisplaySet setSavedExample(File example) {
		ImageAndDisplaySet file = ImageDisplayIO.showFile(example);
		 ImageAndDisplaySet.exampletree = new GraphicTreeUI(file.getImageAsWrapper());
		
		 ImageAndDisplaySet. exampletree  .showTreeForLayerSet(file.getImageAsWrapper()) ;
		 
		//locatedObject object0 = file.getImageAsWrapper().getLocatedObjects().get(0);
		//PathGraphic p2=(PathGraphic) object0;
		//file.getImageAsWrapper().getGraphicLayerSet().add(p2.copy());
		//p2.setStrokeColor(Color.orange);
		//p2.getPoints().cullPointAndAdjustCurvature(p2.getPoints().get(1));
		 
		 
		 
		 return file;
	}
}
