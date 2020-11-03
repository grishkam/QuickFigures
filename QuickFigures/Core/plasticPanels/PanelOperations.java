package plasticPanels;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class PanelOperations<Type extends Rectangle2D> {
	
	
	public Type getNearestPanel(Type[] pabels, double x, double y) {
		double shortest=Double.MAX_VALUE;
		Type closest=null;
		for(int i=0; i<pabels.length; i++) {
			Type p=pabels[i];
			if (p==null) continue;
			if (p.contains(x, y)) return p;
			double dist=(new Point2D.Double(p.getCenterX(), p.getCenterY())).distance(x, y);
		if (dist<shortest) {
			closest=p;
			shortest=dist;
		}
		}
		return closest;
	}
	
	
	
	
	public Type getNearestPanel(ArrayList<Type> pabels, Rectangle2D panel) {
		double x=panel.getX();
		double y=panel.getY();
		double shortest=Double.MAX_VALUE;
		Type closest=null;
		for(int i=0; i<pabels.size(); i++) {
			Type p=pabels.get(i);
			if (p==null) continue;
			if (p.contains(x, y)) return p;
			double dist=(new Point2D.Double(p.getCenterX(), p.getCenterY())).distance(x, y);
		if (dist<shortest) {
			closest=p;
			shortest=dist;
		}
		}
		return closest;
	}

}
