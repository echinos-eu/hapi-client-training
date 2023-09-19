

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
import org.hl7.fhir.r4.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName.NameUse;
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
//    Bundle bundle = client.search()
//        .forResource(Patient.class)
//        .where(Patient.NAME.matches().value("Smith"))
//        .returnBundle(Bundle.class)
//        .totalMode(SearchTotalModeEnum.ACCURATE)
//        .execute();
//
//    String s = jsonParser.encodeResourceToString(bundle);
//    System.out.println(s);
//
//    bundle.getEntry().stream().map(e -> e.getResource())
//        .forEach(r -> System.out.println("ResourceId: " + r.getId()));
//    System.out.println("Total Entries: " + bundle.getTotal());
//
//    // getNextPage
//    if (bundle.getLink(Bundle.LINK_NEXT) != null) {
//      Bundle bundle2 = client.loadPage().next(bundle).execute();
//      System.out.println(jsonParser.encodeResourceToString(bundle2));
//    }

    // add new Patient
    Patient pat = new Patient();
    pat.addName().setFamily("Werner").addGiven("Vorname").addGiven("Fritz")
        .setUse(NameUse.OFFICIAL);
    pat.setGender(AdministrativeGender.MALE);
    pat.setActive(true);
    String identifierSystem = "http://echinos.eu/fhir/sid/patientNumber";
    String identifierValue = "946098979840294";
    pat.addIdentifier().setSystem(identifierSystem).setValue(identifierValue);

    System.out.println(jsonParser.encodeResourceToString(pat));
  }
}