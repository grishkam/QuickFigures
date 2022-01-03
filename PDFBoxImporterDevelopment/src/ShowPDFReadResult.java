import java.io.File;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import logging.IssueLog;

/**
 * Author: Greg Mazo
 * Date Modified: Jan 2, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */

/**
 
 * 
 */
public class ShowPDFReadResult {
	
	public static String path=new DirectoryHandler().getFigureFolderPath()+"/importME.pdf";
	

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File file = new File(path);
		IssueLog.log(file.exists()+"will check  "+path);
		IssueLog.sytemprint=true;
		PDFReadTest.showPDFFile(file);
		
		//reader.renderPageToGraphics(0, graphics);
	}
}
