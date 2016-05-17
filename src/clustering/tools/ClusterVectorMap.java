package clustering.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import clustering.models.Article;

/**
 * Created by Liam on 16-May-16.
 */
public class ClusterVectorMap extends HashMap<String, Double> {
    private Set<Article> articles;
    private double vectorSum;

    public ClusterVectorMap() {
        articles = new HashSet<>();
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public double getVectorSum() {
        return vectorSum;
    }

    /**
     * @param additionSet
     * @param removalSet
     * @return true if changed
     */
    public boolean updateVector(final Set<Article> additionSet, final Set<Article> removalSet) {
        if (additionSet.size() > 0 || removalSet.size() > 0) {
            convertToTotal();

            for (final Article article : additionSet) {
                putAll(article.getVector());
            }

            articles.addAll(additionSet);

            for (final Article article : removalSet) {
                removeAll(article.getVector());
            }
            articles.removeAll(removalSet);

            convertToAverage();

            vectorSum = DocumentSimilarity.getVectorSum(this);

            additionSet.clear();
            removalSet.clear();

            return true;
        }

        return false;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Double> m) {
        if (isEmpty()) {
            super.putAll(m);
        }
        else {
            for (final Entry<? extends String, ? extends Double> newItem : m.entrySet()) {
                putWithAddition(newItem.getKey(), newItem.getValue());
            }
        }
    }

    private void putWithAddition(final String key, final Double value) {
        Double tempValue;
        if ((tempValue = get(key)) == null) {
            tempValue = 0.0;
        }

        super.put(key, tempValue + value);
    }

    private void removeAll(final Map<? extends String, ? extends Double> m) {
        for (final Entry<? extends String, ? extends Double> newItem : m.entrySet()) {
            putWithSubtraction(newItem.getKey(), newItem.getValue());
        }
    }

    private void putWithSubtraction(final String key, final Double value) {
        final Double tempValue = get(key) - value;

        if (tempValue == 0.0) {
            remove(key);
        }
        else {
            put(key, tempValue);
        }
    }

    private void convertToTotal() {
        for (final Map.Entry<String, Double> item : entrySet()) {
            put(item.getKey(), item.getValue() * articles.size());
        }
    }

    private void convertToAverage() {
        for (final Map.Entry<String, Double> item : entrySet()) {
            put(item.getKey(), item.getValue() / articles.size());
        }
    }

    public void print() {
        entrySet().forEach(System.out::println);
        System.out.println("Articles = " + articles.size());
    }
}
