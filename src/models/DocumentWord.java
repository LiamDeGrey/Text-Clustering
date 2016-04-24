package models;

import com.google.gson.annotations.SerializedName;

/**
 * A word to be used in each article's document vector.
 * Contains it's weight for that document and the String
 * Created by Liam on 24-Apr-16.
 */
public class DocumentWord {
    @SerializedName("word")
    private final String word;
    @SerializedName("weight")
    private double weight;

    private transient double termFrequency;
    private transient double inverseDocumentFrequency;


    public DocumentWord(final String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setTermFrequency(final double termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void setInverseDocumentFrequency(final double inverseDocumentFrequency) {
        this.inverseDocumentFrequency = inverseDocumentFrequency;

        weight = termFrequency * inverseDocumentFrequency;
    }

    public double getWeight() {
        return weight;
    }
}
