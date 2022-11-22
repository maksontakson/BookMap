import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class CustomWriter {
    private final BufferedWriter bufferedWriter;
    public CustomWriter () throws IOException {
          bufferedWriter = new BufferedWriter(new FileWriter("output.txt"));
    }

    public void write(String str)  {
        try {
            bufferedWriter.write(str + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
