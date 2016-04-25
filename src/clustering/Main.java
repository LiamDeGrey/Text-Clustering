package clustering;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import clustering.models.Article;
import clustering.tools.DocumentParser;
import clustering.tools.DocumentSimilarity;
import clustering.tools.DocumentVectorCreator;

/**
 * The master
 * Created by Liam on 23-Apr-16.
 */
public class Main {

    private static List<Article> retrieveArticles() throws IOException {
        List<Article> articles;

        articles = DocumentParser.parseArticles();
        final Set<String> uniqueWords = DocumentVectorCreator.setArticleVectors(articles);

        if (articles != null) {
            DocumentSimilarity.findDocumentSimilarities(articles);
        }

        return articles;
    }

    public static void main(final String[] args) {
        List<Article> articles = null;
        try {
            articles = retrieveArticles();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Articles ready");


    }
}