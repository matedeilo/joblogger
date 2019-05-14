import org.junit.Test;

import java.util.Collections;

public class JobLoggerTest {

    @Test
    public void shouldLogFile() throws Exception {
//        JobLogger jobLogger = new JobLogger(true, false, false, true, false, false, null);
//        jobLogger.logMessage("text", true, false,false);

        Loggerr loggerr = NewJobLogger.console(Collections.emptyMap());
        loggerr.message("hello");
    }
}
