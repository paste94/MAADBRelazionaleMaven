import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import edu.stanford.nlp.simple.Sentence;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.CompleteTweet;
import utils.LexicalResource;
import utils.SentimentEnum;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static ConnectToRelational connectToMAADB = new ConnectToRelational();
    private static List<CompleteTweet> tweets = new ArrayList<>();
    private static final String EMOTICONS = "B-\\)|:\\)|:-\\)|:'\\)|:'-\\)|:D|:-D|:\\'-\\)|:\\'-\\)|:o\\)|:\\]|:3|:c\\)|:>|=\\]|8\\)|=\\)|:\\}|:\\^\\)|8-D|8D|x-D|xD|X-D|XD|=-D|=D|=-3|=3|B\\^D|:\\*|:\\^\\*|\\( \\'\\}\\{\\' \\)|\\^\\^|\\(\\^_\\^\\)|\\^-\\^|\\^.\\^|\\^3\\^|\\^L\\^|d:|:\\(|:-\\(|:'\\(|:'-\\(|>:\\[|:-c|:c|:-<|:<|:-\\[|:\\[|:\\{|:\\'-\\(|_\\(|:\\'\\[|='\\(|' \\[|='\\[|:'-<|:' <|:'<|='<|=' <|T_T|T.T|\\(T_T\\)|y_y|y.y|\\(Y_Y\\)|;-;|;_;|;.;|:_:|o .__. o|.-.";
    private static final List<String> STOP_WORDS_ARRAY = new ArrayList<>(Arrays.asList("<<",">>","=","_","let","go","0","1","2","3","4","5","6","7","8","9","just","like","now","get","know","will","one","username","url","o","I", "\\", "/", "!!", "?!", "??", "!?", "`", "``", "''", "-lrb-", "-rrb-", "-lsb-", "-rsb-", ",", ".", ":", ";", "\"", "'", "?", "<", ">", "{", "}", "[", "]", "+", "-", "(", ")", "&", "%", "$", "@", "!", "^", "#", "*", "..", "...", "'ll", "'s", "'m", "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "###", "return", "arent", "cant", "couldnt", "didnt", "doesnt", "dont", "hadnt", "hasnt", "havent", "hes", "heres", "hows", "im", "isnt", "its", "lets", "mustnt", "shant", "shes", "shouldnt", "thats", "theres", "theyll", "theyre", "theyve", "wasnt", "were", "werent", "whats", "whens", "wheres", "whos", "whys", "wont", "wouldnt", "youd", "youll", "youre", "youve"));


    public static void main(String[] args) {
        printMenuRelational();
    }
/*
    private static int chooseDB(){
        Scanner in = new Scanner(System.in);
        System.out.println("Selezionare il tipo di database: ");
        System.out.println("1. Relazionale Postgres");
        System.out.println("2. MongoDB");

        System.out.print("Scelta: ");
        String menuItem = in.next();

        boolean next = false;
        switch (menuItem){
            case "1":
                connectToMAADB = new ConnectToRelational();
                break;
            case "2":
                connectToMAADB = new ConnectToMongo();
        }
        return Integer.parseInt(menuItem);

    }
*/
    private static void printMenuRelational(){
        Scanner in = new Scanner(System.in);

        // print menu
        System.out.println("1. Inizializza tabella lexicalresource");
        System.out.println("2. Calcola le frequenze nei tweet (parole, hashtag, emoji ed emoticon)");
        System.out.println("3. Genera le word clouds");
        System.out.println("4. Genera le hashtag clouds");
        System.out.println("5. Genera le emoticon clouds");
        //System.out.println("6. Genera le emoticon clouds");
        System.out.println("0. Termina");

        // handle user commands
        boolean quit = false;
        String menuItem;

        do {
            System.out.print("Scelta: ");
            menuItem = in.next();
            switch (menuItem) {
                case "1":
                    //Inizializza tabella lexicalresource
                    lexicalResources(); //12888
                    break;
                case "2":
                    //Calcola le frequenze dei tweet
                    calculateFrequences();
                    break;
                    //Ottieni word clouds dai tweet
                case "3":
                    //Ottieni word clouds dal DB
                    calculateWordClouds();
                    break;
                case "4":
                    //Ottieni word clouds con lexicalresource
                    calculateClouds("hashtag");
                    break;
                case "5":
                    //Aggiungi nuove risorse
                    calculateClouds("emoticon");
                    break;
                case "6":
                    //Ottieni hashtag clouds SENZA considerare lexicalresource
                    calculateClouds("emoticon");
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
/*
    private static void printMenuMongo(){
        Scanner in = new Scanner(System.in);

        // print menu
        System.out.println("1. Inizializza tabella lexicalresource");
        System.out.println("2. Calcola le frequenze nei tweet (parole, hashtag, emoji ed emoticon)");
        System.out.println("3. Calcola Map Reduce");
        System.out.println("4. Genera word clouds");
        //System.out.println("6. Genera le emoticon clouds");
        System.out.println("0. Termina");

        // handle user commands
        boolean quit = false;
        String menuItem;

        do {
            System.out.print("Scelta: ");
            menuItem = in.next();
            switch (menuItem) {
                case "1":
                    //Inizializza tabella lexicalresource
                    lexicalResources(); //12888
                    break;
                case "2":
                    //Calcola le frequenze dei tweet
                    calculateFrequences();
                    break;
                //Ottieni word clouds dai tweet
                case "3":
                    //Ottieni word clouds dal DB
                    calculateMapReduce();
                    break;
                case "4":
                    //Ottieni word clouds con lexicalresource
                    calculateClouds("hashtag");
                    break;
                case "5":
                    //Aggiungi nuove risorse
                    calculateClouds("emoticon");
                    break;
                case "6":
                    //Ottieni hashtag clouds SENZA considerare lexicalresource
                    calculateClouds("emoticon");
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
*/
/*
    private static void calculateMapReduce() {
            connectToMAADB.mapReduce();
    }

 */

    private static void lexicalResources(){
        WordsFrequenceCalculator wordsFrequenceCalculator = new WordsFrequenceCalculator();
        ArrayList<LexicalResource> words = new ArrayList<>();

        //Calcolo le frequenze delle parole
        SentimentEnum.getMap().forEach((id, name) -> {
            try (Stream<Path> walk = Files.walk(Paths.get("./src/main/resources/risorse_lessicali/" + name))) {
                List<String> files = walk.filter(Files::isRegularFile)
                        .map(Path::toString).collect(Collectors.toList());
                words.addAll(wordsFrequenceCalculator.countFrequences(id, files));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        System.out.println("1) Lettura da file eseguita");

        //Salva nel DB
        connectToMAADB.deleteTable("lexicalresource");
        connectToMAADB.saveLexicalResource(words);
        System.out.println("2) Salvataggio nel DB terminato\n");
    }

    private static void calculateWordClouds() {
        for (Map.Entry<Integer, String> entry : SentimentEnum.getFileMap().entrySet()) {
            Integer id = entry.getKey();
            String fileName = entry.getValue();
            connectToMAADB.printWordClouds(id, fileName);
        }
    }

    private static void calculateClouds(String cloudType){
        for (Map.Entry<Integer, String> entry : SentimentEnum.getFileMap().entrySet()) {
            Integer id = entry.getKey();
            String fileName = entry.getValue();
            connectToMAADB.printCloud(id, cloudType);
        }
    }

    private static void calculateFrequences(){
            String jsonString = "";
            try {
                jsonString = new String (Files.readAllBytes(Paths.get("./src/main/resources/slang/slang.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String, String> slang =new Gson().fromJson( jsonString, new TypeToken<HashMap<String, String>>() {}.getType());

            //Leggi i tweet dai file e genera la lista di oggetti CompleteTweet
            for (Map.Entry<Integer, String> entry : SentimentEnum.getFileMap().entrySet()) {
                Integer id = entry.getKey();
                String fileName = entry.getValue();
                List<String> hashtags = new ArrayList<>();
                List<String> emojis = new ArrayList<>();
                List<String> emoticons = new ArrayList<>();

                System.out.println("Lavoro sul file " + fileName);
                try {
                    List<String> allLines = Files.readAllLines(Paths.get("./src/main/resources/tweet/" + fileName), StandardCharsets.UTF_8);

                    //rimuovi username, url, hashtags e rimpiazza slang words
                    for (int i = 0; i < allLines.size(); i++) {

                        //Salva hashtags
                        String[] list = allLines.get(i).split(" ");
                        for(String s:list){
                            //Salva hashtag
                            if(s.length()>0 && s.startsWith("#")){
                                hashtags.add(s);
                            }

                            //Salva emoji
                            byte[] utf8Bytes = s.getBytes("UTF-8");
                            String utf8tweet = new String(utf8Bytes, "UTF-8");
                            Pattern unicodeOutliers = Pattern.compile(
                                    "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                                    Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
                            Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);
                            if (unicodeOutlierMatcher.find()) {
                                emojis.add(unicodeOutlierMatcher.group());
                            }

                            //Salva emoticons
                            Pattern emoticonsPattern = Pattern.compile(EMOTICONS);
                            Matcher mat = emoticonsPattern.matcher(s);
                            if (mat.find()) {
                                emoticons.add(mat.group());
                            }


                        }

                        //Rimuovi user, url, hashtag
                        String newString = allLines.get(i).replaceAll("\\bUSERNAME\\b|\\bURL\\b|#\\w+", "");

                        //Rimpazza slang
                        for (Map.Entry<String, String> e : slang.entrySet()) {
                            String k = e.getKey();
                            String v = e.getValue();
                            Pattern pattern = Pattern.compile("\\b" + k + "\\b");
                            Matcher matcher = pattern.matcher(newString);
                            newString = matcher.replaceAll(v);
                        }

                        //Lemmatizza la frase
                        newString = newString.toLowerCase();
                        try{
                            if(newString.replace(" ", "").length()>0) {
                                Sentence sentence = new Sentence(newString);
                                List<String> l = sentence.lemmas();
                                l = l.stream().distinct().collect(Collectors.toList());
                                newString = "";
                                for (String str : l) {
                                    newString = newString + str + " ";
                                }
                            }
                            //System.out.println(newString);
                        }catch (IllegalStateException e){
                            System.out.println("-"+newString+"-");
                            e.printStackTrace();
                        }

                        //Sostituisci la nuova riga con la vecchia
                        allLines.set(i, newString);
                    }
                    //Genera l'immagine
                    final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
                    frequencyAnalyzer.setWordFrequenciesToReturn(500);
                    frequencyAnalyzer.setStopWords(STOP_WORDS_ARRAY); //Rimuove le stop words
                    final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(allLines); //La load elimina già emoji ed emoticons di default
                    System.out.println("Terminato il calcolo, salvo nel DB");
                    System.out.println(wordFrequencies);
                    connectToMAADB.addLexRes(wordFrequencies, id);
                    connectToMAADB.addHashtags(hashtags, id);
                    connectToMAADB.addEmoticon(emoticons, id);
                    connectToMAADB.addEmojis(emojis, id);
                    System.out.println("Salvataggio nel DB riuscito");
                    System.out.println();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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




    }
}
