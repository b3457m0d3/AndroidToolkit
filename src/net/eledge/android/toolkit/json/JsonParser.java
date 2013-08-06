package net.eledge.android.toolkit.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.eledge.android.toolkit.StringUtils;
import net.eledge.android.toolkit.json.annotations.JsonField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser<T> {
	
	private final Class<T> jsonClazz;
	
	private final Map<String, Field> fieldCache = new HashMap<String, Field>();
	
	public JsonParser(Class<T> clazz) {
		jsonClazz = clazz;
		scanJsonClass();
	}
	
	public void parseToObject(JSONObject json, T target) throws JSONException {
		try {
			parseToObject(json, "", target);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseToObject(JSONObject json, String path, T target) throws JSONException, IllegalArgumentException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = json.keys();
        while( keys.hasNext() ){
            String key = keys.next();
            String itempath = StringUtils.isNotBlank(path) ? StringUtils.join(path, ".", key) :  key;
			Object o = json.get(key);
			if (o instanceof JSONObject) {
				parseToObject((JSONObject)o, itempath, target);
				continue;
			}
			if (o instanceof JSONArray) {
				parseToObject((JSONArray)o, itempath, target);
				continue;
			}
			if (fieldCache.containsKey(itempath)) {
				Field field = fieldCache.get(itempath);
				if ( field.getDeclaringClass() == String.class) {
					field.set(target, json.getString(key));
				}
			}
		}
	}

	private void parseToObject(JSONArray jsonArray, String path, T target) throws JSONException, IllegalArgumentException, IllegalAccessException {
        String itempath = StringUtils.join(path, "[]");
        if (jsonArray.length() > 0) {
        	for (int i=0; i<jsonArray.length(); i++) {
        		JSONObject o = jsonArray.optJSONObject(i);
        		if ( o != null) {
        			parseToObject(o, itempath, target);
        		}
        	}
        }
	}
	
	private void scanJsonClass() {
		for (Field field : jsonClazz.getFields()) {
			if (field.isAnnotationPresent(JsonField.class)) {
				fieldCache.put(getFieldName(field), field);
			}
		}
	}
	
	private String getFieldName(Field field) {
		JsonField jsonField = field.getAnnotation(JsonField.class);
		return StringUtils.defaultValue(jsonField.value(), field.getName());
	}
	

}
