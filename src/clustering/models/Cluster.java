package clustering.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clustering.tools.DocumentSimilarity;

/**
 * A class to represent each individual cluster holding
 * a variety of articles
 * Created by Liam on 26/04/2016.
 */
public class Cluster {
    private List<Article> articles;
    private Map<String, Double> centroidVector;
    private Set<Article> removalSet;
    private double vectorSum;

    public Cluster() {
        articles = new ArrayList<>();
        centroidVector = new HashMap<>();
        removalSet = new HashSet<>();
    }

    public List<Article> getArticles() {
        return articles;
    }

    public Map<String, Double> getCentroidVector() {
        return centroidVector;
    }

    public void addArticle(final Article article) {
        addArticleVector(article);
        articles.add(article);
    }

    public void removeArticle(final Article article) {
        removeArticleVector(article);
        removalSet.add(article);
    }

    public boolean clearRemovalSet() {
        if (removalSet.size() == 0) {
            return false;
        }

        for (final Article article : removalSet) {
            articles.remove(article);
        }
        removalSet.clear();

        return true;
    }

    public double getVectorSum() {
        return vectorSum;
    }

    private void removeArticleVector(final Article article) {
        final int initialArticleSize = articles.size() - removalSet.size();
        final Map<String, Double> articleVector = article.getArticleVector();
        final Set<String> wordsForRemoval = new HashSet<>();

        double initialSum;
        Double articleValue;
        for (final Map.Entry<String, Double> centroidEntry : centroidVector.entrySet()) {
            initialSum = centroidEntry.getValue() * initialArticleSize;
            if ((articleValue = articleVector.get(centroidEntry.getKey())) != null) {
                initialSum -= articleValue;
            }
            if (initialSum != 0) {
                centroidVector.put(centroidEntry.getKey(), initialSum / (initialArticleSize - 1));
            }
            else {
                wordsForRemoval.add(centroidEntry.getKey());
            }
        }

        for (final String word : wordsForRemoval) {
            centroidVector.remove(word);
        }

        vectorSum = DocumentSimilarity.getVectorSum(centroidVector);
    }

    private void addArticleVector(final Article article) {
        final int initialArticleSize = articles.size();
        final Map<String, Double> articleVector = article.getArticleVector();

        double initialSum;
        Double articleValue;
        for (final Map.Entry<String, Double> centroidEntry : centroidVector.entrySet()) {
            initialSum = centroidEntry.getValue() * initialArticleSize;
            if ((articleValue = articleVector.get(centroidEntry.getKey())) != null) {
                initialSum += articleValue;
            }
            centroidVector.put(centroidEntry.getKey(), initialSum / (initialArticleSize + 1));
        }

        articleVector.entrySet().stream().filter(articleEntry -> centroidVector.get(articleEntry.getKey()) == null).forEach(articleEntry -> {
            centroidVector.put(articleEntry.getKey(), articleEntry.getValue() / (initialArticleSize + 1));
        });

        vectorSum = DocumentSimilarity.getVectorSum(centroidVector);
    }
}
