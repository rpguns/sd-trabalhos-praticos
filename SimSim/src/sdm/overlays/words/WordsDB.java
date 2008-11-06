package sdm.overlays.words;

import java.io.* ;
import java.util.* ;

import simsim.utils.*;

public class WordsDB {

	static RandomList<Word> words = new RandomList<Word>() ;
		
	public static Word randomWord() {
		return words.randomElement() ;
	}
	
	public static Set<Word> randomWords( int numOfWords) {
		HashSet<Word> res = new HashSet<Word>() ;
		while( res.size() < Math.min( numOfWords, words.size()) )
				res.add( randomWord() ) ;
		
		return res ;
	}
	
	public static void init() {
		try {
			System.err.print("Reading words...") ;
			FileInputStream fis = new FileInputStream("src/sdm/overlays/words/port5000") ;
			Scanner s = new Scanner( fis ) ;
			while( s.hasNextLine() )
				words.add( new Word( s.nextLine() ) ) ;

			s.close() ;
			fis.close() ;
			System.err.println("done. Total words:" + words.size() ) ;
							
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
