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
 * Date Modified: Mar 8, 2021
 * Version: 2022.2
 */
package illustratorScripts;

/**a java class that generates scripts to create and modify a path point object in 
adobe illustrator*/
public class PathPointRef extends IllustratorObjectRef {

	
	private PathItemRef parent;

	public PathPointRef(PathItemRef parent) {
		this.setParent(parent);
		String out=this.getAssignment()+parent.refname+".pathPoints.add()";
		this.addScript(out);
		
	}

	public PathItemRef getParent() {
		return parent;
	}

	public void setParent(PathItemRef parent) {
		this.parent = parent;
	}
	
	/**sets the left direction curve control point*/
	public String setleftDirection(double x, double y) {
		setSmooth();
		String out = this.refname+".leftDirection"+equalNumberArray(x,y); ;
		this.addScript(out);
		return out;
		
	}
	
	/**sets the right direction curve control point*/
	public String setrightDirection(double x, double y) {
		setSmooth();
		String out = this.refname+".rightDirection"+equalNumberArray(x,y); 
		this.addScript(out);
		
		return out;
		
	}
	
	/**sets the anchor point*/
	public String setAnchor(double x, double y) {
		String out = this.refname+".anchor"+equalNumberArray(x,y); 
		this.addScript(out);
		return out;
		
	}
	
	/**Sets the point type to smooth*/
	public String setSmooth() {
		String out = this.refname+".pointType=PointType.SMOOTH;"; 
		this.addScript(out);
		return out;
		
	}
	
String equalNumberArray(double x, double y) {
		return "="+pointToJSarray(x,y)+";";
	}


	
}
