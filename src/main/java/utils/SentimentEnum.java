package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum SentimentEnum {
    ANGER(1,"dataset_dt_anger_60k.txt", "Anger"),
    ANTICIPATION(2,"dataset_dt_anticipation_60k.txt", "Anticipation"),
    DISGUST(3,"dataset_dt_disgust_60k.txt", "Disgust"),
    FEAR(4,"dataset_dt_fear_60k.txt", "Fear"),
    JOY(5,"dataset_dt_joy_60k.txt", "Joy"),
    SADNESS(6,"dataset_dt_sadness_60k.txt", "Sadness"),
    SURPRISE(7,"dataset_dt_surprise_60k.txt", "Surprise"),
    TRUST(8,"dataset_dt_trust_60k.txt", "Trust");
	
	private int tableId;
	private String fileName;
	private String sentimentName;

	private SentimentEnum(int tableId, String fileName, String sentimentName){
		this.tableId = tableId;
		this.fileName = fileName;
		this.sentimentName = sentimentName;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public static Set<String> toStringSet(){
		Set<String> set = new HashSet<>();
		set.add(ANGER.name());
		set.add(ANTICIPATION.name());
		set.add(DISGUST.name());
		set.add(FEAR.name());
		set.add(JOY.name());
		set.add(SADNESS.name());
		set.add(SURPRISE.name());
		set.add(TRUST.name());
		
		return set;
	}
	
	public static Set<Integer> getIds(){
		Set<Integer> result = new HashSet<>();
		result.add(ANGER.getTableId());
		result.add(ANTICIPATION.getTableId());
		result.add(DISGUST.getTableId());
		result.add(FEAR.getTableId());
		result.add(JOY.getTableId());
		result.add(SADNESS.getTableId());
		result.add(SURPRISE.getTableId());
		result.add(TRUST.getTableId());
		return result;
	}
	
	public static SentimentEnum getSentimentFromId(Integer id){
		for(SentimentEnum sentiment : SentimentEnum.values()){
			if(sentiment.getTableId() == id){
				return sentiment;
			}
		}
		return null;
	}
	
	public static Map<Integer, String> getMap(){
		HashMap<Integer, String> map = new HashMap<>();
		map.put(ANGER.tableId, ANGER.sentimentName);
		map.put(ANTICIPATION.tableId, ANTICIPATION.sentimentName);
		map.put(DISGUST.tableId, DISGUST.sentimentName);
		map.put(FEAR.tableId, FEAR.sentimentName);
		map.put(JOY.tableId, JOY.sentimentName);
		map.put(SADNESS.tableId, SADNESS.sentimentName);
		map.put(SURPRISE.tableId, SURPRISE.sentimentName);
		map.put(TRUST.tableId, TRUST.sentimentName);
		return map;
	}

	public static Map<Integer, String> getFileMap(){
		HashMap<Integer, String> map = new HashMap<>();
		map.put(ANGER.tableId, ANGER.fileName);
		map.put(ANTICIPATION.tableId, ANTICIPATION.fileName);
		map.put(DISGUST.tableId, DISGUST.fileName);
		map.put(FEAR.tableId, FEAR.fileName);
		map.put(JOY.tableId, JOY.fileName);
		map.put(SADNESS.tableId, SADNESS.fileName);
		map.put(SURPRISE.tableId, SURPRISE.fileName);
		map.put(TRUST.tableId, TRUST.fileName);
		return map;
	}


}
