package undo;

import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner.InsetGraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;

public class UndoInsetDefinerGraphic extends AbstractUndoableEdit2 {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InsetGraphicLayer iLayer;
	private MontageLayoutGraphic iLayout;
	private PanelGraphicInsetDefiner def;
	private InsetGraphicLayer fLayer;
	private MontageLayoutGraphic fLayout;

	public UndoInsetDefinerGraphic(PanelGraphicInsetDefiner def) {
		this.def=def;
		iLayer=def.personalLayer;
		iLayout=def.personalGraphic;
	}
	

	public void establishFinalState() {
		
		fLayer=def.personalLayer;
		fLayout=def.personalGraphic;
	}
	public void redo() {
		def.personalLayer=fLayer;
		def.personalGraphic=fLayout;
	
	}
	
	public void undo() {
		def.personalLayer=iLayer;
		def.personalGraphic=iLayout;
	}

}
