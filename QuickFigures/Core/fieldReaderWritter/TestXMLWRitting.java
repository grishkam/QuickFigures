package fieldReaderWritter;

import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestXMLWRitting {
	
	public static void main(String[] arg) {
		/**SourceStackEntry sse = new SourceStackEntry("/My foler", "Is mine");
		sse.setPath("/path1");
		sse.setDescription("My path is this");
		
		SubFigure subfig=new SubFigure("Titlenumber45");
		subfig.getLayout().setCols(30);
		subfig.addSourceStack(sse);//new ArrayList<SourceStackEntry>();
		//subfig.source1.add( new SourceStackEntry("/path2/", "Is new"));
		//subfig.source1.add( sse);
		
		saveObject("/Test1", subfig);
		try {
		Object output = testXmlReading.readObjectFromFile("/Test1", subfig.getClass());
		
		testXmlReading.reportAnnotatedFields( output);
			} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */

		
	
		
		

	
	} 
	

	
	public static void drawSet(String newpath) {
	
		
	}
	


	public static void saveObject(String newpath, Object o) {
			try {
				File file=new File(newpath);
				int i=FileChoiceUtil.overrideQuestion(file);
				if(i!=0) return;
				// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
	
			docBuilder = docFactory.newDocumentBuilder();
		
 
		// root elements
		Document doc = docBuilder.newDocument();
		
		
		
		
		Element rootElement = doc.createElement("Root");
		doc.appendChild(rootElement);
		
		
		writeObjectToDoc(doc, rootElement, o);
			
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(newpath);
 
		
 
		transformer.transform(source, result);
 
			
			
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**appends an object that has the right annotation 
	  puts fields that have the retreivable option annotation as subelements
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException */
	public static Element writeObjectToDoc(Document doc, Element rootElement, Object o ) throws IllegalArgumentException, IllegalAccessException {
		RetriveableClass arg0 = o.getClass().getAnnotation( RetriveableClass.class);
		
		if (arg0==null) { IssueLog.log("no retrivable class issue ");
			return null;}
		Element theClass = doc.createElement(arg0.tag());
		Node node = rootElement.appendChild(theClass);
		
		
		if (o instanceof XMLWritable) {
			XMLWritable o2=(XMLWritable) o;
			o2.addFieldsToElement(doc, (Element) node, o);
		} else
			addAnnotatedFieldsToElement(doc, (Element) node, o);
		
		
		return theClass;
		
	}
	
	public static void addAnnotatedFieldsToElement(Document doc, Element theElement, Object o) {
			for (Field f: o.getClass().getDeclaredFields())  try{
			 
			 RetrievableOption option= f.getAnnotation( RetrievableOption.class);
			f.setAccessible(true);
			 Object value = f.get(o);
		
			if (option!=null&&f!=null) {
				/**creates the atributes */
				
				RetriveableClass subel = f.getType().getAnnotation(RetriveableClass.class);
				
				 
				if (subel!=null) {
					//Element el2 = doc.createElement(subel.tag());
					//Node childel = theElement.appendChild(el2);
					writeObjectToDoc(doc,(Element) theElement, value);
				} else
							{
					Attr attr = doc.createAttribute(option.key());
					String saved=BasicMetaDataHandler.entryString(value);
					attr.setValue(saved);
					theElement.setAttributeNode(attr);
				}
				
			} 
		 }catch (Exception e) {
			
			}
	}

	
	/**boolean isArrayList=f.getType()==ArrayList.class;
	
	if (isArrayList){
	Method methodget = f.getType().getMethod("get", int.class);
	int msize= (Integer) f.getType().getMethod("size").invoke(value);
	for(int i=0; i<msize; i++) {
		Object v2 = methodget.invoke(value, i);
		writeObjectToDoc(doc, theClass,v2);
	}
	
 }*/
	
}
