package tools;

import models.Article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Very specific to my needs, pulls out the specific fields I am after
 * as suggested in the fields below, and creates articles with these.
 * Created by Liam on 23-Apr-16.
 */
public class DocumentParser {
    private static final String FILEPATH = "DATA/";
    private static final String FILE_EXTENSION = ".sgm";

    private static final String TAG_REUTERS_OPENED = "<REUTERS";
    private static final String TAG_REUTERS_CLOSED = "</REUTERS>";
    private static final String TAG_TOPICS_OPENED = "<TOPICS>";
    private static final String TAG_TOPICS_CLOSED = "</TOPICS>";
    private static final String TAG_TITLE_OPENED = "<TITLE>";
    private static final String TAG_TITLE_CLOSED = "</TITLE>";
    private static final String TAG_BODY_OPENED = "<BODY>";
    private static final String TAG_BODY_CLOSED = "</BODY>";
    private static final String TAG_ITEM_OPENED = "<D>";
    private static final String TAG_ITEM_CLOSED = "</D>";

    public static List<Article> parseArticles() {
        final List<Article> articles = parseDataFiles();
        System.out.println("Size = " + articles.size() + " articles");
        return articles;
    }

    private static List<Article> parseDataFiles() {
        final List<Article> articles = new ArrayList<>();
        try {
            Files.walk(Paths.get(FILEPATH)).forEach(filePath -> {
                if (filePath.toString().endsWith(FILE_EXTENSION)) {
                    articles.addAll(parseDataFile(filePath));
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return articles;
    }

    private static List<Article> parseDataFile(final Path path) {
        System.out.println("Parsing file " + path.toString());

        final List<Article> articles = new ArrayList<>();
        Article currentArticle = null;
        try {
            final Stream<String> stream = Files.lines(path);
            final String[] articleData = stream.toArray(String[]::new);

            for (final String s : articleData) {
                if (currentArticle == null) {
                    if (s.contains(TAG_REUTERS_OPENED)) {
                        articles.add(currentArticle = new Article());
                    }
                }
                else {
                    int tempIndex;

                    if (s.contains(TAG_REUTERS_CLOSED)) {
                        currentArticle = null;
                    }
                    else if (s.contains(TAG_TOPICS_OPENED)) {
                        addTopics(currentArticle, s);
                    }
                    else if ((tempIndex = s.indexOf(TAG_TITLE_OPENED)) != -1) {
                        addTitle(currentArticle, s, tempIndex);
                    }
                    else if ((tempIndex = s.indexOf(TAG_BODY_OPENED)) != -1) {
                        startBody(currentArticle, s, tempIndex);
                    }
                    else if ((tempIndex = s.indexOf(TAG_BODY_CLOSED)) != -1) {
                        endBody(currentArticle, s, tempIndex);
                    }
                    else if (currentArticle.requiresBodyText()) {
                        appendBody(currentArticle, s);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }

    private static void addTopics(final Article currentArticle, String s) {
        s = s.replace(TAG_TOPICS_OPENED, "");
        s = s.replace(TAG_TOPICS_CLOSED, "");
        s = s.replace(TAG_ITEM_OPENED, "");
        String[] topics = s.split(TAG_ITEM_CLOSED);
        currentArticle.setTopics(topics);
    }

    private static void addTitle(final Article currentArticle, final String s, int tempIndex) {
        int tempIndex2;
        tempIndex += TAG_TITLE_OPENED.length();

        currentArticle.setTitle(s.substring(
                tempIndex,
                (tempIndex2 = s.indexOf(TAG_TITLE_CLOSED)) != -1 ? tempIndex2 : s.length())
        );
    }

    private static void startBody(final Article currentArticle, final String s, int tempIndex) {
        currentArticle.startBody();
        tempIndex += TAG_BODY_OPENED.length();

        currentArticle.appendText(s.substring(
                tempIndex,
                (tempIndex = s.indexOf(TAG_BODY_CLOSED)) != -1 ? tempIndex : s.length()) + "\n"
        );

        if (s.contains(TAG_BODY_CLOSED)) {
            currentArticle.finishBody();
        }
    }

    private static void appendBody(final Article currentArticle, final String s) {
        currentArticle.appendText(s + "\n");
    }

    private static void endBody(final Article currentArticle, final String s, int tempIndex) {
        currentArticle.appendText(s.substring(0, tempIndex));
        currentArticle.finishBody();
    }
}
