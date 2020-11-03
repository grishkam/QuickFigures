package utilityClassesForObjects;

import java.io.Serializable;

public interface TextSubSection extends HasText, Serializable {

	final int NORMAL=0, SUPER_SCRIPT=1, SUB_SCRIPT=2;
	
	public void setParent(TextItem ti) ;
	public TextItem getParent() ;
	public int isSubOrSuperScript();
	
	
}
