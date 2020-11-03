package exportMenus;


import java.awt.Desktop;
import java.io.File;

import applicationAdapters.DisplayedImageWrapper;
import fieldReaderWritter.SVGsaver;
import logging.IssueLog;

public class SVGQuickExport extends QuickExport {
	protected String getExtension() {
		return "svg";
	}
	
	protected String getExtensionName() {
		return "SVG Images";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {

		try{
			System.setProperty("javax.xml.transform.TransformerFactory",
	                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			File file = getFileAndaddExtension();
			if (file==null) return;
		String newpath=file.getAbsolutePath();
		
		 
		   
		  SVGsaver saver = new SVGsaver();
		   
		  saver.saveWrapper(newpath, diw);
		    Desktop.getDesktop().open(new File(newpath));
		
		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				IssueLog.showMessage("Opps"+ "It seems imageJ cant find "+t.getMessage());
			//this.getClass().getClassLoader().loadClass(arg0)
			}
			IssueLog.log(t);
		}
	        
	}

	@Override
	public String getCommand() {
		return "Export as SVG";
	}

	@Override
	public String getNameText() {
		return "Export as SVG";
	}
	
}
