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
