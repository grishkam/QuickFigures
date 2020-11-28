package illustratorScripts;

public class CompoundPathItemRef extends IllustratorObjectRef{
	/**when given a referance to an illustrator object with a compoundpathitems collection, creates a script to 
	 att a new pathitem*/
	public String createItem(IllustratorObjectRef artlayer) {
		set=true;
		String output="";
		output+='\n'+"var "+refname+" ="+artlayer.refname+".compoundPathItems.add();";
		addScript(output);
		return output;
	}
	
}
