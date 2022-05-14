/**
 * Author: Greg Mazo
 * Date Created Jan 2, 2022
 * Date Modified: Jan 3, 2022
 * Version: 2022.1
 */
package pdfImporter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import figureFormat.DirectoryHandler;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import render.GraphicsRenderQF;

/**
 
 * 
 */
public class PageDrawer2 extends PageDrawer {
	
	
	
	  
    String codeForImage = "Do";
    String codeForText = "TJ";
    String codeForColor="rg";
    String codeForGrey="rg";
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

	private boolean textAsGyphs=false;
	private PDFont defaultFont;
	 

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
        IssueLog.log("operator for "+operator);
       
        if(this.codeForColor.equals(operationName)) {
        	
        }
        
		if( codeForImage.equals(operationName) )
        {
			super.processOperator( operator, operands);
            replaceLastImage(operands);
        }
        else
        	if( codeForText.equals(operationName) )
            {
    			super.processOperator( operator, operands);
                
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
	

	
	 /**
     * Process text from the PDF Stream. You should override this method if you want to
     * perform an action when encoded text is being processed.
     *
     * @param string the encoded text
     * @throws IOException if there is an error processing the string
     */
    protected void showText(byte[] string) throws IOException
    {
    	
    	if(textAsGyphs) {
    		super.showText(string);
    		return;
    	}
        PDGraphicsState state = getGraphicsState();
        PDTextState textState = state.getTextState();

        // get the current font
        PDFont font = textState.getFont();
        if (font == null)
        {
            IssueLog.log("No font found, will use default");
            font = getDefaultFont();
        }

        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100f;
        float charSpacing = textState.getCharacterSpacing();

        // put the text state parameters into matrix form
        Matrix parameters = new Matrix(
                fontSize * horizontalScaling, 0, // 0
                0, fontSize,                     // 0
                0, textState.getRise());         // 1

        // read the stream until it is empty
        InputStream in = new ByteArrayInputStream(string);
        String output="";
        while (in.available() > 0)
        {
            // decode a character
            int before = in.available();
            int code = font.readCode(in);
            output+=(char) code;
            
            int codeLength = before - in.available();

            // Word spacing shall be applied to every occurrence of the single-byte character code
            // 32 in a string when using a simple font or a composite font that defines code 32 as
            // a single-byte code.
            float wordSpacing = 0;
            if (codeLength == 1 && code == 32)
            {
                wordSpacing += textState.getWordSpacing();
            }

            // text rendering matrix (text space -> device space)
           // Matrix ctm = state.getCurrentTransformationMatrix();
           // Matrix textRenderingMatrix = parameters.multiply(textMatrix).multiply(ctm);

            // get glyph's position vector if this is vertical text
            // changes to vertical text should be tested with PDFBOX-2294 and PDFBOX-1422
           /** if (font.isVertical())
            {
                // position vector, in text space
                Vector v = font.getPositionVector(code);

                // apply the position vector to the horizontal origin to get the vertical origin
                textRenderingMatrix.translate(v);
            }*/
            // get glyph's horizontal and vertical displacements, in text space
           // Vector w = font.getDisplacement(code);

            // process the decoded glyph
           // showGlyph(textRenderingMatrix, font, code, w);

            // calculate the combined displacements
            /**
            float tx;
            float ty;
            if (font.isVertical())
            {
                tx = 0;
                ty = w.getY() * fontSize + charSpacing + wordSpacing;
            }
            else
            {
                tx = (w.getX() * fontSize + charSpacing + wordSpacing) * horizontalScaling;
                ty = 0;
            }

            // update the text matrix
            textMatrix.translate(tx, ty);*/
        }
        IssueLog.log("possible parsed text is "+output);
    }
    
    private PDFont getDefaultFont()
    {
        if (defaultFont == null)
        {
            defaultFont = new PDType1Font(FontName.HELVETICA);
        }
        return (PDFont) defaultFont;
    }
    
    public void showTextStrings(COSArray array) throws IOException
    {
    if(textAsGyphs)	{
    	super.showTextStrings(array);
    	return ;
    }
    PDGraphicsState state = getGraphicsState();
    PDTextState textState = getGraphicsState().getTextState();
    float fontSize = textState.getFontSize();
    float horizontalScaling = textState.getHorizontalScaling() / 100f;
    
    // put the text state parameters into matrix form
    Matrix parameters = new Matrix(
            fontSize * horizontalScaling, 0, // 0
            0, fontSize,                     // 0
            0, textState.getRise());         // 1
    
    PDFont font = textState.getFont();
    String start="";
    for (COSBase obj : array) {
    	if(obj instanceof COSString)
        {
            COSString obj2 = (COSString)obj;
			byte[] string = obj2.getBytes();
            InputStream in = new ByteArrayInputStream(string);
          
            while (in.available() > 0)
            {
            	 int code = font.readCode(in);
                 start+=(char) code;
            	
            };
        }
    	
    }
    Matrix ctm = state.getCurrentTransformationMatrix();
    Matrix textMatrix2 = super.getTextMatrix();
	Matrix textRenderingMatrix = parameters.multiply(textMatrix2).multiply(ctm);
    IssueLog.log("Render text at "+textRenderingMatrix  );
    reportOnMatrix(ctm, "ctm");
    reportOnMatrix( textMatrix2, "textMatrix2");
    reportOnMatrix(textRenderingMatrix, "textRenderingMatrix");
    
    if(start.equals(""))
    	return;
    else {
    	TextGraphic tg = new TextGraphic(start);
    	tg.setFont(Font.decode(font.getName()));
    	IssueLog.log("string is "+start);
    	IssueLog.log("font name is "+font.getName());
    	IssueLog.log("font name is "+Font.decode(font.getName()));
    	IssueLog.log("color name is "+this.getGraphicsState().getNonStrokingColor());
    	
    	
    	int rgb = this.getGraphicsState().getNonStrokingColor().toRGB();
    	tg.setTextColor(new Color(rgb));
    	tg.setFontSize((int) (fontSize*0.75));
    	tg.setContent(start);
    	tg.setLocation(textRenderingMatrix.getTranslateX(), ctm.getTranslateY()-textRenderingMatrix.getTranslateY());
    	tg.setAngle(determineAngle(ctm));
    	layer.add(tg);
    }
    	
    }


	/**
	 * @param ctm
	 */
	public void reportOnMatrix(Matrix ctm, String st) {
		IssueLog.log("Checking Matrix "+st);
		IssueLog.log(st+":scale of matrix "+ctm + " which is scaled "+ctm.getScaleX()+", "+ctm.getScaleY());
		IssueLog.log(st+":shear is "+ ctm.getShearX()+",   "+ctm.getShearY());
		IssueLog.log(st+": translate is "+ctm.getTranslateX()+",  "+ctm.getTranslateY());
	}
    
    
    private static double determineAngle(Matrix at)
    {
        Point2D point0 = new Point2D.Double();
        Point2D point1 = new Point2D.Double(1,0);
       at.transform(point0);
         at.transform(point1);
        double dx = point1.getX() - point0.getX();
        double dy = point1.getY() - point0.getY();
        double angle = Math.atan2(dy, dx);
        return angle;
    }
}


