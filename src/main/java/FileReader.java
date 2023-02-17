import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileReader {

  public static byte[] getFile (String pathString) {
    Path path = Paths.get(pathString);
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return bytes;
  }

  public static String getBase64ofFile(String pathString) {
    byte[] file = getFile(pathString);
    return Base64.getEncoder().encodeToString(file);
  }

  public static void main(String[] args) {
    String base64ofFile = FileReader.getBase64ofFile("files/Happy-patrick-star.jpg");
  }
}
