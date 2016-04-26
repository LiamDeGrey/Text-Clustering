package clustering.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import clustering.models.Article;
import clustering.models.Cluster;

/**
 * Created by Liam on 26/04/2016.
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of articles
    private int K = 3;

    private List<Article> articles;
    private List<Cluster> clusters;

    public KMeans(final List<Article> articles) {
        this.articles = articles;
        this.clusters = new ArrayList<>();

        init();
        calculate();
    }

    //Initializes the process
    public void init() {

        //Create Clusters
        //Set Random Centroids
        final HashSet<Integer> randoms = new HashSet<>();
        while (randoms.size() != K) {
            randoms.add((int) (Math.random() * (articles.size() - 1)));
        }

        for (final Integer random : randoms) {
            Cluster cluster = new Cluster(random);
            cluster.setCentroid(articles.get(random));
            clusters.add(cluster);
        }

        //Print Initial state
        plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < K; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while (!finish) {
            //Clear cluster state
            clearClusters();

            List<Article> lastCentroids = getCentroids();

            //Assign articles to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List<Article> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += DocumentSimilarity.findDocumentSimilarities(lastCentroids.get(i), currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();

            if (distance == 0) {
                finish = true;
            }
        }
    }

    private void clearClusters() {
        clusters.forEach(Cluster::clear);
    }

    private List<Article> getCentroids() {
        return clusters.stream().map(Cluster::getCentroid).collect(Collectors.toList());
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for (final Article article : articles) {
            min = max;
            for (int i = 0; i < K; i++) {
                Cluster c = clusters.get(i);
                distance = DocumentSimilarity.findDocumentSimilarities(article, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            article.setCluster(cluster);
            clusters.get(cluster).addArticle(article);
        }
    }

    private void calculateCentroids() {
        for (Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Article> list = cluster.getArticles();
            int n_points = list.size();

            for (final Article article : list) {
                sumX += article.pointX;
                sumY += article.pointY;
            }

            Article centroid = cluster.getCentroid();
            if (n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.pointX = newX;
                centroid.pointY = newY;
            }
        }
    }
}