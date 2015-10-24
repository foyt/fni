package fi.foyt.fni.persistence.model.system;

public enum SystemSettingKey {
	
	/* System Wide */
	
	DEFAULT_LOCALE,
	DEFAULT_LANGUAGE,
	SYSTEM_USER_EMAIL,
	SYSTEM_MAILER_NAME,
	SYSTEM_MAILER_MAIL,
	
	/* Google Analytics */
	
	GOOGLE_ANALYTICS_TRACKING_ID, 
	
	/* Piwik */
	
	PIWIK_URL,
  PIWIK_SITEID,
	
  /* UserVoice */
  
  USERVOICE_CLIENT_KEY,
	
	/* Pdf Service */

	PDF_SERVICE_URL,
	PDF_SERVICE_SECRET,
	PDF_SERVICE_CALLBACK_SECRET,
	
	/* Guest */
	
	GUEST_USERNAME,
	GUEST_PASSWORD,
	
	/* Google */
	
	GOOGLE_APIKEY,
	GOOGLE_APISECRET,
	GOOGLE_CALLBACKURL,
	GOOGLE_PUBLIC_API_KEY,
	
	/* Yahoo */
	
	YAHOO_APIKEY,
	YAHOO_APISECRET,
	YAHOO_CALLBACKURL,
	
	/* Facebook */ 
	
	FACEBOOK_APIKEY,
	FACEBOOK_APISECRET,
	FACEBOOK_CALLBACKURL,
	
	/* Dropbox */
	
	DROPBOX_APIKEY,
	DROPBOX_APISECRET,
	DROPBOX_CALLBACKURL,
	DROPBOX_ROOT,

	/* Game Library specific */
	
	GAMELIBRARY_PUBLICATION_FORUM_ID,
	GAMELIBRARY_ORDERMAILER_NAME,
	GAMELIBRARY_ORDERMAILER_MAIL,
	GAMELIBRARY_SHOP_OWNER_NAME,
  GAMELIBRARY_SHOP_OWNER_MAIL,
	
  /* Paytrail */
  
  PAYTRAIL_MERCHANT_ID,
  PAYTRAIL_MERCHANT_SECRET,
  
  /* Chat */
  
  CHAT_DOMAIN,
  CHAT_MUC_HOST,
  CHAT_BOSH_SERVICE,
  CHAT_CREDENTIAL_SALT,
  CHAT_CREDENTIAL_PASSPHRASE,
  CHAT_CREDENTIAL_ITERATIONS,
  CHAT_ADMIN_JID,
  CHAT_ADMIN_PASSWORD,
  CHAT_BOT_JID,
  
  /* Illusion */
  
  ILLUSION_GROUP_HANDLING_FEE,
  ILLUSION_GROUP_HANDLING_FEE_CURRENCY,
  
  /* Larp-kalenteri */
  
  LARP_KALENTERI_URL,
  LARP_KALENTERI_CLIENT_ID,
  LARP_KALENTERI_CLIENT_SECRET;
}

