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
 * Version: 2023.2
 */
package journalCriteria;

import java.awt.Font;
import java.util.ArrayList;

import layout.RetrievableOption;
import locatedObject.StrokedItem;
import textObjectProperties.TextItem;

/**Journals typically demand limits fonts, line widths
 * and other properties. this class stores a description of those limits
 * along with methods to force figures to fit those limits*/
public class JournalCriteria {
	@RetrievableOption(key="MinStroke", label="Minimun line width")
	public float minimumStroke=0;
	@RetrievableOption(key="MinFontSize", label="Minimun font size")
	public float minimumFont=0;
	
	@RetrievableOption(key="theIdealfont", label="Ideal font ")
	public String prefferedFontFamily="";
	
	public ArrayList<String> permittedFontFamilies=new ArrayList<String>();
	
	public JournalCriteria() {
		
	}
	
	boolean isFamilyPermitted(Font f) {
		String fam = f.getFamily();
		
		if (permittedFontFamilies.contains(fam)) return true;
		if (fam.equals(prefferedFontFamily)) return true;
		if (permittedFontFamilies.size()==0 && (prefferedFontFamily==null ||!prefferedFontFamily.equals("")) ) return true;
		
		return false;
	}
	
	public void ApplyCriteria(Object o) {
		if (o instanceof TextItem) {
			TextItem t=(TextItem) o;
			Font f = t.getFont();
			Font newfont = f;
			if (f.getSize()<minimumFont) {
				newfont=newfont.deriveFont(minimumFont);
			}
			
			if(!isFamilyPermitted(f)&&prefferedFontFamily!=null) {
				newfont=new Font(prefferedFontFamily, newfont.getStyle(), newfont.getSize()); 
			}
			
			//if changes had been made to the font, resets font for object
			if(newfont!=f) {
				t.setFont(newfont);
			}
			
		}
		
		if (o instanceof StrokedItem) {
			StrokedItem s=(StrokedItem) o;
			float width = s.getStrokeWidth();
			if (width>0 && width<minimumStroke) s.setStrokeWidth(minimumStroke);
		}
		
		
		
	}

}
