package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.v4.HeaderType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestOutcomeBodyType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestPatientType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestPersonIdType;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome.ResponseListFactoryImpl;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObject;

public class ResponseListFactoryTest {

	private final static String SUBJECT_OF_CARE = UUID.randomUUID().toString();
	private final static int NUMBER_OF_RESPONSES = 5;
	
	private final ResponseListFactoryImpl testObject = new ResponseListFactoryImpl();
	private final List<Object> responseList = new ArrayList<Object>();
	
	private final QueryObject queryObject = Mockito.mock(QueryObject.class);
	private final FindContentType findContentType = Mockito.mock(FindContentType.class);
	
	@Before
	public void setup() {
		Mockito.when(findContentType.getRegisteredResidentIdentification()).thenReturn(SUBJECT_OF_CARE);
		Mockito.when(queryObject.getFindContent()).thenReturn(findContentType);
		
		for(int i = 0; i < NUMBER_OF_RESPONSES; i++) {
			final GetRequestOutcomeResponseType resp = new GetRequestOutcomeResponseType();
			final RequestOutcomeType ref = new RequestOutcomeType();
			RequestOutcomeBodyType body = new RequestOutcomeBodyType();
			body.setPatient(new RequestPatientType());
			body.getPatient().setPersonId(new RequestPersonIdType());
			body.getPatient().getPersonId().setExtension(SUBJECT_OF_CARE);
			body.getPatient().getPersonId().setRoot("");
			ref.setBody(body);
			ref.setHeader(new HeaderType());

			resp.getRequestOutcome().add(ref);
			responseList.add(resp);
		}
	}

	@Test
	public void testGetXmlFromAggregatedResponse() {
		final JaxbUtil jaxbUtil = new JaxbUtil(GetRequestOutcomeResponseType.class);

		final String after = testObject.getXmlFromAggregatedResponse(queryObject, responseList);
		final GetRequestOutcomeResponseType type = (GetRequestOutcomeResponseType) jaxbUtil.unmarshal(after);
		
		assertEquals(NUMBER_OF_RESPONSES, type.getRequestOutcome().size());
		for(RequestOutcomeType ref : type.getRequestOutcome()) {
			assertEquals(SUBJECT_OF_CARE, ref.getBody().getPatient().getPersonId().getExtension());
		}
	}
}
