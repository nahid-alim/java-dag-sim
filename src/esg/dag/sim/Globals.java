package esg.dag.sim;
import java.io.PrintWriter;

public class Globals {
    public static float currTime;
    public static PrintWriter writer;
    public static void createFileforWriter() throws Exception{
        writer =  new PrintWriter("securityLogs.txt", "UTF-8");
    }
}
