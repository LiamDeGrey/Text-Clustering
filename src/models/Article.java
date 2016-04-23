package models;

/**
 * Created by Liam on 23-Apr-16.
 */
public class Article {
    //Within Reuters tag
    //private final boolean hasTopic;//Error prone data
    //private final String usedFor;//Training, Test or No-used
    //private final String cgiSplit;//Training-set or test-set
    //private final String oldId;//ID of article in old db
    //private final String newId;//ID in new db
//
    //private final String date;//Date of document
    private String[] topics;//Topics known to be relevant
    //private final List<String> places;//Places known to be relevant
    //private final List<String> people;//People known to be relevant
    //private final List<String> orgs;//Orgs known to be relevant
    //private final List<String> exchanges;//Exchanges known to be relevant
//
    ////Within Text tag
    //private final String author;//Article author
    //private final String dateLine;//Location of story, day of year
    private String title;//Title of article
    private String body = "";//The body text

    private boolean requiresBodyText;

    public Article() {

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

    public String getBody() {
        return body;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setTopics(final String[] topics) {
        this.topics = topics;
    }
}
