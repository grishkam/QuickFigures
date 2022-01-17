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
 * Version: 2022.0
 */
package layout.plasticPanels;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**contains methods that find panels that are near
 *a given panels x/y location*/
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
