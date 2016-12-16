package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome

import se.skltp.agp.testnonfunctional.TPPatientsAbstract

/**
 * Test GetAggregatedRequestOutcome using test cases defined in patients.csv (or patients-override.csv)
 */
class TPPatients extends TPPatientsAbstract with CommonParameters {
  setUp(setUpAbstract(serviceName, urn, responseElement, responseItem, baseUrl))
}
