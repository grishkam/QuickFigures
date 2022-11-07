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
 * Date Modified: Mar 20, 2021
 * Version: 2022.2
 */
package illustratorScripts;

/**A java object that generates text that can be understood by adobe illustrator javascript
 * this class contains methods related to one Art layer
 * */
public class ArtLayerRef extends IllustratorObjectRef {

	String addLayer=".layers.add();";
	//String currentLayer=".layers.activeLayer;";
	
	/**creates a new art layer*/
	public String createNewRef(IllustratorDocRef document) {
		String output="";
		if (!document.set) output+= document.setReftoActiveDocument()+'\n'; 
		output+="var "+refname+" ="+ document.refname+addLayer;
		addScript(output);
		return output;
	}
		
	/**creates a sublayer and returns it*/
	public ArtLayerRef createSubRef() {
		String script="";
		ArtLayerRef output = new ArtLayerRef();
		script+=output.getAssignment()+refname+addLayer;
		addScript(script);
		return output;
	}
	
	/**creates a group and returns it*/
	public GroupItemRef createSubRefG() {
		String script="";
		GroupItemRef output = new GroupItemRef();
		script+=output.getAssignment()+refname+GroupItemRef.addLayer;
		addScript(script);
		return output;
	}

}
