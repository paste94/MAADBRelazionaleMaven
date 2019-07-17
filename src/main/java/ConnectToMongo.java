import com.kennycason.kumo.WordFrequency;
import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.*;
import org.bson.Document;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.bson.conversions.Bson;
import utils.LexicalResource;
import utils.SentimentEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConnectToMongo implements ConnectToDB{
    private MongoClient mongoClient = MongoClients.create();
    private MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");

    public static final String mapFunction = "function() {  " +
                                                 "emit({ " +
                                                     "'sentiment': this.sentiment , " +
                                                     "'word': this.word " +
                                                 "}, 1); " +
                                             "}";

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
            Document document = new Document("sentiment", sent).append("id", num);
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
    public void addLexRes(List<WordFrequency> listOfWords, int sentiment) {

    }

    public void addLexRes(int threshold, boolean hashtag) {

    }

    @Override
    public void deleteTable(String tableName) {
        MongoCollection collection = database.getCollection(tableName);
        collection.drop();
        System.out.println("DROPPED");
    }

    @Override
    public void printWordClouds(int sentiment, String fileName) {

    }

    @Override
    public void addEmojis(List<String> emojis, Integer id) {

    }

    @Override
    public void addEmoticon(List<String> emoticons, Integer id) {

    }

    @Override
    public void addHashtags(List<String> hashtags, Integer id) {

    }

    @Override
    public void printCloud(int id, String cloudType) {

    }
/*
    public void executeMapReduce(MongoCollection outputCollection){
        System.out.println("MONGODB: Executing mapreduce for collection tweet");
		DB db = new Mongo(host, port).getDB(database.getName());
		MapReduceCommand command = new MapReduceCommand(db.getCollection(inputCollection.getMongoName()), Constants.mapFunction, Constants.reduceFunction,outputCollection.getMongoName(), MapReduceCommand.OutputType.REPLACE, null);
		db.getCollection(inputCollection.getMongoName()).mapReduce(command);
        MongoCollection collection = database.getCollection("tweet")

        Bson command = new Document()
                .append("mapreduce", "tweet")
                .append("map", Constants.mapFunction)
                .append("reduce", Constants.reduceFunction)
                .append("out", new Document()
                        .append("merge", outputCollection.getMongoName()))
						/*.append("sharded", false)
						.append("nonAtomic", false);
        database.runCommand(command);
    }


 */


}
