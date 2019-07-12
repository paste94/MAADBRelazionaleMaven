import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static ConnectToMAADB connectToMAADB = new ConnectToMAADB();
    static MultiKeyMap hashTags = MultiKeyMap.multiKeyMap(new LinkedMap<>());
    static Map<Integer, Set<String>> wordClouds; // Mappa [emozione]-->listaParole
    static List<CompleteTweet> tweets = new ArrayList<>();
    private static final ArrayList<String> STOP_WORDS_ARRAY = new ArrayList<>(Arrays.asList("go","0","1","2","3","4","5","6","7","8","9","get","know","will","one","username","url","o","I", "\\", "/", "!!", "?!", "??", "!?", "`", "``", "''", "-lrb-", "-rrb-", "-lsb-", "-rsb-", ",", ".", ":", ";", "\"", "'", "?", "<", ">", "{", "}", "[", "]", "+", "-", "(", ")", "&", "%", "$", "@", "!", "^", "#", "*", "..", "...", "'ll", "'s", "'m", "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "###", "return", "arent", "cant", "couldnt", "didnt", "doesnt", "dont", "hadnt", "hasnt", "havent", "hes", "heres", "hows", "im", "isnt", "its", "lets", "mustnt", "shant", "shes", "shouldnt", "thats", "theres", "theyll", "theyre", "theyve", "wasnt", "were", "werent", "whats", "whens", "wheres", "whos", "whys", "wont", "wouldnt", "youd", "youll", "youre", "youve"));


    public static void main(String[] args) {
        printMenu();
    }

    public static void printMenu(){
        Scanner in = new Scanner(System.in);

        // print menu
        System.out.println("1. Inizializza tabella lexicalresource");
        System.out.println("2. Inizializza tabella tweet");
        System.out.println("3. Ottieni word clouds SENZA considerare lexicalresource");
        System.out.println("4. Ottieni word clouds con lexicalresource");
        System.out.println("5. Aggiungi nuove risorse");
        System.out.println("9. Test");
        System.out.println("0. Termina");

        // handle user commands
        boolean quit = false;
        String menuItem;

        do {
            System.out.print("Scelta: ");
            menuItem = in.next();
            switch (menuItem) {
                case "1":
                    lexicalResources(); //12888
                    break;
                case "2":
                    processTweets();
                    break;
                case "3":
                    calculateWordClouds();
                    break;
                case "4":
                    calculateWordCloudsWithLexRes();
                    break;
                case "5":
                    addNewWords();
                    break;
                case "9":
                    test();
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
        try {
            connectToMAADB.deleteTable("lexicalResource");
            connectToMAADB.saveLexicalResource(words);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Tabella lexicalresources inizializzata correttamente\n---------------------------------------\n\n");


    }

    public static void processTweets(){

        //Leggi i tweet dai file e genera la lista di oggetti CompleteTweet
        SentimentEnum.getFileMap().forEach((id, fileName) -> {
            try {
                List<String> allLines = Files.readAllLines(Paths.get("./src/main/resources/tweet/" + fileName), StandardCharsets.UTF_8);
                List<CompleteTweet> completeTweets = allLines.stream().map(l -> new CompleteTweet(l, id)).collect(Collectors.toList());
                tweets.addAll(completeTweets);
                System.out.print(".");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("\n1) Lettura tweet eseguita con successo");

        //Elimina la vecchia versione della tabella
        try {
            connectToMAADB.deleteTable("tweet");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("2) Eliminata vecchia tabella");

        //Genera l'insieme degli hashtag
        /*
        tweets.forEach(t->{
            MapIterator it = t.getHashTags().mapIterator();

            while (it.hasNext()) {
                it.next();

                MultiKey mk = (MultiKey) it.getKey();
                Integer value = (Integer) it.getValue();

                if(hashTags.containsKey(mk.getKey(0), mk.getKey(1))){
                    hashTags.put(mk.getKey(0), mk.getKey(1), value + (Integer)hashTags.get(mk.getKey(0), mk.getKey(1)));
                }else {
                    hashTags.put(mk.getKey(0), mk.getKey(1), value);
                }
            }
        });
        System.out.println("2) Estrazione hashtag eseguita con successo");
         */

        //Per ogni emozione metti nella mappa delle frequenze le parole con le relatve frequenze
        Integer i;
        for(i = 1; i <= 8; i++){
            Integer finalI = i;
            //Seleziona solo le parole che sono del sentimento i
            List<CompleteTweet> tweet = tweets.stream().filter(e->e.getSentimentId() == finalI).collect(Collectors.toList());

            //Raggruppa tutti i lemmi dei tweet appartenenti allo stesso sentimento
            List<String> lemmas = new ArrayList<>();
            tweet.forEach(t->lemmas.addAll(t.getLemmaList()));
            try {
                lemmas.removeAll(STOP_WORDS_ARRAY);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            //Salva in una mappa <parola, frequenza> le occorrenze di ogni parola dei lemmi
            Map<String, Long> frequences = lemmas.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            //Carica sul DB i dati
            try {
                connectToMAADB.saveTweets(frequences, i);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static ResultSet calculateWordClouds() {
        ResultSet resultSet = null;
        try {
            resultSet = connectToMAADB.getWordClouds(1000);
            connectToMAADB.printResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    private static ResultSet calculateWordCloudsWithLexRes() {
        ResultSet resultSet = null;
        try {
            resultSet = connectToMAADB.getWordCloudsWithLexRes(1000);
            connectToMAADB.printResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    private static void addNewWords(){
        ResultSet res = calculateWordClouds();
        ResultSet resWithLexRes = calculateWordCloudsWithLexRes();

        Map<String, Integer> resMap = new HashMap<>();

    }

    private static void test(){

        List<CompleteTweet> tweets = new ArrayList<>();

        tweets.add(new CompleteTweet("a i , wish b4n cya iow , #ciao #what i could go drive #stocazzo #ciao #ciao a  myself to wingstop but noo i a have to stay and babysit ? ? ? . ; : \uD83D\uDE12 #fuck #dick a a", 8));
        tweets.add(new CompleteTweet("let [ ioioio ] me find out USERNAME back on twitter ii*cant*deal \uD83D\uDE3E ", 1));
        tweets.add(new CompleteTweet("my phone is acting stupid ! ! \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 ", 8));
        tweets.add(new CompleteTweet("i'm getting shapy again \uD83D\uDE11 \uD83D\uDE10 \uD83D\uDE15 \uD83D\uDE14 \uD83D\uDE1E \uD83D\uDE23 \uD83D\uDE16 \uD83D\uDE29\uD83D\uDE2B ", 5));
        tweets.add(new CompleteTweet("USERNAME lmfao ! yes , and that's the problem . i need my some ride or die people . >.< ", 6));
        tweets.add(new CompleteTweet("“USERNAME poor USERNAME lol URL who got the double wammy now ! ! ! lmao >_< ", 7));
        tweets.add(new CompleteTweet("USERNAME rawr *kisses you and grabs your butt* mine ! ! ! >_< ", 8));
        tweets.add(new CompleteTweet("I am very \\ \" fool \" , I don't like do things and I love not to have had the truth ! $ / / ( pork ) hidden / really my intention", 9));

        tweets.forEach(t-> System.out.println(t.getLemmaList()));



    }
}
