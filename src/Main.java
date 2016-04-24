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

    public static void main(final String[] args) {
        List<Article> articles = null;
        try {
            articles = ArticleStorage.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (articles == null) {
            articles = DocumentParser.parseArticles();
            DocumentVectorCreator.setArticleVectors(articles);
        }

        System.out.println("Articles ready");
    }
}
