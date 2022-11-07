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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package layout.plasticPanels;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import layout.PanelLayout;
import locatedObject.RectangleEdges;
import logging.IssueLog;

/**A class that locates which rectangles are near each other*/
public class NeighborFinder<Type extends Rectangle2D> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double hRange=10;
	private double vRange=10;
	private PanelLayout panelLayout;
	
	private boolean performBottomEdgeAllign=false;
	private boolean performRightEdgeAllign=false;
	
	public NeighborFinder(PanelLayout panelLayout, double d, double e) {
		super();
		this.panelLayout = panelLayout;
		this.hRange = d;
		this.vRange = e;
	}

	
	
	/**sets the range in the x direction that qualifies as near*/
	public void setHRange(double d) {
		this.hRange = d;
	}
	
	
	/**sets the range in the y direction that qualifies as near*/
	public void setVRange(double d) {
		this.vRange = d;
	}
	
	/**how close panels need to be to be 'near' each other in the y direction*/
	public double getVRange() {
		return vRange;
	}
	/**how close panels need to be to be 'near' each other in the x direction*/
	public double getHRange() {
		return hRange;
	}
	
	/**how close panels need to be to be 'near' each other in the diagonal direction*/
	public double getDiagnolRange() {
		double square = getVRange()*getVRange()+getHRange()*getHRange();
		return Math.sqrt(square);
	}
	
	public PanelLayout getPanelLayout() {
		return panelLayout;
	}
	public void setPanelLayout(SpacedPanelLayout panelLayout) {
		this.panelLayout = panelLayout;
	}
	
	public Rectangle2D getPanelAbove(Rectangle p) {
		double vrange=getVRange();
		return getPanelAbove(p,this.getPanels(), vrange);
	}
	
	/**finds the panel just above the given panel*/
	public Rectangle2D getPanelAbove(Rectangle2D p, Rectangle2D[] panels, double d) {
		//int vrange=getVRange();
		ArrayList<Rectangle2D> possibleups = new ArrayList<Rectangle2D> ();
		for(Rectangle2D p2: panels) {
			if (p.equals(p2)) continue;
			if (p2.getY()+p2.getHeight()+d <p.getY()) continue;
			if (p2.getY()+p2.getHeight()-d >p.getY()) continue;
			if (p2.getY()>p.getY()) continue;
			if (p2.getX()+p2.getWidth()<=p.getX() || p2.getX()>=p.getX()+p.getWidth()) continue;//continues if the panel is not located directly above or below;
			possibleups.add(p2);
		}
		return new PanelOperations<Rectangle2D>().getNearestPanel(possibleups, p);
		//return null;
		
	}
	
	/**finds the panel just left the given panel*/
public Rectangle2D getPanelLeftOf(Rectangle2D p) {
		double hrange=getHRange();
		return getPanelLeftOf(p,this.getPanels(),hrange);
		
	}

/**finds the panel just left the given panel*/
public Rectangle2D getPanelLeftOf(Rectangle2D p, Rectangle2D[] panels, double d) {
	ArrayList<Rectangle2D> possibleLefts = new ArrayList<Rectangle2D> ();
	for(Rectangle2D p2: panels) {
		if (p.equals(p2)) continue;
		
		if (p2.getX()+p2.getWidth()+d <p.getX()) continue;
		if (p2.getX()+p2.getWidth()-d>p.getX()) continue;
		if (p2.getX()>p.getX()) continue;
		if (p2.getY()+p2.getHeight()<=p.getY() || p2.getY()>=p.getY()+p.getHeight()) continue;
		possibleLefts.add(p2);
		
	}
	
	return new PanelOperations<Rectangle2D>().getNearestPanel(possibleLefts, p);
	
	
	
}

public double getVOverLap(Rectangle r1, Rectangle r2) {
	double lowest=r1.getY();
	if (r2.getY()>r1.getY()) lowest=r2.getY();
	double highest=r1.getY()+r1.getHeight();
	if (r2.getY()+r2.getHeight()<r1.getY()+r1.getHeight())  highest=r2.getY()+r2.getHeight();
	if (r1.getY()>=r2.getY()+r2.getHeight()) return 0;
	if (r2.getY()>=r1.getY()+r1.getHeight()) return 0;
	return highest-lowest;
}

public ArrayList<Rectangle2D> getLeftNeighborChain(Rectangle2D p, Rectangle2D[] panels, double d) {
	ArrayList<Rectangle2D> output = new ArrayList<Rectangle2D>();
	Rectangle2D leftpanel=getPanelLeftOf(p,panels, d);
	while(leftpanel!=null) {
		output.add(leftpanel);
		leftpanel=getPanelLeftOf(leftpanel,panels, d);
	}
	return output;
}

public ArrayList<Rectangle2D> getLeftNeighborChain(Rectangle2D p) {
	return getLeftNeighborChain(p, this.getPanels(),this.getHRange());
}

public ArrayList<Rectangle2D> getAboveNeighborChain(Rectangle2D p) {
	return getAboveNeighborChain(p, this.getPanels(),this.getVRange());
}

public ArrayList<Rectangle2D> getCompiledAboveAndLeftChains(Rectangle2D p) {
	return addCompiledAboveAndLeftChains( p, new ArrayList<Rectangle2D> ());
}

public ArrayList<Rectangle2D> addCompiledAboveAndLeftChains(Rectangle2D p, ArrayList<Rectangle2D> output) {
	
	ArrayList<Rectangle2D> chain1 = getAboveNeighborChain(p);
	ArrayList<Rectangle2D> chain2 = getLeftNeighborChain(p);
	output.addAll(chain1);
	output.addAll(chain2);
	if (chain1.size()>0) for(Rectangle2D r: chain1) {
		addCompiledAboveAndLeftChains(r, output);
	}
	if (chain1.size()>0) for(Rectangle2D r: chain2) {
		addCompiledAboveAndLeftChains(r, output);
	}
	
	return output;
}

public ArrayList<Rectangle2D> getAboveNeighborChain(Rectangle2D p, Rectangle2D[] panels, double d) {
	ArrayList<Rectangle2D> output = new ArrayList<Rectangle2D>();
	Rectangle2D leftpanel=getPanelAbove(p,panels, d);
	while(leftpanel!=null) {
		output.add(leftpanel);
		leftpanel=getPanelAbove(leftpanel,panels, d);
	}
	return output;
}


/**
 HashMap<Integer, Integer> getLeftMap( Rectangle[] panels) {
	HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
	for(int i=0; i<panels.length; i++) {
		Rectangle pan=panels[i];
		if (pan==null) continue;
		Rectangle panelLeft = this.getPanelLeftOf(pan, panels, this.getHRange());
		if (panelLeft==null) {continue;}
		
	}
	
	
	return output;
 }*/

public Rectangle2D[] getPanels() {
	return this.getPanelLayout().getPanels();
}


/***/
public Rectangle autoLocatePanel(Rectangle p, int horozontal, int vertical) {
	Rectangle2D ab = getPanelAbove(p);
	Rectangle2D lo = getPanelLeftOf(p);
	//IssueLog.log("Trying to autolocate panel "+p, "will use "+ab+" above it ","and "+lo+" to its left");
	return autoLocatePanel(p,horozontal, vertical, ab,lo);
}

public HashMap<Rectangle2D, Rectangle2D> getLeftNeighborMap(Rectangle2D[] panels) {
	HashMap<Rectangle2D, Rectangle2D> out=new HashMap<Rectangle2D, Rectangle2D>();
	for(Rectangle2D p:panels) {
		if (p==null) continue;
		out.put(p, getPanelLeftOf(p, panels, this.getHRange()));
	}
	return out;
}

public HashMap<Rectangle2D, Rectangle2D> getUpNeighborMap(Rectangle2D[] panels) {
	HashMap<Rectangle2D, Rectangle2D> out=new HashMap<Rectangle2D, Rectangle2D>();
	for(Rectangle2D p:panels) {
		if (p==null) continue;
		out.put(p, getPanelAbove(p, panels, this.getVRange()));
	}
	return out;
}

/**alters the panel location based on a given neighbors location*/
public Rectangle autoLocatePanel(Rectangle p, int horozontal, int vertical, Rectangle2D ab, Rectangle2D lo) {
		 if (ab!=null) {
		 p.y=(int) (ab.getY()+ab.getHeight()+vertical);
		if  (doesPanelAboveShareLeftEdge(p,ab, lo))  p.x=(int) ab.getX();
		
	 }
	 if (lo!=null) {
		 p.x=(int) (lo.getMaxX()+horozontal);
		 if (doesPanelToLeftShareUpperEdge(p, lo, ab)) p.y=(int) lo.getY();
		 if (doesPanelToLeftShareLowerEdge(p,lo,ab) &&this.performBottomEdgeAllign)
			 {p.y=(int) (lo.getY()+lo.getHeight()-p.getHeight()); IssueLog.log("performing bottom edge allignment");}
	 }
	 
	 if (ab!=null&& doesPanelAboveShareRightEdge(p,ab,lo) && this.performRightEdgeAllign ) {
		 					p.x=(int) (ab.getX()+ab.getWidth()-p.getWidth());
		 					}
	 
	 return p;
} 

boolean useCornerDis=false;


boolean doesPanelAboveShareLeftEdge(Rectangle2D p, Rectangle2D abovepanel, Rectangle2D leftpanel) {
	
	if (useCornerDis) {
		Point2D upperleft = RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, p);
		Point2D lowerleft = RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, abovepanel);
		if (upperleft.distance(lowerleft)>getDiagnolRange()) return false;
	}
	
	if (Math.abs(p.getX()-abovepanel.getX())>p.getWidth()/3) return false;
	//if ( getHRange()>p.getWidth() ) return false;
	if ( abovepanel.getX()+abovepanel.getWidth()/2<p.getX()) return false;
	return true;
}

boolean doesPanelAboveShareRightEdge(Rectangle p, Rectangle2D abovepanel, Rectangle2D leftpanel) {
	
	if (useCornerDis) {
		Point2D upperleft = RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, p);
		Point2D lowerleft = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, abovepanel);
		if (upperleft.distance(lowerleft)>getDiagnolRange()) return false;
	}
	
	if (Math.abs(p.getX()+p.getWidth()-abovepanel.getX()-abovepanel.getWidth())>p.getWidth()/3) return false;
	//if ( getHRange()>p.getWidth() ) return false;
	if ( abovepanel.getX()+abovepanel.getWidth()/2>p.getX()) return false;
	return true;
}



boolean doesPanelToLeftShareUpperEdge(Rectangle p, Rectangle2D leftpanel, Rectangle2D abovepanel) {
	
	if (useCornerDis) {
		Point2D upperleft = RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, p);
		Point2D upperright = RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, leftpanel);
		if (upperleft.distance(upperright)>getDiagnolRange() ) return false;
	}
	
	if (Math.abs(p.getY()-leftpanel.getY())>p.getHeight()/3) return false;
	//if (getVRange()>p.getHeight()) return false;
	 if (leftpanel.getY()+leftpanel.getHeight()/2<p.getY()) return false;
	return true;
}

boolean doesPanelToLeftShareLowerEdge(Rectangle p, Rectangle2D leftpanel, Rectangle2D abovepanel) {
	
	if (useCornerDis) {
		Point2D upperleft = RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, p);
		Point2D upperright = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, leftpanel);
		if (upperleft.distance(upperright)>getDiagnolRange() ) return false;
	}
	
	if (Math.abs(p.getY()+p.getHeight()-leftpanel.getY()-leftpanel.getHeight())>p.getHeight()/3) return false;
	//if (getVRange()>p.getHeight()) return false;
	 if (leftpanel.getY()+leftpanel.getHeight()/2>p.getY()) return false;
	return true;
}


public void getLeftAndAboveSorted(Rectangle2D[] panels) {
	Arrays.sort(panels, new leftNeighborCompartor());
}

public boolean isPerformBottomEdgeAllign() {
	return performBottomEdgeAllign;
}




public void setPerformBottomEdgeAllign(boolean performBottomEdgeAllign) {
	this.performBottomEdgeAllign = performBottomEdgeAllign;
}

public boolean isPerformRightEdgeAllign() {
	return performRightEdgeAllign;
}




public void setPerformRightEdgeAllign(boolean performRightEdgeAllign) {
	this.performRightEdgeAllign = performRightEdgeAllign;
}

public class leftCompartor implements Comparator<Rectangle>{



	@Override
	public int compare(Rectangle o1, Rectangle o2) {
		double x1 = o1.getCenterX();
		double x2 = o2.getCenterX();
		if (x1==x2)
		return 0;
		if (x1<x2) return -1;
		if (x2<x1) return 1;
		return 0;
	}}

public class leftNeighborCompartor implements Comparator<Rectangle2D>{

	/**
	Rectangle[] panels;
	public leftNeighborCompartor(Rectangle[] pan) {
		panels=pan;
	}*/

	@Override
	public int compare(Rectangle2D o1, Rectangle2D o2) {
		ArrayList<Rectangle2D> left =  getCompiledAboveAndLeftChains(o1);
		ArrayList<Rectangle2D> left2 =  getCompiledAboveAndLeftChains(o2);
		//ArrayList<Rectangle> above =getAboveNeighborChain(o1);
		//ArrayList<Rectangle> above2 = getAboveNeighborChain(o2);
		
		
		
		if (left.contains(o2)){return 1;}
		
		if (left2.contains(o1)){return -1;}
		
		if (left.size()>left2.size()) {return 1;}
		if (left.size()<left2.size()) {return -1;}
		/**
		if (above.contains(o2)){
			return 1;
			
		}
		if (above2.contains(o1)){
			return -1;
			
		}*/
		
		
		
		return 0;
	}}

}
