package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.ObjectFactory;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.ResponseListFactory;

public class ResponseListFactoryImpl implements ResponseListFactory {

    private static final Logger log = LoggerFactory.getLogger(ResponseListFactoryImpl.class);
    private static final JaxbUtil jaxbUtil = new JaxbUtil(GetRequestOutcomeResponseType.class, ProcessingStatusType.class);
    private static final ObjectFactory OF = new ObjectFactory();

    @Override
    public String getXmlFromAggregatedResponse(QueryObject queryObject, List<Object> aggregatedResponseList) {
        final GetRequestOutcomeResponseType aggregatedResponse = new GetRequestOutcomeResponseType();

        for (Object object : aggregatedResponseList) {
            final GetRequestOutcomeResponseType response = (GetRequestOutcomeResponseType) object;
            aggregatedResponse.getRequestOutcome().addAll(response.getRequestOutcome());
        }
/*
        aggregatedResponse.setResult(new ResultType());
        aggregatedResponse.getResult().setLogId("NA");
        aggregatedResponse.getResult().setResultCode(ResultCodeEnum.INFO);
*/
        String subjectOfCareId = queryObject.getFindContent().getRegisteredResidentIdentification();
        log.info("Returning {} aggregated alert informations for subject of care id {}", aggregatedResponse.getRequestOutcome().size(),
                subjectOfCareId);

        return jaxbUtil.marshal(OF.createGetRequestOutcomeResponse(aggregatedResponse));
    }
}
