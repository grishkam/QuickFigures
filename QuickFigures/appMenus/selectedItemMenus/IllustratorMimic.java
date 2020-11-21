package selectedItemMenus;

import java.awt.Dimension;
import java.util.ArrayList;

import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorDocRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.ZIllustratorScriptGenerator;

/**implements a menu item to generate an illustrator script*/
public class IllustratorMimic extends BasicMultiSelectionOperator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean makeNewDoc=false;
	
	public IllustratorMimic(boolean makeNewDoc) {
		super();
		this.makeNewDoc = makeNewDoc;
	}

	
	
	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		if (makeNewDoc) {return "Make Objects in new Illustrator Doc";}
		return "Make Objects in Illustrator";
	}

	@Override
	public void run() {
		
		ArrayList<ZoomableGraphic> theExportedItems = this.array;
		Dimension canvasDims = getSelector().getGraphicDisplayContainer().getCanvasDims();
		if (theExportedItems.size()==0)  theExportedItems.add(getSelector().getGraphicDisplayContainer().getGraphicLayerSet());
		
		generateIllustratorScript(theExportedItems, canvasDims);
		//	aref.setName(Montage.getTitle());
		//this.getSelector().getGraphicDisplayContainer().
		// TODO Auto-generated method stub

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
	
	void sentToIlls(Object mont, ArtLayerRef aref) {
		if (mont instanceof FigureDisplayContainer) {
			FigureDisplayContainer mont2 = (FigureDisplayContainer)mont;
			if (mont2.getGraphicLayerSet() instanceof IllustratorObjectConvertable) {
				((IllustratorObjectConvertable) mont2.getGraphicLayerSet()).toIllustrator(aref);
			}
			
		}
}

}
