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
 * Date Modified: Jan 6, 2021
 * Version: 2023.2
 */
package illustratorScripts;

/**a java class that generates scripts to create and modify a tag in 
adobe illustrator*/
public class TagRef extends IllustratorObjectRef {

	public String setToNewTag(IllustratorObjectRef angle) {
		//String output=refname+".name="+angle+";";
		String output=getAssignment()+angle.refname+".tags.add();";
		addScript(output);
		return output;
		}
	
	
	public String setName(String name) {
		String output=refname+".name='"+name+"';";
		addScript(output);
		return output;
		}
	
	public String setValue(String value) {
		String output=refname+".value='"+value+"';";
		addScript(output);
		return output;
		}
	
	
	
}
