import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteUnderscores {
    public static void main(String[] args) {
        try (Stream<Path> walk = Files.walk(Paths.get("./src/main/resources/risorse_lessicali"))) {

            //Scorre tutti i file nella cartella
            List<String> files = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            files.forEach(file ->{
                try {
                    List<String> allLines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
                    List<String> removed = allLines.stream()
                            .filter(s -> !s.contains("_"))
                            .collect(Collectors.toList());
                    Files.write(Paths.get(file), removed, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Underscores eliminati correttamente");
    }
}
