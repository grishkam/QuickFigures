package infoStorage;



public interface MetaInfoWrapper {
	/**The Setter*/
	public  void setEntry(String entryname, String number);
	
	/**The Setters*/
	public  String getEntryAsString( String entryname ) ;
	public  Integer getEntryAsInt( String entryname ) ;
	public  Double getEntryAsDouble( String entryname ) ;
	public Object getEntryAsDestringedClass( String b, Class<?> c);
	
	
	public  void removeEntry(String entyname);
	
	
	
}
