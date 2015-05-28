/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Game.java */

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public final class Game {

  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 8;

  private Deck deck;
  private ArrayList<Player> players; /* this list includes CPU players */
  private ArrayList<Card> communityCards;
  private Scanner in;

  public Game() {
    deck           = new Deck();
    players        = new ArrayList<>();
    communityCards = new ArrayList<>();
    in             = new Scanner(System.in);
  }

  public void start() {

    int winner;
    int numberOfPlayers;
    ArrayList<Integer> listOfWinners;
    Random random = new Random();

    /* Read number of players. */
    players.clear();
    numberOfPlayers = 0;
    while (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      System.out.print("How many players ("
        + MIN_PLAYERS + "-"
        + MAX_PLAYERS + ") ? ");
      numberOfPlayers = in.nextInt();
    }
    System.out.println("");

    /* Add human player. */
    addPlayer(Player.Type.HUMAN, "");

    /* Add CPU players. */
    for (int i = 1; i < numberOfPlayers; i++) {
      addPlayer(Player.Type.CPU, "");
    }

    /* Initialize deck. */
    deck.initializeDeck();
    deck.shuffle();

    /* Deal cards to players and display. */
    for (int i = 0; i < players.size(); i++) {
      dealCard(i);
      dealCard(i);
    }
    printPlayersCards();
    System.out.println();

    /* Flop */
    System.out.print("Flop:  ");
    deck.burnTopCard();
    for (int i = 0; i < 3; i++) {
      dealCard();
    }
    printCommunityCards();

    /* Turn */
    System.out.print("Turn:  ");
    deck.burnTopCard();
    dealCard();
    printCommunityCards(communityCards.size()-1);

    /* CPUs decide whether they want to fold or not. */
    for (int i = 1; i < players.size(); i++) {
      Rank rank = new Rank(communityCards, players.get(i).getHand());
      if (rank.getDegree() >= 8) {
        if (random.nextInt(100) >= 30) {
          players.get(i).fold();
          System.out.println(players.get(i).getName() + " has folded.");
        }
      }
    }

    /* River */
    System.out.print("River: ");
    deck.burnTopCard();
    dealCard();
    printCommunityCards(communityCards.size()-1);

    /* Display hands with ranks. */
    System.out.println("");
    for (int i = 0; i < players.size(); i++) {
      Rank rank = new Rank(communityCards, players.get(i).getHand());
      System.out.print(players.get(i).getName());
      if (players.get(i).hasFolded()) {
        System.out.print(" would've had: ");
      }
      else {
        System.out.print(" has: ");
      }
      System.out.println(rank.toString());
    }

    /* Display winners. */
    System.out.println("\nWinners: ");
    listOfWinners = getWinners();
    for (int i = listOfWinners.size()-1; i >= 0 ; i--) {
      System.out.println(players.get(listOfWinners.get(i)).getName());
    }

  }

  public void addPlayer(Player.Type type, String name) {
    String newName = name;
    if (!newName.equalsIgnoreCase("") && type == Player.Type.CPU) {
      newName = "[CPU] " + newName;
    }
    else if (newName.equalsIgnoreCase("") && type == Player.Type.CPU) {
      newName = "[CPU] Player " + (players.size() + 1);
    }
    else if (newName.equalsIgnoreCase("") && type == Player.Type.HUMAN) {
      newName = "Player " + (players.size() + 1);
    }
    players.add(new Player(type, newName));
  }

  public void removePlayer(int id) {
    if (id >= 0 && id < players.size()) {
      players.remove(id);
    }
  }

  public void removePlayer(String name) {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getName().equalsIgnoreCase(name)) {
        players.remove(i);
        break;
      }
    }
  }

  public void printCommunityCards() {
    String msg = "";
    for (int i = 0; i < communityCards.size(); i++) {
      msg += communityCards.get(i).toString() + " ";
    }
    System.out.println(msg);
  }

  public void printCommunityCards(int startIndex) {
    String msg = "";
    for (int i = startIndex; i < communityCards.size(); i++) {
      msg += communityCards.get(i).toString() + " ";
    }
    System.out.println(msg);
  }

  public void printPlayersCards() {
    for (int i = 0; i < players.size(); i++) {
      players.get(i).printHand();
    }
  }

  /* Deal community card. */
  private void dealCard() {
    Card newCard = deck.drawCard();
    if (newCard != null) {
      communityCards.add(newCard);
    }
  }

  /* Deal card to specific player. */
  private void dealCard(int player) {
    Card newCard = deck.drawCard();
    if (newCard != null) {
      players.get(player).receiveCard(newCard);
    }
  }

  private ArrayList<Integer> getWinners() {
    ArrayList<Integer> listOfWinners = new ArrayList<>();
    int winner = 0;
    int lowestDegree = new Rank(communityCards, players.get(winner).getHand()).getDegree();
    int tmpDegree;
    Rank highestRank = new Rank(communityCards, players.get(winner).getHand());
    Rank tmpRank;

    for (int i = 0; i < players.size(); i++) {
      if (!players.get(i).hasFolded()) {
        tmpRank = new Rank(communityCards, players.get(i).getHand());
        tmpDegree = tmpRank.getDegree();

        /* Current player with highest hand. */
        if (tmpDegree < lowestDegree) {
          listOfWinners.clear();
          listOfWinners.add(i);
          lowestDegree = tmpDegree;
          highestRank = tmpRank;
        }
        /* Tie? */
        else if (tmpDegree == lowestDegree) {
          if (tmpRank.getSumOfCards() > highestRank.getSumOfCards()) {
            listOfWinners.clear();
            listOfWinners.add(i);
            lowestDegree = tmpDegree;
            highestRank = tmpRank;
          }
          else if (tmpRank.getSumOfCards() == highestRank.getSumOfCards()) {
            listOfWinners.add(i);
          }
        }
      }
    }

    return listOfWinners;
  }

}
