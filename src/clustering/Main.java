package clustering;

import java.io.IOException;
import java.util.List;

import clustering.models.Article;
import clustering.tools.DocumentParser;
import clustering.tools.DocumentVectorCreator;
import clustering.tools.KMeansClusterTool;

/**
 * The master
 * Created by Liam on 23-Apr-16.
 */
public class Main {

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
