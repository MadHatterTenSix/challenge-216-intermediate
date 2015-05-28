/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Rank.java */

import java.util.ArrayList;
import java.util.Collections;

/*
    http://www.texasholdem-poker.com/handrank

     0  Royal Flush    (Straight Flush w/ Ace as High Card)
     1  Straight Flush (Straight + Flush)
     2  4 of a Kind
     3  Full House     (3 of a Kind + 1 Pair)
     4  Flush          (all same suit of any values)
     5  Straight       (5 consecutive values of any suit)
     6  3 of a Kind
     7  2 Pair
     8  1 Pair
     9  High Card

     Rank notation for a hand in this class:
     (<degree_of_rank>,<highest_card_of_rank>)
     e.g. (1,10) for Straight Flush and 10 being the highest card
     e.g. (6,11) for 3 of a Kind with Jack being the highest card
     e.g. (9,14) for High Card Ace
*/

public class Rank {

  int degree;
  private ArrayList<Card> cards;
  private ArrayList<Card> highestCards;

  public Rank(ArrayList<Card> communityCards, ArrayList<Card> playersCards) {

    degree = -1;
    cards = new ArrayList<>();
    highestCards = new ArrayList<>();

    /* Merge all cards into one list. */
    for (Card card : communityCards) {
      cards.add(card);
    }
    for (Card card : playersCards) {
      cards.add(card);
    }

    /* Determine highest degree. */
    if (isRoyalFlush(cards)
        || isStraightFlush(cards)
        || isFourOfAKind(cards)
        || isFullHouse(cards)
        || isFlush(cards)
        || isStraight(cards)
        || isThreeOfAKind(cards)
        || isTwoPair(cards)
        || isOnePair(cards)
        || isHighCard(cards)
    ) {
      /* Do nothing. Everything gets set during the checks.*/
    }

  }

  public static Card getHighestCard(ArrayList<Card> listOfCards) {
    ArrayList<Card> tmpCards = Card.getCopyOfCards(listOfCards);
    Collections.sort(tmpCards, Card.CardValueComparator);
    return tmpCards.get(0);
  }

  /* Return list of cards matching specified value. */
  public static ArrayList<Card> getCardsByValue(ArrayList<Card> listOfCards, int value) {
    ArrayList<Card> tmpCards = new ArrayList<>();
    for (Card card : listOfCards) {
      if (card.getValue() == value) {
        tmpCards.add(card);
      }
    }
    return tmpCards;
  }

  /* Return list of cards matching specified suit. */
  public static ArrayList<Card> getCardsBySuit(ArrayList<Card> listOfCards, int suit) {
    ArrayList<Card> tmpCards = new ArrayList<>();
    for (Card card : listOfCards) {
      if (card.getSuit() == suit) {
        tmpCards.add(card);
      }
    }
    return tmpCards;
  }

  public static boolean isCardFound(ArrayList<Card> listOfCards, Card card) {
    for (Card tmpCard : listOfCards) {
      if (tmpCard.getValue() == card.getValue()
          && tmpCard.getSuit() == card.getSuit()) {
        return true;
      }
    }
    return false;
  }

  public static boolean isValueFound(ArrayList<Card> listOfCards, int value) {
    for (Card card : listOfCards) {
      if (card.getValue() == value) {
        return true;
      }
    }
    return false;
  }

  public int getDegree()
    { return degree; }

  public ArrayList<Card> getCards()
    { return cards; }

  public ArrayList<Card> getHighestCards()
    { return highestCards; }

  public int getSumOfCards() {
    int sum = 0;
    for (Card card : cards) {
      sum += card.getValue();
    }
    return sum;
  }

  @Override
  public String toString() {
    String s;
    switch (degree) {
      case 0: s = "Royal Flush"; break;
      case 1: s = "Straight Flush"; break;
      case 2: s = "Four of a Kind"; break;
      case 3: s = "Full House"; break;
      case 4: s = "Flush"; break;
      case 5: s = "Straight"; break;
      case 6: s = "Three of a Kind"; break;
      case 7: s = "Two Pair"; break;
      case 8: s = "One Pair"; break;
      case 9: s = "High Card"; break;
      default: s = "UNKNOWN_RANK"; break;
    }

    if (degree >= 0 && degree <= 9) {
      s += " ";
      Collections.sort(cards, Card.CardValueComparator);
      for (int i = 0; i < cards.size(); i++) {
        s += cards.get(i).toString() + " ";
      }
    }

    return s;
  }

  private boolean isRoyalFlush(ArrayList<Card> listOfCards) {
    int tmpSuit;
    ArrayList<Card> listOfAces;
    ArrayList<Card> newListOfCards;

    /* Get list of aces. */
    listOfAces = getCardsByValue(listOfCards, 14);

    /* Check each ace for matching King, Queen, Jack, and 10. */
    for (Card card : listOfAces) {
      tmpSuit = card.getSuit();
      if (isCardFound(listOfCards, new Card(13, tmpSuit))
              && isCardFound(listOfCards, new Card(12, tmpSuit))
              && isCardFound(listOfCards, new Card(11, tmpSuit))
              && isCardFound(listOfCards, new Card(10, tmpSuit))) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        for (int i = 14; i >= 10; i--) {
          listOfCards.add(new Card(i, tmpSuit));
        }

        degree = 0;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isStraightFlush(ArrayList<Card> listOfCards) {
    ArrayList<Card> tmpCards;

    Collections.sort(listOfCards, Card.CardValueComparator);

    for (int i = 0; i < listOfCards.size(); i++) {
      tmpCards = getCardsBySuit(listOfCards, listOfCards.get(i).getSuit());
      if (isStraight(tmpCards)) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        for (int j = 0; j < tmpCards.size(); j++) {
          listOfCards.add(tmpCards.get(j));
        }

        degree = 1;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isFourOfAKind(ArrayList<Card> listOfCards) {
    int tmpValue;
    ArrayList<Card> tmpCards;

    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Check each card for total of four matching cards. */
    for (int i = 0; i < listOfCards.size(); i++) {
      tmpCards = Card.getCopyOfCards(listOfCards);
      tmpValue = listOfCards.get(i).getValue();
      tmpCards = getCardsByValue(tmpCards, tmpValue);
      if (tmpCards.size() >= 4) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        Collections.sort(tmpCards, Card.CardValueComparator);
        for (int j = 0; j < 4; j++) {
          listOfCards.add(tmpCards.get(j));
        }

        degree = 2;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isFullHouse(ArrayList<Card> listOfCards) {
    ArrayList<Card> tmpCards;
    ArrayList<Card> threeOfAKind = new ArrayList<>();
    ArrayList<Card> twoOfAKind = new ArrayList<>();

    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Find three of a kind. */
    tmpCards = Card.getCopyOfCards(listOfCards);
    if (isThreeOfAKind(tmpCards)) {
      /* Save three of a kind. */
      threeOfAKind = Card.getCopyOfCards(tmpCards);
      /* Remove three of a kind from copy of original list. */
      tmpCards.clear();
      for (int i = 0; i < listOfCards.size(); i++) {
        if (listOfCards.get(i).getValue() != threeOfAKind.get(0).getValue()) {
          tmpCards.add(listOfCards.get(i));
        }
      }

      /* Find two of a kind. */
      if (isOnePair(tmpCards)) {
        /* Save two of a kind. */
        twoOfAKind = Card.getCopyOfCards(tmpCards);

        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        for (int j = 0; j < 3; j++) {
          listOfCards.add(threeOfAKind.get(j));
        }
        for (int j = 0; j < 2; j++) {
          listOfCards.add(twoOfAKind.get(j));
        }

        degree = 3;
        highestCards.clear();
        highestCards.add(threeOfAKind.get(0));
        highestCards.add(twoOfAKind.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isFlush(ArrayList<Card> listOfCards) {
    int tmpSuit;
    ArrayList<Card> tmpCards;

    Collections.sort(listOfCards, Card.CardValueComparator);

    for (int i = 0; i < listOfCards.size(); i++) {
      tmpCards = Card.getCopyOfCards(listOfCards);
      tmpSuit = listOfCards.get(i).getSuit();
      tmpCards = getCardsBySuit(tmpCards, tmpSuit);
      if (tmpCards.size() >= 5) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        Collections.sort(tmpCards, Card.CardValueComparator);
        for (int j = 0; j < 5; j++) {
          listOfCards.add(tmpCards.get(j));
        }

        degree = 4;
        highestCards = Card.getCopyOfCards(listOfCards);
        Collections.sort(highestCards, Card.CardValueComparator);

        return true;
      }
    }

    return false;
  }

  private boolean isStraight(ArrayList<Card> listOfCards) {
    int counter;
    ArrayList<Card> tmpCards = new ArrayList<>();

    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Remove duplicates. */
    for (int i = 0; i < listOfCards.size(); i++) {
      if (!isValueFound(tmpCards, listOfCards.get(i).getValue())) {
        tmpCards.add(listOfCards.get(i));
      }
    }

    counter = 0;
    Collections.sort(tmpCards, Card.CardValueComparator);
    for (int i = 0; i < tmpCards.size()-1; i++) {
      if (tmpCards.get(i).getValue() == tmpCards.get(i+1).getValue()+1) {
        counter++;
        if (counter >= 4) {
          /* Remove irrelevant cards from original list. */
          listOfCards.clear();
          for (int j = i+1; j > (i-4); j--) {
            listOfCards.add(tmpCards.get(j));
          }
          Collections.sort(listOfCards, Card.CardValueComparator);

          degree = 5;
          highestCards.clear();
          highestCards.add(listOfCards.get(0));

          return true;
        }
      }
      else {
        counter = 0;
      }
    }

    return false;
  }

  private boolean isThreeOfAKind(ArrayList<Card> listOfCards) {
    int tmpValue;
    ArrayList<Card> tmpCards;

    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Check each card for total of three matching cards. */
    for (int i = 0; i < listOfCards.size(); i++) {
      tmpCards = Card.getCopyOfCards(listOfCards);
      tmpValue = listOfCards.get(i).getValue();
      tmpCards = getCardsByValue(tmpCards, tmpValue);
      if (tmpCards.size() >= 3) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        Collections.sort(tmpCards, Card.CardValueComparator);
        for (int j = 0; j < 3; j++) {
          listOfCards.add(tmpCards.get(j));
        }

        degree = 6;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isTwoPair(ArrayList<Card> listOfCards) {
    int tmpValue;
    ArrayList<Card> tmpCards;
    ArrayList<Card> pair1;
    ArrayList<Card> pair2;

    pair1 = new ArrayList<>();
    pair2 = new ArrayList<>();
    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Find first pair. */
    tmpCards = Card.getCopyOfCards(listOfCards);
    if (isOnePair(tmpCards)) {
      /* Save first pair. */
      pair1 = Card.getCopyOfCards(tmpCards);

      /* Remove first pair from copy of original list of cards. */
      tmpCards.clear();
      for (Card card : listOfCards) {
        if (card.getValue() != pair1.get(0).getValue()) {
          tmpCards.add(card);
        }
      }

      /* Find second pair. */
      if (isOnePair(tmpCards)) {
        /* Save second pair. */
        pair2 = Card.getCopyOfCards(tmpCards);

        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        for (int i = 0; i < 2; i++) {
          listOfCards.add(pair1.get(i));
        }
        for (int i = 0; i < 2; i++) {
          listOfCards.add(pair2.get(i));
        }

        degree = 7;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isOnePair(ArrayList<Card> listOfCards) {
    int tmpValue;
    ArrayList<Card> tmpCards;

    Collections.sort(listOfCards, Card.CardValueComparator);

    /* Check each card for total of three matching cards. */
    for (int i = 0; i < listOfCards.size(); i++) {
      tmpCards = Card.getCopyOfCards(listOfCards);
      tmpValue = listOfCards.get(i).getValue();
      tmpCards = getCardsByValue(tmpCards, tmpValue);
      if (tmpCards.size() >= 2) {
        /* Remove irrelevant cards from original list. */
        listOfCards.clear();
        Collections.sort(tmpCards, Card.CardValueComparator);
        for (int j = 0; j < 2; j++) {
          listOfCards.add(tmpCards.get(j));
        }

        degree = 8;
        highestCards.clear();
        highestCards.add(listOfCards.get(0));

        return true;
      }
    }

    return false;
  }

  private boolean isHighCard(ArrayList<Card> listOfCards) {
    Card highCard;
    Card tmpCard;
    Collections.sort(listOfCards, Card.CardValueComparator);
    highCard = listOfCards.get(0);
    for (int i = 1; i < listOfCards.size(); i++) {
      tmpCard = listOfCards.get(i);
      if (tmpCard.getValue() > highCard.getValue()) {
        highCard = tmpCard;
      }
    }
    listOfCards.clear();
    listOfCards.add(highCard);

    degree = 9;
    highestCards.clear();
    highestCards.add(listOfCards.get(0));

    return true;
  }

}
