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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package export.eps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.render.ps.EPSTranscoder;

import export.svg.BatiKExportContext;

/**A class that functions to save a Worksheet as an .eps file*/
public class EPSsaver extends PDFsaver{


	/**
	 transcodes a SVG file into an EPS file
	 */
	public void transcode(String newpath, File tempFile) throws FileNotFoundException, TranscoderException {
		EPSTranscoder transcoder = new EPSTranscoder();
	

		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(tempFile));
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(newpath));
		transcoder.transcode(transcoderInput, transcoderOutput);
	}
	

	/**
	 * @return
	 */
	@Override
	protected
	 BatiKExportContext getTheRightContext() {
	
		return BatiKExportContext.EPS;
	}
	
	

}
