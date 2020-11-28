package export.eps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.render.ps.EPSTranscoder;


public class EPSsaver extends PDFsaver{


	/**
	 
	 */
	public void transcode(String newpath, File tempFile) throws FileNotFoundException, TranscoderException {
		EPSTranscoder transcoder = new EPSTranscoder();
	

		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(tempFile));
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(newpath));
		transcoder.transcode(transcoderInput, transcoderOutput);
	}
	
	public static void main(String[] args ) {
		
	}
	
	
	

}
