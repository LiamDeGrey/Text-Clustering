package clustering.tools;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import clustering.models.Article;

/**
 * A helper class used to store and load the structured data.
 * Saves running the DocumentVectorCreator every time
 * Created by Liam on 24-Apr-16.
 */
public class ArticleStorage {
    private static final String FILEPATH = "DATA/storage";

    public static List<Article> load() throws IOException {
        if (!hasCompleteData()) {
            return null;
        }

        final Stream<String> stream = Files.lines(Paths.get(FILEPATH));
        final String[] jsonData = stream.toArray(String[]::new);

        final Gson gson = new Gson();
        final List<Article> articles = new ArrayList<>();
        for (int i = 1; i < jsonData.length; i++) {
            articles.add(gson.fromJson(jsonData[i], Article.class));
        }

        System.out.println(String.format("%d Articles loaded", articles.size()));

        return articles;
    }

    public static void store(final List<Article> articles) throws IOException {
        if (hasCompleteData()) {
            System.out.println("Articles already stored");
            return;
        }

        System.out.println("Requires storage");

        if (Files.exists(Paths.get(FILEPATH))) {
            Files.delete(Paths.get(FILEPATH));
        }

        final Gson gson = new Gson();
        final List<String> jsonData = articles.stream().map(gson::toJson).collect(Collectors.toList());
        jsonData.add(0, String.format("%d", articles.size() + 1));//Total lines expected for file [For error checking]

        Files.write(
                Paths.get(FILEPATH),
                jsonData,
                StandardOpenOption.CREATE_NEW
        );

        System.out.println("Storing complete");
    }

    private static boolean hasCompleteData() throws IOException {
        final Path filePath = Paths.get(FILEPATH);

        return Files.exists(filePath) &&
                Files.lines(filePath).findFirst().isPresent() &&
                String.valueOf(Files.lines(filePath).count()).equals(Files.lines(filePath).findFirst().get());
    }
}
