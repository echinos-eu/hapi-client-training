

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

public class ClientTest {

  /**
   * This is the Java main method, which gets executed
   */
  public static void main(String[] args) {

    // Create a context
    FhirContext ctx = FhirContext.forR4();

    // Create a client
    IGenericClient client = ctx.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
    //add logging interceptor
    client.registerInterceptor(new LoggingInterceptor());

    // search for Patient "Smith"
    Bundle returnBundle = client.search()
        .forResource(Patient.class)
        .where(Patient.NAME.matches().value("Smith"))
        .returnBundle(Bundle.class)
        .execute();

    returnBundle.getEntry().stream().map(e -> e.getResource())
        .forEach(r -> System.out.println("ResourceId: " + r.getId()));
    System.out.println("Found a total of: " + returnBundle.getTotal());
  }
}