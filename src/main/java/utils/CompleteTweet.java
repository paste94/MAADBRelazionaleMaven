package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.stanford.nlp.simple.Sentence;
import org.apache.commons.collections4.IterableGet;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompleteTweet {

    private String tweet;
    private String lemma;
    private List<String> words;
    private int sentimentId;
    private MultiKeyMap<Serializable, Integer> hashTags; //https://www.techiedelight.com/implement-map-with-multiple-keys-multikeymap-java/
    //private MultiKeyMap<Serializable, Integer> emojis;
    //private MultiKeyMap<Serializable, Integer> emoticons;

    public CompleteTweet(String tweet, int sentimentId) {
        this.tweet = tweet;
        this.lemma = tweet;
        slang();
        this.words = new ArrayList<>(Arrays.asList(tweet.split(" ")));
        this.sentimentId = sentimentId;
        this.hashTags = calculateHashTags();
        try {
            removeEmojis();
            removeEmoticons();
            //this.emojis = calculateEmojis();
            //this.emoticons = calculateEmoticons();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void slang() {
        //tratta slang words
        String jsonString = "";
        try {
            jsonString = new String (Files.readAllBytes(Paths.get("./src/main/resources/slang/slang.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> slang =new Gson().fromJson( jsonString, new TypeToken<HashMap<String, String>>() {}.getType());
        slang.forEach((k,v)->{
            Pattern pattern = Pattern.compile("\\b"+k+"\\b");
            Matcher matcher = pattern.matcher(lemma);
            lemma = matcher.replaceAll(v);
        });
    }

    public int getSentimentId() {
        return sentimentId;
    }

    @Override
    public String toString() {
        return "CompleteTweet{" +
                "tweet='" + tweet + '\'' +
                ", sentimentId=" + sentimentId +
                '}';
    }

    private MultiKeyMap<Serializable, Integer> calculateHashTags(){
        //  creates an ordered map
        MultiKeyMap<Serializable, Integer> multiKeyMap = MultiKeyMap.multiKeyMap(new LinkedMap<>());

        for(String w:words){
            if(w.length()>1 && w.startsWith("#")){
                Integer oldValue = multiKeyMap.get(w, this.sentimentId);
                if(oldValue == null){
                    multiKeyMap.put(w, this.sentimentId, 1);
                }else {
                    multiKeyMap.put(w, sentimentId, oldValue+1);
                }

            }
        }
        lemma = lemma.replaceAll("#\\w+", "");


        return multiKeyMap;
    }

    private void removeEmojis() throws UnsupportedEncodingException {
        byte[] utf8Bytes = this.tweet.getBytes(StandardCharsets.UTF_8);
        String utf8tweet = new String(utf8Bytes, StandardCharsets.UTF_8);

        Pattern unicodeOutliers = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);

        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);
        while (unicodeOutlierMatcher.find()) {
            this.lemma = this.lemma.replaceAll(unicodeOutlierMatcher.group(), "");
        }
    }

    private void removeEmoticons(){
        String POSEMOTICONS_REGEX = ":\\\\|dx|:d|xd|o.o|:p|<3|B-\\)|:\\)|:-\\)|:'\\)|:'-\\)|:D|:-D|:\\'-\\)|:\\'-\\)|:o\\)|:\\]|:3|:c\\)|:>|=\\]|8\\)|=\\)|:\\}|:\\^\\)|8-D|8D|x-D|xD|X-D|XD|=-D|=D|=-3|=3|B\\^D|:\\*|:\\^\\*|\\( \\'\\}\\{\\' \\)|\\^\\^|\\(\\^_\\^\\)|\\^-\\^|\\^.\\^|\\^3\\^|\\^L\\^|d:";
        String NEGEMOTICONS_REGEX = ":\\(|:-\\(|:'\\(|:'-\\(|>:\\[|:-c|:c|:-<|:<|:-\\[|:\\[|:\\{|:\\'-\\(|_\\(|:\\'\\[|='\\(|' \\[|='\\[|:'-<|:' <|:'<|='<|=' <|T_T|T.T|\\(T_T\\)|y_y|y.y|\\(Y_Y\\)|;-;|;_;|;.;|:_:|o .__. o|.-.|>_<|>.<";
        this.lemma = this.lemma.replaceAll(POSEMOTICONS_REGEX, "");
        this.lemma = this.lemma.replaceAll(NEGEMOTICONS_REGEX, "");
    }
    /*
    private MultiKeyMap<Serializable, Integer> calculateEmojis() throws UnsupportedEncodingException {
        MultiKeyMap<Serializable, Integer> multiKeyMap = MultiKeyMap.multiKeyMap(new LinkedMap<>());

        String[] words = this.tweet.split(" ");


        byte[] utf8Bytes = this.tweet.getBytes("UTF-8");
        String utf8tweet = new String(utf8Bytes, "UTF-8");

        Pattern unicodeOutliers = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);

        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);

        while (unicodeOutlierMatcher.find()) {
            String emoji = unicodeOutlierMatcher.group();
            Integer oldValue = multiKeyMap.get(unicodeOutlierMatcher.group(), this.sentimentId);
            if(oldValue == null){
                multiKeyMap.put(emoji, this.sentimentId, 1);
            }else {
                multiKeyMap.put(emoji, this.sentimentId, oldValue + 1);
            }
            this.lemma = this.lemma.replaceAll(emoji, "");
        }
        return multiKeyMap;

    }

     */
    /*
    private MultiKeyMap<Serializable, Integer> calculateEmoticons(){
        MultiKeyMap<Serializable, Integer> multiKeyMap = MultiKeyMap.multiKeyMap(new LinkedMap<>());

        String POSEMOTICONS_REGEX = ":\\\\|dx|:d|xd|o.o|:p|<3|B-\\)|:\\)|:-\\)|:'\\)|:'-\\)|:D|:-D|:\\'-\\)|:\\'-\\)|:o\\)|:\\]|:3|:c\\)|:>|=\\]|8\\)|=\\)|:\\}|:\\^\\)|8-D|8D|x-D|xD|X-D|XD|=-D|=D|=-3|=3|B\\^D|:\\*|:\\^\\*|\\( \\'\\}\\{\\' \\)|\\^\\^|\\(\\^_\\^\\)|\\^-\\^|\\^.\\^|\\^3\\^|\\^L\\^|d:";
        Pattern emoticonsPattern = Pattern.compile(POSEMOTICONS_REGEX);
        Matcher mat = emoticonsPattern.matcher(this.tweet);
        while (mat.find()) {
            String emoticon = mat.group();
            Integer oldValue = multiKeyMap.get(mat.group(), this.sentimentId);
            if(oldValue == null){
                multiKeyMap.put(emoticon, this.sentimentId, 1);
            }else {
                multiKeyMap.put(emoticon, this.sentimentId, oldValue + 1);
            }
        }

        String NEGEMOTICONS_REGEX = ":\\(|:-\\(|:'\\(|:'-\\(|>:\\[|:-c|:c|:-<|:<|:-\\[|:\\[|:\\{|:\\'-\\(|_\\(|:\\'\\[|='\\(|' \\[|='\\[|:'-<|:' <|:'<|='<|=' <|T_T|T.T|\\(T_T\\)|y_y|y.y|\\(Y_Y\\)|;-;|;_;|;.;|:_:|o .__. o|.-.|>_<|>.<";
        emoticonsPattern = Pattern.compile(NEGEMOTICONS_REGEX);
        mat = emoticonsPattern.matcher(this.tweet);
        while (mat.find()) {
            String emoticon = mat.group();
            Integer oldValue = multiKeyMap.get(mat.group(), this.sentimentId);
            if(oldValue == null){
                multiKeyMap.put(emoticon, this.sentimentId, 1);
            }else {
                multiKeyMap.put(emoticon, this.sentimentId, oldValue + 1);
            }
        }
        this.lemma = this.lemma.replaceAll(POSEMOTICONS_REGEX, "");
        this.lemma = this.lemma.replaceAll(NEGEMOTICONS_REGEX, "");
        return multiKeyMap;
    }


     */
    public List<String> getLemmaList(){
        Sentence sentence;
        lemma = lemma.toLowerCase();
        try{
            sentence = new Sentence(lemma);
        }catch (IllegalStateException e){
            return new ArrayList<>();
        }

        return sentence.lemmas();
    }


    public MultiKeyMap<Serializable, Integer> getHashTags() {
        return this.hashTags;
    }
}
