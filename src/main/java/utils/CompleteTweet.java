package utils;

public class CompleteTweet {
    private String tweet;
    private int sentimentId;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public int getSentimentId() {
        return sentimentId;
    }

    public void setSentimentId(int sentimentId) {
        this.sentimentId = sentimentId;
    }

    public CompleteTweet(String tweet, int sentimentId) {
        this.tweet = tweet;
        this.sentimentId = sentimentId;
    }

    @Override
    public String toString() {
        return "CompleteTweet{" +
                "tweet='" + tweet + '\'' +
                ", sentimentId=" + sentimentId +
                '}';
    }
}
