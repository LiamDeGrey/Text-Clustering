package clustering.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import clustering.models.Article;
import clustering.models.Cluster;

/**
 * A tool used to cluster a list articles
 * Created by Liam on 26/04/2016.
 */
public class KMeansClusterTool {
    private static final int MAX_ITERATIONS_CLUSTERING = 20;

    private static int iteration = 0;

    public static void clusterArticles(final List<Article> finalArticles) {
        System.out.println("CLUSTERING BEGINNING");

        final int k = findK(finalArticles);

        final List<Article> articles = new ArrayList<>();
        final List<Cluster> clusters = new ArrayList<>();

        System.out.println(String.format("Attempting to converge with %d clusters", k));

        articles.clear();
        clusters.clear();

        articles.addAll(finalArticles);

        Cluster initialCluster;
        Article article;
        int random;
        for (int i = 0; i < k; i++) {
            random = (int) (Math.random() * (articles.size() - 1));
            initialCluster = new Cluster();
            article = articles.get(random);
            initialCluster.addArticle(article);
            article.setCluster(initialCluster);
            articles.remove(random);
            clusters.add(initialCluster);
        }

        recalculateClusters(clusters);

        iteration = 0;
        evaluateClusters(finalArticles, clusters);

        int size = 0;
        for (Cluster cluster : clusters) {
            System.out.println("Clusters size = " + cluster.getArticles().size());
            size += cluster.getArticles().size();
        }
        System.out.println("TOTAL SIZE = " + size);

        System.out.println();
        System.out.println(String.format("When using %d clusters, accuracy was %.2f%%", k, checkPerformance(clusters)));
    }

    private static int findK(final List<Article> articles) {
        final Set<String> topics = new HashSet<>();
        for (final Article article : articles) {
            topics.addAll(article.getTopics());
        }
        return topics.size();
    }

    private static void evaluateClusters(final List<Article> articles, final List<Cluster> clusters) {
        if (++iteration != MAX_ITERATIONS_CLUSTERING) {
            Cluster bestFitCluster = clusters.get(0);
            double maxSimilarity, tempSimilarity;
            int articleIndex = 0;
            for (final Article article : articles) {
                if ((++articleIndex % 100) == 0) {
                    System.out.println(String.format("Iteration : %d of a potential %d; Evaluating Article %d of %d", iteration, MAX_ITERATIONS_CLUSTERING, articleIndex, articles.size()));
                }
                maxSimilarity = Double.MIN_VALUE;
                for (final Cluster cluster : clusters) {
                    if ((tempSimilarity = DocumentSimilarity.findDocumentSimilarities(cluster, article)) > maxSimilarity) {
                        maxSimilarity = tempSimilarity;
                        bestFitCluster = cluster;
                    }
                }
                if (article.getCluster() == null) {
                    bestFitCluster.addArticle(article);
                    article.setCluster(bestFitCluster);
                }
                else {
                    if (!article.getCluster().equals(bestFitCluster)) {
                        article.getCluster().removeArticle(article);
                        bestFitCluster.addArticle(article);
                        article.setCluster(bestFitCluster);
                    }
                }
            }

            if (recalculateClusters(clusters)) {
                evaluateClusters(articles, removeRedundantClusters(clusters));
            }
        }
    }

    private static boolean recalculateClusters(final List<Cluster> clusters) {
        boolean mustRetry = false;
        int index = 0;
        for (final Cluster cluster : clusters) {
            mustRetry = cluster.recalculateVector() || mustRetry;
            System.out.println("MUST RETRY " + index++ + "? " + mustRetry);
        }

        return mustRetry;
    }

    private static List<Cluster> removeRedundantClusters(final List<Cluster> clusters) {
        return clusters
                .stream()
                .filter(cluster -> cluster.getArticles().size() > 0)
                .collect(Collectors.toList());
    }

    private static double checkPerformance(final List<Cluster> clusters) {
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

        return score * 100 / total;
    }
}