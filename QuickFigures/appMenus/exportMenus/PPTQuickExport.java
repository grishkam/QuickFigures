package exportMenus;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;

import officeConverter.OfficeObjectConvertable;
import utilityClassesForObjects.ArrayObjectContainer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;

public class PPTQuickExport extends QuickExport implements MenuItemForObj{

	protected String getExtension() {
		return "ppt";
	}
	
	protected String getExtensionName() {
		return "PowerPoint Images";
	}
	
	


	
	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File f=getFileAndaddExtension();
		
		
		
		
		//File f=new File("testPPT.ppt");
		try{
			System.setProperty("javax.xml.transform.TransformerFactory",
	                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			
			
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();
        XSLFGroupShape group = slide.createGroup();
    
     
   
   Rectangle r ;
   r=ArrayObjectContainer.combineOutLines(diw.getImageAsWrapper().getLocatedObjects()).getBounds();
 
   group.setAnchor(r);
   group.setInteriorAnchor(r);
  

   GraphicLayer set = diw.getImageAsWrapper().getGraphicLayerSet();
   
   
        for(ZoomableGraphic z: set.getAllGraphics()) try {
        	addObjectToSlide(ppt, group, z);
        }catch (Throwable t) {
        	t.printStackTrace();
        }
        

        FileOutputStream out;
		try {
			out = new FileOutputStream(f.getAbsolutePath());
		
        ppt.write(out);
        out.close();
        
        Desktop.getDesktop().open(f);
        
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		}catch (Throwable t) {
			IssueLog.logT(t);
			}
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "To PowerPoint";
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "Create PowerPoint Slide";
	}

	
	
	public void addObjectToSlide( XMLSlideShow ppt, XSLFShapeContainer slide, Object o) throws FileNotFoundException, IOException {
		
		if (o instanceof OfficeObjectConvertable)try {
			OfficeObjectConvertable t=(OfficeObjectConvertable) o;
			t.getObjectMaker().addObjectToSlide( ppt, slide);
			
		}
		catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		
		
	}
	
	
	

}
