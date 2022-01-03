/**
 * Author: Greg Mazo
 * Date Modified: Jan 2, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package pdfImporter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import appContextforIJ1.ImageDisplayTester;
import figureFormat.DirectoryHandler;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BufferedImageGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import render.GraphicsRenderQF;

/**
 
 * 
 */
public class PageDrawer2 extends PageDrawer {
	
	
	public static String path=new DirectoryHandler().getFigureFolderPath()+"/importME.pdf";
	  
    String codeForImage = "Do";
	 /**
    * @param args The command line arguments.
    *
    * @throws IOException If there is an error parsing the document.
   
   public static void main( String[] args ) throws IOException
   {
	   IssueLog.sytemprint=true;
       PDDocument document = null;
       ImageDisplayTester.main(null);
   	ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 400, 300);
   	
       try
       {
       	File file = new File(path);
           document =  Loader.loadPDF(file);
          QFPDFRenderer render = new QFPDFRenderer(document, diw.getImageAsWorksheet().getTopLevelLayer(), null);
        
           int pageNum = 0;
           for( PDPage page : document.getPages() )
           {
               pageNum++;
              	IssueLog.log( "On page: " + pageNum );
               //extact.processPage(page);
           }
       }
       finally
       {
           if( document != null )
           {
               document.close();
           }
       }
   }
 */
	
	private GraphicLayer layer;
	 

	public PageDrawer2(PageDrawerParameters parameters, GraphicLayer layer) throws IOException {
		super(parameters);
		this.layer=layer;
	}
	
	
	 /**
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    @Override
    protected void processOperator( Operator operator, List<COSBase> operands) throws IOException
    {
        String operationName = operator.getName();
       
      
        IssueLog.log("processing operator "+operationName+ " wnich is "+ operator);
		if( codeForImage.equals(operationName) )
        {
			super.processOperator( operator, operands);
            processForImage(operands);
        }
        else
        {
        	
            super.processOperator( operator, operands);
        }
    }


	/**
	 * @param operands
	 * @throws IOException
	 */
	public void processForImage(List<COSBase> operands) throws IOException {
		PDXObject xobject = getPDXObject(operands);
		if( xobject instanceof PDImageXObject)
		{
		    PDImageXObject image = (PDImageXObject)xobject;
 
		    
		   ImagePanelGraphic imagePanel = new ImagePanelGraphic(image.getImage());
		   if ( GraphicsRenderQF.lastImage!=null) {
			   GraphicsRenderQF.lastImage.setImage(image.getImage());
		   }
		   else
			   layer.add( imagePanel);
		   
		  
 
		}
		else if(xobject instanceof PDFormXObject)
		{
			IssueLog.log("showing form of "+xobject);
		    PDFormXObject form = (PDFormXObject)xobject;
		    showForm(form);
		} else {
			IssueLog.log("dont know what do to with "+xobject);
		}
	}


	/**
	 * @param operands
	 * @return
	 * @throws IOException
	 */
	public PDXObject getPDXObject(List<COSBase> operands) throws IOException {
		COSName objectName = (COSName) operands.get( 0 );
		PDXObject xobject = getResources().getXObject( objectName );
		return xobject;
	}
	

}
