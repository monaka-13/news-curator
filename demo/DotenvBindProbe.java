import io.github.cdimascio.dotenv.Dotenv;
public class DotenvBindProbe {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
    dotenv.entries().forEach(e -> {
      String key = e.getKey();
      if (System.getenv(key) == null && System.getProperty(key) == null) {
        System.setProperty(key, e.getValue());
      }
    });
    System.out.println("APP_SUMMARIZATION_ENABLED=" + System.getProperty("APP_SUMMARIZATION_ENABLED"));
    System.out.println("app.summarization.enabled=" + System.getProperty("app.summarization.enabled"));
  }
}
