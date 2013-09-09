package net.eledge.android.toolkit.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.json.annotations.JsonField;
import net.eledge.android.toolkit.json.exception.JsonParserException;
import net.eledge.android.toolkit.json.internal.FieldConvertor;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser<T> {

	private final Class<T> jsonClazz;

	private String[] pathCache;

	private final Map<String, Field> fieldCache = new HashMap<String, Field>();

	public JsonParser(Class<T> clazz) {
		jsonClazz = clazz;
		scanJsonClass();
	}

	public void parseToObject(JSONObject json, T target) throws JsonParserException {
		parseToObject(json, ".", target);
	}

	private void parseToObject(JSONObject json, String path, T target) throws JsonParserException {
		String itempath = path;
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keys = json.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				itempath = StringUtils.equals(".", path) ? 
						StringUtils.join(StringArrayUtils.toArray(".", key)) : 
						StringUtils.join(StringArrayUtils.toArray(path, ".", key));
				if (!ArrayUtils.contains(pathCache, key) && !ArrayUtils.contains(pathCache, itempath)) {
					continue;
				}
				Object o = json.get(key);
				if (!fieldCache.containsKey(itempath) && o instanceof JSONObject) {
					parseToObject((JSONObject) o, itempath, target);
					continue;
				}
				if (o instanceof JSONArray) {
					if (parseToObject((JSONArray) o, itempath, target)) {
						continue;
					}
				}
				if (fieldCache.containsKey(itempath) || fieldCache.containsKey(key)) {
					Field field = fieldCache.containsKey(itempath) ? fieldCache.get(itempath) : fieldCache.get(key);
					FieldConvertor convertor = FieldConvertor.getConvertor(field);
					if (convertor != null) {
						convertor.setFieldValue(field, target, json, key);
					}
				}
			}
		} catch (JSONException e) {
			throw new JsonParserException("JSONException while parsing. Path:" + itempath, e);
		} catch (IllegalArgumentException e) {
			throw new JsonParserException("IllegalArgumentException while parsing. Path:" + itempath, e);
		} catch (IllegalAccessException e) {
			throw new JsonParserException("IllegalAccessException while parsing. Path:" + itempath, e);
		}
	}

	private boolean parseToObject(JSONArray jsonArray, String path, T target) throws JsonParserException {
		if (jsonArray.length() > 0) {
			if (jsonArray.optJSONObject(0) != null) {
				String itempath = StringUtils.join(StringArrayUtils.toArray(path, "[]"));
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject o = jsonArray.optJSONObject(i);
					if (o != null) {
						parseToObject(o, itempath, target);
					}
				}
				return true;
			}
		}
		return false;
	}

	private void scanJsonClass() {
		Set<String> paths = new TreeSet<String>();
		for (Field field : jsonClazz.getFields()) {
			if (field.isAnnotationPresent(JsonField.class)) {
				String key = getFieldName(field);
				fieldCache.put(key, field);
				if (StringUtils.containsNone(key, ".")) {
					paths.add(key);
				} else {
					String[] pathElements = StringUtils.split(StringUtils.stripStart(key, "."), ".");
					StringBuilder sb = new StringBuilder(".");
					for (String p : pathElements) {
						if (sb.length() > 1) {
							sb.append(".");
						}
						if (StringUtils.endsWith(p, "[]")) {
							sb.append(StringUtils.substringBefore(p, "[]"));
							paths.add(sb.toString());
							sb.append("[]");
							paths.add(sb.toString());
						} else {
							sb.append(p);
							paths.add(sb.toString());
						}
					}
				}
			}
		}
		pathCache = paths.toArray(new String[paths.size()]);
	}

	private String getFieldName(Field field) {
		JsonField jsonField = field.getAnnotation(JsonField.class);
		return StringUtils.defaultIfBlank(jsonField.value(), field.getName());
	}

}
