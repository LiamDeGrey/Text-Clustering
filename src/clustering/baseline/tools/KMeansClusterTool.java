package clustering.baseline.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clustering.common.models.Article;
import clustering.common.models.Cluster;

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

        final int k = findK(articles);
        System.out.println("K = " + k);

        final List<Cluster> clusters = new ArrayList<>(k);

        Cluster initialCluster;
        int random;
        for (int i = 0; i < k; i++) {
            random = (int) (Math.random() * (articles.size() - 1));
            initialCluster = new Cluster();
            initialCluster.addArticle(articles.get(random));
            articles.remove(random);
            clusters.add(initialCluster);
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

        checkPerformance(clusters);
    }

    private static int findK(final List<Article> articles) {
        final Set<String> topics = new HashSet<>();
        for (final Article article : articles) {
            topics.addAll(article.getTopics());
        }
        return topics.size();
    }

    private static void populateClustersInitial(final List<Article> articles, final List<Cluster> clusters) {
        Cluster bestCluster = clusters.get(0);
        double maxSimilarity, tempSimilarity;
        double index = 0;
        for (final Article article : articles) {
            index++;
            maxSimilarity = -1;
            for (final Cluster cluster : clusters) {
                if ((tempSimilarity = DocumentSimilarity.findDocumentSimilarities(cluster, article)) > maxSimilarity) {
                    maxSimilarity = tempSimilarity;
                    bestCluster = cluster;
                }
            }
            bestCluster.addArticle(article);
            if (index % 200 == 0) {
                System.out.println(String.format("Cluster initialisation %.2f%% (Article %.0f of %d)", index * 100 / articles.size(), index, articles.size()));
            }
        }
    }

    private static void evaluateClusters(final List<Cluster> clusters) {
        if (++iteration != maxIterations) {
            System.out.println("iteration " + iteration + " out of a potential " + maxIterations);
            boolean mustRetry = false;
            Cluster bestCluster = clusters.get(0);
            double maxSimilarity, tempSimilarity;
            int clusterIndex = 0;
            for (final Cluster cluster : clusters) {
                clusterIndex++;
                System.out.println(String.format("Evaluating cluster %d of %d", clusterIndex, clusters.size()));
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

    private static void checkPerformance(final List<Cluster> clusters) {
        double score = 0;
        double total = 0;

        Map<String, Integer> topics = new HashMap<>();
        Integer topicValue;
        String commonTopic = "";
        int commonTopicValue;
        for (final Cluster cluster : clusters) {
            topics.clear();
            commonTopicValue = 0;
            for (final Article article : cluster.getArticles()) {
                for (final String topic : article.getTopics()) {
                    if ((topicValue = topics.get(topic)) == null) {
                        topicValue = 0;
                    }
                    topics.put(topic, topicValue + 1);

                    if (topicValue + 1 > commonTopicValue) {
                        commonTopic = topic;
                        commonTopicValue = topicValue + 1;
                    }
                }
            }
            for (final Article article : cluster.getArticles()) {
                if (article.getTopics().contains(commonTopic)) {
                    score++;
                }
                total++;
            }
        }

        System.out.println(String.format("K-Means using %d clusters, achieved an accuracy of %.2f%%", clusters.size(), score * 100 / total));
    }
}