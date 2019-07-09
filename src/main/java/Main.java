import utils.CompleteTweet;
import utils.LexicalResource;
import utils.SentimentEnum;
import utils.Tweet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        /*
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres")){
                System.out.println("Java JDBC PostgreSQL Example");
                System.out.println("Connected to PostgreSQL database!");
                Statement statement = connection.createStatement();
                System.out.println("Reading car records...");
                ResultSet resultSet = statement.executeQuery("SELECT * FROM words");
                printResultSet(resultSet);

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        */
        printMenu();

    }

    public static void printMenu(){
        Scanner in = new Scanner(System.in);

        // print menu
        System.out.println("1. Inizializza tabella lexicalresource");
        System.out.println("2. Inizializza tabella tweets");
        System.out.println("0. Termina");

        // handle user commands
        boolean quit = false;
        String menuItem;

        do {
            System.out.print("Scelta: ");
            menuItem = in.next();
            switch (menuItem) {
                case "1":
                    lexicalResources();
                    break;
                case "2":
                    removeUserPwd();
                    break;
                case "3":
                    System.out.println("You've chosen item #3");
                    // do something...
                    break;
                case "4":
                    System.out.println("You've chosen item #4");
                    // do something...
                    break;
                case "5":
                    System.out.println("You've chosen item #5");
                    // do something...
                    break;
                case "0":
                    quit = true;
                    break;
                default:
                    System.out.println("Selezione non valida");
            }
        } while (!quit);
        System.out.println("Terminato");

    }

    static void lexicalResources(){
        SentimentEnum sentimentEnum;
        WordsFrequenceCalculator wordsFrequenceCalculator = new WordsFrequenceCalculator();
        ArrayList<LexicalResource> words = new ArrayList<>();

        //Calcolo le frequenze delle parole
        SentimentEnum.getMap().forEach((id, name) -> {
            try (Stream<Path> walk = Files.walk(Paths.get("./src/main/resources/risorse_lessicali/" + name))) {
                List<String> files = walk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());
                //System.out.println(wordsFrequenceCalculator.countFrequences(id, files));
                words.addAll(wordsFrequenceCalculator.countFrequences(id, files));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        //Salva nel DB

        ConnectToMAADB connectToMAADB = new ConnectToMAADB();
        try {
            connectToMAADB.saveToDB(words);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Resettare? [s,N]");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.next();
            if(str.equals("s")){
                try {
                    connectToMAADB.deleteTable("LexicalResource");
                    connectToMAADB.saveToDB(words);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                System.out.println("Operazione annullata\n---------------------------------------\n\n");
                return;
            }
        }

        System.out.println("Tabella lexicalresources inizializzata correttamente\n---------------------------------------\n\n");


    }

    static void removeUserPwd(){
        //Step1 - rimozione degli username e password dall'elenco dei tweet
        List<CompleteTweet> completeTweets = new ArrayList<>();
        TweetManager tweetManager = new TweetManager();
        SentimentEnum.getFileMap().forEach((id, fileName) -> {
            try {
                completeTweets.addAll(tweetManager.getTweetsWithoutUserPwd(id, "./src/main/resources/tweet/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    static void hashtag(){

    }
}
