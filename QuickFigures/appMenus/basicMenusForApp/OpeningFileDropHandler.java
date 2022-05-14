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
package basicMenusForApp;

import java.io.File;

import addObjectMenus.FigureAdder;
import figureOrganizer.FigureOrganizingLayerPane;
import genericTools.NormalToolDragHandler;
import graphicActionToolbar.QuickFigureMaker;
import imageDisplayApp.ImageDisplayIO;

/**items of this class respond to files dropped unto the toolbars*/
public class OpeningFileDropHandler extends FileDropHandler {

	@Override
	public void performsingleFileAction(File f) {
		if (isImageFormat(f)) {
			generateFigureFromFile(f);
			
			return;
		}
		ImageDisplayIO.showFile(f);

	}

	/**Generates a figure from the given file
	 * @param file the file
	 */
	void generateFigureFromFile(File file) {
		QuickFigureMaker quickFigureMaker = new QuickFigureMaker(FigureAdder.MERGE_PANELS_ONLY, false);
		
		FigureOrganizingLayerPane f2 = quickFigureMaker.createFigure(file.getAbsolutePath(), null);
		f2.getPrincipalMultiChannel().getSlot().showImage();
	}

private boolean isImageFormat(File f) {
		if (NormalToolDragHandler.isMicroscopeFormat(f))
			return true;
		if (NormalToolDragHandler.isImageFormat(f))
			return true;
		
		return false;
	}

	@Override
	public void performFileListAction(Iterable<File> files) {
		// TODO Auto-generated method stub

	}

}
