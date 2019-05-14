import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class NewJobLogger {

    private static Map<Character, Loggerr> map;

    static {
        map = new HashMap<>();
    }

    public static Loggerr console(Map params){
        if (!map.containsKey('C')) {
            map.put('C', new ConsoleLogger());
        }
        return get('C');
    }

    public static Loggerr file(Map params) throws IOException {
        if (map.containsKey('F')) {
            map.put('F', new FileLogger(params));
        }
        return get('F');
    }

    public static Loggerr database(Map params) throws SQLException {
        if (map.containsKey('D')) {
            map.put('D', new DatabaseLogger(params));
        }
        return get('D');
    }

    private static Loggerr get(Character c){
        return map.get(c);
    }

    static class ConsoleLogger implements Loggerr {
        Logger logger = Logger.getLogger("MyLog");

        public ConsoleLogger() {
            logger.addHandler(new ConsoleHandler());
        }

        @Override
        public void log(String message, Level level){
            logger.log(INFO, message);
        }
    }

    static class FileLogger implements Loggerr {
        private Logger logger = Logger.getLogger("MyLog");

        public FileLogger(Map dbParams) throws IOException {
            File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
            logger.addHandler(fh);
        }

        @Override
        public void log(String message, Level level){
            logger.log(INFO, message);
        }
    }

    static class DatabaseLogger implements Loggerr {

        private Connection connection;

        public DatabaseLogger (Map dbParams) throws SQLException {
            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));
            connectionProps.put("password", dbParams.get("password"));

            connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                    + ":" + dbParams.get("portNumber") + "/", connectionProps);
        }

        @Override
        public void log(String message, Level level){
            Statement stmt = getStatement();
            try {
                stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(getT(level)) + ")");
            } catch (SQLException e) {
                throw new RuntimeException("Error on query execution");
            }
        }

        private Statement getStatement() throws RuntimeException {
            try {
                return connection.createStatement();
            } catch (SQLException sqle) {
                throw new RuntimeException("Error on db connection");
            }
        }

        private int getT(Level level){
            switch (level.getName()) {
                case "INFO": return 1;
                case "SEVERE": return 2;
                case "WARNING": return 3;
                default: return 0;
            }
        }
    }

}
