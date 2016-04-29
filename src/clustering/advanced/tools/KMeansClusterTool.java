package clustering.advanced.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clustering.common.models.Article;
import clustering.common.models.Cluster;
import clustering.common.tools.DocumentSimilarity;

/**
 * A tool used to cluster a list articles
 * Created by Liam on 26/04/2016.
 */
public class KMeansClusterTool {
    private static final int MAX_ITERATIONS_CLUSTERING = 20;
    private static final int CLUSTER_TRIALS = 10;

    private static int iteration = 0;

    public static void clusterArticles(final List<Article> finalArticles) {
        System.out.println("CLUSTERING BEGINNING");

        final int lastK = findK(finalArticles);
        final int firstK = 1 + lastK - CLUSTER_TRIALS;

        final List<Article> articles = new ArrayList<>();
        final List<Cluster> clusters = new ArrayList<>();

        int[] kValue = new int[CLUSTER_TRIALS];
        double[] accuracy = new double[CLUSTER_TRIALS];
        double[] silhouetteValues = new double[CLUSTER_TRIALS];
        for (int k = firstK; k <= lastK; k++) {
            System.out.println(String.format("Attempting to converge with %d clusters", k));

            articles.clear();
            clusters.clear();

            articles.addAll(finalArticles);

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

            int size = 0;
            for (Cluster cluster : clusters) {
                System.out.println("Clusters size = " + cluster.getArticles().size());
                size += cluster.getArticles().size();
            }
            System.out.println("TOTAL SIZE = " + size);

            iteration = 0;
            evaluateClusters(clusters);

            size = 0;
            for (Cluster cluster : clusters) {
                System.out.println("Clusters size = " + cluster.getArticles().size());
                size += cluster.getArticles().size();
            }
            System.out.println("TOTAL SIZE = " + size);

            kValue[k - firstK] = k;
            accuracy[k - firstK] = checkPerformance(clusters);
            //silhouetteValues[k - firstK] = clusterValidation(clusters);
        }

        System.out.println();
        for (int i = 0; i < kValue.length; i++) {
            System.out.println(String.format("When using %d clusters, accuracy was %.2f and Silhouette value was %.2f", kValue[i], accuracy[i], silhouetteValues[i]));
        }
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
        if (++iteration != MAX_ITERATIONS_CLUSTERING) {
            System.out.println("iteration " + iteration + " out of a potential " + MAX_ITERATIONS_CLUSTERING);
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

        final double accuracy = score * 100 / total;
        System.out.println(String.format("K-Means using %d clusters, achieved an accuracy of %.2f%%", clusters.size(), accuracy));

        return accuracy;
    }

    private static double clusterValidation(final List<Cluster> clusters) {
        return SilhouetteValidation.getSilhouetteValue(clusters);
    }
}