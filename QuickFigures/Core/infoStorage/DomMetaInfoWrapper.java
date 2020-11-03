package infoStorage;
import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import logging.IssueLog;

/**Allows saving as a dom tree of metadata using the classes in this package*/
public class DomMetaInfoWrapper extends BasicMetaInfoWrapper {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Node node;

	public DomMetaInfoWrapper(Node node) {
		this.node=node;
	}
	
	public ArrayList<DomMetaInfoWrapper> getSubNodesWithTag(String tag, String classType) {
		ArrayList<DomMetaInfoWrapper> output=new ArrayList<DomMetaInfoWrapper>();
		NodeList nodes = getNode().getChildNodes();
		for(int i=0; i<nodes.getLength(); i++) {
			Node nodeI = nodes.item(i);
			if(tag==null ||nodeI.getNodeName().trim().equals(tag)) {
				DomMetaInfoWrapper e = new DomMetaInfoWrapper(nodeI);
				if(classType==null)
					output.add(e);
				String entryclass = e.getEntryAsString("class");
				if(entryclass!=null &&entryclass.equals(classType))
					output.add(e);

			}
			
		}
		return output;
	}
	
	
	 
	@Override
	public void setEntry(String entryname, String number) {
		
		getItem(entryname).setNodeValue(number);
	}
	
	boolean listiflost=true;
	
	private Node getItem(String entryname) {
		NamedNodeMap att = getNode().getAttributes();
			
		
		Node item = att.getNamedItem(entryname);
		
		
		
		if (item==null) {
			IssueLog.log("dom node does not seem to have an attribute called "+entryname);
			if (listiflost) {
						IssueLog.log("see list of attributes below "+entryname);
						for(int i=0; i<att.getLength(); i++) {
							Node item2 = att.item(i);
							if (item2.getNodeName().equals(entryname)) return item2;
							
							IssueLog.log(item2.getNodeName()+" => "+item2.getNodeValue());
						}
						
						
						IssueLog.log("also see child nodes");
						NodeList att3 = getNode().getChildNodes();
						for(int i=0; i<att3.getLength(); i++) {
							Node item2 = att3.item(i);
							IssueLog.log(item2.getNodeName()+" => "+item2.getNodeValue());
						}}
			
		}
		
		return item;
		
	}

	@Override
	public void replaceInfoMetaDataEntry(String b, String entryC) {
		String item = getEntryAsString(b);
		String item2 =getEntryAsString(entryC);
		setEntry(b, item2);
		setEntry(entryC, item);

	}

	@Override
	public String getEntryAsString(String b) {
		String output="";//returns empty string
		// TODO Auto-generated method stub
		if (getItem(b)!=null)
			{
			output=getItem(b).getNodeValue();
			
			String apos = "&apos;";
			if(output.contains(apos)) {
				String[] split = output.split(apos);
				if(split.length>1) output=split[1];
			}
			}
		
		return output;
	}

	@Override
	public void removeEntry(String entryname) {
		getNode().getAttributes().removeNamedItem(entryname);

	}

	public String getTextContent() {
		// TODO Auto-generated method stub
		return getNode().getTextContent();
	}

	public Node getNode() {
		return node;
	}



}
