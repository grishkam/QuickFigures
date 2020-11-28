package illustratorScripts;

public class ArtLayerRef extends IllustratorObjectRef {

	String addLayer=".layers.add();";
	//String currentLayer=".layers.activeLayer;";
	
	public String createNewRef(IllustratorDocRef document) {
		String output="";
		if (!document.set) output+= document.setReftoActiveDocument()+'\n'; 
		output+="var "+refname+" ="+ document.refname+addLayer;
		addScript(output);
		return output;
	}
	
	/**sets this object to the current art layer
	public String settoCurrentLayerRef(IllustratorDocRef document) {
		String output="";
		if (!document.set) output+= document.setReftoActiveDocument()+'\n'; 
		output+="var "+refname+" ="+ document.refname+currentLayer;
		addScript(output);
		return output;
		
	}*/
	
	
	public ArtLayerRef createSubRef() {
		String script="";
		ArtLayerRef output = new ArtLayerRef();
		script+=output.getAssignment()+refname+addLayer;
		addScript(script);
		return output;
	}
	
	public GroupItemRef createSubRefG() {
		String script="";
		GroupItemRef output = new GroupItemRef();
		script+=output.getAssignment()+refname+GroupItemRef.addLayer;
		addScript(script);
		return output;
	}

}
