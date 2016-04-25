package clustering.tools;

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
    private static final String KEY_FORMAT = "\"%d\"\"%d\"";

    public static void findDocumentSimilarities(final List<Article> articles) {
        final Map<String, Double> documentVectorSums = new HashMap<>();
        final Map<String, Double> weightSum = new HashMap<String, Double>() {
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

        Double vectorSum;
        Article article, comparisonArticle;
        for (int i = 0; i < articles.size(); i++) {
            article = articles.get(i);
            for (int j = i + 1; j < articles.size(); j++) {
                comparisonArticle = articles.get(j);

                if ((vectorSum = documentVectorSums.get(String.format(KEY_FORMAT, i, j))) == null) {
                    weightSum.clear();

                    weightSum.putAll(article.getDocumentWords());
                    weightSum.putAll(comparisonArticle.getDocumentWords());

                    vectorSum = weightSum.values().stream().mapToDouble(Double::intValue).sum();

                    documentVectorSums.put(String.format(KEY_FORMAT, i, j), vectorSum);
                }

                //documentDistances.add(1 - (vectorSum / (article.getArticleSum() * comparisonArticle.getArticleSum())));
            }
            System.out.println(String.format("PROGRESS = %.8f%% (processing %d out of %d)", (i * 1.0) / (articles.size() * 1.0), i, articles.size()));
        }
    }
}
