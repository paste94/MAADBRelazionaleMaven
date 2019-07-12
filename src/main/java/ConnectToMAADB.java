import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import utils.HashTag;
import utils.LexicalResource;
import utils.Tweet;

import javax.swing.plaf.nimbus.State;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConnectToMAADB {


    public void printResultSet(ResultSet resultSet){
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
                System.out.println("");
            }
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    void saveLexicalResource(List<LexicalResource> words) throws SQLException{

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LexicalResource");
        //System.out.println(words.stream().filter(e->e.getWord().equals("perfection")).collect(Collectors.toList()));

        //Controllo se il result set è pieno, lancio eccezione per non sovrascrivere
        if (resultSet.next()) {
            throw new SQLException("Database pieno!");
        }

        //Salva i dati nel DB
        for (LexicalResource w : words) {
            try {
                statement.executeUpdate("INSERT INTO LEXICALRESOURCE(WORD,SENTIMENT_FK,EMOSN,NRC,SENTISENSE) VALUES ('" + w.getWord() + "',"+ w.getSentimentIdFk() +","+w.getEmosnFreq()+","+w.getNrcFreq()+","+w.getSentisenseFreq()+") ");
            } catch (SQLException e) {
                e.getMessage();
            }
        }

        connection.close();
    }

    void saveHashtags(MultiKeyMap hashTags) throws SQLException{

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM hashtag");
        //System.out.println(words.stream().filter(e->e.getWord().equals("perfection")).collect(Collectors.toList()));

        //Controllo se il result set è pieno, lancio eccezione per non sovrascrivere
        if (resultSet.next()) {
            throw new SQLException("Database pieno!");
        }


        MapIterator it = hashTags.mapIterator();

        while (it.hasNext()) {
            it.next();

            MultiKey mk = (MultiKey) it.getKey();
            Integer value = (Integer) it.getValue();

            try {
                statement.executeUpdate("INSERT INTO HASHTAG(WORD,SENTIMENT,FREQUENCE) VALUES ('" + mk.getKey(0) + "',"+ mk.getKey(1) +","+ value +") ");
            } catch (SQLException e) {
                e.getMessage();
            }
        }
        connection.close();
    }


    public void saveTweets(Map<String, Long> freq, int sent) throws SQLException {
        System.out.println("Salvo elementi " + sent);
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM tweet");
        //System.out.println(words.stream().filter(e->e.getWord().equals("perfection")).collect(Collectors.toList()));

        freq.forEach((word, frequence)->{
            try {
                statement.executeUpdate("INSERT INTO tweet(WORD,SENTIMENT,FREQUENCE,HASHTAG) VALUES ('" + word + "',"+ sent +","+ frequence +", false) ");
            } catch (SQLException e) {
                e.getMessage();
            }
        });
        System.out.println("Finito di salvare elementi " + sent);
    }

    public ResultSet getWordClouds(int threshold) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();

        return statement.executeQuery("SELECT sentiment.name, sentiment.id, tweet.word, tweet.frequence " +
                                          "FROM tweet JOIN sentiment ON tweet.sentiment = sentiment.id " +
                                          "WHERE frequence >= " + threshold +
                                          "ORDER BY sentiment.name, frequence desc");
    }

    public ResultSet getWordCloudsWithLexRes(int threshold) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();

        return statement.executeQuery("SELECT DISTINCT sentiment.name, sentiment.id, tweet.word, tweet.frequence " +
                                          "FROM tweet JOIN sentiment ON tweet.sentiment = sentiment.id JOIN lexicalresource ON tweet.word = lexicalresource.word " +
                                          "WHERE frequence >= " + threshold +
                                          "ORDER BY sentiment.name, frequence desc");
    }

    void deleteTable(String tableName) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + tableName);
        connection.close();
    }
}
