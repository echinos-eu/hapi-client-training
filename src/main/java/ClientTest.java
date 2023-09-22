

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import java.util.Date;
import org.checkerframework.checker.units.qual.K;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

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
//    createReadUpdateDeletePatient();
    createPatientWithConditions();
  }

  private static void createPatientWithConditions() {
    Patient patient = new Patient();
    patient.addName().addGiven("Patrick").addGiven("Fritz")
        .setUse(NameUse.OFFICIAL);
    StringType familyElement = patient.getNameFirstRep().getFamilyElement();
    familyElement.setValue("Werner");
    familyElement.addExtension().setUrl("http://hl7.org/fhir/StructureDefinition/humanname-own-name")
        .setValue(new StringType("Werner"));
    String identSystem = "http://echinos.eu/fhir/sid/PatientIdentifier";
    String identValue = "012598419642sfdf34";
    CodeableConcept mrcc = new CodeableConcept();
    mrcc.addCoding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("MR");
    patient.addIdentifier().setSystem(identSystem).setValue(identValue).setType(mrcc);
    patient.setGender(AdministrativeGender.MALE);
    patient.setActive(true);
    patient.setBirthDateElement(new DateType("1980-01-01"));

    patient.getMeta()
        .addProfile("https://gematik.de/fhir/isik/v3/Basismodul/StructureDefinition/ISiKPatient");

    System.out.println(iParser.encodeResourceToString(patient));

    MethodOutcome outcome = client.validate()
        .resource(patient).execute();

    OperationOutcome outcomeR4 = (OperationOutcome) outcome.getOperationOutcome();

    outcomeR4.getIssue().stream().forEach(i -> System.out.println(i.getDiagnostics()));
    System.out.println(iParser.encodeResourceToString(outcomeR4));

    MethodOutcome patientOutcome = client.create().resource(patient).execute();
    patient = (Patient) patientOutcome.getResource();

    Condition condition = new Condition();
    condition.getCode().setText("Adipositas durch übermäßige Kalorienzufuhr").addCoding()
        .setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("E66.01").setDisplay("Adipositas durch übermäßige Kalorienzufuhr");
    condition.setSubject(new Reference(patient));
    condition.setRecordedDate(new Date());

    System.out.println(iParser.encodeResourceToString(condition));
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

  private static void createReadUpdateDeletePatient() {
    Patient patient = new Patient();
    patient.addName().addGiven("Patrick").addGiven("Fritz").setFamily("Werner");
    String identSystem = "http://echinos.eu/fhir/sid/PatientIdentifier";
    String identValue = "012598419642sfdf34";
    patient.addIdentifier().setSystem(identSystem).setValue(identValue);
    patient.setGender(AdministrativeGender.MALE);
    patient.setActive(true);

    String patString = iParser.encodeResourceToString(patient);
    System.out.println(patString);

//    MethodOutcome outcome = client.create()
//        .resource(patient)
//        .conditional()
//        .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(identSystem, identValue))
//        .execute();
//    String patId = outcome.getId().getValueAsString();
//    System.out.println(patId);
//
//    System.out.println(outcome.getResponseStatusCode());
//    System.out.println("Resource created?: " + outcome.getCreated());

    // read created resource back

    Patient readPatient = null;
    try {
      readPatient = client.read()
          .resource(Patient.class)
          .withId(new IdType("Patient/14994670"))
          .execute();
    } catch (ResourceGoneException e) {
      System.out.println("Resource was deleted: " + e.getMessage());;
    }

    //System.out.println(iParser.encodeResourceToString(readPatient));

    patient.setBirthDate(new Date());
    patient.addAddress().addLine(IdType.newRandomUuid().getValue());

    MethodOutcome updateOutcome = client.update()
        .resource(patient)
        .withId("Patient/14994670")
        .execute();
    System.out.println(updateOutcome.getId().getValueAsString());

    client.delete()
        .resourceById(new IdType("Patient/14994670"))
        .execute();
  }
}

