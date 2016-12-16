package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedrequestoutcome

trait CommonParameters {
  val serviceName:String     = "RequestOutcome"
  val urn:String             = "urn:riv:clinicalprocess:healthcond:actoutcome:GetRequestOutcomeResponder:3"
  val responseElement:String = "GetRequestOutcomeResponse"
  val responseItem:String    = "RequestOutcome"
  var baseUrl:String         = if (System.getProperty("baseUrl") != null && !System.getProperty("baseUrl").isEmpty()) {
                                   System.getProperty("baseUrl")
                               } else {
                                   "http://33.33.33.33:8081/GetAggregatedRequestOutcome/service/v3"
                               }
}
