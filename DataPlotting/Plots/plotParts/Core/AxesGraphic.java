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
package plotParts.Core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import dialogs.AxisDialog;
import export.pptx.GroupToOffice;
import export.pptx.OfficeObjectMaker;
import export.svg.SVGExporter;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.RectangleEdges;
import locatedObject.ScaleInfo;
import locatedObject.Scales;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;

public class AxesGraphic extends ShapeGraphic  implements Scales{

	/**
	 * 
	 */
	
	
	private PlotAxes axis=new PlotAxes();
	private PlotArea plotArea;
	TextGraphic labelModel=new TextGraphic();
	private double ticLength=5;
	boolean drawNumericTics=true;
	private boolean labelsVisible=true;
	private boolean integerTics;
	private double y2;
	private double x2;
	private boolean alternateSide=false;
	
	
	
	private HashMap<java.lang.Double, String> alternateNames=new HashMap<java.lang.Double, String> ();
	String labelSuffix="";
	
	//HashMap<Double, ComplexTextGraphic> alternateNames=new HashMap<Double, String>();
	
	
	static int All_TICS_SHOWN=0, NO_TICS=1, MAJOR_ONLY=2;
	private int hideTics=0;
	private transient BasicShapeGraphic gapmarker;
	private int scaleLabelType=0;
	public final static int Label_Scientific=1;
	
	
	private static final long serialVersionUID = 1L;
	

	
	
	
	public  AxesGraphic(boolean vert) {
		//super.setHeadnumber(0);
		super.setStrokeWidth(1);
		getAxisData().setVertical(vert);
		this.setStrokeColor(Color.black);
		if (vert){ 
			this.setName("y-axis"); 
		}else {
			this.setName("x-axis");
			}
	}
	
	public AxesGraphic copy() {
		AxesGraphic out = new AxesGraphic(axis.isVertical());
		out.copyEveryThingFrom(this);
		return out;
	}
	
	public void copyEveryThingFrom(AxesGraphic g ) {
		super.copyColorsFrom(g);
		super.copyStrokeFrom(g);
		super.copyAttributesFrom(g);
		scaleLabelType=g.scaleLabelType;
		hideTics=g.hideTics;
		alternateSide=g.alternateSide;
		labelsVisible=g.labelsVisible;
		ticLength=g.ticLength;
		
		integerTics=g.integerTics;
		drawNumericTics=g.drawNumericTics;
		axis.copyFieldsFrom(g.axis);
		labelModel.copyAttributesFrom(g.labelModel);
		labelModel.copyBasicTraitsFrom(g.labelModel);
	}

	public void setPlot(PlotArea plotLayers) {
		plotArea=plotLayers;
		 MatchToPlotArea() ;
		
	}
	
	public PlotArea getPlot() {return plotArea;}
	
	/**Fits the location of this axis graphic to its plot area.
	  It must be called frequently as the plot area rectangle may change*/
	public void MatchToPlotArea() {
		Rectangle rect = plotArea.getPlotArea();
		if (getAxisData().isVertical()) {
			double x=	rect.getMinX();
			if (this.isOnAlternateSide()) x=	rect.getMaxX();
			double y1=rect.getMaxY();
			double y2=rect.getMinY();
			setPoints(new Point2D.Double(x, y1), new Point2D.Double(x, y2));
		} 
		else {
			double x1=	rect.getMinX();
			double x2=rect.getMaxX();
			double y=rect.getMaxY();
			if (this.isOnAlternateSide()) y=	rect.getMinY();
			setPoints(new Point2D.Double(x1, y), new Point2D.Double(x2, y));
		}
		
		
		
	}
	
	
	/**
	@Override
	public Point2D getLocation() {
		//this.MatchToArea();
		return super.getLocation();
	}

	protected Point2D getDrawnLineEnd1() {
		//this.MatchToArea();
		return super.getDrawnLineEnd1();
	}
	
	protected Point2D getDrawnLineEnd2() {
		//this.MatchToArea();
		return super.getDrawnLineEnd2();
	}
	


*/
	
	/**ensures that any attempts to move this item will not place it out of 
	  position*/
	@Override
	public void setLocation(double x,double y) {
		 MatchToPlotArea() ;
	}
	
	@Override
	public void moveLocation(double x, double y) {
		 MatchToPlotArea() ;
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		super.draw(g, cords);
		if (this.drawNumericTics) drawNumericTicmarks(g, cords);
		//u.drawLine(g, cords, point, baseLineEnd, false);
	}
	
	protected void drawNumericTicmarks(Graphics2D g, CordinateConverter cords) {
		
		for(ZoomableGraphic a: drawNumericTicmarks()) try {
			a.draw(g, cords);
		} catch (Throwable t) {t.printStackTrace();}
	}

	protected ArrayList<ZoomableGraphic> drawNumericTicmarks() {
		ArrayList<ShapeGraphic> resultingTics=new ArrayList<ShapeGraphic> ();
		ArrayList<TextGraphic> resultingNumbers=new ArrayList<TextGraphic>();
		ArrayList<ZoomableGraphic> all=new ArrayList<ZoomableGraphic>();
		
		
		double starting=getAxisData().getMinValue();
		double displayed=translate(starting);
		
			/**continues untill outside the bounds of the plot. although the gap is 'outside' second term makes it plotable*/
			while (isWithinAxis(displayed) || (!isWithinAxis(displayed)&&getGap().isInside(starting))) {
				
				displayed=translate(starting);
				boolean major=isMajorTicLocation(starting);
				
				
				if (this.isValueDisplayable(starting)){
					double theLength = getTicLength();
					if (major) theLength*=2;
		
					Point2D.Double ticLocation = getTicPointOnAxisLine(starting,  theLength);
					Point2D.Double axisLocation = getPointOnAxisLine(starting);
					//u.drawLine(g, cords, axisLocation, ticLocation,false);
					
					if (theLength!=0 &&getHideTicmarks()!=NO_TICS && !(getHideTicmarks()==MAJOR_ONLY&&!major)) {
						
						PathGraphic ticmark = new PathGraphic(ticLocation);
						ticmark.addPoint(axisLocation);
						axisLocation.x+=0.01;//bugfix for ppt export. dot know why this works, but strait line swith two point turn out odd
						axisLocation.y+=0.01;//bugfix for ppt export
						ticmark.addPoint(axisLocation);//bugfix for ppt export
						ticmark.updatePathFromPoints();
						ticmark.setStrokeColor(getStrokeColor());
						ticmark.copyStrokeFrom(this);
						resultingTics.add(ticmark);
						//ticmark.draw(g, cords);
					}
					
					if (major &&labelsVisible) {
						TextGraphic label = createLabelGraphicFor(starting, ticLocation);
				
						resultingNumbers.add(label);
					}
					
			}
				/**progress of loop*/
				if (this.integerTics)starting=nextInteger(starting); else
				starting=nextMinorTic(starting);//getMinorTic();//progress
				displayed=translate(starting);
			}
			
			//draws the gap
			if (usesGap()) {
				Double axisLocation = getPointOnAxisLine(getGap().location());
				double xg = axisLocation.getX();
				double yg = axisLocation.getY();
				if (axis.isVertical())
					gapmarker=new BasicShapeGraphic(new Rectangle2D.Double(xg-getGap().markerTicWidth, yg-getGap().gapMarkerHeight, getGap().markerTicWidth+2, getGap().gapMarkerHeight));
				else gapmarker=new BasicShapeGraphic(new Rectangle2D.Double(xg, yg-getGap().markerTicWidth/2, getGap().gapMarkerHeight, getGap().markerTicWidth+2));
					
				gapmarker.setFillColor(Color.white);
				gapmarker.setStrokeWidth(-1);
				
				
				Path2D zigzag=new Path2D.Double();
				drawGapMarkerLines(axisLocation, zigzag);
				
				
				BasicShapeGraphic gapmarker2 = new BasicShapeGraphic(zigzag);
				gapmarker2.copyColorsFrom(this);
				gapmarker2.copyStrokeFrom(this);
				
				all.add(gapmarker);
				all.add(gapmarker2);
			}
			
			all.addAll(resultingTics);
			all.addAll(resultingNumbers);
			
			
			return all;
			
		
			
		}

	protected TextGraphic createLabelGraphicFor(double starting, Point2D.Double ticLocation) {
		TextGraphic label = getLabelText().copy();
		label.setText(((int)starting)+labelSuffix);
		
		if (this.scaleLabelType==Label_Scientific &&!(starting>-0.00001&&starting<0.00001)) {
			ComplexTextGraphic c = new ComplexTextGraphic();
			c.copyAttributesFrom(label);
			//c.draw(new Graphics2D(), cords);
			
			c.setTextColor(label.getTextColor());
			c.setLocationType(label.getLocationType());
			c.setLocation(ticLocation);
			TextParagraph p = c.getParagraph();
			
			double logV = Math.log(starting)/Math.log(getLogBase());
			if (logV-Math.floor(logV)>0.98) {logV=Math.ceil(logV);}
			logV=Math.floor(logV);
			
				double n=starting/(Math.pow(getLogBase(), logV));
				if(n-1<0.02) p.get(0).get(0).setText(""+getLogBase());
				else
				p.get(0).get(0).setText(n+"*"+getLogBase());
				
			TextLineSegment seg = p.get(0).addSegment(""+(int)logV, c.getTextColor());
			seg.makeSuperScript();
			
			label=c;
		}
		
		String altName = this.getAlternateNames().get(starting);
		if (altName!=null && altName.trim().length()>0)label.setText(altName);
		label.setLocation(ticLocation);
		return label;
	}

	

	private void drawGapMarkerLines(Double axisLocation, Path2D zigzag) {
		if (axis.isVertical())  {
		zigzag.moveTo( axisLocation.getX()-getGap().markerTicWidth/2,  axisLocation.getY());
		zigzag.lineTo( axisLocation.getX()+getGap().markerTicWidth/2,  axisLocation.getY());
		
		zigzag.moveTo( axisLocation.getX()-getGap().markerTicWidth/2,  axisLocation.getY()-getGap().gapMarkerHeight);
		zigzag.lineTo( axisLocation.getX()+getGap().markerTicWidth/2,  axisLocation.getY()-getGap().gapMarkerHeight);
		} else drawGapMarkerLinesHorizontal(axisLocation, zigzag);
		
		} 
	
	private void drawGapMarkerLinesHorizontal(Double axisLocation, Path2D zigzag) {
		zigzag.moveTo( axisLocation.getX(), axisLocation.getY()-getGap().markerTicWidth/2 );
		zigzag.lineTo( axisLocation.getX(), axisLocation.getY()+getGap().markerTicWidth/2);
		
		zigzag.moveTo( axisLocation.getX()+getGap().gapMarkerHeight, axisLocation.getY()-getGap().markerTicWidth/2 );
		zigzag.lineTo( axisLocation.getX()+getGap().gapMarkerHeight, axisLocation.getY()+getGap().markerTicWidth/2);
		
		
	} 
			
			private int nextInteger(double d) {
				if (d%1==0) return (int)(d+1);
				else return (int)Math.ceil(d);
			}
	
			/**returns the value of the next minor tic.
			  Normally, just goes to the next multiple of 
			  the minor tic interval. For an interval of 2 {2,4,6,8,10,12,14....}
			  Not perfected for the log scale
			 If using a log scale, increments by 'minortic'*10^x  
			 for an interval of 2 that would be {2,4,6,8,10, 20,40, 60,80, 100, 200, 400...}
			 * .*/
			private double nextMinorTic(double d) {
				if (d%getMinorTic()!=0) d-=d%getMinorTic();
				
				double output = d+getMinorTic();
				
				if (this.axis.usesLogScale()) {
					double logd = Math.log(d)/Math.log(getLogBase());
					if (logd-Math.floor(logd)>0.98) {logd=Math.ceil(logd);}
					double increMentPower=Math.pow(getLogBase(), Math.floor(logd));
					
					
					//IssueLog.log("increments power for "+d+"  is "+increMentPower+"due to log "+logd);
					
					
					if (logd>=1) {
						output=d+ getMinorTic()*increMentPower;
						
					}
					/**Bugfix, after reaching a power of 10, must progress to the right scale*/
					if (logd%1==0&&d>=getLogBase()) {
						double output2 = getMinorTic()*Math.pow(getLogBase(), Math.floor(logd));
						if (output2>d) output=output2;
						
					}
				}
			
				return output;
				
				
			}
			
			
			/**Returns true if the value has a major tic
			  Works imperfectly for the log scale*/
			protected boolean isMajorTicLocation(double d) {
				
				if (this.axis.usesLogScale()) {
					double logd = Math.log(d)/Math.log(getLogBase());
					if (logd>=1) {
						double increment = /**getMinorTic()**/Math.pow(getLogBase(), Math.floor(logd));
						double remainder = (d/increment)%getMajorTic();
						//IssueLog.log("Attempting to identify major tic "+d+"  of magnitude "+increment+" "+remainder);
						if (increment==d) return getLogBase()%getMajorTic()==0;
						return remainder==0;
					}
				}
				
				return (d)%getMajorTic()==0;
			}
	
	
	/**When given the numeric value of a point, returns the relative position where
	 * the value is ploted. cannot handle negative axis now*/
	double translate(double value) {
		if (this.getAxisData().usesLogScale()) return this.translateLogScale(value, 10);
		
		double relativeTo0 = value-getAxisData().getMinValue();//the units from the minimum/axis
		//if (value<0) relativeTo0 = (Math.abs(value)-getAxisData().getMinValue())*-1;
		
		double gapShift=0;
		double range=getAxisData().getRange();
		
		if (usesGap()) {
			//range+=getGap().size;
				if (getGap().isInside(value)) {return 0;}
				if (getGap().isAfter(value)) {
					relativeTo0-=getGap().getSize();
					gapShift=getGap().gapMarkerHeight;
				}
		}
		
		double ratio= relativeTo0/range;//A double from 0.0 to 1 describing its position from min to max
		
		if (getAxisData().isVertical())  {
			return y-(y-y2)*ratio-gapShift;
		
		} else  {
			return x+(x2-x)*ratio+gapShift;
		}
	}
	
	/**When given the numeric value of a point, returns the relative position where
	 * the value is ploted on log scale of base base. cannot handle negative axis now.
	  does not work with gaps*/
	double translateLogScale(double value, double base) {
		
		double min=getAxisData().getMinValue();
		double max=getAxisData().getMaxValue();
		if (min==0) min=1;//log 0 undefined
		if (value==0) value=0;//log 0 undefined
		
		double relativeTo0 = Math.log(value)-Math.log(min);
		
		double gapShift=0;
		double range=Math.log(max)-Math.log(min);
		
		/**gap shifting is unfinished with the log scale*/
		if (usesGap()) {
			//range+=getGap().size;
				if (getGap().isInside(value)) {return 0;}
				if (getGap().isAfter(value)) {
					relativeTo0-=Math.log(getGap().getSize());
					gapShift=getGap().gapMarkerHeight;
				}
		}
		
		double ratio= relativeTo0/range;//A double from 0.0 to 1 describing its position from min to max
		
		if (getAxisData().isVertical())  {
			return y-(y-y2)*ratio-gapShift;
		
		} else  {
			return x+(x2-x)*ratio+gapShift;
		}
	}

	private boolean usesGap() {
		if (getGap().location()+getGap().size>axis.getMaxValue()) return false;
		return getGap().size>0;
	}
	
	/**When given the value of a data point returns the x,y point on the axis right below it*/
	public Point2D.Double getPointOnAxisLine(double starting) {
		double linex=plotArea.getPlotArea().getMinX();
		double liney=plotArea.getPlotArea().getMaxY();
		if (this.isOnAlternateSide()) {
			 linex=plotArea.getPlotArea().getMaxX();
			 liney=plotArea.getPlotArea().getMinY();
		}
		
		if (this.axis.isVertical())
		return  new Point2D.Double(linex, translate(starting));
	else  return new Point2D.Double(translate(starting), liney);
	}
	
	/**When given the value of a data point and the length of a desired ticmark 
	returns the x,y point on the axis right below it*/
	public Point2D.Double getTicPointOnAxisLine(double starting, double ticLength2) {
		double linex=plotArea.getPlotArea().getMinX();
		double liney=plotArea.getPlotArea().getMaxY();
		if (this.isOnAlternateSide()) {
			 linex=plotArea.getPlotArea().getMaxX();
			 liney=plotArea.getPlotArea().getMinY();
			 ticLength2=-ticLength2;
		}
		
		
		if (this.axis.isVertical())
		return  new Point2D.Double(linex-ticLength2, translate(starting));
	else  return new Point2D.Double(translate(starting), liney+ticLength2);
	}

	public ScaleInfo getScaleInfo() {
		// TODO Auto-generated method stub
		return new ScaleInfo();
	}

	public PlotAxes getAxisData() {
		return axis;
	}

	public void setAxisData(PlotAxes axis) {
		this.axis = axis;
	}

	public boolean isShowText() {
		return labelsVisible;
	}
	public void setShowText(boolean b) {
		labelsVisible=b;
	}

	public TextGraphic getLabelText() {
		if (this.getAxisData().isVertical()) {
			labelModel.setLocationType(this.alternateSide? RectangleEdges.LEFT : RectangleEdges.RIGHT);
		} else labelModel.setLocationType(this.alternateSide? RectangleEdges.BOTTOM :RectangleEdges.TOP);
		
		
		
		return labelModel;
	}
	
	public void showOptionsDialog() {
		new AxisDialog(this).showDialog();;
	}

	boolean isWithinAxis(double displayed) {
		
		if (getAxisData().isVertical()) {
			if (displayed>=this.plotArea.getPlotArea().getMinY()) return true;
			}
		else {
			if (displayed<=plotArea.getPlotArea().getMaxX()) return true;
		}
		
		return false;
	}
	
	/**Returns true if the value translates to a point inside of the plot, false otherwise*/
	boolean isValueDisplayable(double value) {
		if (getGap().isInside(value)) return false;
		if (this.getAxisData().getMinValue()>value) return false;
		if (this.getAxisData().getMaxValue()+getGap().size<value) return false;
		return true;
	}
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		//IssueLog.log("Set ");
		
		
		if (handlenum==1) {
			//if (this.axis.isVertical()) plotArea.pullTopAxisTo(p2.getY());
			//else plotArea.pullRightAxisTo(p2.getX());
		}
		
	}
	
	@Override
	public Area getOutline() {
    
		Area ar = new Area();
		Rectangle relevantPlot = plotArea.getPlotArea();
		double x=0;
		double y=0;
		double w=0;
		double h=0;
		double wide=getTicLength()*2;
		if (wide==0)  wide=2;
		
		if (this.axis.isVertical()) {
			x=relevantPlot.x-wide;
			y=relevantPlot.y;
			w=wide;
			h=relevantPlot.height;
			if (this.alternateSide) x+=wide+relevantPlot.width;
		} else {
			
			x=relevantPlot.x;
			y=relevantPlot.getMaxY();
			w=relevantPlot.width;
			h=wide;
			if (this.alternateSide) y-=wide+relevantPlot.height;
		}
		
		ar.add(new Area(new Rectangle2D.Double(x, y, w, h)));
		
		return ar;
	}

	public void setIntergerTics(boolean b) {
		integerTics=b;
		
	}

	public double getTicLength() {
		return ticLength;
	}

	public void setTicLength(double ticLength) {
		this.ticLength = ticLength;
	}

	/**runs the update method for the entire plot.
	  needed to be called if changes made are to be visible*/
	public void updatePlotArea() {
		plotArea.onAxisUpdate();
		
	}

	
	public void setPoints(Point2D p1, Point2D p2) {
		x=p1.getX();//.x;
		y=p1.getY();//.y;
		x2=p2.getX();//.x;
		y2=p2.getY();//.y;
	}


	@Override
	public Rectangle getBounds() {
		return getOutline().getBounds();
	}

	@Override
	public Shape getShape() {
		return new Line2D.Double(x, y, x2, y2);
		/**Path2D output = new Path2D.Double();
		output.moveTo(x, y);
		output.lineTo(x2, y2);
		return output;*/
	}

	public int getHideTicmarks() {
		return hideTics;
	}

	public void setHideTicmarks(int hideTics) {
		this.hideTics = hideTics;
	}
	
	public GraphicGroup breakToGroup() {
		GraphicGroup output = new GraphicGroup();
		output.setName(this.getName());
		
		output.getTheInternalLayer().add(lineCopy());
		ArrayList<ZoomableGraphic> items = this.drawNumericTicmarks();
		for(ZoomableGraphic a: items) {
			output.getTheInternalLayer().add(a);
		}
		return output;
	}
	
	public ShapeGraphic lineCopy() {
		PathGraphic b = new PathGraphic(new Point2D.Double(x, y));
		b.addPoint(new Point2D.Double(x2, y2));
		b.addPoint(new Point2D.Double(x2+0.001, y2+0.001));//bugfix for ppt export. not true solutin
		b.copyColorsFrom(this);b.copyStrokeFrom(this);b.copyAttributesFrom(this);
		return b;
	}
	
	@Override
	public SVGExporter getSVGEXporter() {
		// TODO Auto-generated method stub
		return breakToGroup() .getTheInternalLayer().getSVGEXporter();
	}
	

	@Override
	public OfficeObjectMaker getObjectMaker() {
		return new GroupToOffice(breakToGroup());
	}
	
	
	public Gap getGap() {
		return axis.getGap();
	}

	public HashMap<java.lang.Double, String> getAlternateNames() {
		if (alternateNames==null) alternateNames=new HashMap<java.lang.Double, String>();
		return alternateNames;
	}

	public void setAlternateNames(HashMap<java.lang.Double, String> hashMap) {
		this.alternateNames = hashMap;
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		ticLength*=mag;
		getLabelText().scaleAbout(p, mag);
		
	}

	public boolean isOnAlternateSide() {
		return alternateSide;
	}

	public void setOnAlternateSide(boolean alternateSide) {
		this.alternateSide = alternateSide;
	}

	public void setSetScaleType(int choiceIndex) {
		this.axis.scaleType=choiceIndex;
	}
	
	public int getScaleType() {return axis.scaleType;}
	public int getLogBase() {return 10;}

	public int getScaleLabelType() {
		return scaleLabelType;
	}

	public void setScaleLabelType(int scaleLabelType) {
		this.scaleLabelType = scaleLabelType;
	}
	
	private int getMinorTic() {
		if (getAxisData().usesLogScale() &&getAxisData().getMinorTic()>=getLogBase()) return 1;
		return this.getAxisData().getMinorTic();
	}
	
	private int getMajorTic() {
		if (getAxisData().usesLogScale() && getAxisData().getMajorTic()>=getLogBase()) return getLogBase();
		return getAxisData().getMajorTic();
	}
	
}
