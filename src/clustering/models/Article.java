package clustering.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import clustering.tools.DocumentSimilarity;

/**
 * An article from my collection that contains a full vector of weightings
 * using TFIDF for each unique word
 * Created by Liam on 23-Apr-16.
 */
public class Article {
    @SerializedName("topics")
    private String[] topics;//Topics known to be relevant
    @SerializedName("title")
    private String title;//Title of article
    @SerializedName("body")
    private String body = "";//The body text
    @SerializedName("vectorSum")
    private double vectorSum = -1;

    @SerializedName("articleVector")
    private Map<String, Double> articleVector = new HashMap<>();

    private int cluster;

    private transient boolean requiresBodyText;

    public int getCluster() {
        return cluster;
    }

    public void setCluster(final int cluster) {
        this.cluster = cluster;
    }

    public String getBody() {
        return body;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTopics(final String[] topics) {
        this.topics = topics;
    }

    public String[] getTopics() {
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
