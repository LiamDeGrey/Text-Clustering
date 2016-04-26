package clustering.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 26/04/2016.
 */
public class Cluster {
    private List<Article> articles;
    private Point centroid;
    private int id;

    public Cluster(final int id) {
        this.id = id;
        articles = new ArrayList<>();
        centroid = null;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(final List<Article> articles) {
        this.articles = articles;
    }

    public void addArticle(final Article article) {
        articles.add(article);
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(final Point centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        articles.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id + "]");
        System.out.println("[Centroid: " + centroid + "]");
        System.out.println("[Points: \n");
        for (final Article article : articles) {
            System.out.println("x:" + article.getPoint().getX() + "y:" + article.getPoint().getY());
        }
        System.out.println("]");
    }
}
