import utils.LexicalResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class WordsFrequenceCalculator {
    private String[] resources = {"EmoSN", "NRC", "sentisense"};

    public List<LexicalResource> countFrequences(int sentimentId, List<String> paths) throws IOException {
        HashMap<String, LexicalResource> lexicalResources = new HashMap<>();
        ArrayList<String> listOfreads = new ArrayList<>();

        //Per ogni file
        for(String path : paths){
            //Leggi le righe del file
            List<String> allLines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

            //Se la risorsa ha il tipo EmoSN
            if(Paths.get(path).getFileName().toString().startsWith("EmoSN")){
                //Per ogni riga (parola) letta
                allLines.forEach(line -> {
                    //Prendi il valore della mappa
                    LexicalResource lr = lexicalResources.get(line);
                    if(lr == null){ //Se non esiste quel valore crealo
                        lexicalResources.put(line, new LexicalResource(line, sentimentId, "EmoSN"));
                    }else{ //Altrimenti aumenta solo il contatore delle frequenze
                        lr.addFreq("EmoSN");
                    }
                });
            }else if(Paths.get(path).getFileName().toString().startsWith("NRC")){
                allLines.forEach(line -> {
                    LexicalResource lr = lexicalResources.get(line);
                    if(lr == null){
                        lexicalResources.put(line, new LexicalResource(line, sentimentId, "NRC"));
                    }else{
                        lr.addFreq("NRC");
                    }
                });
            }else if(Paths.get(path).getFileName().toString().startsWith("sentisense")){
                allLines.forEach(line -> {
                    LexicalResource lr = lexicalResources.get(line);
                    if(lr == null){
                        lexicalResources.put(line, new LexicalResource(line, sentimentId, "sentisense"));
                    }else{
                        lr.addFreq("sentisense");
                    }
                });
            }
        }
        System.out.println(lexicalResources.get("perfection"));
        return new ArrayList<>(lexicalResources.values());
    }
}
