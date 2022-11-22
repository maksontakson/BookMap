import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CustomWriter {
    private BufferedWriter bufferedWriter;
    public CustomWriter () throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter("D:\\Programs\\JavaProjects\\BookMap\\out\\artifacts\\BookMap_jar\\output.txt"));
    }

    public void write(String str)  {
        try {
            bufferedWriter.write(str + "\n");
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
