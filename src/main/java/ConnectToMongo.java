import com.kennycason.kumo.WordFrequency;
import com.mongodb.*;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.MapReduceAction;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.bson.conversions.Bson;
import utils.LexicalResource;
import utils.SentimentEnum;

import javax.print.Doc;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public class ConnectToMongo implements ConnectToDB{
    String shards = "mongodb://localhost:27000";



    private Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    /*
    {   lemma: "frowing",
        sentiment: {
            sentimentName: "Anger",
            sentimentID: 1
        },
        EmoSN: 0,
        NRC: 1,
        sentisense: 0
    },...
     */
    @Override
    public void saveLexicalResource(List<LexicalResource> words) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        MongoCollection collection = database.getCollection("lexicalresource");

        Map<Integer, String> sentMap = SentimentEnum.getMap();

        sentMap.forEach((num, sent)-> {
            List<Document> documents = new ArrayList<>();
            List<LexicalResource> lexRes = words.stream().filter(e-> e.getSentimentIdFk().equals(num)).collect(Collectors.toList());

            lexRes.forEach(w->{
                documents.add(new Document("lemma", w.getWord())
                        .append("sentiment", new Document("sentName", sent).append("sentID", num))
                        .append("EmoSN", w.getEmosnFreq())
                        .append("NRC", w.getNrcFreq())
                        .append("sentisense", w.getSentisenseFreq())
                        .append("type", "word"));
            });
            collection.insertMany(documents);
        });

        /*
        sentMap.forEach((num, sent)-> {
            List<Document> documents = new ArrayList<>();
            List<LexicalResource> lexRes = words.stream().filter(e-> e.getSentimentIdFk().equals(num)).collect(Collectors.toList());

            lexRes.forEach(w->{
                documents.add(new Document("lemma", w.getWord())
                                    .append("sentiment", new Document("sentName", sent).append("sentID", num))
                                    .append("EmoSN", w.getEmosnFreq())
                                    .append("NRC", w.getNrcFreq())
                                    .append("sentisense", w.getSentisenseFreq())
                                    .append("type", "word"));
            });
            collection.insertMany(documents);
        });

         */
        mongoClient.close();
    }

    public void mapReduceTweet(List<String> tweets){

    }

    @Override
    public void addLexRes(List<WordFrequency> listOfWords, int sentiment) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        MongoCollection collection = database.getCollection("lexicalresource");
        String sentimentName = SentimentEnum.idToString(sentiment);

        listOfWords.forEach(w->{

            //{lemma:"prison", "sentiment.sentID": 1}
            Document find = new Document("lemma", w.getWord()).append("sentiment.sentID", sentiment).append("sentiment.sentimentName", sentimentName);

            //{$set:{frequence: 4}}
            Document update = new Document("$set", new Document("type", "word"));

            collection.updateOne(find, update, new UpdateOptions().upsert(true));
        });

        mongoClient.close();
    }

    public void addLexRes(int threshold, boolean hashtag) {

    }

    @Override
    public void deleteTable(String tableName) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        MongoCollection collection = database.getCollection(tableName);
        collection.deleteMany(new BasicDBObject());
        System.out.println("DROPPED");
        mongoClient.close();
    }

    @Override
    public void printWordClouds(int sentiment, String fileName) {

    }


    public void mapReduce(){

        final String mapFunction =  "function(){" +
                                    "    emit(this.sentiment.sentimentID, {'lemma': this.lemma, 'frequence':this.frequence});" +
                                    "}";

        final String reduceFunction =   "function(k,val){" +
                                        "  ret = [];" +
                                        "  val.forEach(v=>{" +
                                        "    ret.push(val);" +
                                        "  });" +
                                        "  return {'words':val};" +
                                        "}";



        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        MongoCollection collection = database.getCollection("lexicalresource");
        System.out.println(1);

        MapReduceIterable iterable = collection.mapReduce(mapFunction, reduceFunction).collectionName("reduced").action(MapReduceAction.REPLACE);
        iterable.toCollection();
        System.out.println(2);
        mongoClient.close();
    }

    /*
        {
            emoji: ":)"
            sentiment: {..}
            frequence: 2
        }
     */
    @Override
    public void addEmojis(List<String> emojis, Integer sentiment) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        Map<String, Long> freq = emojis.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        String sentimentName = SentimentEnum.idToString(sentiment);
        MongoCollection collection = database.getCollection("lexicalresource");

        freq.forEach((w,f)->{
            Document find = new Document("lemma", w).append("sentiment.sentimentName", sentimentName).append("sentiment.sentimentID", sentiment);

            Document update = new Document("$set", new Document("type", "emoji"));

            collection.updateOne(find, update, new UpdateOptions().upsert(true));
        });
        mongoClient.close();
    }

    @Override
    public void addEmoticon(List<String> emoticons, Integer sentiment) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        Map<String, Long> freq = emoticons.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        String sentimentName = SentimentEnum.idToString(sentiment);
        MongoCollection collection = database.getCollection("lexicalresource");


        freq.forEach((w,f)->{
            Document find = new Document("lemma", w).append("sentiment.sentimentName", sentimentName).append("sentiment.sentimentID", sentiment);

            Document update = new Document("$set", new Document("type", "emoticon"));

            collection.updateOne(find, update, new UpdateOptions().upsert(true));
        });
        mongoClient.close();

    }

    @Override
    public void addHashtags(List<String> hashtags, Integer sentiment) {
        MongoClient mongoClient = MongoClients.create(shards);
        MongoDatabase database = mongoClient.getDatabase("ProgettoMAADB");
        Map<String, Long> freq = hashtags.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        String sentimentName = SentimentEnum.idToString(sentiment);
        MongoCollection collection = database.getCollection("lexicalresource");

        freq.forEach((w,f)->{
            Document find = new Document("lemma", w).append("sentiment.sentimentName", sentimentName).append("sentiment.sentimentID", sentiment);

            Document update = new Document("$set", new Document("type", "hashtag"));

            collection.updateOne(find, update, new UpdateOptions().upsert(true));
        });
        mongoClient.close();
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
