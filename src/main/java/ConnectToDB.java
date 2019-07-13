import org.apache.commons.collections4.map.MultiKeyMap;
import utils.LexicalResource;

import java.util.List;
import java.util.Map;

public interface ConnectToDB {
    void saveLexicalResource(List<LexicalResource> words);
    void saveHashtags(MultiKeyMap hashTags);
    void saveTweets(Map<String, Long> freq, int sent);
    void printWordClouds(int threshold, boolean hashtag);
    void printWordCloudsWithLexRes(int threshold, boolean hashtag);
    void addLexRes(int threshold, boolean hashtag);
    void deleteTable(String tableName);
}
