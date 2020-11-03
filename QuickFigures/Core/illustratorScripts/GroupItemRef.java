package illustratorScripts;

public class GroupItemRef extends ArtLayerRef {
	
	static String addLayer=".groupItems.add();";
	
	public String createNewRef(IllustratorDocRef document) {
		String output="";
		if (!document.set) output+= document.setReftoActiveDocument()+'\n'; 
		output+="var "+refname+" ="+ document.refname+GroupItemRef.addLayer;
		addScript(output);
		return output;
	}
	
	public String createNewRef(ArtLayerRef document) {
		String output="";
		output+="var "+refname+" ="+ document.refname+GroupItemRef.addLayer;
		addScript(output);
		return output;
	}
	
}
