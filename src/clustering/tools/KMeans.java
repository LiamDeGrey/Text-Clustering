package clustering.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import clustering.models.Article;
import clustering.models.Cluster;
import clustering.models.Point;

/**
 * Created by Liam on 26/04/2016.
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of articles
    private int NUM_CLUSTERS = 3;
    //Number of Points
    private int NUM_POINTS = 15;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;

    private List<Article> articles;
    private List<Cluster> clusters;

    public KMeans() {
        this.articles = new ArrayList<>();
        this.clusters = new ArrayList<>();
    }

    public static void main(String[] args) {

        KMeans kmeans = new KMeans();
        kmeans.init();
        kmeans.calculate();
    }

    protected static Point createRandomPoint(int min, int max) {
        Random r = new Random();
        double x = min + (max - min) * r.nextDouble();
        double y = min + (max - min) * r.nextDouble();
        return new Point(x, y);
    }

    protected static List<Point> createRandomPoints(int min, int max, int number) {
        List<Point> points = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            points.add(createRandomPoint(min, max));
        }
        return points;
    }

    //Initializes the process
    public void init() {

        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);
            Point centroid = createRandomPoint(MIN_COORDINATE, MAX_COORDINATE);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        //Print Initial state
        plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < NUM_CLUSTERS; i++) {
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

            List lastCentroids = getCentroids();

            //Assign articles to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += distance(lastCentroids.get(i), currentCentroids.get(i));
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

    protected static double distance(Point p, Point centroid) {
        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List<Cluster> getCentroids() {
        final List<Cluster> clusters2 = new ArrayList<>();
        Collections.addAll(clusters2, clusters);
        return clusters2;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for (final Article article : articles) {
            min = max;
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = clusters.get(i);
                distance = Point.distance(article, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            article.setCluster(cluster);
            clusters.get(cluster).addPoint(article);
        }
    }

    private void calculateCentroids() {
        for (Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Article> list = cluster.getArticles();
            int n_points = list.size();

            for (final Article article : list) {
                sumX += article.getPoint().getX();
                sumY += article.getPoint().getY();
            }

            Point centroid = cluster.getCentroid();
            if (n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}