package fi.foyt.fni.utils.licenses;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CreativeCommonsUtils {
	
	public static CreativeCommonsLicense parseLicenseUrl(String url) {
		if (StringUtils.startsWith(url, CreativeCommonsLicense.URL_PREFIX)) {
			String[] parts = StringUtils.substring(url, CreativeCommonsLicense.URL_PREFIX.length()).split("/");
			if (parts.length == 1) {
			  // Public domain ? 
				if (StringUtils.equals(parts[0], "publicdomain")) {
					return new CreativeCommonsLicense(new String[] { "publicdomain" }, "", "");
				} else {
		  		// Without jurisdiction and version
					return new CreativeCommonsLicense(parts[0].split("-"), "3.0", "");
				}
			} else if (parts.length == 2) {
				// Without jurisdiction
				return new CreativeCommonsLicense(parts[0].split("-"), parts[1], "");
			} else if (parts.length == 3) {
				// With jurisdiction
				return new CreativeCommonsLicense(parts[0].split("-"), parts[1], parts[2]);
			}
		}
		
		return null;
	}
	
	public static String createLicenseUrl(String[] properties, String version, String jurisdiction) {
		CreativeCommonsLicense license = new CreativeCommonsLicense(properties, version, jurisdiction);
		return license.getUrl();
	}
	
	public static String createLicenseUrl(boolean attribution, boolean derivatives, boolean shareAlike, boolean commercial) {
		List<String> properties = new ArrayList<String>();
		if (attribution) {
			properties.add("by");
		}
		
		if (!commercial) {
			properties.add("nc");
		}
		
		if (!derivatives) {
			properties.add("nd");
		}
		
		if (shareAlike) {
			properties.add("sa");
		}

		return createLicenseUrl(properties.toArray(new String[0]), null, null);
	}
}
