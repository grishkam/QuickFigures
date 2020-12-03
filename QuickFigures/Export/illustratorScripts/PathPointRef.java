/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package illustratorScripts;

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
	
	public String setleftDirection(double x, double y) {
		String out = this.refname+".leftDirection"+equalNumberArray(x,y); ;
		this.addScript(out);
		return out;
		
	}
	
	public String setrightDirection(double x, double y) {
		String out = this.refname+".rightDirection"+equalNumberArray(x,y); 
		this.addScript(out);
		return out;
		
	}
	
	public String setAnchor(double x, double y) {
		String out = this.refname+".anchor"+equalNumberArray(x,y); 
		this.addScript(out);
		return out;
		
	}
	
String equalNumberArray(double x, double y) {
		return "="+pointToJSarray(x,y)+";";
	}
	
}
