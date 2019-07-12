package utils;

import java.lang.annotation.Documented;

public class HashTag {
	private String word;
	private Integer sentimentIdFk;
	private Integer frequence;

	public HashTag(String word, Integer sentimentIdFk) {
		this.word = word;
		this.sentimentIdFk = sentimentIdFk;
		this.frequence = 1;
	}

	public void add(){
		this.frequence++;
	}

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Integer getSentimentIdFk() {
		return sentimentIdFk;
	}
	public void setSentimentIdFk(Integer sentimentIdFk) {
		this.sentimentIdFk = sentimentIdFk;
	}

	@Override
	public boolean equals(Object obj) {
		HashTag other = (HashTag)obj;
		return word.equals(other.getWord()) && sentimentIdFk.equals(other.getSentimentIdFk());
	}
}
