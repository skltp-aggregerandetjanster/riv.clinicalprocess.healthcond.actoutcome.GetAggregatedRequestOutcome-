package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome.integrationtest;

import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_ONE_HIT;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcome.v4.rivtabp21.GetRequestOutcomeResponderInterface;
import riv.clinicalprocess.healthcond.actoutcome.v4.PersonIdType;
import se.skltp.agp.test.consumer.AbstractTestConsumer;
import se.skltp.agp.test.consumer.SoapHeaderCxfInterceptor;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome.GetAggregatedRequestOutcomeMuleServer;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;

public class GetAggregatedRequestOutcomeTestConsumer extends AbstractTestConsumer<GetRequestOutcomeResponderInterface> {

	private static final Logger log = LoggerFactory.getLogger(GetAggregatedRequestOutcomeTestConsumer.class);

	public static void main(String[] args) {
		String serviceAddress = GetAggregatedRequestOutcomeMuleServer.getAddress("SERVICE_INBOUND_URL");
		String personnummer = TEST_RR_ID_ONE_HIT;

		GetAggregatedRequestOutcomeTestConsumer consumer = new GetAggregatedRequestOutcomeTestConsumer(serviceAddress, SAMPLE_SENDER_ID, SAMPLE_ORIGINAL_CONSUMER_HSAID, SAMPLE_CORRELATION_ID);
		Holder<GetRequestOutcomeResponseType> responseHolder = new Holder<GetRequestOutcomeResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();

		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
	}

	public GetAggregatedRequestOutcomeTestConsumer(String serviceAddress, String senderId, String originalConsumerHsaId, String correlationId) {
		// Setup a web service proxy for communication using HTTPS with Mutual Authentication
		super(GetRequestOutcomeResponderInterface.class, serviceAddress, senderId, originalConsumerHsaId, correlationId);
	}

	public void callService(String logicalAddress, String registeredResidentId, Holder<ProcessingStatusType> processingStatusHolder, Holder<GetRequestOutcomeResponseType> responseHolder) {
		log.debug("Calling GetRequestOutcome-soap-service with Registered Resident Id = {}", registeredResidentId);

		GetRequestOutcomeType request = new GetRequestOutcomeType();
		final PersonIdType personId = new PersonIdType();
		personId.setId(registeredResidentId);
		personId.setType("1.2.752.129.2.1.3.1");
		request.setPatientId(personId);

		final GetRequestOutcomeResponseType response = _service.getRequestOutcome(logicalAddress, request);
		responseHolder.value = response;

		processingStatusHolder.value = SoapHeaderCxfInterceptor.getLastFoundProcessingStatus();
	}
}
