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
 * Date Created Jan 2, 2022
 * Date Modified: Jan 3, 2022
 * Version: 2022.0
 */
import java.io.File;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import logging.IssueLog;


public class ShowPDFReadResult {
	
	public static String path=new DirectoryHandler().getFigureFolderPath()+"/rotations.pdf";
	

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
