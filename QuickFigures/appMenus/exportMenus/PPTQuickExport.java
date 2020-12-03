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
package exportMenus;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;

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
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
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
			OfficeObjectMaker objectMaker = t.getObjectMaker();
			if (objectMaker==null) {
				IssueLog.log("object maker not found for "+t);
				} else
			objectMaker.addObjectToSlide( ppt, slide);
			
		}
		catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		
		
	}
	
	
	

}
