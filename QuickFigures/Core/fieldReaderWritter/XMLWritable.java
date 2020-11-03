package fieldReaderWritter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLWritable {

	void addFieldsToElement(Document doc, Element node, Object o);
	
	void setObjectFieldsToElement(Element element);

}
