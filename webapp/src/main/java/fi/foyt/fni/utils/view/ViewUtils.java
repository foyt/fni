package fi.foyt.fni.utils.view;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ViewUtils {

	public static Map<String, String> explodeActionParameters(String actionParameters) {
		Map<String, String> result = new HashMap<String, String>();

		if (StringUtils.isNotBlank(actionParameters)) {
			String[] paramsArray = actionParameters.split(";");
			for (String param : paramsArray) {
				String[] paramArray = param.split(":");
				result.put(paramArray[0], paramArray[1]);
			}
		}

		return result;
	}

	public static String implodeActionParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		Iterator<String> keys = parameters.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			result.append(key + ":" + parameters.get(key));
			if (keys.hasNext())
				result.append(";");
		}

		return URLDecoder.decode(result.toString(), "UTF-8");
	}

}
