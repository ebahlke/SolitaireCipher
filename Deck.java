/* Author: Emma Bahlke
 * Date: April 6, 2014
 * Name: Deck.java
 * Description: This class represents a deck of 52 cards (see the Card class). It allows shuffling of the cards
 * by the "perfect shuffle" algorithm, and also has a method for determining whether or not the deck is in order
 * and a method for finding the current position of any specified card in the deck.
 * */

import java.util.*;

public class Deck {
  // instance variables:
  private LinkedList<Card> myDeck;
  
  private final int DECK_LENGTH = 54;
  private final int CHAR_READJUSTMENT = 64; // move all char values down by 64 to map A->1, B->2, ..., Z->26.
  
  // constructor:
  public Deck() {
    myDeck = new LinkedList<Card>();
    
    for (int i = 1; i <= DECK_LENGTH; i++) { // add all the cards in order
      Card toAdd = new Card(i);
      myDeck.add(toAdd);
    }
  }
  
  public String toString() {
    String s = "";
    
    for (int i = 0; i < DECK_LENGTH; i++)
      s+=(myDeck.get(i)).toString() + "\n";
    
    return s.trim();
  }
  
  /* This method will perform the "perfect shuffle" algorithm on the deck once.  In this shuffle,
   * we split the deck into two halves and then merge the halves by alternately adding a card from the first
   * half and then the second. */
  public void shuffle() {
    LinkedList<Card> half1 = new LinkedList<Card>();
    LinkedList<Card> half2 = new LinkedList<Card>();
    LinkedList<Card> temp = new LinkedList<Card>();
    
    // first we split the deck into two even halves:
    for (int i = 0; i <= (DECK_LENGTH/2 - 1) ; i++)
      half1.add(myDeck.get(i));
    for (int j = (DECK_LENGTH/2); j <= (DECK_LENGTH - 1); j++)
      half2.add(myDeck.get(j));
    
    // then we "shuffle" by adding alternating cards:
    for (int i = 0; i < DECK_LENGTH; i++) {
      if (i%2 == 0)
        temp.add(half1.get(i/2)); // if i is even, we add the corresponding index from the half1 LinkedList
      else
        temp.add(half2.get((i-1)/2)); // if i is odd, we add from half2
    }
    
    myDeck = temp;
  }
  
  /* This method performs the entire "solitaire shuffle" algorithm on the deck once. */
    public void solShuffle() {
    Card jokerA = new Card("A", "Joker");
    Card jokerB = new Card("B", "Joker");
    int firstPivot = findInDeck(jokerA);
    
    moveDownBy(1, firstPivot); // swap(firstPivot,(firstPivot+1)%DECK_LENGTH);
    
    int secondPivot = findInDeck(jokerB);
    
    moveDownBy(2, secondPivot);
    
    int thirdPivot = findInDeck(jokerA);
    int fourthPivot = findInDeck(jokerB);
    
    tripleCut(Math.min(thirdPivot, fourthPivot), Math.max(thirdPivot, fourthPivot));
    
    countCut();
  }
  
  /* Generates a complete keystream of lettersNeeded letters in one fell swoop. */
  public String generateKeystream(int lettersNeeded, String soFar) {
    if (lettersNeeded == 0)
      return soFar.trim();
    else {
      solShuffle(); // perform the first shuffle
      int countDown = myDeck.getFirst().getOrder(); // find the value of the card at the top of the deck
      int convertToLetter = myDeck.get(countDown).getOrder(); // then count down that many cards and get the order of that card
      
      // if it's a joker, skip generating a letter
      if (convertToLetter == 53) {
        return generateKeystream(lettersNeeded, soFar);
      }
      
      else { // otherwise, convert it to a letter:
        String nextLetter = (char)((convertToLetter)%26 + CHAR_READJUSTMENT) + "";
        return generateKeystream(lettersNeeded-1, soFar+nextLetter);
      }
    }
  }
  
  /* Generates a single keystream character by counting down from the top card in the deck by its order,
   * and converting the order of the card it finds there into a letter. */
  public char generateKeyChar() {
    solShuffle();
    int countDown = myDeck.getFirst().getOrder();
    int charValue = myDeck.get(countDown).getOrder();
    
    if (charValue == 53)
      return generateKeyChar();
    
    else
      return (char)(charValue%26 + CHAR_READJUSTMENT);
  }
  
  /* Swaps the card at index 1 with the card at index 2. */
  public void swap(int index1, int index2) {
    Card card1 = myDeck.get(index1);
    Card card2 = myDeck.get(index2);
    
    myDeck.set(index1, card2);
    myDeck.set(index2, card1);
  }
  
  /* Moves the card at the given index (2nd arg) down by n spaces in the deck. */
  public void moveDownBy(int n, int cardAt) {
    if ((cardAt + n) < DECK_LENGTH) {
      Card toMove = myDeck.get(cardAt);
      myDeck.remove(cardAt);
      myDeck.add((cardAt+n)%DECK_LENGTH, toMove);
    }
    
    else {
      Card toMove = myDeck.get(cardAt);
      myDeck.remove(cardAt);
      myDeck.add((cardAt+n+1)%DECK_LENGTH, toMove);
    }
  }
  
  /* This method performs a "triple cut" around the two cards at the
   * specified indices, swapping the stack of cards above the first card
   * with the stack of cards below the second card. */
  public void tripleCut(int index1, int index2) {
    Card deletedCards;
    LinkedList<Card> above = new LinkedList<Card>();
    LinkedList<Card> below = new LinkedList<Card>();
    
    for (int i = 0; i < index1; i++)   // put the cards above the first cut card
      above.add(myDeck.get(i));        // into the "above" pile
    
    for (int j = index2+1; j < DECK_LENGTH; j++)   // put the cards below the second
      below.add(myDeck.get(j));                    // cut card into the "below" pile
    
    for (int i = 0; i < index1; i++) // remove all the cards at the top of the deck
      myDeck.remove(0);
    
    for (int j = index2+1; j < DECK_LENGTH; j++) // then remove all the cards below the second cut card
      deletedCards = myDeck.pollLast();
    
    myDeck.addAll(above); // add the "above" pile at the end of the deck (after the second cut card)
    myDeck.addAll(0, below); // add the "below" pile to the start of the deck
  }
  
  /* Performs a "countCut" using the value ("order", in Card class terminology) of the
   * last card in the deck. */
  public void countCut() {
    Card cutBy = myDeck.get(53);
    int cutValue = cutBy.getOrder();
    
    if (cutValue == 53)
      return; // if the card is a joker, we do nothing
    
    // otherwise:
    else {
      tripleCut(cutValue,53); // with 53 as the 2nd arg to tripleCut, we simply move the first cutValue cards to the bottom
      moveDownBy(cutValue, 53-cutValue); // and then move the initial last card back down to last place, with the first
    }                                   // cutValue cards inserted right above it 
  }
  
  /* Returns the size of the deck. */
  public int size() {
    return (myDeck.size());
  }
  
  /* This method determines whether or not the deck is in order (i.e. arranged with Ace->King of Spades, Ace->King of Clubs,
   * Ace->King of Diamonds, and then Ace->King of Hearts) and returns true if so, false if not. */
  public boolean inOrder() {
    boolean isInOrder = true;
    int i = 0;
    
    //now, if there's a difference of more or less than 1 in the point/suit value of the current card and the one after it,
    //the deck is not in order, so we check this differential for every card in the deck:
    while (isInOrder && (i < (DECK_LENGTH - 1))) {
      if (myDeck.get(i+1).getOrder() - myDeck.get(i).getOrder() != 1)
        isInOrder = false;
      i++;
    }
    
    return isInOrder;
  }
  
  /* Allows the user to try to find a card in the deck and returns its position if found.  If the card is not found,
   * returns the absurd value -1. */
  public int findInDeck(Card c) {
    
    // in the case where C is a joker, it's easiest to use the card name (A or B) to find it, since these are unique
    if (c.getOrder() == 53) {
      for (int i = 0; i < myDeck.size(); i++) {
        if ((myDeck.get(i).getCardName()).equalsIgnoreCase(c.getCardName()))
          return i;
      }
    }
    
    // otherwise, order is intrinsically linked to the suit & value pair (for jokers though, both have order 53)
    // so it's the easiest thing to use to determine equality
    for (int i = 0; i < myDeck.size(); i++) {
      if (myDeck.get(i).getOrder() == c.getOrder()) {
        return i;
      }
    }
    
    return -1;
  }
  
  public int length() {
    return DECK_LENGTH;
  }
  
  public Card getCardAtIndex(int index) {
    return myDeck.get(index);
  }
  
  public static void main(String[] args) {
    //TESTS:
    Deck testDeck = new Deck();
    System.out.println("Initialized a deck for testing. testDeck.toString() returns:\n" + testDeck.toString());
    System.out.println("inOrder() should return true. Actually returns: " + testDeck.inOrder());
    testDeck.shuffle();
    System.out.println("Shuffling the deck once. testDeck.toString() new results:\n" + testDeck.toString());
    System.out.println("inOrder() should now return false. Actually returns: " + testDeck.inOrder());
    Card toFind = new Card(11);
    System.out.println("Attempting to find the Jack of Spades: " + testDeck.findInDeck(toFind));
  }
}