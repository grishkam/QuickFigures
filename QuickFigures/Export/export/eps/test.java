package export.eps;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.commons.io.IOUtils;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFDocumentGraphics2D;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import ultilInputOutput.FileChoiceUtil;

/**
class for written while i tinkered with pdf and eps export
 */
public class test {

 

    /**
     * Command-line interface
     * @param args command-line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	try {
    		emptyPDF();
			//trPDF();
			
			
		//	trEPS();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * @throws TranscoderException
	 * @throws IOException 
	 */
	public static void trPDF() throws TranscoderException, IOException {
		org.apache.fop.svg.PDFTranscoder transcoder = new PDFTranscoder();
		
		TranscodingHints hints = transcoder.getTranscodingHints();
	//	hints.put(transcoder.KEY_AUTO_FONTS, false);
	//	transcoder.setTranscodingHints(hints);
	
		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(new File(FileChoiceUtil.getWorkingDirectory()+"/Slide.svg")));
		String outputFileName = FileChoiceUtil.getWorkingDirectory()+"/Slide.pdf";
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(outputFileName));
		transcoder.transcode(transcoderInput, transcoderOutput);
		Desktop.getDesktop().open(new File( outputFileName));
	}

	/**method created for testing pdf2 creates. simply using the transcoder 
	  resulted in every font becoming times new roman*/
	public static void emptyPDF() throws TranscoderException, IOException {
		
		String outputFileName = FileChoiceUtil.getWorkingDirectory()+"/Slide_Test.pdf";
		PDFDocumentGraphics2D graphics = new PDFDocumentGraphics2D();
		graphics.setupDefaultFontInfo();
		
		
		graphics.getFontInfo().addFontProperties("Arial", "normal", "normal", 0);
		graphics.getFontInfo().addFontProperties("Arial", "bold", "bold", 1);
		
		System.out.println(graphics.getFontInfo().getFonts());//prints the limited number of fonts available
		
		FileOutputStream fois = new FileOutputStream(outputFileName);
		graphics.setupDocument(fois, 500, 400);
		graphics.setSVGDimension(500, 400);
		graphics.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
	
		graphics.fill(new Rectangle(50, 50, 75,90));
		
		
		Font font = new Font("Arial", Font.BOLD,  12);
		graphics.setFont(font);
		System.out.println(graphics.getFontMetrics(font));
		
		graphics.setColor(Color.RED);
		graphics.drawString("Hello boy",170, 110);
		graphics.finish();
		fois.close();
		Desktop.getDesktop().open(new File( outputFileName));
	}
	
	/**
	 * @throws TranscoderException
	 * @throws IOException 
	 */
	public static void trEPS() throws TranscoderException, IOException {
		org.apache.fop.render.ps.EPSTranscoder transcoder = new EPSTranscoder();

		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(new File(FileChoiceUtil.getWorkingDirectory()+"/a.svg")));
		String outputFileName = FileChoiceUtil.getWorkingDirectory()+"/Slide.eps";
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(outputFileName));
		transcoder.transcode(transcoderInput, transcoderOutput);
		  
	}

}
