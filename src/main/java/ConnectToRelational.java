import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.LexicalResource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ConnectToRelational implements ConnectToDB{

    private final String url = "jdbc:postgresql://localhost:5432/MAADB";
    private final String user = "postgres";
    private final String password = "postgres";


    private void printResultSet(ResultSet resultSet){
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            for(int j = 1; j <= columnsNumber; j++){
                System.out.printf("%-20.20s", rsmd.getColumnName(j));
            }
            System.out.println();
            for(int j = 1; j <= columnsNumber; j++){
                System.out.print("--------------------");
            }
            System.out.println();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.printf("%-20.20s", resultSet.getString(i) );
                }
                System.out.println();
            }
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void saveLexicalResource(List<LexicalResource> words) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            connection.close();

            //Salva i dati nel DB
            for (LexicalResource w : words) {
                statement.executeUpdate("INSERT INTO LEXICALRESOURCE(WORD,SENTIMENT_FK,EMOSN,NRC,SENTISENSE) VALUES ('" + w.getWord() + "',"+ w.getSentimentIdFk() +","+w.getEmosnFreq()+","+w.getNrcFreq()+","+w.getSentisenseFreq()+") ");
            }

        } catch (SQLException e) {
            e.getMessage();
        }
    }

    @Override
    public void saveHashtags(MultiKeyMap hashTags){
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            connection.close();

            MapIterator it = hashTags.mapIterator();

            while (it.hasNext()){
                it.next();
                MultiKey mk = (MultiKey) it.getKey();
                Integer value = (Integer) it.getValue();
                    statement.executeUpdate("INSERT INTO tweet(WORD,SENTIMENT,FREQUENCE,HASHTAG) VALUES ('" + mk.getKey(0) + "',"+ mk.getKey(1) +","+ value +", true) ");

            }
            System.out.println("Finito di salvare elementi hashtags");
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    @Override
    public void saveTweets(Map<String, Long> freq, int sent) {
        try {
            System.out.println("Salvo elementi " + sent);
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            connection.close();

            freq.forEach((word, frequence)->{
                try {
                    statement.executeUpdate("INSERT INTO tweet(WORD,SENTIMENT,FREQUENCE,HASHTAG) VALUES ('" + word + "',"+ sent +","+ frequence +", false) ");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            });
            System.out.println("Finito di salvare elementi " + sent);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void printWordClouds(int threshold, boolean hashtag) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            printResultSet(statement.executeQuery("SELECT sentiment.name, sentiment.id, tweet.word, tweet.frequence " +
                                                      "FROM tweet JOIN sentiment ON tweet.sentiment = sentiment.id " +
                                                      "WHERE frequence >= " + threshold + " AND hashtag = " + hashtag + " " +
                                                      "ORDER BY sentiment.name, frequence desc")
            );
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void printWordCloudsWithLexRes(int threshold, boolean hashtag) {
        try{
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();

            printResultSet(statement.executeQuery("SELECT DISTINCT sentiment.name, sentiment.id, tweet.word, tweet.frequence " +
                                                      "FROM tweet JOIN sentiment ON tweet.sentiment = sentiment.id JOIN lexicalresource ON tweet.word = lexicalresource.word AND tweet.sentiment = lexicalresource.sentiment_fk " +
                                                      "WHERE frequence >= " + threshold + " AND hashtag = " + hashtag + " " +
                                                      "ORDER BY sentiment.name, frequence desc")
            );
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addLexRes(int threshold, boolean hashtag) {
        try {
            List<LexicalResource> toAdd = new ArrayList<>();
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();

            ResultSet resultset = statement.executeQuery("SELECT tweet.word, tweet.sentiment " +
                                                             "FROM ( " +
                                                                 "SELECT DISTINCT tweet.sentiment as rissent, tweet.word as risword " +
                                                                 "FROM tweet JOIN sentiment ON tweet.sentiment = sentiment.id JOIN lexicalresource ON tweet.word = lexicalresource.word AND tweet.sentiment = lexicalresource.sentiment_fk " +
                                                                 "WHERE frequence >= " + threshold + " AND hashtag = " + hashtag + " " +
                                                             ") AS ris RIGHT JOIN tweet ON tweet.word = risword AND tweet.sentiment = rissent " +
                                                             "WHERE frequence >= "+ threshold +" AND risword IS null AND hashtag = " + hashtag);
            connection.close();

            while (resultset.next()){
                toAdd.add(new LexicalResource(resultset.getString("word"), resultset.getInt("sentiment"), ""));
            }
            System.out.println(toAdd);
            this.saveLexicalResource(toAdd);
            System.out.println("Finito");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteTable(String tableName) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM " + tableName);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
