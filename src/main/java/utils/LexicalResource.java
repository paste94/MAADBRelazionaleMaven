package utils;

public class LexicalResource {
	
	private Integer id;
	private String word;
	private Integer emosnFreq;
	private Integer nrcFreq;
	private Integer sentisenseFreq;

	public LexicalResource(String word, Integer sentimentIdFk, String resType) {
		
		this.word = word;
		this.sentimentIdFk = sentimentIdFk;
		switch (resType) {
			case "EmoSN":
				this.emosnFreq = 1;
				this.nrcFreq = 0;
				this.sentisenseFreq = 0;
				break;
			case "NRC":
				this.emosnFreq = 0;
				this.nrcFreq = 1;
				this.sentisenseFreq = 0;
				break;
			case "sentisense":
				this.emosnFreq = 0;
				this.nrcFreq = 0;
				this.sentisenseFreq = 1;
				break;
		}
	}

	public void addFreq(String resType){
		switch (resType) {
			case "EmoSN":
				this.emosnFreq++;
				break;
			case "NRC":
				this.nrcFreq++;
				break;
			case "sentisense":
				this.sentisenseFreq++;
				break;
		}
	}

	private Integer sentimentIdFk;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Integer getEmosnFreq() {
		return emosnFreq;
	}
	public void setEmosnFreq(Integer emosnFreq) {
		this.emosnFreq = emosnFreq;
	}
	public Integer getNrcFreq() {
		return nrcFreq;
	}
	public void setNrcFreq(Integer nrcFreq) {
		this.nrcFreq = nrcFreq;
	}
	public Integer getSentisenseFreq() {
		return sentisenseFreq;
	}
	public void setSentisenseFreq(Integer sentisenseFreq) {
		this.sentisenseFreq = sentisenseFreq;
	}
	public Integer getSentimentIdFk() {
		return sentimentIdFk;
	}
	public void setSentimentIdFk(Integer sentimentIdFk) {
		this.sentimentIdFk = sentimentIdFk;
	}
	
	@Override
	public String toString() {
		return "utils.LexicalResource [word=" + word + ", emosnFreq=" + emosnFreq + ", nrcFreq=" + nrcFreq
				+ ", sentisenseFreq=" + sentisenseFreq + ", sentimentIdFk=" + sentimentIdFk + "]";
	}
	
	
	

}
