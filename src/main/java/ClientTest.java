

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

public class ClientTest {

  private static IGenericClient client;
  private static IParser iParser;

  /**
   * This is the Java main method, which gets executed
   */
  public static void main(String[] args) {

    // Create a context
    FhirContext ctx = FhirContext.forCached(FhirVersionEnum.R4);
    String serverBase = "http://hapi.fhir.org/baseR4";
    client = ctx.newRestfulGenericClient(serverBase);
    client.registerInterceptor(new LoggingInterceptor());
    iParser = ctx.newJsonParser().setPrettyPrint(true);

    //searchPatientSmith();

  }

  private static void searchPatientSmith() {
    Bundle bundle = client.search()
        .forResource(Patient.class)
        .where(Patient.NAME.matches().value("Smith"))
        .returnBundle(Bundle.class)
        .totalMode(SearchTotalModeEnum.ACCURATE)
        .count(2)
        .execute();

    bundle.getEntry().stream().map(e -> e.getResource())
        .forEach(r -> System.out.println("ResourceID: " + r.getId()));

    System.out.println(bundle.getTotal());
  }
}