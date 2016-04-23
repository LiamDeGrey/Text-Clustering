package models;

/**
 * Created by Liam on 23-Apr-16.
 */
public class Article {
    private String[] topics;//Topics known to be relevant
    private String title;//Title of article
    private String body = "";//The body text

    private boolean requiresBodyText;

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
        requiresBodyText = false;
    }
}
