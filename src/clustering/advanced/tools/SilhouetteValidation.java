package clustering.advanced.tools;

import java.util.List;

import clustering.common.models.Article;
import clustering.common.models.Cluster;
import clustering.common.tools.DocumentSimilarity;

/**
 * NOT IDEAL BECAUSE TOO MANY ARTICLES DON"T SHARE PHRASES
 * - CAN'T GET ACCURATE VALUE
 * Created by Liam on 29-Apr-16.
 */
public class SilhouetteValidation {

    public static double getSilhouetteValue(final List<Cluster> clusters) {
        double numArticles = 0;
        double sumSilhouetteValues = 0;

        double articleCoupling;
        double articleCohesion;
        for (final Cluster cluster : clusters) {
            numArticles += cluster.getArticles().size();
            for (final Article article : cluster.getArticles()) {
                articleCoupling = calculateCoupling(article, clusters);
                articleCohesion = calculateCohesion(article, cluster);

                sumSilhouetteValues += (articleCoupling - articleCohesion) / Math.max(articleCoupling, articleCohesion);
            }
        }

        System.out.println(String.format("Sum silhouette values = %.2f, number of articles = %.0f", sumSilhouetteValues, numArticles));
        return sumSilhouetteValues / numArticles;
    }

    private static double calculateCoupling(final Article article, final List<Cluster> clusters) {
        List<Article> comparisonArticles;
        double sumDistance, maxSimilarity = Double.MIN_VALUE;
        for (final Cluster cluster : clusters) {
            if (!(comparisonArticles = cluster.getArticles()).contains(article)) {
                sumDistance = 0;
                for (final Article comparisonArticle : comparisonArticles) {
                    sumDistance += DocumentSimilarity.findDocumentSimilarities(article, comparisonArticle);
                }
                if (sumDistance != 0 && comparisonArticles.size() != 0) {
                    maxSimilarity = Math.max(sumDistance / comparisonArticles.size(), maxSimilarity);
                }
            }
        }

        return maxSimilarity;
    }

    private static double calculateCohesion(final Article article, final Cluster cluster) {
        double sumDistance = 0;
        for (final Article comparisonArticle : cluster.getArticles()) {
            if (!article.equals(comparisonArticle)) {
                sumDistance += DocumentSimilarity.findDocumentSimilarities(article, comparisonArticle);
            }
        }

        return sumDistance / (cluster.getArticles().size() - 1);
    }
}
