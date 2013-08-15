package net.eledge.android.toolkit.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonField {
	
	/**
	 * Give the JSON field name, or a full path if the field name is not unique.
	 * JSON field names are case sensitive. 
	 * 
	 * default: the class field name is used as the JSON field name.
	 */
	String value() default "";
	
	/**
	 * give an alternate method for retrieving the appropriate enumeration.
	 * Must be a static method accepting a String as only parameter.
	 * 
	 * default: valueOf(String) is used.
	 */
	String enumMethod() default "";

}
