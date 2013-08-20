package net.eledge.android.toolkit.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eledge.android.toolkit.StringArrayUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class UrlBuilder {

	private String baseUrl;

	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, List<String>> multiParams = new HashMap<String, List<String>>();

	public UrlBuilder(String baseUrl) {
		this.baseUrl = baseUrl;
		if (StringUtils.contains(baseUrl, '?')) {
			stripBaseUrl();
		}
	}

	private void stripBaseUrl() {
		baseUrl = StringUtils.replace(baseUrl, "&amp;", "&");
		String[] result = StringUtils.split(this.baseUrl, '?');
		String toProcess = null;
		if (result.length == 2) {
			baseUrl = result[0];
			toProcess = result[1];
		} else {
			if (StringUtils.endsWith(baseUrl, "?")) {
				baseUrl = result[0];
			} else {
				baseUrl = "";
				toProcess = result[0];
			}
		}
		addParamsFromURL(toProcess);
	}

	public void addParamsFromURL(String url, String... ignoreKeys) {
		if (StringUtils.isNotBlank(url)) {
			String[] parameters = StringUtils.split(url, "&");
			for (String string : parameters) {
				String[] param = StringUtils.split(string, "=");
				if (param.length == 2) {
					if (StringArrayUtils.isBlank(ignoreKeys)
							|| !ArrayUtils.contains(ignoreKeys, param[0])) {
						if (multiParams.containsKey(param[0])
								|| params.containsKey(param[0])) {
							addMultiParam(param[0], param[1]);
						} else {
							addParam(param[0], param[1], true);
						}
					}
				}
			}
		}
	}

	public void addParam(String key, String value, boolean override) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			if (!params.containsKey(key)
					|| (params.containsKey(key) && override)) {
				params.put(key, value);
			}
		}
	}

	public void addParam(String key, String[] values, boolean override) {
		if (StringUtils.isNotBlank(key)) {
			if (override) {
				removeParam(key);
			}
			if ((values != null) && (values.length > 0)) {
				for (String value : values) {
					addMultiParam(key, value);
				}

			}
		}
	}

	public boolean hasParam(String key) {
		return multiParams.containsKey(key) || params.containsKey(key);
	}

	public void removeParam(String key) {
		if (StringUtils.isNotBlank(key)) {
			if (multiParams.containsKey(key)) {
				multiParams.remove(key);
			}
			if (params.containsKey(key)) {
				params.remove(key);
			}
		}
	}

	public void removeDefault(String key, String value) {
		if (StringUtils.isNotBlank(key)) {
			if (multiParams.containsKey(key)) {
				if (multiParams.get(key).contains(value)) {
					multiParams.get(key).remove(value);
				}
			}
			if (params.containsKey(key)) {
				if (StringUtils.equals(value, params.get(key))) {
					params.remove(key);
				}
			}
		}
	}

	public void removeStartWith(String key, String value) {
		if (params.containsKey(key)) {
			if (StringUtils.startsWith(params.get(key), value)) {
				params.remove(key);
			}
		}
		if (multiParams.containsKey(key)) {
			List<String> toRemove = new ArrayList<String>();
			for (String string : multiParams.get(key)) {
				if (StringUtils.startsWith(string, value)) {
					toRemove.add(string);
				}
			}
			if (toRemove.size() > 0) {
				multiParams.get(key).removeAll(toRemove);
			}
			if (multiParams.get(key).size() == 0) {
				removeParam(key);
			}
		}
	}

	public void addMultiParam(String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			List<String> list = null;
			if (multiParams.containsKey(key)) {
				list = multiParams.get(key);
			} else {
				list = new ArrayList<String>();
				multiParams.put(key, list);
				if (params.containsKey(key)) {
					// convert to Array...
					list.add(params.get(key));
					params.remove(key);
				}
			}
			if (!list.contains(value)) {
				list.add(value);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(baseUrl);
		if (params.size() + multiParams.size() > 0) {
			boolean first = true;
			sb.append("?");
			for (String key : params.keySet()) {
				if (!first) {
					sb.append("&");
				}
				sb.append(key).append("=").append(params.get(key));
				first = false;
			}
			for (String key : multiParams.keySet()) {
				for (String s : multiParams.get(key)) {
					if (!first) {
						sb.append("&");
					}
					sb.append(key).append("=").append(s);
					first = false;
				}
			}
		}
		return sb.toString();
	}
}
