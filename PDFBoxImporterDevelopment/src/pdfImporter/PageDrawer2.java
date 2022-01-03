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
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import figureFormat.DirectoryHandler;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.RectangleEdges;
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
       
		if( codeForImage.equals(operationName) )
        {
			super.processOperator( operator, operands);
            replaceLastImage(operands);
        }
        else
        {
        	
            super.processOperator( operator, operands);
        }
    }

    
    @Override
    public void drawImage(PDImage pdImage) throws IOException
    	{
    	
    	super.drawImage(pdImage);
    	}
    
 

	/**Replaces the scaled version of the image with the unscaled version
	 * @param operands
	 * @throws IOException
	 */
	public void replaceLastImage(List<COSBase> operands) throws IOException {
		PDXObject xobject = getPDXObject(operands);
		if( xobject instanceof PDImageXObject)
		{
		    PDImageXObject image = (PDImageXObject)xobject;
 
		    
		   ImagePanelGraphic imagePanel = new ImagePanelGraphic(image.getImage());
		   if ( GraphicsRenderQF.lastImage!=null) {
			   BufferedImage newImage = image.getImage();
			  double startWidth= GraphicsRenderQF.lastImage.getBufferedImage().getWidth();
			  double ratio = newImage.getWidth()/startWidth;
			  GraphicsRenderQF.lastImage.setLocationType(RectangleEdges.UPPER_LEFT);
			   GraphicsRenderQF.lastImage.setImage(newImage);
			   GraphicsRenderQF.lastImage.setRelativeScale(GraphicsRenderQF.lastImage.getRelativeScale()/ratio);
			   GraphicsRenderQF.lastImage.setLocationUpperLeft( GraphicsRenderQF.lastImageX,  GraphicsRenderQF.lastImageY);//Note, this works because there is no scale
			 //  ShowMessage.showOptionalMessage("Warning: image import", true, "Import of image panels not yet fully implemented");
		   }
		   else
			   layer.add( imagePanel);
		   
		  
 
		}
		else if(xobject instanceof PDFormXObject)
		{
			
		    PDFormXObject form = (PDFormXObject)xobject;
		    showForm(form);
		    
		} else {
			
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
