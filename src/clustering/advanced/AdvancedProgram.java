package clustering.advanced;

import java.io.IOException;
import java.util.List;

import clustering.advanced.tools.DocumentVectorCreator;
import clustering.advanced.tools.KMeansClusterTool;
import clustering.common.models.Article;
import clustering.common.tools.DocumentParser;

/**
 * A more advanced clustering approach using phrases instead of words
 * Created by Liam on 27-Apr-16.
 */
public class AdvancedProgram {

    private static List<Article> retrieveArticles() throws IOException {
        List<Article> articles;

        articles = DocumentParser.parseArticles();
        DocumentVectorCreator.setArticleVectors(articles);

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

        if (articles != null) {
            System.out.println("Articles ready");
            KMeansClusterTool.clusterArticles(articles.subList(0, 1000));
        }
    }
}
