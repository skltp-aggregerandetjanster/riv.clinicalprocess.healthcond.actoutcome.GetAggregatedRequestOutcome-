/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome.integrationtest;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import riv.clinicalprocess.healthcond.actoutcome.enums.v4.RequestOutcomeTypeEnum;
import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.v4.CodeRequestOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.v4.FullOrganisationType;
import riv.clinicalprocess.healthcond.actoutcome.v4.HeaderType;
import riv.clinicalprocess.healthcond.actoutcome.v4.HealthcareProfessionalType;
import riv.clinicalprocess.healthcond.actoutcome.v4.IIType;
import riv.clinicalprocess.healthcond.actoutcome.v4.OrganisationType;
import riv.clinicalprocess.healthcond.actoutcome.v4.OutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RecipientType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RecordType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestOutcomeBodyType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestPatientType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestPersonIdType;
import riv.clinicalprocess.healthcond.actoutcome.v4.RequestOutcomeAuthorType;
import riv.clinicalprocess.healthcond.actoutcome.v4.SourceType;
import se.skltp.agp.test.producer.TestProducerDb;

public class GetAggregatedRequestOutcomeTestProducerDb extends TestProducerDb {

	private static final Logger log = LoggerFactory.getLogger(GetAggregatedRequestOutcomeTestProducerDb.class);
	private static final ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat("yyyyMMddhhmmss");

	@Override
	public Object createResponse(Object... responseItems) {
		log.debug("Creates a response with {} items", responseItems);
		final GetRequestOutcomeResponseType response = new GetRequestOutcomeResponseType();
		for (int i = 0; i < responseItems.length; i++) {
			response.getRequestOutcome().add((RequestOutcomeType) responseItems[i]);
		}
		return response;
	}

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {
    	log.info("Created one response item for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[] {logicalAddress, registeredResidentId, businessObjectId});
		
		final RequestOutcomeType ref = new RequestOutcomeType();
		
		final HeaderType header = new HeaderType();
		SourceType source = new SourceType();
		IIType iitype =new IIType();
		iitype.setRoot("1.2.3");
		iitype.setExtension(logicalAddress);
		source.setSystemId(iitype);
		header.setSource(source);
		
		RecordType record = new RecordType();
		record.setCareContactId(iitype);
		header.setRecord(record);
		
		ref.setHeader(header);
		ref.setBody(body(logicalAddress, registeredResidentId));
		
		
		return ref;
	}
	
	protected HealthcareProfessionalType hp(final String logicalAddress) {
		final HealthcareProfessionalType hp = new HealthcareProfessionalType();
		
		hp.setId(logicalAddress);
		hp.setName("Healtcareprofessional");

		return hp;
	}
	
	protected RequestOutcomeBodyType body(final String logicalAddress, String registeredResidentId) {
		final RequestOutcomeBodyType type = new RequestOutcomeBodyType();
		final OutcomeType outcomeType = new OutcomeType();
		outcomeType.setOutcomeText("OutcomeText");
		
		final RecipientType recipientType = new RecipientType();
		OrganisationType org = new OrganisationType();
		org.setCareUnitId("CUID");
		recipientType.setOrganisation(org);
		
		type.setOutcome(outcomeType);
		type.setRequestOutcomeTime(df.format(new Date()));
		FullOrganisationType fo = new FullOrganisationType();
		fo.setCareUnitAddress("Address");
		fo.setCareUnitEmail("a@b");
		fo.setCareUnitId(logicalAddress);
		type.setRespondingOrganisation(fo );
		CodeRequestOutcomeType cro = new CodeRequestOutcomeType();
		cro.setCode(RequestOutcomeTypeEnum.SVA);
		type.setTypeOfRequestOutcome(cro);
		RequestOutcomeAuthorType author = new RequestOutcomeAuthorType();
		author.setHealthcareProfessional(hp(logicalAddress));
		type.setAuthor(author);
		
		type.setPatient(new RequestPatientType());
		RequestPersonIdType person = new RequestPersonIdType();
		person.setExtension(registeredResidentId);
		person.setRoot("1.2.752.129.2.1.3.1");
		type.getPatient().setPersonId(person);
		
		return type;
	}

}
