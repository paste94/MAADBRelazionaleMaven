import com.kennycason.kumo.WordFrequency;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.LexicalResource;

import java.util.List;
import java.util.Map;

public interface ConnectToDB {
    void saveLexicalResource(List<LexicalResource> words);
    void addLexRes(List<WordFrequency> listOfWords, int sentiment);
    void deleteTable(String tableName);
    void printWordClouds(int sentiment, String fileName);
    void addEmojis(List<String> emojis, Integer id);
    void addEmoticon(List<String> emoticons, Integer id);
    void addHashtags(List<String> hashtags, Integer id);
    void printCloud(int id, String cloudType);
}
