package fi.foyt.fni.paytrail;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import fi.foyt.paytrail.PaytrailService;
import fi.foyt.paytrail.io.IOHandler;
import fi.foyt.paytrail.json.Marshaller;

public class PaytrailServiceProducer {

	@Dependent
	@Produces
	public PaytrailService producePaytrailService() {
		IOHandler ioHandler = new HttpClientIOHandler();
		Marshaller marshaller = new JacksonMarshaller();
		PaytrailService paytrailService = new PaytrailService(ioHandler, marshaller, "13466", "6pKF4jkv97zmqBJ3ZL8gUw5DfT2NMQ");
		return paytrailService;
	}
	
}
