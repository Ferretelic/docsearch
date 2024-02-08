import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class TestDocSearch {
  @Test
  public void testIndex() throws URISyntaxException, IOException {
    Handler h = new Handler("./technical/");
    URI rootPath = new URI("http://localhost/");
    assertEquals("There are 1391 total files to search.", h.handleRequest(rootPath));
  }

  @Test
  public void testSearch() throws URISyntaxException, IOException {
    Handler h = new Handler("./technical/");
    String sep = File.separator;
    URI rootPath = new URI("http://localhost/search?q=Resonance");
    String expect = String.format(
        "Found 2 paths:\n.%stechnical%sbiomed%sar615.txt\n.%stechnical%splos%sjournal.pbio.0020150.txt", sep,
        sep, sep, sep, sep, sep);
    assertEquals(expect, h.handleRequest(rootPath));
  }

  @Test
  public void testSearchQuery() throws URISyntaxException, IOException {
    Handler h = new Handler("./technical/");
    String sep = File.separator;
    URI rootPath = new URI("http://localhost/search?q=computer%20science");
    String expect = String.format(
        "Found 3 paths:\n.%stechnical%sbiomed%s1471-2229-2-9.txt\n.%stechnical%sgovernment%sGen_Account_Office%spe1019.txt\n.%stechnical%splos%sjournal.pbio.0030032.txt",
        sep, sep, sep, sep, sep, sep, sep, sep, sep, sep);
    assertEquals(expect, h.handleRequest(rootPath));
  }

  @Test
  public void testSearchQueryDirectory() throws URISyntaxException, IOException {
    Handler h = new Handler("./technical/biomed");
    String sep = File.separator;
    URI rootPath = new URI("http://localhost/search?q=computer%20science");
    String expect = String.format(
        "Found 1 paths:\n.%stechnical%sbiomed%s1471-2229-2-9.txt",
        sep, sep, sep);
    assertEquals(expect, h.handleRequest(rootPath));
  }
}
