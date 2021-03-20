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
 * Version: 2021.1
 */
package graphicActionToolbar;

import graphicalObjects.FigureDisplayWorksheet;
import icons.AcronymIcon;
import illustratorScripts.AdobeScriptMaker;
import illustratorScripts.ZIllustratorScriptGenerator;

/**An action tool that generates an illustrator script for making 
 * a figure in adobe illustrator*/
public class PSActionTool extends DisplayActionTool {
	AdobeScriptMaker sm=	new AdobeScriptMaker();

	public PSActionTool() {
		super("SendTOIL", new AcronymIcon("Ai", 0).generateIconSet());
	}

	
	
	
	protected void perform(FigureDisplayWorksheet graphic) {
		if (graphic!=null) {
			
		sm.sendWrapperToills(graphic.getAsWrapper(), true, null);
		 ZIllustratorScriptGenerator.instance.execute();
			
		}
	}
	

	@Override
	public String getToolTip() {
			return "Create Figure using Illustrator Script";
		}
	
}
