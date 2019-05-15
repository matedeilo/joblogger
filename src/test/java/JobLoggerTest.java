import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JobLoggerTest {

    @Test
    public void shouldLogConsole() throws Exception {
        Loggerr loggerr = NewJobLogger.console(Collections.emptyMap());
        loggerr.message("hello");
    }

    @Test
    public void shouldLogFile() throws Exception {
        Loggerr loggerr = NewJobLogger.file(Collections.singletonMap("logFileFolder", "/Users/manueltejeda/Documents/mate/java/logger/"));
        loggerr.error("hello");
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfParametersAreInvalid_file() throws Exception {
        NewJobLogger.file(Collections.emptyMap());
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfParametersAreInvalid_database() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("userName", "");
        map.put("someattrb", "");
        NewJobLogger.database(map);
    }
}
