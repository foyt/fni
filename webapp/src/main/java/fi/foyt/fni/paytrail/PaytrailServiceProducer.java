package fi.foyt.fni.paytrail;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.paytrail.PaytrailService;
import fi.foyt.paytrail.io.IOHandler;
import fi.foyt.paytrail.json.Marshaller;

public class PaytrailServiceProducer {
  
  @Inject
  private SystemSettingsController systemSettingsController;

	@Dependent
	@Produces
	public PaytrailService producePaytrailService() {
		IOHandler ioHandler = new HttpClientIOHandler();
		Marshaller marshaller = new JacksonMarshaller();
    String merchantId = systemSettingsController.getSetting(SystemSettingKey.PAYTRAIL_MERCHANT_ID);
    String merchantSecret = systemSettingsController.getSetting(SystemSettingKey.PAYTRAIL_MERCHANT_SECRET);
    PaytrailService paytrailService = new PaytrailService(ioHandler, marshaller, merchantId, merchantSecret);
		return paytrailService;
	}
	
}
