package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.models.Article;

/**
 * a class to find the similarity between all of the
 * documents using cosine similarity
 * Created by Liam on 25-Apr-16.
 */
public class DocumentSimilarity {

    public static void findDocumentSimilarities(final List<Article> articles) {
        final List<Double> documentDistances = new ArrayList<>((int) (Math.pow(articles.size(), 2) - articles.size()) / 2);

        Map<String, Double> weightSum = new HashMap<String, Double>() {
            @Override
            public void putAll(final Map<? extends String, ? extends Double> m) {
                if (isEmpty()) {
                    super.putAll(m);
                }
                else {
                    String word;
                    Double weight;
                    for (final Entry<? extends String, ? extends Double> documentWord : m.entrySet()) {
                        word = documentWord.getKey();
                        weight = get(word);
                        put(word, weight != null ? weight * documentWord.getValue() : documentWord.getValue());
                    }
                }
            }
        };

        double vectorSum;
        Article article, comparisonArticle;
        for (int i = 0; i < articles.size(); i++) {
            article = articles.get(i);
            for (int j = i + 1; j < articles.size(); j++) {
                comparisonArticle = articles.get(j);
                weightSum.clear();

                weightSum.putAll(article.getDocumentWords());
                weightSum.putAll(comparisonArticle.getDocumentWords());

                vectorSum = 0;
                for (final Double weight : weightSum.values()) {
                    vectorSum += weight;
                }

                documentDistances.add(1 - (vectorSum / (article.getArticleSum() * comparisonArticle.getArticleSum())));
            }
            System.out.println(String.format("PROGRESS = %.8f%% (processing %d out of %d)", (i * 1.0) / (articles.size() * 1.0), i, articles.size()));
        }
    }
}
