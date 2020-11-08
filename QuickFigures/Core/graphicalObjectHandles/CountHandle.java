package graphicalObjectHandles;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjects_BasicShapes.CountParameter;
import standardDialog.StandardDialog;
import undo.SimpleItemUndo;
import utilityClassesForObjects.LocatedObject2D;

public class CountHandle extends SmartHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int ALL_IN_ONE=0, DISPLAY_COUNT=2, COUNT_UP=3, COUNT_DOWN=1;
	int form=ALL_IN_ONE;
	private LocatedObject2D polygon;
	private CountParameter count;
	int oX = 25;
	int oY = 24;
	private boolean loopToStart=false;
	private double widthStretch=2;
	String up = "+";
	String down = "-";
	
	

	public CountHandle(LocatedObject2D regularPolygonGraphic, CountParameter number, int handleNumber) {
		super(0,0);
		polygon=regularPolygonGraphic;
		this.count=number;
		this.setHandleNumber(handleNumber);
		message=count.getValueAsString();
		this.handlesize=10;
	}
	
	public CountHandle(LocatedObject2D r, CountParameter n, int handle, int dx, int dy, boolean h, double expandWidth) {
		this(r,n, handle);
		oX=dx;
		oY=dy;
		loopToStart=h;
		this.widthStretch=expandWidth;
	}
	
	public static CountHandle[] createTriad(LocatedObject2D r, CountParameter n, int handle) {
		CountHandle[] out = new CountHandle[3];
		for(int i=0; i<3; i++) {
			out[i]=new CountHandle(r, n, handle+i);
			out[i].form=COUNT_DOWN+i;
			if(out[i].form!=DISPLAY_COUNT) {
				out[i].widthStretch=0.7;
			}
					}
		
		return out;
	}
	
	public int getDrawnHandleWidth() {
		return (int) (handleSize()*2*widthStretch);
	}
	
	public int getDrawnHandleHeight() {
		return handleSize()*2;
	}
	
	public Point2D getCordinateLocation() {
		
		double px = polygon.getBounds().getMaxX()+oX;
		double py = polygon.getBounds().getMinY()+oY;
		if(form==COUNT_UP) px+=25;
		if(form==COUNT_DOWN) px-=15;
		return new Point2D.Double(px, py);
	}
	public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		
		
		SimpleItemUndo<CountParameter> undo = createUndo();
		boolean right=canvasMouseEventWrapper. getClickedXScreen()>lastDrawShape.getBounds().getCenterX();
		if(form!=ALL_IN_ONE) right=true;
		
		if((canvasMouseEventWrapper.clickCount()==2&&form==ALL_IN_ONE&&!right)||form==DISPLAY_COUNT) {
			double i = StandardDialog.getNumberFromUser("Input "+count.parameterName, count.getValue());
			 count.setValue((int)i);
		} else {
		
			double place = this.lastDrawShape.getBounds().getCenterY();
			boolean down=canvasMouseEventWrapper. getClickedYScreen()>place;
			
			if(form==COUNT_UP) down=false;
			if(form==COUNT_DOWN) down=true;
			if(!right)return;
			int nV=count.getValue()+(down?-1:1);
			if(nV<count.getMinValue() &&!loopToStart) return;
			
			if(nV<count.getMinValue() &&loopToStart)  count.setValue(count.getMaxValue());
				else
			if (nV>count.getMaxValue()) count.setValue(count.getMinValue());
				else
				count.setValue(nV);
		}
		undo.establishFinalState();
		canvasMouseEventWrapper.addUndo(undo);
		
		canvasMouseEventWrapper.getAsDisplay().updateDisplay();
	}

	public SimpleItemUndo<CountParameter> createUndo() {
		return new SimpleItemUndo<CountParameter> (count);
	}
	
	
	protected void drawMessage(Graphics2D graphics, Shape s) {
		
			graphics.setColor(messageColor);
			graphics.setFont(getMessageFont());
			int y2 = (int)(s.getBounds().getCenterY()/2+s.getBounds().getMaxY()/2);
			int x2 = (int)s.getBounds().getX()+4;
			String valueAsString = count.getValueAsString();
			if(form==COUNT_DOWN) {valueAsString=down;}
			if(form==COUNT_UP) {valueAsString=up;}
			Rectangle2D b = graphics.getFontMetrics().getStringBounds(valueAsString, graphics);
			double end = b.getWidth();
			
			
			graphics.drawString(valueAsString, x2, y2);
			
			
			
			if(form==ALL_IN_ONE) {
				float size2 = (float)(getMessageFont().getSize()/2);
				graphics.setFont(getMessageFont().deriveFont(size2));
				double tX =s.getBounds().getCenterX();// x2+12+end;
				graphics.drawLine((int)tX, (int)s.getBounds().getMinY(), (int)tX, (int)s.getBounds().getMaxY());
				graphics.drawLine((int)tX, (int)s.getBounds().getCenterY(), (int)s.getBounds().getMaxX(), (int)s.getBounds().getCenterY());
				
				tX+=5;
				graphics.drawString(up, (float) tX+3, y2-size2-2);
				graphics.drawString(down, (float) tX+4, y2+size2/2);
			}
	}
	
	public boolean handlesOwnUndo() {
		return true;
	}

}