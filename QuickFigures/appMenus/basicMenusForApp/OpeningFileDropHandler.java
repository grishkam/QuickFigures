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
