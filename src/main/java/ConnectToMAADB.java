import utils.LexicalResource;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectToMAADB {


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
                System.out.println("");
            }
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    void saveToDB(List<LexicalResource> words) throws SQLException{

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LexicalResource");
        System.out.println(words.stream().filter(e->e.getWord().equals("perfection")).collect(Collectors.toList()));

        //Controllo se il result set è pieno, lancio eccezione per non sovrascrivere
        if (resultSet.next()) {
            throw new SQLException("Database pieno!");
        }

        String selectSQL = "INSERT INTO LEXICALRESOURCE(WORD,SENTIMENT_FK,EMOSN,NRC,SENTISENSE) VALUES (?,?,?,?,?) ";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        for (LexicalResource w : words) {
            try {
                statement.executeUpdate("INSERT INTO LEXICALRESOURCE(WORD,SENTIMENT_FK,EMOSN,NRC,SENTISENSE) VALUES ('" + w.getWord() + "',"+ w.getSentimentIdFk() +","+w.getEmosnFreq()+","+w.getNrcFreq()+","+w.getSentisenseFreq()+") ");
                /*
                preparedStatement.setString(1, w.getWord());
                preparedStatement.setInt(2, w.getSentimentIdFk());
                preparedStatement.setInt(3, w.getEmosnFreq());
                preparedStatement.setInt(4, w.getNrcFreq());
                preparedStatement.setInt(5, w.getSentisenseFreq());
                preparedStatement.executeUpdate();
                 */
            } catch (SQLException e) {
                e.getMessage();
            }
        }
        //preparedStatement.executeQuery();
        connection.close();
    }

    void deleteTable(String tableName) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAADB", "postgres", "postgres");
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + tableName);
        connection.close();
    }
}
