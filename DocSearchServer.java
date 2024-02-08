import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

class FileHelpers {
  static List<File> getFiles(Path start) throws IOException {
    File f = start.toFile();
    List<File> result = new ArrayList<>();
    if (f.isDirectory()) {
      File[] paths = f.listFiles();
      for (File subFile : paths) {
        result.addAll(getFiles(subFile.toPath()));
      }
    } else {
      result.add(start.toFile());
    }
    return result;
  }

  static String readFile(File f) throws IOException {
    return new String(Files.readAllBytes(f.toPath()));
  }
}

class Handler implements URLHandler {
  Path base;

  Handler(String directory) throws IOException {
    this.base = Paths.get(directory);
  }

  List<File> searchByTitle(List<File> paths, String title) throws IOException {
    List<File> foundPaths = new ArrayList<>();
    for (File f : paths) {
      if (f.toString().contains(title)) {
        foundPaths.add(f);
      }
    }
    return foundPaths;
  }

  List<File> searchByQuery(List<File> paths, String query) throws IOException {
    List<File> foundPaths = new ArrayList<>();
    for (File f : paths) {
      if (FileHelpers.readFile(f).contains(query)) {
        foundPaths.add(f);
      }
    }
    return foundPaths;
  }

  public String handleRequest(URI url) throws IOException {
    List<File> paths = FileHelpers.getFiles(this.base);
    if (url.getPath().equals("/")) {
      return String.format("There are %d total files to search.", paths.size());
    } else if (url.getPath().equals("/search")) {
      List<File> foundPaths = null;
      if (url.getQuery().contains("&")) {
        String[] parameters = url.getQuery().split("&");
        String[] queryParameters = parameters[0].split("=");
        String[] titleParameters = parameters[1].split("=");
        if (queryParameters[0].equals("q") && titleParameters[0].equals("title")) {
          foundPaths = this.searchByQuery(paths, queryParameters[1]);
          foundPaths = this.searchByTitle(foundPaths, titleParameters[1]);
        } else {
          return "Couldn't find query parameter q and title";
        }
      } else {
        String[] parameters = url.getQuery().split("=");
        if (parameters[0].equals("q")) {
          foundPaths = this.searchByQuery(paths, parameters[1]);
        } else if (parameters[0].equals("title")) {
          foundPaths = this.searchByTitle(paths, parameters[1]);
        } else {
          return "Couldn't find query parameter q";
        }
      }
      List<String> results = new ArrayList<String>();
      for (File file : foundPaths) {
        results.add(file.toString());
      }
      Collections.sort(results);
      String result = String.join("\n", results);
      return String.format("Found %d paths:\n%s", foundPaths.size(), result);
    } else {
      return "Don't know how to handle that path!";
    }
  }
}

class DocSearchServer {
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println(
          "Missing port number or directory! The first argument should be the port number (Try any number between 1024 to 49151) and the second argument should be the path of the directory");
      return;
    }

    int port = Integer.parseInt(args[0]);

    Server.start(port, new Handler(args[1]));
  }
}
