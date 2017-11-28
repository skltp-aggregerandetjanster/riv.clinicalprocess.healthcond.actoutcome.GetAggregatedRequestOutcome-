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
package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.healthcond.actoutcome.getrequestoutcomeresponder.v4.GetRequestOutcomeType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.RequestListFactory;

public class RequestListFactoryImpl implements RequestListFactory {

	private static final Logger log = LoggerFactory.getLogger(RequestListFactoryImpl.class);

	/**
	 * Filtrera svarsposter från i EI (ei-engagement) baserat parametrar i GetRequestOutcome requestet (req).
	 * Följande villkor måste vara sanna för att en svarspost från EI skall tas med i svaret:
	 *
	 * 1. req.fromDate <= ei-engagement.mostRecentContent <= req.toDate
	 * 2. req.careUnitId.size == 0 or req.careUnitId.contains(ei-engagement.logicalAddress)
	 *
	 * Svarsposter från EI som passerat filtreringen grupperas på fältet sourceSystem samt postens fält logicalAddress (= PDL-enhet) samlas i listan careUnitId per varje sourceSystem
	 *
	 * Ett anrop görs per funnet sourceSystem med följande värden i anropet:
	 *
	 * 1. logicalAddress = sourceSystem (systemadressering)
	 * 2. subjectOfCareId = orginal-request.subjectOfCareId
	 * 3. careUnitId = listan av PDL-enheter som returnerats från EI för aktuellt source system)
	 */
	public List<Object[]> createRequestList(QueryObject qo, FindContentResponseType src) {
		final GetRequestOutcomeType request = (GetRequestOutcomeType)qo.getExtraArg();
		final String reqCareUnit = request.getSourceSystemHSAId();
		
		FindContentResponseType eiResp = (FindContentResponseType)src;
		List<EngagementType> inEngagements = eiResp.getEngagement();

		log.info("Got {} hits in the engagement index", inEngagements.size());

		Map<String, List<String>> sourceSystem_pdlUnitList_map = new HashMap<String, List<String>>();

		for (EngagementType engagement : inEngagements) {
			if(isPartOf(reqCareUnit, engagement.getLogicalAddress())) {
                log.debug("Add source system: {} for producer: {}", engagement.getSourceSystem(), engagement.getLogicalAddress());
				addPdlUnitToSourceSystem(sourceSystem_pdlUnitList_map, engagement.getSourceSystem(), engagement.getLogicalAddress());
			}
		}

		// Prepare the result of the transformation as a list of request-payloads,
		// one payload for each unique logical-address (e.g. source system since we are using systemaddressing),
		// each payload built up as an object-array according to the JAX-WS signature for the method in the service interface
		List<Object[]> listOfRequests = new ArrayList<Object[]>();
		for (Entry<String, List<String>> entry : sourceSystem_pdlUnitList_map.entrySet()) {
			final String sourceSystem = entry.getKey();
           	log.info("Calling source system using logical address {} for subject of care {}", sourceSystem, request.getPatientId().getId());
			Object[] reqArr = new Object[] {sourceSystem, request};
			listOfRequests.add(reqArr);
		}

		log.debug("Transformed payload: {}", listOfRequests);
		return listOfRequests;
	}

	boolean isPartOf(List<String> careUnitIdList, String careUnit) {

		log.debug("Check presence of {} in {}", careUnit, careUnitIdList);

		if (careUnitIdList == null || careUnitIdList.size() == 0) return true;

		return careUnitIdList.contains(careUnit);
	}
	
	boolean isPartOf(final String careUnitId, final String careUnit) {
		log.debug("Check careunit {} equals expected {}", careUnitId, careUnit);
		if(StringUtils.isBlank(careUnitId)) return true;
		return careUnitId.equals(careUnit);
	}

	void addPdlUnitToSourceSystem(Map<String, List<String>> sourceSystem_pdlUnitList_map, String sourceSystem, String pdlUnitId) {
		List<String> careUnitList = sourceSystem_pdlUnitList_map.get(sourceSystem);
		if (careUnitList == null) {
			careUnitList = new ArrayList<String>();
			sourceSystem_pdlUnitList_map.put(sourceSystem, careUnitList);
		}
		careUnitList.add(pdlUnitId);
	}
}
