import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class Converter {
  static IParser xmlParser;
  static IParser jsonParser;

  public static void main(String[] args) throws IOException {
    FhirContext ctx = FhirContext.forR4();
    xmlParser = ctx.newXmlParser();
    jsonParser = ctx.newJsonParser();

    List<Path> xmls;
    try (Stream<Path> walk = Files.walk(Path.of("fhir"))) {
      xmls = walk
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith("xml"))
          .toList();
    }
    xmls.forEach(Converter::saveFHIRinJson);

  }

  private static void saveFHIRinJson(Path p){
    try (FileInputStream in = new FileInputStream(p.toFile())) {
      IBaseResource iBaseResource = xmlParser.parseResource(in);
      String jsonResource = jsonParser.encodeResourceToString(iBaseResource);

      String filename = p.toFile().getPath().replace(".xml", ".json");
      Path path = Paths.get(filename);
      Files.write(path, jsonResource.getBytes(StandardCharsets.UTF_8));

    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
