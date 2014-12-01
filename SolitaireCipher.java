import java.util.*;

public class SolitaireCipher {
  private String message;
  private String ciphertext;
  private Deck keyDeck;
  
  public SolitaireCipher(String toEncrypt) {
    keyDeck = new Deck();
    message = reformat(toEncrypt);
    encrypt();
  }
  
  public SolitaireCipher(String toEncrypt, Deck toUse) {
    message = reformat(toEncrypt);
    keyDeck = toUse;
    encrypt();
  }
  
  /* Reformats the given input string to five-letter upercase blocks of
   * only alphabetical characters. */
  public String reformat(String input) {
    input = input.toUpperCase();
    String firstReformat = "";
    String reformatted = "";
    
    for (int i = 0; i < input.length(); i++) { // add only alphabetical characters to the reformatted.v1 string
      if ((input.charAt(i) < 65) || (input.charAt(i) > 90))
        continue;
      else firstReformat+=input.charAt(i);
    }
    
    while (!(firstReformat.length()%5 == 0)) //pad with X's
      firstReformat+="X";
    
    int insertSpace = 5;
    
    while (insertSpace <= firstReformat.length()) { // finally, insert the spaces
      reformatted += firstReformat.substring((insertSpace-5),insertSpace) + " ";
      insertSpace+=5;
    }
    
    return reformatted.trim();
  }
  
  public void encrypt() {
    int nonSpaceChars = message.length() - (message.length()-5)/6;
    char[] messageLetters = new char[nonSpaceChars];
    char[] keystreamLetters = new char[nonSpaceChars];
    char[] cipherLetters = new char[nonSpaceChars];
    int j = 0; // keeps track of position in messageLetters when skipping the space characters in the message
    
    for (int i = 0; i < message.length(); i++) {
      if (!Character.isSpaceChar(message.charAt(i))) {
        messageLetters[j] = message.charAt(i);
        j++;
      }
    }
    
    for (int i = 0; i < keystreamLetters.length; i++)
      keystreamLetters[i] = keyDeck.generateKeyChar();
      
    for (int i = 0; i < cipherLetters.length; i++)
      cipherLetters[i] = (char)((messageLetters[i] + keystreamLetters[i] + 1)%26 + 65); // the +1 is to readjust from 0-indexing, so A gives a shift of 1, etc.
    
    String ciphertext = new String(cipherLetters);
    this.ciphertext = reformat(ciphertext);
  }
  
  public String decrypt(String ciphertext, Deck keyDeck) {
    int nonSpaceChars = ciphertext.length() - (ciphertext.length()-5)/6;
    char[] cipherLetters = new char[nonSpaceChars];
    char[] keystreamLetters = new char[nonSpaceChars];
    char[] plainLetters = new char[nonSpaceChars];
    int j = 0;
    
    for (int i = 0; i < ciphertext.length(); i++) {
      if (!Character.isSpaceChar(ciphertext.charAt(i))) {
        cipherLetters[j] = ciphertext.charAt(i);
        j++;
      }
    }
    
    for (int i = 0; i < keystreamLetters.length; i++)
      keystreamLetters[i] = keyDeck.generateKeyChar();
    
    for (int i = 0; i < plainLetters.length; i++)
      plainLetters[i] = (char)((cipherLetters[i] - keystreamLetters[i] + 25)%26 + 65);
    
    String plaintext = new String(plainLetters);
    return reformat(plaintext);
  }
  
  public String getCiphertext() {
    return ciphertext;
  }
  
  public static void main(String[] args) {
    SolitaireCipher x = new SolitaireCipher("what's going on??");
    x.encrypt();
    System.out.println(x.getCiphertext());
    Deck deck = new Deck();
    String s = x.decrypt("CLEPK HHNIY CFPWH FDFEH", deck);
    System.out.println(s);
    Deck deck2 = new Deck();
    String t = x.decrypt("ABVAW LWZSY OORYK DUPVH", deck2);
    System.out.println(t);
    
    Deck keyed = new Deck();
    keyed.shuffle();
    keyed.shuffle();
    
    SolitaireCipher secret = new SolitaireCipher("what's going on?", keyed);
    System.out.println(secret.getCiphertext());
    
    Deck wontWork = new Deck();
    Deck willWork = new Deck();
    willWork.shuffle();
    willWork.shuffle();
    
    System.out.println(secret.decrypt("XSXXI ZEEDX XZWXA", wontWork));
    System.out.println(secret.decrypt("XSXXI ZEEDX XZWXA", willWork));
  }
}