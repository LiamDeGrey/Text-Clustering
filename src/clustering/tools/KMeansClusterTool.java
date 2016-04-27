package clustering.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import clustering.models.Article;
import clustering.models.Cluster;

/**
 * A tool used to cluster a list articles
 * Created by Liam on 26/04/2016.
 */
public class KMeansClusterTool {
    private static final int K = 3;
    private static final int maxIterations = 20;

    private static int iteration = 0;

    public static void clusterArticles(List<Article> articles) {
        System.out.println("CLUSTERING BEGINNING");
        final List<Cluster> clusters = new ArrayList<>();

        final HashSet<Integer> randoms = new HashSet<>();
        while (randoms.size() != K) {
            randoms.add((int) (Math.random() * (articles.size() - 1)));
        }

        for (final int random : randoms) {
            Cluster cluster = new Cluster();
            cluster.addArticle(articles.get(random));
            articles.remove(random);
            clusters.add(cluster);
        }

        populateClustersInitial(articles, clusters);

        articles = null;

        int size = 0;
        for (Cluster cluster : clusters) {
            System.out.println("Clusters size = " + cluster.getArticles().size());
            size += cluster.getArticles().size();
        }
        System.out.println("TOTAL SIZE = " + size);

        evaluateClusters(clusters);

        size = 0;
        for (Cluster cluster : clusters) {
            System.out.println("Clusters size = " + cluster.getArticles().size());
            size += cluster.getArticles().size();
        }
        System.out.println("TOTAL SIZE = " + size);
    }

    private static void populateClustersInitial(final List<Article> articles, final List<Cluster> clusters) {
        Cluster bestCluster = clusters.get(0);
        double maxSimilarity, tempSimilarity;
        for (final Article article : articles) {
            maxSimilarity = -1;
            for (final Cluster cluster : clusters) {
                if ((tempSimilarity = DocumentSimilarity.findDocumentSimilarities(cluster, article)) > maxSimilarity) {
                    maxSimilarity = tempSimilarity;
                    bestCluster = cluster;
                }
            }
            bestCluster.addArticle(article);
        }
    }

    private static void evaluateClusters(final List<Cluster> clusters) {
        if (++iteration != maxIterations) {
            System.out.println("iteration " + iteration + " out of a potential " + maxIterations);
            boolean mustRetry = false;
            Cluster bestCluster = clusters.get(0);
            double maxSimilarity, tempSimilarity;
            for (final Cluster cluster : clusters) {
                for (final Article article : cluster.getArticles()) {
                    maxSimilarity = -1;
                    for (final Cluster comparisonCluster : clusters) {
                        if ((tempSimilarity = DocumentSimilarity.findDocumentSimilarities(comparisonCluster, article)) > maxSimilarity) {
                            maxSimilarity = tempSimilarity;
                            bestCluster = comparisonCluster;
                        }
                    }

                    if (!bestCluster.equals(cluster)) {
                        cluster.removeArticle(article);
                        bestCluster.addArticle(article);
                    }
                }

                mustRetry = cluster.clearRemovalSet() || mustRetry;
            }

            if (mustRetry) {
                evaluateClusters(clusters);
            }
        }
    }
}