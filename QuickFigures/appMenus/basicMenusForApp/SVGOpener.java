package basicMenusForApp;

import java.io.File;

import ultilInputOutput.FileChoiceUtil;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ToolbarTester;
import fieldReaderWritter.GraphicSVGParser;
import imageDisplayApp.GraphicSet;
import imageDisplayApp.ImageAndDisplaySet;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;

/**does a crude and dirty import of an SVG file. simply used to open shapes*/
public class SVGOpener   extends BasicMenuItemForObj {


	
	public static void main(String[] arts) {
		ToolbarTester.startToolbars(true);
		String path="/Users/Greg Mazo/Documents/Quick Figures/export 5.svg";
		File f = new File(path);
		SVGOpener.showFile(f);
		
		//svgOpener.showFile(f);
		/**
	   path="/Users/mazog/Desktop/test.svg";
		File f2 = new File(path);
	   svgOpener.showFile(f2);
	   svgOpener.showFile(f);*/
	  
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		File f=FileChoiceUtil.getOpenFile();
	
		showFile(f);
			
		
	}
	
	public static ImageAndDisplaySet showFile(File f) {
		if (f==null) return null;
		//diw.getImageAsWrapper();
		
		
		IssueLog.log("showing file "+'\n'+f.getAbsolutePath());
			
			GraphicSet set = readFromFile(f);
			if (set==null) return null;
			ImageAndDisplaySet output = new ImageAndDisplaySet(set);
			
			new CanvasAutoResize().performActionDisplayedImageWrapper(output);
			return output;
	}
	
	public static GraphicSet readFromFile(File f) {
	
		if (!f.exists()) {
			IssueLog.log("file "+" is non existent"+f);
		}
		
		try {
			
			
			
			
			GraphicSet set = new GraphicSVGParser().openSVG(f.getAbsolutePath());
		return set;
			//return GraphicSVGParser.openSVG(f.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "openSVGDisplaySet";
	}

	@Override
	public String getNameText() {
		
		return "Drawing (experimental)";
	}

	@Override
	public String getMenuPath() {
		return "File<Open";
	}

}
