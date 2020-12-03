/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package fieldReaderWritter;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
//import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import logging.IssueLog;


public class SVGReader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		try {
		    String parser = XMLResourceDescriptor.getXMLParserClassName();
		    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		    String uri = "/Users/mazog/Desktop/test.svg";
		    Document doc = f.createDocument(uri);
		   Element element1 = doc.getDocumentElement();
		   printAllNodes(element1, ""); 
		   
		  // GraphicSVGParser.getGraphic(element1.getFirstChild());
		   
		} catch (IOException ex) {
		   ex.printStackTrace();
		}
	}
	

	
	static void printAllNodes(Node element1, String prefix) {
		PrintStream output = IssueLog.debugStream();
		
		 NodeList list1 = element1.getChildNodes();
		 int length = list1.getLength();
		 
		 output.println(prefix+element1.getNodeName());
	
		 
		 output.println(prefix+" "+length+" nodes");
		    for(int i=0; i<length; i++) {
		    	Node l1 = list1.item(i);
		    	
		    	printAllNodes(l1, prefix+"\t");
		    	NamedNodeMap att = l1.getAttributes();
		    	printAttributes(att, prefix+'\t');
		    	// output.println(prefix+l1);
		    	 
		    }
		
	}
	
	static void printAttributes(NamedNodeMap att, String prefix) {
		if (att==null) {
			IssueLog.log("no attributes");
			return;
		}
		int l=att.getLength();
		for (int i=0; i<l; i++) {
			IssueLog.log(prefix+"Attribute  "+'\t'+att.item(i));
		}
		
		
	}
	
	
	

}
