

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

public class ClientTest {

  /**
   * This is the Java main method, which gets executed
   */
  public static void main(String[] args) {

    // Create a context
    FhirContext ctx = FhirContext.forCached(FhirVersionEnum.R4);

    // increase socketTimeout
    ctx.getRestfulClientFactory().setSocketTimeout(20 * 1000);

    String baseUrl = "https://hapi.fhir.org/baseR4";

    //create Client
    IGenericClient client = ctx.newRestfulGenericClient(baseUrl);
    IParser jsonParser = ctx.newJsonParser().setPrettyPrint(true);

    client.registerInterceptor(new LoggingInterceptor());

    // search for Patient "Smith"
    Bundle bundle = client.search()
        .forResource(Patient.class)
        .where(Patient.NAME.matches().value("Smith"))
        .returnBundle(Bundle.class)
        .execute();

    String s = jsonParser.encodeResourceToString(bundle);
    System.out.println(s);


  }
}