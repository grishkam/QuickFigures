/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Version: 2022.2
 */
package exportMenus;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.ArrayObjectContainer;
import locatedObject.PointsToFile;
import logging.IssueLog;
import messages.ShowMessage;
import ultilInputOutput.FileChoiceUtil;
import utilityClasses1.SizeConstants;

/**A menu item for powerpoint export*/
public class PPTQuickExport extends QuickExport implements MenuItemForObj{
	
	

	public PPTQuickExport(boolean openNow) {
		super(openNow);
	}

	protected String getExtension() {
		return "ppt";
	}
	
	protected String getExtensionName() {
		return "PowerPoint Images";
	}
	

	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File f=getFileAndaddExtension();
		FileChoiceUtil.overrideQuestion(f);
		saveToPath(diw, f);
	}

	/**Saves the figure image to the given file 
	 * @param figure
	 * @param saveFile
	 */
	void saveToPath(DisplayedImage figure, File saveFile) {
					try{
						System.setProperty("javax.xml.transform.TransformerFactory",
				                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
						
						
			        XMLSlideShow ppt = new XMLSlideShow();
			        Dimension slideSize = SizeConstants.SLIDE_SIZE;
					ppt.setPageSize(slideSize);
			        XSLFSlide slide = ppt.createSlide();
			        
			        XSLFGroupShape group = slide.createGroup();
			    
			     
			   
			   Rectangle r ;
			   r=ArrayObjectContainer.combineOutLines(figure.getImageAsWorksheet().getLocatedObjects()).getBounds();
			 
			   group.setAnchor(r);
			   group.setInteriorAnchor(r);
			  
			
			   GraphicLayer set = figure.getImageAsWorksheet().getTopLevelLayer();
			   
			   
			        for(ZoomableGraphic z: set.getAllGraphics()) try {
			        	addObjectToSlide(ppt, group, z);
			        }catch (Throwable t) {
			        	t.printStackTrace();
			        }
			        
			        addNotesToSlide(slide, ppt, figure);
			        
			
			        FileOutputStream out;
					try {
						out = new FileOutputStream(saveFile.getAbsolutePath());
					
			        ppt.write(out);
			        out.close();
			        
			     if (openImmediately)   Desktop.getDesktop().open(saveFile);
			        
			        
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					}catch (Throwable t) {
						IssueLog.logT(t);
						}
	}
	
	
	/**
	 * @param slide
	 * @param ppt 
	 * @param figure 
	 */
	private void addNotesToSlide(XSLFSlide slide, XMLSlideShow ppt, DisplayedImage figure) {
		 String notesString = "This slide was exported from QuickFigures";
		 for(ZoomableGraphic object: figure.getImageAsWorksheet().getTopLevelLayer().getObjectsAndSubLayers()) {
			 if(object instanceof PointsToFile) {
				 PointsToFile pane=(PointsToFile) object;
				 File file = pane.getFile();
				 if(file!=null)
					 notesString+='\n'+file.getAbsolutePath();
			 }
			
		 }
		
		XSLFNotes notes = ppt.getNotesSlide(slide);
		for (XSLFTextShape shape : notes.getPlaceholders()) {
	        if (shape.getTextType() == Placeholder.BODY) {
	           
				shape.setText(notesString);
	            break;
	        }
	    }
	}

	/**
	 saves the image in the given path
	 */
	public void saveInPath(DisplayedImage diw, String newpath)
		{
		saveToPath(diw, new File(newpath));
	}

	@Override
	public String getCommand() {
		return "To PowerPoint";
	}

	@Override
	public String getNameText() {
		return "Create PowerPoint Slide (.ppt)";
	}
	


	
	
	public static void addObjectToSlide( XMLSlideShow ppt, XSLFShapeContainer slide, Object o) throws FileNotFoundException, IOException {
		
		if (o instanceof OfficeObjectConvertable)try {
			OfficeObjectConvertable t=(OfficeObjectConvertable) o;
			OfficeObjectMaker objectMaker = t.getObjectMaker();
			if (objectMaker==null) {
				IssueLog.log("object maker not found for "+t);
				} else
			objectMaker.addObjectToSlide( ppt, slide);
			
			
			
		}
		catch (Throwable t) {
			if ((t instanceof NoClassDefFoundError) || (t instanceof NoSuchMethodError)) {
				ShowMessage.showOptionalMessage("Install POI 4.1.2", false, "it appears the either an older version of Apache POI is installed (or POI is not installed correctly)", "cannot export", "Please install POI 4.1.2");
			} 
			IssueLog.logT(t);
		}
		
		
		
	}
	
	
	

}
