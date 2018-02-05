import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.tartarus.snowball.SnowballStemmer;

import lib.com.google.gson.Gson;
import lib.com.google.gson.GsonBuilder;

public class IndexTheText {

	ResourceBundle mybundle = null;
	//the properties file
	private final String PROPERTIES_FILE="resources/file";
	//the words to exclude
	private String[] myExcludedWords;
	//the text to index
	private String mySentences = "";
	private SnowballStemmer theStemmer;
	/*
	 * Constructor : initialize properties
	 */
	public IndexTheText() throws Exception {
		try {
		initBundle() ;
		}
		catch (MissingResourceException e) {
			System.out.println("The properties file is not found");
			throw new Exception(e);
		}
		try {
			//the words to exclude must be initialized
			initExcludedWords();
		}
		catch (MissingResourceException mre)
		{
			System.out.println("The Exclude File is not found");
			//continue
		}
		catch(Exception e) {
			System.out.println("The words to exclure have not been found");
			//continue
		}
		try
		{
			//the text to index must be initialized
			loadFile();
		}
		catch (IOException e) {
			System.out.println("The file to index is not found");
			throw new Exception(e);
		}
		catch (Exception e) {
			System.out.println("The file to index has a problem");
			throw new Exception(e);
		}
		try
		{
			//the class to Stem must be initialized
			initStem();
		} catch (ClassNotFoundException e) {
			System.out.println("Library for Steam not found");
			throw new Exception(e);
		} catch (InstantiationException e) {
			System.out.println("Cannot instanciate the stemmer");
			throw new Exception(e);
		} catch (IllegalAccessException e) {
			System.out.println("Cannot access the stemmer class");
			throw new Exception(e);
		}		 
	}
/**
 * initialize the bundle with the properties file
 */
	private void initBundle()  
	{

		//find the file to scan
		mybundle = ResourceBundle.getBundle(PROPERTIES_FILE);

	}
/**
 * Load the file to index into a String
 * @throws IOException
 */
	private void loadFile() throws IOException 
	{
		
		if(mybundle!=null)
		{
			
		BufferedInputStream in;
		StringWriter out = new StringWriter();
		String testFile = mybundle.getString("fileName");
		
		File f = new File(testFile);
		FileInputStream fileinputS= new FileInputStream(f);
		in = new BufferedInputStream(fileinputS);

		//read the text and put it into string
		int b;
		while ((b=in.read()) != -1)
			out.write(b);
		out.flush();
		out.close();
		in.close();

		mySentences=out.toString();
		}
	}
/**
 * The excluded words from the file in the property into String[]
 */
	private void initExcludedWords()
	{
		if(mybundle!=null)
		{
			//take the good line
			String excludeWordsConfigured = mybundle.getString("excludeWords");

			//split the words
			myExcludedWords = excludeWordsConfigured.split(",");
		}
	}

	/**
	 * Init the Stem Class from the language in properties
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initStem() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{

		String theLanguage = "";
		if(mybundle!=null)
		{
			//take the good line
			theLanguage = mybundle.getString("StemLang");
		}
		//create the class name from property
		String className =  "org.tartarus.snowball.ext." + 
				theLanguage.toLowerCase() + "Stemmer";

		Class<?> stemClass = Class.forName(className);
		theStemmer = (SnowballStemmer) stemClass.newInstance();

	}

	/**
	 * Count, sort and print
	 */
	public void countAndDisplayWords() {

		//count each words
		Map<String, WordOccurenceSentence> mapCounter = countOccurrences(mySentences);
		//sort words
		List<WordOccurenceSentence> sortedEntriesWordOccurenceSentence=	sortEntries(mapCounter);
		//print results
		printResultToJson(sortedEntriesWordOccurenceSentence);  
	}




	/**
	 * Count the occurrences of all words in the text
	 * @param counter
	 * @param text
	 */
	private Map<String, WordOccurenceSentence> countOccurrences(String text) {

		Map<String,WordOccurenceSentence> mapCounter = new HashMap<String, WordOccurenceSentence>();
		//separate by point to separate sentences
		StringTokenizer stringTokenizerBySentence = new StringTokenizer(text, ".");
		Integer sentenceCounter =0;
		while (stringTokenizerBySentence.hasMoreElements()) {
			String sentence = stringTokenizerBySentence.nextToken();
			//erase all ponctuation but not spaces
			sentence= sentence.replaceAll("[^A-Za-z0-9 ]", "");
			//separate by space
			StringTokenizer stringTokenizer = new StringTokenizer(sentence, " ");
			
			while (stringTokenizer.hasMoreElements()) {
				String word = stringTokenizer.nextToken();
				//to lower case ("A" and "a" are the same)
				word= word.toLowerCase();

				//take the stem of the word
				theStemmer.setCurrent(word);
				//possible to stem several times
				theStemmer.stem();
				String myStem = theStemmer.getCurrent();

				Boolean wordFounded=false;
				//filter words 
				for(String strExcluded:myExcludedWords)
				{
					if(strExcluded.equals(myStem))
					{
						wordFounded=true;
					}
				}
				//only if its not forbidden
				if(wordFounded==false)
				{
					//find the stem of the word in the list
					WordOccurenceSentence wordOccurence= mapCounter.get(myStem);
					if (wordOccurence == null) {
						wordOccurence=new WordOccurenceSentence(word,myStem,sentenceCounter);
					}
					else
					{
						wordOccurence.addWord(word);	
						wordOccurence.increaseCounter();
						wordOccurence.addSentenceNumber(sentenceCounter);
					}
					//put the word and its new counter
					mapCounter.put(myStem, wordOccurence);
				}

			}

			sentenceCounter ++;
		}

		return mapCounter;
	}

/**
 * Sort by Stem (compareTo of WordOccurenceSentence )
 * @param mapCounter
 * @return
 */
	private List<WordOccurenceSentence>  sortEntries(Map<String, WordOccurenceSentence> mapCounter) {

		List<WordOccurenceSentence> sortedEntriesWordOccurenceSentence = new ArrayList<>();
		//for each words, final form to sort
		for (Entry<String, WordOccurenceSentence> entry : mapCounter.entrySet()) {
			//the word : the number of occurrences
			sortedEntriesWordOccurenceSentence.add(entry.getValue());
		}
		//sort
		Collections.sort(sortedEntriesWordOccurenceSentence);

		return sortedEntriesWordOccurenceSentence;


	}
/**
 * Print the list into Json form
 * @param myFinalWordsList
 */
	private void printResultToJson( List<WordOccurenceSentence> myFinalWordsList)
	{
		//pretty print in lines
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String json = gson.toJson(myFinalWordsList);

		System.out.println("results : " + json);

	}


}
