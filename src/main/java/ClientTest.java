

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

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
//    Bundle returnBundle = client.search()
//        .forResource(Patient.class)
//        .where(Patient.NAME.matches().value("Smith"))
//        .returnBundle(Bundle.class)
//        .totalMode(SearchTotalModeEnum.ACCURATE)
//        .execute();
//
//    returnBundle.getEntry().stream().map(e -> e.getResource())
//        .forEach(r -> System.out.println("ResourceId: " + r.getId()));
//    System.out.println("Found a total of: " + returnBundle.getTotal());

    Patient patient = new Patient();
    patient.addName().addGiven("Patrick").addGiven("Fritz")
        .setFamily("Nobre Gomes Areal Werner");
    patient.setActive(true);
    patient.addIdentifier().setSystem("http://testHospital.org/sid/patientNumbers")
        .setValue("0148946344648984");

    IParser jsonParser = ctx.newJsonParser().setPrettyPrint(true);
    System.out.println(jsonParser.encodeResourceToString(patient));

    //send the patient to the FHIR Server/Endpoint
    MethodOutcome methodOutcome = client.create()
        .resource(patient).execute();
    IIdType id = methodOutcome.getId();
    System.out.println("The Patient ID: " + id);

    Condition condition = new Condition();
    // add ICD-10-GM Coding
    condition.getCode().addCoding().setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("I30.1").setDisplay("Infektiöse Perikarditis");
    condition.setSubject(new Reference(id));

    Condition condition2 = new Condition();
    // add ICD-10-GM Coding
    condition2.getCode().addCoding().setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("I37.0").setDisplay("Pulmonalklappenstenose");
    condition2.setSubject(new Reference(id));

    System.out.println(jsonParser.encodeResourceToString(condition2));

  }
}