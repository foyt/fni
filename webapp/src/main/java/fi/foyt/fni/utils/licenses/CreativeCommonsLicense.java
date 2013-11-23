package fi.foyt.fni.utils.licenses;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

public class CreativeCommonsLicense {

  public static final String URL_PREFIX = "//creativecommons.org/licenses/";
	private static final String ICON_PREFIX = "//i.creativecommons.org/l/";
	private static final String NORMAL_ICON = "88x31.png";
	private static final String COMPACT_ICON = "80x15.png";
	private static final String DEFAULT_VERSION = "3.0";
		
	public CreativeCommonsLicense(boolean secure, String[] properties, String version, String jurisdiction) {
	  this.secure = secure;
		this.properties = properties;
		this.version = version;
		this.jurisdiction = jurisdiction;
	}
	
	public String getIconUrl(boolean compact) {
		StringBuilder urlBuilder = new StringBuilder();
		
		if (secure) {
      urlBuilder.append("https:");
    } else {
      urlBuilder.append("http:");
    }
    
    urlBuilder.append(ICON_PREFIX);
		urlBuilder.append(StringUtils.join(properties, '-'));
    urlBuilder.append('/');
		
		if (StringUtils.isNotBlank(version)) {
		  urlBuilder.append(version);
		} else {
      urlBuilder.append(DEFAULT_VERSION);
		}
		
		if (StringUtils.isNotBlank(jurisdiction)) {
			urlBuilder.append('/');
			urlBuilder.append(jurisdiction);
		}
		
		urlBuilder.append('/');
		
		if (compact) {
			urlBuilder.append(COMPACT_ICON);
		} else {
			urlBuilder.append(NORMAL_ICON);
		}
		
		return urlBuilder.toString();
	}
	
	public String getIconUrl() {
		return getIconUrl(false);
	}
	
	public String getCompactIconUrl() {
		return getIconUrl(true);
	}
	
	public boolean getPublicDomain() {
		return properties[0].equals("publicdomain");
	}
	
	public boolean getShareAlike() {
		return (!getPublicDomain()) && ArrayUtils.contains(properties, "sa");
	}
	
	public boolean getDerivatives() {
		return getPublicDomain() || !ArrayUtils.contains(properties, "nd");
	}
	
	public boolean getCommercial() {
		return getPublicDomain() || !ArrayUtils.contains(properties, "nc");
	}
	
	public boolean getAttribution() {
		return (!getPublicDomain()) && ArrayUtils.contains(properties, "by");
	}
	
	public String getUrl() {
		StringBuilder urlBuilder = new StringBuilder();
		
		if (secure) {
      urlBuilder.append("https:");
    } else {
      urlBuilder.append("http:");
    }
    
    urlBuilder.append(URL_PREFIX);
		urlBuilder.append(StringUtils.join(properties, '-'));
    urlBuilder.append('/');
		
		if (StringUtils.isNotBlank(version)) {
	  	urlBuilder.append(version);
		} else {
      urlBuilder.append(DEFAULT_VERSION);
		}
		
		if (StringUtils.isNotBlank(jurisdiction)) {
			urlBuilder.append('/');
			urlBuilder.append(jurisdiction);
		}
		
		return urlBuilder.toString();
	}
	
	private boolean secure;
	private String[] properties;
	private String version;
	private String jurisdiction;
}
