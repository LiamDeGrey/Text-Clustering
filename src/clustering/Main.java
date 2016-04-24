package clustering;

import java.io.IOException;
import java.util.List;

import clustering.models.Article;
import clustering.tools.ArticleStorage;
import clustering.tools.DocumentParser;
import clustering.tools.DocumentVectorCreator;

/**
 * The master
 * Created by Liam on 23-Apr-16.
 */
public class Main {
    public static final boolean DEBUG = false;

    private static List<Article> retrieveArticles() throws IOException {
        List<Article> articles;

        articles = ArticleStorage.load();

        if (articles == null) {
            articles = DocumentParser.parseArticles();
            DocumentVectorCreator.setArticleVectors(DEBUG ? articles.subList(0, 2) : articles);

            ArticleStorage.store(DEBUG ? articles.subList(0, 2) : articles);
        }

        return articles;
    }

    public static void main(final String[] args) {
        try {
            retrieveArticles();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Articles ready");
    }
}
