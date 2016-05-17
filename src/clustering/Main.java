package clustering;

import java.io.IOException;
import java.util.List;

import clustering.models.Article;
import clustering.tools.DocumentParser;
import clustering.tools.DocumentVectorCreator;
import clustering.tools.KMeansClusterTool;

/**
 * A more advanced clustering approach using phrases instead of words
 * Created by Liam on 27-Apr-16.
 */
public class Main {
    public static final boolean FLAG_PHRASES = true;

    private static List<Article> retrieveArticles() throws IOException {
        List<Article> articles;

        articles = DocumentParser.parseArticles();
        DocumentVectorCreator.setArticleVectors(articles);

        return articles;
    }

    public static void main(final String[] args) {
        final long startTime = System.currentTimeMillis();
        List<Article> articles = null;

        try {
            articles = retrieveArticles();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (articles != null) {
            System.out.println("Articles ready");

            KMeansClusterTool.clusterArticles(articles.subList(0, 500));
        }

        System.out.println("Program finished in " + (System.currentTimeMillis() - startTime));
    }
}
