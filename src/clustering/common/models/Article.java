package clustering.common.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.common.tools.DocumentSimilarity;

/**
 * An article from my collection that contains a full vector of weightings
 * using TFIDF for each unique word
 * Created by Liam on 23-Apr-16.
 */
public class Article {
    private List<String> topics;//Topics known to be relevant
    private String title;//Title of article
    private String body = "";//The body text
    private double vectorSum = -1;
    private Map<String, Double> articleVector = new HashMap<>();

    private transient boolean requiresBodyText;

    public String getBody() {
        return body;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTopics(final List<String> topics) {
        this.topics = topics;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setArticleVector(final Map<String, Double> articleVector) {
        this.articleVector.putAll(articleVector);

        body = null;
    }

    public Map<String, Double> getArticleVector() {
        return articleVector;
    }

    public double getVectorSum() {
        if (vectorSum == -1) {
            vectorSum = DocumentSimilarity.getVectorSum(articleVector);
        }

        return vectorSum;
    }

    public void appendText(final String bodyText) {
        body += bodyText;
    }

    /**
     * a check to see if text is still being added to the body
     *
     * @return true if body tag has been opened but not yet closed
     */
    public boolean requiresBodyText() {
        return requiresBodyText;
    }

    public void startBody() {
        requiresBodyText = true;
    }

    public void finishBody() {
        body = body.replaceAll("\\s+", " ");
        requiresBodyText = false;
    }
}
