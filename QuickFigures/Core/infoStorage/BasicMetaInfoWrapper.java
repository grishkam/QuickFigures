package infoStorage;

import java.io.Serializable;

import utilityClasses1.NumberUse;

public abstract class BasicMetaInfoWrapper implements MetaInfoWrapper, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public abstract void replaceInfoMetaDataEntry(String b, String entryC) ;
	public abstract String getEntryAsString(String b) ;
	public abstract void removeEntry(String entryname);


	/**this method switched the number or value for two metadata keys.*/
	public  void switchMetaDataEntries(String b, String c){
	//	ImagePlus a = image;
		if (b==null||c==null) return;
		String entryB=getEntryAsString( b ) ;
		String entryC=getEntryAsString(c ) ;
		//IJ.showMessage("replacing entries "+entryB +" and " +entryC);
		if (entryB==null || entryC==null) return;
		replaceInfoMetaDataEntry( b, entryC);
		replaceInfoMetaDataEntry(c, entryB);
	}
	

	

	
	



	@Override
	public Integer getEntryAsInt(String entryname) {
		
		String output=getEntryAsString( entryname) ;
		if (output==null) return null;
	    Integer r;
	    try {r=Integer.parseInt(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}

	@Override
	public Double getEntryAsDouble(String entryname) {
		String output=getEntryAsString(entryname) ;
		
		if (output==null) return null;
		if (output.endsWith("px")) {output=output.substring(0, output.length()-2);
		
		}
		
	    Double r;
	    try {r=Double.parseDouble(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}
	
	
	public  String[] parseMetadataStringArrayValue(String b ) {
		String output= getEntryAsString( b) ;
		if (output==null) return null;
	    return BasicMetaDataHandler.stringArrayFromString(output);
	}
	
	public  int[] parseMetadataIntArrayValue(String b ) {
		String output= getEntryAsString( b) ;
		if (output==null) return null;
	    return NumberUse.intArrayFromString1(output);
	}
	
	
	


	protected String getMetaDataEntry(String ss, String b) {
		return BasicMetaDataHandler.getMetaDataEntryFromLine(ss, b);
	}
	
	

	@Override
	public Object getEntryAsDestringedClass(String b, Class<?> c) {
		return BasicMetaDataHandler.getObject(getEntryAsString( b), c);
	}
	
}
