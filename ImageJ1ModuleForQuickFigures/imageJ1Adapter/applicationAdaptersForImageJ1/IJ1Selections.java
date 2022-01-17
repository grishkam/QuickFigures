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
 * Date Modified: Jan 4, 2021
 * Version: 2022.0
 */
package applicationAdaptersForImageJ1;

import java.awt.BasicStroke;
import java.awt.Rectangle;

import graphicalObjects_Shapes.BasicShapeGraphic;
import ij.ImagePlus;
import ij.gui.ShapeRoi;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.LocatedObject2D;

/**a specialized selection manager that sets the selections
 of an imageJ image (or returns the selection) and not a QuickFigures figure
 A programmer might use but it in the future but it is not used presently
 */
public class IJ1Selections extends OverlayObjectManager {
	private ImagePlus imagePlus;

	public IJ1Selections(ImagePlus imp) {
		this.setImagePlus(imp);
	}

	public ImagePlus getImagePlus() {
		return imagePlus;
	}

	public void setImagePlus(ImagePlus imagePlus) {
		this.imagePlus = imagePlus;
	}
public void removeObjectSelections() {
	super.removeObjectSelections();
	if (imagePlus!=null) {
		imagePlus.killRoi();
	}
}
public Rectangle getSelectionBounds1() {
	
	if (imagePlus==null||imagePlus.getRoi()==null)
		return new Rectangle();
	
	return imagePlus.getRoi().getBounds();
}

/**sets the slection*/
@Override
public void setSelection( LocatedObject2D lastRoi, int i) {
	if (imagePlus==null) {return;}
	if (lastRoi instanceof RoiWrapper) {
		RoiWrapper r=(RoiWrapper) lastRoi;
		//ImagePlusWrapper impw=(ImagePlusWrapper) imp;
		imagePlus.setRoi(r.roi);
	} else 
	if (lastRoi instanceof BasicShapeGraphic) {
		BasicShapeGraphic b=(BasicShapeGraphic) lastRoi;
		imagePlus.setRoi(new ShapeRoi(b.getShape()));
	} /**else
	if (imagePlus instanceof GraphicalImagePlus) {
	//	GraphicalImagePlus gimp=(GraphicalImagePlus) imagePlus;
		super.setSelection(lastRoi, i);
		
	}*/
	
	
}

/**removes the roi but stores a version of the shape. */
public void movePrimarySelectionTo2nd() {
	if (imagePlus.getRoi()==null) return;
	RoiWrapper rw = new RoiWrapper(imagePlus.getRoi());
	BasicShapeGraphic bb = new BasicShapeGraphic(rw.getShape());
	selectionGraphic2=bb;
	if (rw.getStroke() instanceof BasicStroke) {
	BasicStroke basicStroke = (BasicStroke) rw.getStroke();
	bb.setStroke(basicStroke);
	}
	imagePlus.killRoi();
}

public boolean hasSelection1() {
	if (imagePlus.getRoi()==null) return false;
	return true;
}



}
