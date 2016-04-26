package clustering.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import clustering.models.Article;
import clustering.models.Cluster;

/**
 * Created by Liam on 26/04/2016.
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of articles
    private int K = 10;

    private int maxIterations = 20;

    private List<Article> articles;
    private List<Cluster> clusters;

    public KMeans(final List<Article> articles) {
        this.articles = articles;
        this.clusters = new ArrayList<>();

        init();
        //calculate();
    }

    //Initializes the process
    public void init() {

        //Create Clusters
        //Set Random Centroids
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

        //Print Initial state
        plotClusters();

        populateClustersInitial();

        for (Cluster cluster : clusters) {
            System.out.println("Clusters size = " + cluster.getArticles().size());
        }

        evaluateClusters();

        for (Cluster cluster : clusters) {
            System.out.println("Clusters size = " + cluster.getArticles().size());
        }
    }

    private void populateClustersInitial() {
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

        articles = null;
    }

    private int iteration = 0;

    private void evaluateClusters() {
        if (++iteration != maxIterations) {
            System.out.println("iteration " + iteration + " out of " + maxIterations);
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
                mustRetry = mustRetry || cluster.clearRemovalSet();
            }

            if (mustRetry) {
                evaluateClusters();
            }
        }
    }

    private void plotClusters() {
        for (int i = 0; i < K; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }
}