package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome.integrationtest;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcome.v4.rivtabp21.GetRequestOutcomeResponderInterface;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeType;
import se.skltp.agp.test.producer.TestProducerDb;

@WebService(serviceName = "GetRequestOutcomeResponderService", 
               portName = "GetRequestOutcomeResponderPort", 
        targetNamespace = "urn:riv:clinicalprocess:healthcond:actoutcome:GetRequestOutcome:4:rivtabp21", 
                   name = "GetRequestOutcomeInteraction")
public class GetAggregatedRequestOutcomeTestProducer implements GetRequestOutcomeResponderInterface {

	protected static final Logger log = LoggerFactory.getLogger(GetAggregatedRequestOutcomeTestProducer.class);

	private TestProducerDb testDb;
	public void setTestDb(TestProducerDb testDb) {
		this.testDb = testDb;
	}

	public GetRequestOutcomeResponseType getRequestOutcome(String logicalAddress, GetRequestOutcomeType request) {
		final Object response = testDb.processRequest(logicalAddress, request.getPatientId().getId());
		if (response == null) {
			return new GetRequestOutcomeResponseType();
		}
        return (GetRequestOutcomeResponseType) response;
	}
}
