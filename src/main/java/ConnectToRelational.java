import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ConnectToRelational{

    private final String url = "jdbc:postgresql://localhost:5432/MAADB";
    private final String user = "postgres";
    private final String password = "postgres";

    void saveLexicalResource(List<LexicalResource> words) {
        try {
            String query = "INSERT INTO LEXICALRESOURCE(WORD,SENTIMENT_FK,EMOSN,NRC,SENTISENSE) VALUES (?,?,?,?,?)";
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(query);
            for (LexicalResource w : words) {
                statement.setString(1, w.getWord());
                statement.setInt(2, w.getSentimentIdFk());
                statement.setInt(3, w.getEmosnFreq());
                statement.setInt(4, w.getNrcFreq());
                statement.setInt(5, w.getSentisenseFreq());

                statement.addBatch();
            }

            statement.executeBatch();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void addLexRes(Map<String, Integer> listOfWords, int sentiment) {
        try {
            String query =  "INSERT INTO lexicalresource (word, sentiment_fk, frequence) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (word, sentiment_fk) DO UPDATE " +
                            "SET frequence = ?";

            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(query);

/*
            for (WordFrequency elem : listOfWords) {
                statement.setString(1, elem.getWord());
                statement.setInt(2, sentiment);
                statement.setInt(3, elem.getFrequency());
                statement.setInt(4, elem.getFrequency());

                statement.addBatch();
            }

 */
            listOfWords.forEach((word, freq) ->{
                try {
                    statement.setString(1, word);
                    statement.setInt(2, sentiment);
                    statement.setInt(3, freq);
                    statement.setInt(4, freq);

                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
            System.out.println(statement);
            statement.executeBatch();
            System.out.println("******************************************");
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void printWordClouds(int sentiment, String fileName){
        //Genera l'immagine per le risorse lessicali
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT word, frequence FROM lexicalresource WHERE sentiment_fk = " + sentiment + " ORDER BY frequence desc");

            List<WordFrequency> wordFrequencies = new ArrayList<>();

            int i = 0;

            while(rs.next() && i<500){
                i++;
                wordFrequencies.add(new WordFrequency(rs.getString("word"), rs.getInt("frequence")));
            }

            System.out.println(wordFrequencies.size());

            System.out.println(wordFrequencies);
            final Dimension dimension = new Dimension(600, 600);
            final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
            wordCloud.setPadding(2);
            wordCloud.setBackground(new CircleBackground(300));
            wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
            wordCloud.setFontScalar(new LinearFontScalar(10, 40));
            wordCloud.build(wordFrequencies);
            wordCloud.writeToFile("./src/main/resources/word_clouds/" + fileName.replace(".txt", ".png"));
            System.out.println("Generato file " + fileName + ".png");

            connection.close();

            //Aggiunge le frequenze e le parile nuove al database
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    void printCloud(int sentiment, String tableName){
        //Genera l'immagine per hashtag, emoji o emoticon
        try {
            String fileName = tableName + "_" + SentimentEnum.idToString(sentiment) + ".png";
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT word, frequence FROM " + tableName + " WHERE sentiment = " + sentiment);

            List<WordFrequency> wordFrequencies = new ArrayList<>();

            while(rs.next()){
                if(rs.getString("word").replace(" ", "").length()>0) {
                    wordFrequencies.add(new WordFrequency(rs.getString("word"), rs.getInt("frequence")));
                }
            }

            final Dimension dimension = new Dimension(400, 400);
            final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
            wordCloud.setPadding(2);
            wordCloud.setBackground(new CircleBackground(200));
            wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
            wordCloud.build(wordFrequencies);
            wordCloud.writeToFile("./src/main/resources/word_clouds/" + fileName);
            System.out.println("Generato file " + fileName);

            connection.close();

            //Aggiunge le frequenze e le parile nuove al database
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    void addEmojis(List<String> emojis, Integer sentiment) {
        try {
            Map<String, Long> freq = emojis.stream()
                    .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            String query =  "INSERT INTO emoji (word, sentiment, frequence) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (word, sentiment) DO UPDATE " +
                    "SET frequence = ?";

            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(query);
            int count = 0;

            for (Map.Entry<String, Long> entry : freq.entrySet()) {
                if(count>=500) break;

                String emoji = entry.getKey();
                int f = entry.getValue().intValue();
                statement.setString(1, emoji);
                statement.setInt(2, sentiment);
                statement.setInt(3, f);
                statement.setInt(4, f);

                statement.addBatch();

                count++;
            }

            statement.executeBatch();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addEmoticon(List<String> emoticons, Integer sentiment) {
        try {
            Map<String, Long> freq = emoticons.stream()
                    .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            String query =  "INSERT INTO emoticon (word, sentiment, frequence) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (word, sentiment) DO UPDATE " +
                    "SET frequence = ?";

            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(query);
            int count = 0;

            for (Map.Entry<String, Long> entry : freq.entrySet()) {
                if(count>=500) break;

                String emoji = entry.getKey();
                int f = entry.getValue().intValue();
                statement.setString(1, emoji);
                statement.setInt(2, sentiment);
                statement.setInt(3, f);
                statement.setInt(4, f);

                statement.addBatch();
                count ++;
            }

            statement.executeBatch();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addHashtags(List<String> hashtags, Integer sentiment) {
        try {
            Map<String, Long> freq = hashtags.stream()
                    .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            String query =  "INSERT INTO hashtag (word, sentiment, frequence) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (word, sentiment) DO UPDATE " +
                    "SET frequence = ?";

            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(query);
            int count = 0;

            for (Map.Entry<String, Long> entry : freq.entrySet()) {
                if(count>=500) break;
                String emoji = entry.getKey();
                int f = entry.getValue().intValue();
                statement.setString(1, emoji);
                statement.setInt(2, sentiment);
                statement.setInt(3, f);
                statement.setInt(4, f);

                statement.addBatch();
                count ++;
            }

            statement.executeBatch();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void deleteTable(String tableName) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM " + tableName);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    String statistics(int sentiment) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
            Statement statement = connection.createStatement();
            int emosnFreq = 0;
            int emosnTot = 0;
            int nrcFreq = 0;
            int nrcTot = 0;
            int sentisenseFreq = 0;
            int sentisenseTot = 0;
            int totFreq = 0;

            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND emosn > 0 AND frequence > 0");
            rs.next();
            emosnFreq += rs.getInt("count");
            rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND emosn > 0");
            rs.next();
            emosnTot += rs.getInt("count");

            rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND nrc > 0 AND frequence > 0");
            rs.next();
            nrcFreq += rs.getInt("count");
            rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND nrc > 0");
            rs.next();
            nrcTot += rs.getInt("count");

            rs= statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND sentisense > 0 AND frequence > 0");
            rs.next();
            sentisenseFreq += rs.getInt("count");
            rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND sentisense > 0");
            rs.next();
            sentisenseTot += rs.getInt("count");

            rs = statement.executeQuery("SELECT COUNT(*) FROM lexicalresource WHERE sentiment_fk = " + sentiment + " AND frequence > 0");
            rs.next();
            totFreq += rs.getInt("count");

            String ret = "";

            ret += "\nFrequenze per sentimento " + SentimentEnum.idToString(sentiment) + " \n";
            if(emosnTot>0){
                ret += "EmoSN: \n";
                ret += "\tIl " + new DecimalFormat("##.##").format(((float)emosnFreq / (float)totFreq) * 100 )+ "% delle parole presenti nei tweet compare anche in EmoSN\n";
                ret += "\t" + (emosnTot - emosnFreq) + " parole su " + emosnTot + " in EmoSN non sono mai utilizzate nei tweet (" +   new DecimalFormat("##.##").format(((float)emosnTot - (float)emosnFreq) / (float)emosnTot * 100) + "%)\n";
            }else{
                ret += "EmoSN: Non presente tra le risorse\n";
            }
            if(nrcTot>0){
                ret += "NRC: \n";
                ret += "\tIl " + new DecimalFormat("##.##").format(((float)nrcFreq / (float)totFreq) * 100) + "% delle parole presenti nei tweet compare anche in NRC\n";
                ret += "\t" + (nrcTot - nrcFreq) + " parole su " + nrcTot + " parole in NRC non sono mai utilizzate nei tweet (" + new DecimalFormat("##.##").format((((float)nrcTot - (float)nrcFreq) / (float)nrcTot) * 100) + "%)\n";
            }else{
                ret += "NRC: Non presente tra le risorse\n";
            }
            if(sentisenseTot>0){
                ret += "Sentisense: \n";
                ret += "\tIl " + new DecimalFormat("##.##").format(((float)sentisenseFreq / (float)totFreq) * 100) + "% delle parole presenti nei tweet compare anche in Sentisense\n";
                ret += "\t" + (sentisenseTot - sentisenseFreq) + " parole su " + sentisenseTot + " parole in Sentisense non sono mai utilizzate nei tweet (" + new DecimalFormat("##.##").format((((float)sentisenseTot - (float)sentisenseFreq) / (float)sentisenseTot) * 100) + "%)\n";
            }else{
                ret += "Sentisense: Non presente tra le risorse\n";
            }
            ret += "---------";

            connection.close();

            return ret;

            //Aggiunge le frequenze e le parile nuove al database
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
