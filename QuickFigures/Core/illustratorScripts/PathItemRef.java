package illustratorScripts;

import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import logging.IssueLog;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class PathItemRef extends IllustratorObjectRef {
	
	boolean set=false;
	
	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String createItem(IllustratorObjectRef artlayer) {
		set=true;
		String output="";
		output+='\n'+"var "+refname+" ="+artlayer.refname+".pathItems.add();";
		addScript(output);
		return output;
	}
	
	
	
	
	public String setFilled(boolean b) {
		String output=refname+".filled="+b+";";
		addScript(output);
		return output;
	}
	
	public String setOpacity(double b) {
		String output=refname+".opacity="+b+";";
		addScript(output);
		return output;
	}
	
	
	/**
	public String setClosed(boolean close) {
		String output=refname+".closed="+close+";";
		addScript(output);
		return output;
	}
	*/
	public String setStoke(double b) {
		b*=getGenerator().scale;
		String output=refname+".strokeWidth="+b+";";
		addScript(output);
		return output;
	}
	
	public String setStokeNoScale(double b) {
		String output=refname+".strokeWidth="+b+";";
		addScript(output);
		return output;
	}
	
	public String createRectangle(IllustratorObjectRef artlayer, Rectangle2D r) {
		set=true;
		String output="";
		double x=getGenerator().x0+r.getX()*getGenerator().scale;
		double y=r.getY()*getGenerator().scale;
		if (getGenerator().invertvertical) y=invertY(y);
		y+=getGenerator().y0;
		
		double width =r.getWidth()*getGenerator().scale;
		double height=r.getHeight()*getGenerator().scale;
		output+='\n'+"var "+refname+" ="+artlayer.refname+".pathItems.rectangle("+y+","+x+","+width+","+ height+");";
		addScript(output);
		return output;
	}
	
	public String createElipse(IllustratorObjectRef artlayer, Rectangle2D r) {
		set=true;
		String output="";
		double x=getGenerator().x0+r.getX()*getGenerator().scale;
		double y=r.getY()*getGenerator().scale;
		if (getGenerator().invertvertical) y=invertY(y);
		y+=getGenerator().y0;
		double width =r.getWidth()*getGenerator().scale;
		double height=r.getHeight()*getGenerator().scale;
		output+='\n'+"var "+refname+" ="+artlayer.refname+".pathItems.ellipse("+y+","+x+","+width+","+ height+");";
		addScript(output);
		return output;
	}
	

	
	public String setPointsOnPath(Point2D[] pt, boolean looptostart) {
		if (pt.length==0) return "";
	String output=	refname+".setEntirePath([";
	output+=pointToJSarray(pt[0]);
	for (int i=1; i<pt.length; i++) {
		if (pt[i]!=null) {
			output+=","+pointToJSarray(pt[i]); 
		}
	}
	if (looptostart) output+=","+pointToJSarray(pt[0]);;
	
	output+= ""+"]);";
	addScript(output);
	return output;
	}
	
	public String createPathWithoutCurves(ArtLayerRef aref, Shape p) {
		this.createItem(aref);
		double x = p.getBounds().getCenterX();
		double y = p.getBounds().getCenterX();
		this.setLeftandTop((int)x, (int)y);
		Shape p2 = AffineTransform.getTranslateInstance(0, 0).createTransformedShape(p);
		String output=	refname+".setEntirePath([";
		
		PathIterator pi = p2.getPathIterator(new AffineTransform());
		double[] d=new double[6];
		
		pi.currentSegment(d);
		output+=pointToJSarray(new Point2D.Double(d[0], d[1])); 
		pi.next();
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			output+=","+pointToJSarray(new Point2D.Double(d[0], d[1])); 
			
			pi.next();
		}
		
		
		output+= ""+"]);";
		addScript(output);
		
		return output;
		
	}
	
	
	/**Adds a given pathpoint list*/
	public void addPathWithCurves(IllustratorObjectRef aref, PathPointList pi, boolean create, boolean drawclose) {
		if (create)createItem(aref);
		
		for (PathPoint p:pi) {
			PathPointRef pp = new PathPointRef(this);
			pp.setleftDirection(p.getCurveControl1().x, p.getCurveControl1().y);
			pp.setrightDirection(p.getCurveControl2().x, p.getCurveControl2().y);
			pp.setAnchor(p.getAnchor().x,p.getAnchor().y);
			
		}
		
		if (drawclose) setClosed(true);
	}
	
	
	public void addPathWithCurves(ArtLayerRef aref, PathIterator pi, boolean create, boolean drawclose) {
		if (create)createItem(aref);
		double[] d=new double[6];
		while (!pi.isDone()) {
			int type=pi.currentSegment(d);
			boolean close = type==PathIterator.SEG_CLOSE;
			if (close&&!drawclose) {
				this.setClosed(true);
				pi.next();
				continue;
			}
			addPathFromIterat0rSegment(type, d, drawclose);
			
			pi.next();
		}
	}
	
	PathPointRef addPathFromIterat0rSegment(int type, double[] d, boolean close) {
		PathPointRef pp = new PathPointRef(this);
		if (type==PathIterator.SEG_MOVETO) {
			this.setLeftandTop(d[0], d[1]);
		}
		//pp.setAnchor(d[4], d[5]);
		if (type==PathIterator.SEG_CUBICTO) {
			IssueLog.log("setting curve direction points of cubic");
			//this.setLeftandTop(d[4], d[5]);
		//	pp.setrightDirection(d[0], d[1]);
			//pp.setleftDirection(d[2], d[3]);
			pp.setAnchor(d[2], d[3]);
			pp.setleftDirection(d[0], d[1]);
			pp.setrightDirection(d[4], d[5]);
		}
		else 	if (type==PathIterator.SEG_QUADTO) {
			IssueLog.log("setting curve direction points of quad "+type);
			//this.setLeftandTop(d[4], d[5]);
		//	pp.setrightDirection(d[0], d[1]);
			//pp.setleftDirection(d[2], d[3]);
			pp.setAnchor(d[2], d[3]);
			pp.setleftDirection(d[0], d[1]);
			pp.setrightDirection(d[2], d[3]);
		}
		
		else 	if (type==PathIterator.SEG_CLOSE) {
			IssueLog.log("setting curve direction points of quad "+type);
			
			pp.setAnchor(d[0], d[1]);
			pp.setleftDirection(d[0], d[1]);
			pp.setrightDirection(d[0], d[1]);
			if (d[4]!=0&&d[5]!=0) {
				pp.setAnchor(d[2], d[3]);
				pp.setleftDirection(d[0], d[1]);
				pp.setrightDirection(d[2], d[3]);
			}
			
			this.setClosed(true);
		}else {
			IssueLog.log("setting curve direction points 0,0 to "+type);
			pp.setAnchor(d[0], d[1]);
			pp.setleftDirection(d[0], d[1]);
			pp.setrightDirection(d[0], d[1]);
		}
		IssueLog.log("setting curve direction points "+type);
		
		return pp;
	}
	
	
	
	public String setPointsOnPath(Polygon pts, boolean looptostart) {
		if (pts.xpoints.length==0) return "";
	String output=	refname+".setEntirePath([";
	output+=pointToJSarray(pts.xpoints[0], pts.ypoints[0]);
	for (int i=1; i<pts.xpoints.length; i++) {
	
			output+=","+pointToJSarray(pts.xpoints[i], pts.ypoints[i]); 
		
	}
	if (looptostart) output+=","+pointToJSarray(pts.xpoints[0], pts.ypoints[0]);;
	
	output+= ""+"]);";
	addScript(output);
	return output;
	}
	
	
	public String setPointsOnPath(PathIterator s, boolean looptostart) {
		Polygon p = shapeToPolygon(s);
		return setPointsOnPath(p, looptostart);
	}
	

	public static Polygon shapeToPolygon(PathIterator s) {
		PathIterator pi = s;
		double[] d=new double[6];
		Polygon poly = new Polygon();
		
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			poly.addPoint((int)d[0], (int)d[1]);
			
			pi.next();
		}
		return poly;
	}
	
	
	public String setStrokeDashes(float[] dashlength) {
		
		String output=refname+".strokeDashes="+numberArray(dashlength)+";";
		addScript(output);
		return output;
	
		
	}
	
	
	public String numberArray(float[] dashlength) {
	if (dashlength.length==0) return "[]";
		;
		String output="["+dashlength[0];
		
		
			for(int i=1; i<dashlength.length; i++) {
				output+=","+dashlength[i];
			}
			output+="]";
		return output;
	}
	
	/**sets the stroke cap based on java's basic stroke*/
	public String setStrokeCap(int cap) {
		String out = refname+".strokeCap=";
		if (cap==BasicStroke.CAP_ROUND) out+="StrokeCap.ROUNDENDCAP;";
		else
			if (cap==BasicStroke.CAP_SQUARE) out+="StrokeCap.PROJECTINGENDCAP;";
			else if (cap==BasicStroke.CAP_BUTT) out+="StrokeCap.BUTTENDCAP;";
			else return "";
		addScript(out);
		return out;
	}
	
	public String setMiterLimit(double strokeMiterLimit) {
		String out=refname+".strokeMiterLimit="+strokeMiterLimit+";";
		addScript(out);
		return out;
	}
	
	/**sets the stroke join based on java's basic stroke*/
	public String setStrokeJion(int cap) {
		String out = refname+".strokeJion=";
		if (cap==BasicStroke.JOIN_ROUND) out+="StrokeJoin.ROUNDENDJOIN;";
			else
		if (cap==BasicStroke.JOIN_BEVEL) out+="StrokeJoin.BEVELENDJOIN;";
			else 
		if (cap==BasicStroke.JOIN_MITER) out+="StrokeJoin.MITERENDJOIN;";
			else 
				return "";
		addScript(out);
		return out;
	}
	
	public String setClosed(boolean closed) {
		String out = refname+".closed="+closed+";";
		addScript(out);
		return out;
	};

	

}
