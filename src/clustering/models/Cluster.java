package clustering.models;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import clustering.tools.ClusterVectorMap;

/**
 * A class to represent each individual cluster holding
 * a variety of articles
 * Created by Liam on 26/04/2016.
 */
public class Cluster implements Measurable {
    private ClusterVectorMap centroidVector;
    private Set<Article> additionSet;
    private Set<Article> removalSet;

    public Cluster() {
        centroidVector = new ClusterVectorMap();
        additionSet = new HashSet<>();
        removalSet = new HashSet<>();
    }

    public Set<Article> getArticles() {
        return centroidVector.getArticles();
    }

    @Override
    public Map<String, Double> getVector() {
        return centroidVector;
    }

    @Override
    public double getVectorSum() {
        return centroidVector.getVectorSum();
    }

    public void addArticle(final Article article) {
        additionSet.add(article);
    }

    public void removeArticle(final Article article) {
        removalSet.add(article);
    }

    public boolean recalculateVector() {
        return centroidVector.updateVector(additionSet, removalSet);
    }
}
