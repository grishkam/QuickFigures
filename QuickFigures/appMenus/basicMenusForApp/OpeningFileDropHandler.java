/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package basicMenusForApp;

import java.io.File;

import genericMontageUIKit.MoverDragHandler;
import imageDisplayApp.ImageDisplayIO;

public class OpeningFileDropHandler extends FileDropHandler {

	@Override
	public void performsingleFileAction(File f) {
		if (isImageFormat(f)) {
			//QuickFigureMaker quickFigureMaker = new QuickFigureMaker(true);
			
			//FigureOrganizingLayerPane f2 = quickFigureMaker.createFigure(f.getAbsolutePath());
			//f2.getPrincipalMultiChannel().getSlot().showImage();
			
			return;
		}
		ImageDisplayIO.showFile(f);

	}

private boolean isImageFormat(File f) {
		if (MoverDragHandler.isMicroscopeFormat(f))
			return true;
		if (MoverDragHandler.isImageFormat(f))
			return true;
		
		return false;
	}

	@Override
	public void performFileListAction(Iterable<File> files) {
		// TODO Auto-generated method stub

	}

}
