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
 * Version: 2021.2
 */
package illustratorScripts;
/**a java class that generates scripts to create and modify a text frame item object in 
adobe illustrator*/
public class TextRange extends IllustratorObjectRef {
	
	CharAttributesRef charat=null;
	PathItemRef path=null;
	
	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String createItem(IllustratorObjectRef layer, String text) {
		set=true;
		String output="";
		//output+='\n'+"var "+refname+" ="+layer.refname+".words.add('"+text+"');";
		
		output+='\n'+"var "+refname+" ="+layer.refname+".characters.add('"+text+"',"+layer.refname+", ElementPlacement.PLACEATEND"+");";
		addScript(output);
		return output;
	}
	
	
	

	/**Add the char attributes reerence to the script*/
	public String createCharAttributesRef() {
		charat= new CharAttributesRef();
		String output=charat.getAssignment()+refname+".characterAttributes;";
		addScript(output);
		return output;
	}
	public CharAttributesRef getCharAttributesRef() {
		return charat;
	}
	
	

	

	public String setContents2(String contents) {
		String output="";
		output+=refname+".contents= '"+contents+"';";
		addScript(output);
		return output;
	}





}
