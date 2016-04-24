import java.io.IOException;
import java.util.List;

import models.Article;
import tools.ArticleStorage;
import tools.DocumentParser;
import tools.DocumentVectorCreator;

/**
 * The master
 * Created by Liam on 23-Apr-16.
 */
public class Main {

    private static List<Article> retrieveArticles() throws IOException {
        List<Article> articles;

        articles = ArticleStorage.load();

        if (articles == null) {
            articles = DocumentParser.parseArticles();
            DocumentVectorCreator.setArticleVectors(articles);

            ArticleStorage.store(articles);
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
