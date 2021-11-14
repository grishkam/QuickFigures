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
import java.awt.Polygon;
import java.awt.geom.PathIterator;

@SuppressWarnings("unused")
public class WasteBin {

	/**creates an awt polygon*/
	private static Polygon shapeToPolygon(PathIterator s) {
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
	
}
