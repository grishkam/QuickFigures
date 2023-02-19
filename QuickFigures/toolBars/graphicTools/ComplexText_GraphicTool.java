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
 * Version: 2023.1
 */
package graphicTools;

import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import icons.IconWrappingToolIcon;
import icons.TreeIconWrappingToolIcon;

/**A tool for adding text items with multiple lines.
 * this class always adds rich text*/
public class ComplexText_GraphicTool extends Text_GraphicTool{

	{model= new ComplexTextGraphic();;
	iconSet=TreeIconWrappingToolIcon.createIconSet(model);
	}

	public ComplexText_GraphicTool(boolean b) {
		super(b);
		if (editorOnly) {
			iconSet=IconWrappingToolIcon.createIconSet(new TextCursorIcon());
		}
	}
	
	public TextGraphic makeNewTextObject() {
		ComplexTextGraphic textob = new ComplexTextGraphic();
		textob.copyAttributesFrom(model);
		textob.setTextColor(getForeGroundColor());
		textob.getParagraph().getLastLine().getLastSegment().setText("Text");
		return textob;
	}
	
	@Override
	public String getToolTip() {
		if (editorOnly) return "Edit Text Objects";
			return "Create Multiline/MultiColor Text Objects";
		}

}
