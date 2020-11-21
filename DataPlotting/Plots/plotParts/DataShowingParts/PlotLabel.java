package plotParts.DataShowingParts;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotCordinateHandler;
import plotParts.Core.PlotOrientation;
import utilityClassesForObjects.SnappingPosition;

public class PlotLabel extends ComplexTextGraphic {

	boolean snapNeeded;

	
	private boolean legend;
	SeriesLabelPositionAnchor snapItem;
	Double position;
	PlotArea plotArea;
	int orientation;
	/**
	 * 
	 */
	
	public PlotLabel(String name) {
		super(name);
	}
	
	public PlotLabel(String name, SeriesLabelPositionAnchor anchor) {
		super(name);
		legend=true;
		setSnapTo(anchor);
	}
	
	
	private static final long serialVersionUID = 1L;
	
	public void setSnapTo(SeriesLabelPositionAnchor bar) {
		this.snapItem=bar;
		snapNeeded=true;
	}
	
	
	
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		if ( true) {
			putIntoSnapPosition();
	//	if (snapItem!=null)IssueLog.log(snapItem.getClass().getName());
		}
		super.draw(g, cords);
	}
	
	public void putIntoSnapPosition() {
		if (snapItem==null) 
		{ 
			Rectangle srect = getLabelLocationOnPlot() .getBounds();
	
			getSnapPosition().snapObjectToRectangle(this,srect );
		
		}
		else 
			 getSnapPosition().snapObjectToRectangle(this, snapItem.getPlotLabelLocationShape());;
		 snapNeeded=false;
		 ;
	}
	
	/**Returns the logical location on the plot based on the 'position' of this plot label*/
	Rectangle2D getLabelLocationOnPlot() {
		double wide=20;
	
		if (plotArea==null) return new Rectangle();
		PlotCordinateHandler cordCalc = plotArea.getCordinateHandler(0);
		java.awt.geom.Point2D.Double pos = cordCalc.translate(position, 0, 0, 0);
		double x1 = pos.getX();
		double y1= pos.getY();
		if (isVertical()) {
			return new Rectangle2D.Double(x1-wide/2, y1-0.5, wide, 1);
				}
		else {
			return new Rectangle2D.Double(x1-0.5, y1-wide/2, 1, wide);
		}
	}
	
	

	public void setPosition(Double d) {
		this.position=d;
		snapNeeded=true;
	}

	public void setPlotArea(PlotArea plotArea) {
		this.plotArea=plotArea;
		snapNeeded=true;
	}
	
	boolean isVertical() {
		if (plotArea!=null) {
			if (plotArea.getOrientation()!=this.orientation) this.setPlotOrientation(plotArea.getOrientation());
			
			return plotArea.getOrientation()==PlotOrientation.BARS_VERTICAL;
		}
		return this.orientation==PlotOrientation.BARS_VERTICAL;
	}

	public void setPlotOrientation(int orientation2) {
		this.orientation=orientation2;
		
		if (legend) {}
			else {
					if (orientation2==PlotOrientation.BARS_VERTICAL)
						{setSnapPosition(SnappingPosition.defaultPlotBottomSide());
						setAngle(Math.PI/4);
						}
					else setSnapPosition(SnappingPosition.defaultPlotRigthSide());
		}
	}
	
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (handlenum==1) {
			SnappingPosition s = this.getSnapPosition();
			Point2D p = getUpperLeftCornerOfBounds();
			double dx = p2.getX()-p.getX();
			double dy = p2.getY()-p.getY();
			int[] poles = s.getOffSetPolarities();
		
			if (dx!=0) {
				double newdx = dx*poles[0]+s.getSnapHOffset();
				//if (Math.abs(newdx)<lockbounds2.width/4)
					s.setSnapHOffset((int) newdx);
			}
			
			
			
			if (dy!=0){
				double newdy = dy*poles[1]+s.getSnapVOffset();
			//if (Math.abs(newdy)<lockbounds2.height/4 )
				s.setSnapVOffset((int) newdy);
			
			}
			
			
		} else super.handleMove(handlenum, p1, p2);
		
		
	}

	public boolean isLegend() {
		return legend;
	}

	public void setLegend(boolean l) {
		this.legend = l;
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		double h=this.getSnapPosition().getSnapHOffset();
		double v=this.getSnapPosition().getSnapVOffset();
		super.scaleAbout(p, mag);
		
		//getSnappingBehaviour().setSnapHOffset( Math.round(h*mag));
		//getSnappingBehaviour().setSnapVOffset( Math.round(v*mag));
		this.putIntoSnapPosition();
		
	}

}
