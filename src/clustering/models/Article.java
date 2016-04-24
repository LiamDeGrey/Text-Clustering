package clustering.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("documentWords")
    private List<DocumentWord> documentWords;

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

    public void setTopics(final String[] topics) {
        this.topics = topics;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setDocumentWords(final List<DocumentWord> documentWords) {
        this.documentWords = documentWords;
    }

    public List<DocumentWord> getDocumentWords() {
        return documentWords;
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
