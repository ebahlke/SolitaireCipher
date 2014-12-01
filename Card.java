/* Author: Emma Bahlke
 * Date: April 6, 2014
 * Name: Card.java
 * Description: This class represents a Card object, which has a face value/card name, suit, and order in the deck. The suits
 * are ordered spades < clubs < diamonds < hearts, and for face value we have Aces low
 * (so for example, the Ace of Spades has order 1, Jack of Clubs has order 24, etc.).
 * */

public class Card {
  private String value, suit;
  private int order;
  
  // constructor with value and suit (automatically computes order):
  public Card(String v, String s) {
    this.value = v;
    this.suit = s;
    setOrder(value, suit);
  }
  
  // constructor with order (automatically computes value and suit):
  public Card(int o) {
    this.order = o;
    setCard(order);
  }
  
  public String toString() {
    if (order == 53) {
      String toReturn = "Joker " + value;
      return toReturn;
    }
    
    String toReturn = value + " of " + suit + ": Order " + order;
    
    return toReturn;
  }
  
  // ------------------------------------------------------------------------- //
  // SETTERS:
  
  private void setOrder(String val, String suit) {
    // this method takes the card's value and suit as arguments and uses them to compute its order in the deck, based on the ranking
    // described in the documentation
    
    int suitWeight = 0; // by default, we assume the suit is Spades (so the card's order is simply its face value)
    // but then we check to see if it isn't clubs, diamonds, or hearts instead, and if it is, we augment suitWeight accordingly
    if (suit.equalsIgnoreCase("clubs"))
      suitWeight = 13;
    if (suit.equalsIgnoreCase("diamonds"))
      suitWeight = 26;
    if (suit.equalsIgnoreCase("hearts"))
      suitWeight = 39;
    if (suit.equalsIgnoreCase("joker")) {
      this.order = 53;
      return;
    }
    
    this.order = getPointWorth(val) + suitWeight;
  }
  
  private void setCard(int order) {
    // sets the suit and value (i.e. Ace through King) of the card based on its order
    // CHECK FOR JOKERS:
    
    if (order > 52) {
      this.suit = "Joker";
      if (order == 53)
        this.value = "A";
      else {
        this.value = "B";
        this.order = 53; // NOTE: both jokers get converted to have order 53; this is for the Deck constructor
      }
      return;
    }
    
    // assuming not joker, first, set the value:
    int pointVal = order%13; //find the "point value" of the card independent of its suit
    this.value = getCardName(pointVal); //then find the corresponding name of that type of card
    
    // next, set the suit:
    int suitVal = (order - (order%13))/13; //recall that order%13 is the "point value" of the card, so to find the suit, we subtract that
                                           //& then check what multiple of 13 we're sitting at and return the corresponding suit:
    
    if (suitVal == 0)
      this.suit = "Spades";
    else if (suitVal == 1)
      this.suit = "Clubs";
    else if (suitVal == 2)
      this.suit = "Diamonds";
    else this.suit = "Hearts";
  }
  
  // ------------------------------------------------------------------------- //
  // GETTERS:
  
  public String getCardName() {
    return value;
  }
  
  public String getCardName(int pointVal) {
    // this method converts the order *within a suit*, a.k.a. the point value, of a card into a String representing its name
    // ASSUMPTION: this will never be called with a Joker
    
    if ((pointVal >= 2) && (pointVal <= 10))
      return Integer.toString(pointVal);
    else if (pointVal == 1)
      return "Ace";
    else if (pointVal == 11)
      return "Jack";
    else if (pointVal == 12)
      return "Queen";
    else return "King";
  }
  
  public String getSuit() {
    return suit;
  }
  
  public int getOrder() {
    return order;
  }
  
  public int getPointWorth (String cardName) {
    // this method, if given the String representation of a card's name, that is, its name "Ace", "2", ..., "Jack", "Queen", or "King",
    // will return the integer value or point worth of that card, assuming a system where aces are worth 1, cards 2-10 are worth 2-10, 
    // Jacks are worth 11, Queens 12, and Kings 13
    
    if (cardName.length() == 1)
      return Integer.parseInt(cardName);
    
    else if (cardName.equalsIgnoreCase("ace"))
      return 1;
    else if (cardName.equalsIgnoreCase("jack"))
      return 11;
    else if (cardName.equalsIgnoreCase("queen"))
      return 12;
    else return 13; // ASSUMPTION: the deck has no Jokers
    
  }
  
  public static void main(String[] args) {
    //TESTS:
//    Card test1 = new Card("Jack", "Clubs");
//    System.out.println("Created new Jack of Clubs. Testing toString() (should give order 24): \n" + test1.toString());
//    System.out.println("Testing getters: \nSuit: " + test1.getSuit() + "\nOrder: " + test1.getOrder() + "\nValue/name: " + test1.getCardName());
//    System.out.println("Resetting order to 25. test1.toString() should now print the Queen of Clubs.");
//    test1.setOrder(25);
//    System.out.println(test1.toString());
  }
    
}