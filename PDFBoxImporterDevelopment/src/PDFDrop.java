/**
 * Author: Greg Mazo
 * Date Created Nov 27, 2021
 * Date Modified: Jan 3, 2022
 * Version: 2022.0
 */
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import genericTools.NormalToolDragHandler.FileDropListener;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import undo.CombinedEdit;


public class PDFDrop implements FileDropListener {

	@Override
	public boolean canTarget(ArrayList<File> file) {
		for(File f: file) {
			if(f.getAbsolutePath().toLowerCase().endsWith(".pdf"))
				return true;
		}
		return false;
	}

	@Override
	public CombinedEdit handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location,
			ArrayList<File> file) {
		CombinedEdit ed=new CombinedEdit();
		for(File f: file) try  {
			ed.addEditToList(
					PDFReadTest.addPDFToFigure(f, imageAndDisplaySet)
			);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		return ed;
	}

}
