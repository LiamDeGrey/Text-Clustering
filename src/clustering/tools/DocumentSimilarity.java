package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.models.Article;
import clustering.models.Cluster;

/**
 * a class to find the similarity between all of the
 * documents using cosine similarity
 * Created by Liam on 25-Apr-16.
 */
public class DocumentSimilarity {

    static double findDocumentSimilarities(final Cluster cluster, final Article article) {
        final Map<String, Double> weightSum = new HashMap<String, Double>() {
            @Override
            public void putAll(final Map<? extends String, ? extends Double> m) {
                if (isEmpty()) {
                    super.putAll(m);
                }
                else {
                    final List<String> requireRemoval = new ArrayList<>();
                    String originalWord;
                    Double newWeight;
                    for (final Map.Entry<String, Double> originalItem : entrySet()) {
                        if ((newWeight = m.get(originalWord = originalItem.getKey())) != null) {
                            put(originalWord, get(originalWord) * newWeight);
                        }
                        else {
                            requireRemoval.add(originalWord);
                        }
                    }

                    requireRemoval.forEach(this::remove);
                }
            }
        };

        Double vectorSum;
        weightSum.clear();

        weightSum.putAll(cluster.getCentroidVector());
        weightSum.putAll(article.getArticleVector());

        vectorSum = weightSum.values().stream().mapToDouble(Double::doubleValue).sum();

        return (vectorSum / (cluster.getVectorSum() * article.getVectorSum()));
    }

    public static double getVectorSum(final Map<String, Double> wordVector) {
        double articleSum = 0;

        for (final Double weight : wordVector.values()) {
            articleSum += Math.pow(weight, 2);
        }
        articleSum = Math.sqrt(articleSum);

        return articleSum;
    }
}
