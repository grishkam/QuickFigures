package gridLayout;
import java.lang.annotation.*;

/**during my time programming this package I repeatedly wrote code to do several things on the same field of a given class
 1) add the value of the field to either a file, hashmap or image metadata with a key.
 2) set the value of a field to the value described by a file, hashmap or image metadata.
 3) add items representing said values to a user interface 
 4) set the values of a field based on changes to the items in the user interface 
 5) obtain descriptions of what a field is for
 
 To make future attempts at this easier, I wrote methods to perform the tasks described above to any field
 with this annotation (given that an object of its class is an argument). This makes it easy
 to add one additional field for which these operations may be performed.
 Although I no longer use this annotation extensively, I am retaining it
  I intend to add things to the annotation later that will allow me to do more powerful things
 with it. If I ever need to write user interfaces in the future, I want to write a code that will create elegant 
 looking user interfaces for an arbitrary class that has fields annotated with this annotation.
 
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RetrievableOption {

	/**String key for adding values of this field to either a hashtable, file or the metadata of an image*/
	String key();
	
	/**a string array for adding items to a Choice object so the user can pick from a list of choices for
	   an integer field. if the length of this array is 0 a text field for a number should be created for
	   the field in the user interface
	   */
	String[] choices() default {};
	
	/**the range of numbers that a numeric field should have.*/
	int[] minmax() default {0,0} ;
	
	/**the name that will appear next to this item in a user interface*/
	String label() default "unnamed field";
	
	/**Should this item be stored to a file and retrieved?*/
	boolean save() default true;
	/**Whether to match this field when creating a double of the given object*/
	boolean matchable() default true;
	
	/**For array list objects. keeps a parameter type name*/
	String parameterType() default "java.lang.Object";
	
	
	/**A number used for sorting fields in a particular order*/
	int rank() default 0;
	

}


