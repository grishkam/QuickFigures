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
package selectedItemMenus;

import java.awt.Dimension;
import java.util.ArrayList;

import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.ZoomableGraphic;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorDocRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.ZIllustratorScriptGenerator;

/**implements a menu item to generate an illustrator script
 * for creating the objects selected in Adobe Illustrator*/
public class IllustratorMimic extends BasicMultiSelectionOperator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean makeNewDoc=false;//should illustrator create a new document?
	
	public IllustratorMimic(boolean makeNewDoc) {
		super();
		this.makeNewDoc = makeNewDoc;
	}

	
	
	@Override
	public String getMenuCommand() {
		if (makeNewDoc) {return "Make Objects in new Illustrator Doc";}
		return "Make Objects in Illustrator";
	}

	@Override
	public void run() {
		
		ArrayList<ZoomableGraphic> theExportedItems = this.array;
		Dimension canvasDims = getSelector().getWorksheet().getCanvasDims();
		if (theExportedItems.size()==0)  theExportedItems.add(getSelector().getWorksheet().getTopLevelLayer());
		
		generateIllustratorScript(theExportedItems, canvasDims);
		

	}

	public void generateIllustratorScript(ArrayList<ZoomableGraphic> theExportedItems, Dimension canvasDims) {
		IllustratorDocRef d=new IllustratorDocRef();
		
		if (makeNewDoc)
		d.createDocumentScript(true, canvasDims); else d.setReftoActiveDocument() ;
		 ZIllustratorScriptGenerator.instance.setZero(0,canvasDims.height);
		 ArtLayerRef aref = new ArtLayerRef();
			aref.createNewRef(d);
			
			// sentToIlls(getSelector().getGraphicDisplayContainer(), aref);
			
			for(ZoomableGraphic i: theExportedItems) {
				if (i  instanceof IllustratorObjectConvertable) {
					((IllustratorObjectConvertable) i).toIllustrator(aref);
				}
			}
			
			 ZIllustratorScriptGenerator.instance.execute() ;
	}
	
	/**creates the entire worksheet in an adobe illustrator art layer*/
	void sentToIlls(Object mont, ArtLayerRef aref) {
		if (mont instanceof FigureDisplayWorksheet) {
			FigureDisplayWorksheet mont2 = (FigureDisplayWorksheet)mont;
			if (mont2.getTopLevelLayer() instanceof IllustratorObjectConvertable) {
				((IllustratorObjectConvertable) mont2.getTopLevelLayer()).toIllustrator(aref);
			}
			
		}
}

}
