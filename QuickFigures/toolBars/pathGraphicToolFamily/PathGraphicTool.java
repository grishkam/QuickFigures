package pathGraphicToolFamily;


import java.awt.Color;
import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicTools.GraphicTool;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.UndoAddItem;
import undo.PathEditUndo;

public class PathGraphicTool extends GraphicTool {
	
	

	PathGraphic p=null;
	//{createIconSet("icons2/LineIcon.jpg","icons2/LineIconPressed.jpg","icons2/LineIcon.jpg");}

	PathGraphic model= new PathGraphic(new Point(0,0));
	boolean filled;
	

	private PathEditUndo undo;

	protected int nArrow=0; 
	{super.set=TreeIconWrappingToolIcon.createIconSet(model);}

	public PathGraphicTool(boolean fill, int nArrows) {
		this.nArrow=nArrows;
		filled=fill;
		{model.setStrokeColor(Color.green.darker()); model.setStrokeWidth(2);}
		
		model.setFilled(true);
		if (fill) {model.setClosedShape(true);
		}
		if(fill&&model.getFillColor().getAlpha()==0) {
			model.setFillColor(Color.gray);
		}
		model.setNArrows(nArrow);
	}

	
	@Override
	public void mouseClicked() {
		super.mouseClicked();
		if(this.clickCount()>1)
			finishPath();
	}


	protected void finishPath() {
		p=null;
		this.selectedItem=null;
	}
	
	
	@Override
	public void mousePressed() {
		super.mousePressed();
		if(super.getPressedHandle()>-1) return;
		if (p!=null&&!getImageWrapperClick().getLocatedObjects().contains(p)) {finishPath();}
		
		if(p==null&&super.getSelectedObject() instanceof PathGraphic) 
			{p=(PathGraphic) super.getSelectedObject(); 
			return;}
		if(this.clickCount()>2)
			finishPath();
		super.setSelectedObject(p);
		
		if (p!=null) {
			undo = new PathEditUndo(p);
			
			
			
			p.addPoint(new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
			
			undo.saveFinalPositions();
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
			p.updatePathFromPoints();
			return;
		}
		
		
		p=new PathGraphic(new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
		p.copyAttributesFrom(model);
		p.copyColorsFrom(model);
		if (filled) {
			p.setFilled(true);p.setFillColor(model.getFillColor());
			p.setClosedShape(filled);
			p.setUseFilledShapeAsOutline(true);
		}
		
		p.setNArrows(nArrow);
		
		super.setSelectedObject(p);
		
		GraphicLayer setcur = this.getImageWrapperClick().getGraphicLayerSet();
		setcur.add(p);
		
		getImageDisplayWrapperClick().getUndoManager().addEdit(new UndoAddItem(setcur, p));
		super.getImageWrapperClick().updateDisplay();
		
	}
	
	@Override
	public void mouseReleased() {

		if(super.getPressedHandle()>-1) {
			super.mouseReleased();;
			return;
		}
		
	}
	
	@Override
	public void mouseDragged() {
		
		if(super.getPressedHandle()>-1) {
			super.mouseDragged();
			return;
		}
		
		Point newp = new Point(this.getDragCordinateX(), this.getDragCordinateY());
		if (p!=null &&shiftDown()) {
			p.addPoint(newp);
			
		}
		
		if (p!=null ) {
			try {
				p.transform().createInverse().transform(newp, newp);
			} catch (NoninvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.setHandleMode(PathGraphic.THREE_HANDLE_MODE);

			p.getPoints().getLastPoint().setCurveControl2(newp);
			
			p.getPoints().getLastPoint().makePointsOppositeLine(false);
			p.getPoints().deselectAll();
			p.getPoints().getLastPoint().select();
			p.getPoints().getLastPoint().setPrimarySelected(true);;
			p.updatePathFromPoints();
			
		}
		
		if (undo!=null  &&undo.isMyObject(p)) undo.establishFinalState();
		
		//getImageDisplayWrapperClick().getUndoManager().undo);
		getImageWrapperClick().updateDisplay();
		
		
	}
	
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
	}
	
	@Override
	public String getToolName() {
		if(filled)return "Draw Filled Shape Tool";
		if (nArrow>0) return "Draw Line With Arrow";
		return "Draw Lines Tool";
	}
	
	@Override
	public String getToolTip() {
		if(filled)return "Draw Filled Shape";
		if (nArrow>0) return "Draw Path's With Arrows";
			return "Draw Paths";
		}
	
	
	public void mouseExited() {
		finishPath();
		
	}
	
	
	/**Called when a tool is about to be switched away from (false) or switched to (true)*/
	@Override
	public void onToolChange(boolean b) {
		
			if (b&&lastToolsSelectedItem!=null) {
				super.selectedItem=lastToolsSelectedItem;
				if(lastToolsSelectedItem instanceof PathGraphic) p=(PathGraphic) lastToolsSelectedItem;
				lastToolsSelectedItem=null;
			}
			if(!b) finishPath() ;
		super.onToolChange(b);
		
	}
	

}
