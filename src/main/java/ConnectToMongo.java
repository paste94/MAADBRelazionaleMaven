import com.mongodb.BasicDBObject;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.LexicalResource;
import utils.SentimentEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConnectToMongo implements ConnectToDB{
    private MongoClient mongoClient = MongoClients.create();
    private MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");

    /*
    {   sentiment: "Anger"
        id: 1
        lexreslist: [
            {   lemma: "abandoned"
                resources: { mEmoSN: 1, SentiSense: 1, NRC: 1 }
            },
            {   lemma: .. },
            ...
        ]
    }
     */

    @Override
    public void saveLexicalResource(List<LexicalResource> words) {
        MongoCollection collection = database.getCollection("lexicalresource");
        Map<Integer, String> sentMap = SentimentEnum.getMap();
        sentMap.forEach((num, sent)->{
            List<LexicalResource> lexRes = words.stream().filter(e-> e.getSentimentIdFk().equals(num)).collect(Collectors.toList());
            Document document = new Document("lemma", sent).append("id", num);
            List<Document> wordsList = new ArrayList<>();
            lexRes.forEach(w->{
                wordsList.add(new Document("lemma", w.getWord())
                                   .append("resources",
                                           new Document("EmoSN", w.getEmosnFreq())
                                                .append("NRC", w.getNrcFreq())
                                                .append("sentisense", w.getSentisenseFreq())));
            });
            document.append("lexResList", wordsList);
            collection.insertOne(document);
        });
    }

    @Override
    public void saveHashtags(MultiKeyMap hashTags) {

    }

    @Override
    public void saveTweets(Map<String, Long> freq, int sent) {

    }

    @Override
    public void printWordClouds(int threshold, boolean hashtag) {

    }

    @Override
    public void printWordCloudsWithLexRes(int threshold, boolean hashtag) {

    }

    @Override
    public void addLexRes(int threshold, boolean hashtag) {

    }

    @Override
    public void deleteTable(String tableName) {

    }
}
