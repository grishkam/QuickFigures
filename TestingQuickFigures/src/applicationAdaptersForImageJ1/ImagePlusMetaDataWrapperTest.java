/**
 * Author: Greg Mazo
 * Date Modified: Dec 20, 2020
 * Version: 2021.1
 */
package applicationAdaptersForImageJ1;

import org.junit.jupiter.api.Test;

import ij.IJ;
import infoStorage.BasicMetaDataHandler;
import infoStorage.BasicMetaInfoWrapper;
import infoStorage.StringBasedMetaWrapper;

class ImagePlusMetaDataWrapperTest {

	

	@Test
	void test() {
		
		ImagePlusMetaDataWrapper h = new ImagePlusMetaDataWrapper(IJ.createHyperStack("title", 30, 40, 1,1,1, 8));
		testValueStorage(h);
		testStorageIOfkeys(new StringBasedMetaWrapper());
		
	}

	/**
	 * @param h
	 */
	public void testValueStorage(ImagePlusMetaDataWrapper h) {
		String testme="test not";
		h.setProperty(testme);
		assert(h.getProperty().equals(testme));
		
		testStorageIOfkeys(h);
	}

	/**
	 * @param h
	 */
	public void testStorageIOfkeys(BasicMetaInfoWrapper h) {
		String keyName1 = "text entry";
		h.setEntry(keyName1, "5");
		assert(h.getEntryAsInt(keyName1)==5);
		h.setEntry(keyName1, "5.5");
		assert(h.getEntryAsDouble(keyName1)==5.5);
		h.setEntry(keyName1, "hello");
		assert(h.getEntryAsString(keyName1).equals("hello"));
		
		
		String keyName2 = "key2";
		String value2 = "test2";
		String value1 = "test1";
		h.setEntry(keyName2, value2);
		h.setEntry(keyName1, value1);
		assert(h.getEntryAsString(keyName1).equals(value1));
		assert(h.getEntryAsString(keyName2).equals(value2));
		
		h.switchMetaDataEntries(keyName1, keyName2);
		assert(h.getEntryAsString(keyName1).equals(value2));
		assert(h.getEntryAsString(keyName2).equals(value1));
		
		/**absent entries will hve null returns*/
		h.removeEntry(keyName1);
		assert(h.getEntryAsString(keyName1)==null);
		assert(h.getEntryAsDouble(keyName1)==null);
		assert(h.getEntryAsInt(keyName1)==null);
		
		h.setEntry(keyName1, "1, 15, 4, 80");
		int[] arr=h.parseMetadataIntArrayValue( keyName1);

		assert(arr[0]==1);
		assert(arr[1]==4);
		assert(arr[2]==15);
		assert(arr[3]==80);
		
		h.setEntry(keyName1, "1"+ BasicMetaDataHandler.delimiter+"15"+ BasicMetaDataHandler.delimiter+" 4"+ BasicMetaDataHandler.delimiter+" 80");
		String[] st=h.parseMetadataStringArrayValue(keyName1);
		assert(st[1].equals("15"));
		assert(st[3].equals("80"));
	}

}
