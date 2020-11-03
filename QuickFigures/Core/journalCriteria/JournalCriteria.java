package journalCriteria;

import java.awt.Font;
import java.util.ArrayList;

import utilityClassesForObjects.StrokedItem;
import utilityClassesForObjects.TextItem;

public class JournalCriteria {
	public float minimumStroke=0;
	public float minimumFont=0;
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
