import java.util.ArrayList;


import lib.com.google.gson.annotations.SerializedName;

public class WordOccurenceSentence implements Comparable<WordOccurenceSentence> {
	//not in Json: transient
	transient String stem;
	ArrayList<String> word;
	@SerializedName(value = "total-occurrences") 
	Integer counter;
	@SerializedName(value = "sentence-indexes")
	ArrayList<Integer> sentences;
	
	public WordOccurenceSentence(String word, String myStem, Integer sentence) {
		super();
		this.word = new ArrayList<>();
		this.word.add(word);
		this.stem= myStem;
		this.counter = 1;
		this.sentences = new ArrayList<>();
		this.sentences.add(sentence);
	}
	
public WordOccurenceSentence(String word,String myStem, Integer counter, ArrayList<Integer> sentences) {
		super();
		this.word = new ArrayList<>();
		this.word.add(word);
		this.stem= myStem;
		this.counter = counter;
		this.sentences = sentences;
	}



public String getStem() {
	return stem;
}

public void setStem(String stem) {
	this.stem = stem;
}

public Integer getCounter() {
	return counter;
}
public void setCounter(Integer counter) {
	this.counter = counter;
}
public void increaseCounter() {
	this.counter++;
}
public ArrayList<Integer> getSentences() {
	return sentences;
}
public void setSentences(ArrayList<Integer> sentences) {
	this.sentences = sentences;
}
public void addSentenceNumber(Integer sentenceNumber) {
	//add only if unique index
	if(!this.sentences.contains(sentenceNumber))
	{
		this.sentences.add(sentenceNumber);
	}
}

public void addWord(String word) {
	if(!this.word.contains(word))
	{
		this.word.add(word);		
	}
	
}

public ArrayList<String> getWord() {
	return word;
}

public void setWord(ArrayList<String> word) {
	this.word = word;
}




@Override
public int compareTo(WordOccurenceSentence w2) {

    return this.getStem().compareTo(w2.getStem());
}
}
