import java.io.*;
import java.util.*;

/**
 * Numa Fecher
 * 7/30/2024
 * CSE 122
 * TA: ??????????
 * Culminating Project 2: Data Manipulation
 * A class that processes a file of board games and allows the user to access and edit its details,
 * rate games, mark games as played, find a board game using filter tags, and save this catalogue
 * into a file.
 *
 * List of board games extracted from 
 * https://docs.google.com/spreadsheets/d/e/2PACX-1vQaNXNlj9BiRCNEqq_db9thDElW5vfO9TfrHZOqcWF8gU4lWhRmupN2ftehWTkFYiwK3jrhd7IsGWz0/pubhtml
 * And data on duration, number of players, internet rating, and category tags are from
 * https://boardgamegeek.com/
 * 
 * A smaller, more manageable excerpt of the board game catalogue is used for testing.
 * Any 0 values indicate missing/unentered data.
 */

public class BoardomWars {
    public static void main(String[] args) throws FileNotFoundException {
        // Scanner to read user input:
        Scanner console = new Scanner(System.in);

        // Print intro statement:
        printIntro();
        System.out.print("What is the path of your properly formatted game catalogue? ");
        String path = console.nextLine();

        // Scanner to read the file:
        File gamesCatalogue = new File(path);
        Scanner fileScan = new Scanner(gamesCatalogue);

        // Process file:
        Map<String, List<String>> gameData = new TreeMap<>();
            // Maps game to list of info (excluding tags)
        Map<String, Set<String>> gameTags = new TreeMap<>();
            // Maps game to category tags
        loadFile(fileScan, gameData, gameTags);
        
        
        
        // Initialise input:
        String input = "";

        // Interface menu:
        while (!input.equalsIgnoreCase("Q")) {
            printKey();
            System.out.print("What do you want to do? Enter your choice: ");
            input = console.nextLine();
            System.out.println();

            if (input.equalsIgnoreCase("L")) { // See entire list of games
                printGames(gameData, gameTags, gameData.keySet());

            } else if (input.equalsIgnoreCase("F")) { // Filter for game to play
                filterInterface(console, gameData, gameTags);

            } else if (input.equalsIgnoreCase("E")) { // Edit list of games
                System.out.println("Below are the list of board games in the catalogue.");
                for (String game : gameData.keySet()) {
                    System.out.println("    * " + game.replaceAll("_", " "));
                }
                System.out.print("Which board game would you like to edit? "
                                 + "Enter your choice: ");
                String editGame = console.nextLine().replaceAll(" ", "_");
                System.out.println();
                editInterface(editGame, console, gameData, gameTags);

            } else if (input.equalsIgnoreCase("M")) { // Mark a game played and rate it
                markGamePlayedInterface(console, gameData, gameTags);

            } else if (input.equalsIgnoreCase("P")) { // Display top 10 played games
                System.out.println("== Top 10 most played games ==");
                printTopGames(gameData, 3);

            } else if (input.equalsIgnoreCase("R")) { // Display top 10 society rated games
                System.out.println("== Top 10 highest society rated games ==");
                printTopGames(gameData, 4);

            } else if (input.equalsIgnoreCase("K")) { // Print key of commands again
                printKey();
                System.out.println();

            } else if (!input.equalsIgnoreCase("Q")) { // Invalid input
            System.out.println("Input was either invalid or incorrect. Check your spelling "
                               + "and take a look at the key again if you need.");
            }
        }
        // Q - Exit out of programme & update game catalogue with all changes made
        System.out.println("Exiting out of the programme...");
        updateBoardGameCatalogue(gameData, gameTags, path);
    }


    // B: prints out the introduction to the program
    // E:
    // R:
    // P:
    public static void printIntro() {
        System.out.println("Welcome to Board-om Wars IV: A New Hope");
        System.out.println("This program will help you find the board game of your dreams!");
        System.out.println();
    }


    // B: prints out the key of commands for the main interface
    // E:
    // R:
    // P:
    public static void printKey() {
        System.out.println("Below is a key of commands to use this app.");
        System.out.println("    - [L] see entire list of games");
        System.out.println("    - [F] turn on filter feature to search for a specific game");
        System.out.println("    - [E] edit list of games");
        System.out.println("    - [M] mark a board game played and rate");
        System.out.println("    - [P] list the top 10 played games");
        System.out.println("    - [R] list the top 10 society rated games");
        System.out.println("    - [K] to print the key of commands again.");
        System.out.println("    - [Q] to exit out of the app");
}


    // B: loads a file and processes a given file
    // E:
    // R:
    // P: Scanner fileScan - scanner to scan the games catalogue file
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    // File format: first line is the number of games. Second line contains the headers.
    //              Each line after is a board game entry, starting with the game's name
    //              properly capitalised and with any spaces in it's name separated by
    //              an underscore. Then there are 8 sections in the line, each representing
    //              some data/property of the game.
    //              See file_formatting_info.txt for more detailed explanation
    public static void loadFile(Scanner fileScan, Map<String, List<String>> gameData,
                                Map<String, Set<String>> gameTags) {
        int numGames = Integer.parseInt(fileScan.next()); 
        fileScan.nextLine(); // Skip first line
        fileScan.nextLine(); // Skip second line (headers)

        while (fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            Scanner lineScan = new Scanner(line);

            // Create keys (board game name) in maps:
            String game = lineScan.next();
            gameData.put(game, new ArrayList<>());
            gameTags.put(game, new HashSet<>());

            for (int i = 1; i < 8; i++) {
                gameData.get(game).add(lineScan.next());
            }
            while (lineScan.hasNext()) {
                gameTags.get(game).add(lineScan.next());
            }
        }
        System.out.println("Successfully loaded " + numGames + " board games.");
        System.out.println("-----");
    }


    // B: prints a chosen selection of board games in the catalogue and their associated data
    // E: 
    // R: 
    // P: Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    //    Set<String> gameNames - names of games to be printed
    public static void printGames(Map<String, List<String>> gameData,
                                  Map<String, Set<String>> gameTags,
                                  Set<String> gameNames) {
        for (String game : gameNames) {
            List<String> data = gameData.get(game);
            Set<String> tags = gameTags.get(game);

            System.out.println(game.replaceAll("_", " "));
            System.out.println("Duration: " + data.get(2) + " minutes");
            System.out.println("Players: " + data.get(0) +
                                " | Ideal Players: " + data.get(1));
            System.out.println("Internet Rating: " + data.get(6) +
                                " | Society Rating: " + data.get(4));
            System.out.println("No. times played: " + data.get(3));
            System.out.println("Tags: " + tags.toString());
            System.out.println("-----");
        }
    }


    // B: prints out the key of possible filter commands for the filtering menu
    // E:
    // R:
    // P: List<String> keyList - currently available filter commands for the filtering menu
    public static void printFilterKey(List<String> keyList) {
        System.out.println("Below are the possible filters to find your next game.");
        for (String key : keyList) {
            System.out.println(key);
        }
        System.out.println("\t- [R] Return to main menu");
    }


    // B: prints and runs the menu for filtering options, allowing the user to filter
    //    through the games in the catalogue by each type of data on the games
    // E: throws IllegalArgumentException if user inputs a tag that none of the games have
    // R:
    // P: Scanner console - scanner to read user input
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void filterInterface(Scanner console,
                                       Map<String, List<String>> gameData,
                                       Map<String, Set<String>> gameTags) {
        // Initialising:
        Set<String> matchingGames = new TreeSet<>();
        for (String game : gameData.keySet()) {
            matchingGames.add(game);
        }
        Set<String> auxSet = new HashSet<>();

        String filter = "";
        int filterNumPlayers = 0;
        int filterDuration = 0;
        String filterNovelty = "";
        double filterSocRating = 0;
        double filterInternetRating = 0;
        List<String> filterTags = new ArrayList<>();
        List<String> filterTagsExclude = new ArrayList<>();
        List<List<String>> allFilterTags = new ArrayList<>();
        List<List<String>> allFilterTagsExclude = new ArrayList<>();

        List<String> keyList = new ArrayList<>();
        keyList.add("\t- [1] Number of players");
        keyList.add("\t- [2] Duration (in minutes)");
        keyList.add("\t- [3] Novelty of game");
        keyList.add("\t- [4] Society rating");
        keyList.add("\t- [5] Internet rating");
        keyList.add("\t- [6] Games WITH given tag(s)");
        keyList.add("\t- [7] Games WITHOUT given tag(s)");

        
        // Interface menu for filters:
        filter = filterPrompt(keyList, console);
        while (!filter.equalsIgnoreCase("R") && !matchingGames.isEmpty()) {
            if (filter.equals("1")) {
                System.out.print("How many players? Enter your choice: ");
                filterNumPlayers = Integer.parseInt(console.nextLine());
                filterByNumPlayers(filterNumPlayers, matchingGames, auxSet, gameData);
                keyList.remove("\t- [1] Number of players");
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("2")) {
                System.out.print("How much time do you have? Enter your choice: ");
                filterDuration = Integer.parseInt(console.nextLine());
                filterByDuration(filterDuration, matchingGames, auxSet, gameData);
                keyList.remove("\t- [2] Duration (in minutes)");
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("3")) {
                System.out.print("Are you looking for a game that you have NOT played before? "
                                 + "Enter your choice (Yes or No): ");
                filterNovelty = console.nextLine();
                filterByNovelty(filterNovelty, matchingGames, auxSet, gameData);
                keyList.remove("\t- [3] Novelty of game");
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("4")) {
                System.out.print("Minimum rating given by society to filter by: ");
                filterSocRating = Double.parseDouble(console.nextLine());
                filterByRating(filterSocRating, 4, matchingGames, auxSet, gameData);
                keyList.remove("\t- [4] Society rating");
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("5")) {
                System.out.print("Minimum rating given by internet to filter by: ");
                filterInternetRating = Double.parseDouble(console.nextLine());
                filterByRating(filterInternetRating, 6, matchingGames, auxSet, gameData);
                keyList.remove("\t- [5] Internet rating");
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("6")) {
                Set<String> validTags = validTags(matchingGames, gameTags);
                printValidTags(validTags);

                System.out.println("*** Sorting for games that match at least "
                                   + "one of the given tags. ***");
                System.out.print("Tags to search for (separate multiple tags by a space): ");
                String[] inputTags = console.nextLine().split(" ");
                filterTags.clear();
                for (String inputTag : inputTags) {
                    filterTags.add(inputTag);
                }

                for (String tag : filterTags) {
                    if (!validTags.contains(tag)) {
                        throw new IllegalArgumentException();
                    }
                    filterByContainsTag(filterTags, matchingGames, auxSet, gameTags);
                }
                insertListInList(allFilterTags, filterTags);
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else if (filter.equals("7")) {
                Set<String> validTags = validTags(matchingGames, gameTags);
                printValidTags(validTags);

                System.out.println("*** Sorting for games that do not match any "
                                   + "of the given tags. ***");
                System.out.print("Tags to exclude in search "
                                 + "(separate multiple tags by a space): ");
                String[] inputTags = console.nextLine().split(" ");
                filterTagsExclude.clear();
                for (String inputTag : inputTags) {
                    filterTagsExclude.add(inputTag);
                }

                for (String tag : filterTagsExclude) {
                    if (!validTags.contains(tag)) {
                        throw new IllegalArgumentException();
                    }
                    filterByDoesntContainTag(filterTagsExclude, matchingGames, gameTags);
                }
                insertListInList(allFilterTagsExclude, filterTagsExclude);
                printResults (keyList, filterNumPlayers, filterDuration, filterNovelty,
                              filterSocRating, filterInternetRating, allFilterTags,
                              allFilterTagsExclude, gameData, gameTags, matchingGames);

            } else {
                System.out.println();
                System.out.println("Please enter a valid filter option from the list.");
            }
            
            if (matchingGames.isEmpty()) {
                System.out.println("Oh no! You have filtered too far, "
                                   + "there are no board games matching your filters.");
            } else {
                filter = filterPrompt(keyList, console);
            }
        }
        System.out.println();
    }


    // B: prompts user for the property they want to filter by
    // E:
    // R: returns String - the user's choice for what to filter by (menu option)
    // P: List<String> keyList - currently available filter commands for the filtering menu
    //    Scanner console - scanner to read user input
    public static String filterPrompt(List<String> keyList, Scanner console) {
        printFilterKey(keyList);
        System.out.print("What would you like to filter by? Enter your choice: ");
        return console.nextLine();
    }


    // B: moves all elements from one set to another
    // E:
    // R:
    // P: Set<String> auxSet - set to move the elements from
    //    Set<String> matchingGames - set to move the elements to
    public static void setToSet(Set<String> auxSet, Set<String> matchingGames) {
        matchingGames.clear();
        for (String game : auxSet) {
            matchingGames.add(game);
        }
        auxSet.clear();
    }


    // B: filters list of games to find games that can be played by a given number of players
    //    according to it's number of players data (rather than ideal number of players)
    // E: throws IllegalStateException if the auxilliary set is not empty
    // R:
    // P: int filterNumPlayers - number of players looking to play
    //    Set<String> matchingGames - games to search through
    //    Set<String> auxSet - empty auxilliary set (will keep track of games that can be
    //                         played by the given number of players)
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    public static void filterByNumPlayers(int filterNumPlayers, Set<String> matchingGames,
                                          Set<String> auxSet, Map<String, List<String>> gameData) {
        if (!auxSet.isEmpty()) {
            throw new IllegalStateException();
        }
        for (String game : matchingGames) {
            String numPlayers = gameData.get(game).get(0);
            if (numPlayers.contains("-")) { // Checking for a range
                String[] playerRange = numPlayers.split("-"); 
                if (filterNumPlayers >= Integer.parseInt(playerRange[0]) &&
                    filterNumPlayers <= Integer.parseInt(playerRange[1])) {
                    auxSet.add(game);
                }
            } else if (filterNumPlayers == Integer.parseInt(numPlayers)) {
                auxSet.add(game);
            }
        }
        setToSet(auxSet, matchingGames);
    }


    // B: filters list of games to find games that can be played in a given amount of time
    // E: throws IllegalStateException if the auxilliary set is not empty
    // R:
    // P: int filterDuration - time (in minutes) user is free to play a game
    //    Set<String> matchingGames - games to search through
    //    Set<String> auxSet - empty auxilliary set (will keep track of games that can be
    //                         played by the given number of players)
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    public static void filterByDuration(int filterDuration, Set<String> matchingGames,
                                        Set<String> auxSet, Map<String, List<String>> gameData) {
        if (!auxSet.isEmpty()) {
            throw new IllegalStateException();
        }
        for (String game : matchingGames) {
            if (Integer.parseInt(gameData.get(game).get(2)) <= filterDuration) {
                auxSet.add(game);
            }
        }
        setToSet(auxSet, matchingGames);
    }


    // B: filters list of games to find games that either have or have not been played before
    // E: throws IllegalStateException if the auxilliary set is not empty
    // R:
    // P: String filterNovelty - whether it is filtering for a game that has not been
    //                           played before (Yes) or that has been played before (No)
    //    Set<String> matchingGames - games to search through
    //    Set<String> auxSet - empty auxilliary set (will keep track of games that can be
    //                         played by the given number of players)
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    public static void filterByNovelty(String filterNovelty, Set<String> matchingGames,
                                        Set<String> auxSet, Map<String, List<String>> gameData) {
        if (!auxSet.isEmpty()) {
            throw new IllegalStateException();
        }
        if (filterNovelty.equalsIgnoreCase("Yes")) {
            for (String game : matchingGames) {
                if (gameData.get(game).get(3).equals("0")) {
                    auxSet.add(game);
                }
            }
        } else {
            for (String game : matchingGames) {
                if (!gameData.get(game).get(3).equals("0")) {
                    auxSet.add(game);
                }
            }
        }
        setToSet(auxSet, matchingGames);
    }


    // B: filters list of games to find games that have a rating equal to or higher
    //    than a given minimum rating
    // E: throws IllegalStateException if the auxilliary set is not empty
    // R:
    // P: double filterRating - minimum rating to be filtering for
    //    int columnIndex - index of the column in the catalogue containing the rating
    //                      to search by (society rating or internet rating)
    //    Set<String> matchingGames - games to search through
    //    Set<String> auxSet - empty auxilliary set (will keep track of games that can be
    //                         played by the given number of players)
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    public static void filterByRating(double filterRating, int columnIndex,
                                      Set<String> matchingGames, Set<String> auxSet,
                                      Map<String, List<String>> gameData) {
        if (!auxSet.isEmpty()) {
            throw new IllegalStateException();
        }
        for (String game : matchingGames) {
            if (Double.compare(Double.parseDouble(gameData.get(game).get(columnIndex)),
                               filterRating) >= 0) {
                auxSet.add(game);
            }
        }
        System.out.println(auxSet);
        setToSet(auxSet, matchingGames);
    }


    // B: finds all category tags that appears in the game catalogue
    // E: 
    // R: 
    // P: Set<String> matchingGames - games to search through
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static Set<String> validTags(Set<String> matchingGames,
                                        Map<String, Set<String>> gameTags) {
        Set<String> tags = new TreeSet<>();
        for (String game : matchingGames) {
            for (String tag : gameTags.get(game)) {
                tags.add(tag);
            }
        }
        return tags;
    }


    // B: prints all valid tags (category tags that appear in the game catalogue)
    // E:
    // R:
    // P: Set<String> validTags - category tags that appear in the game catalogue
    public static void printValidTags(Set<String> validTags) {
        System.out.println();
        System.out.println("Current valid category tags:");
        for (String tag : validTags) {
            System.out.println("    * " + tag);
        }
        System.out.println();
    }


    // B: filters list of games to find games that contain at least one of the given tag(s)
    // E: throws IllegalStateException if the auxilliary set is not empty
    // R:
    // P: List<String> filterTags - category tags that the games should have at least one of
    //    Set<String> matchingGames - games to search through
    //    Set<String> auxSet - empty auxilliary set (will keep track of games that can be
    //                         played by the given number of players)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void filterByContainsTag(List<String> filterTags, Set<String> matchingGames,
                                           Set<String> auxSet, Map<String, Set<String>> gameTags) {
        if (!auxSet.isEmpty()) {
            throw new IllegalStateException();
        }
        for (String game : matchingGames) {
            for (String filterTag : filterTags) {
                if (gameTags.get(game).contains(filterTag)) {
                    auxSet.add(game);
                }
            }
        }
        setToSet(auxSet, matchingGames);
    }


    // B: filters list of games to find games that does not contain at least one of
    //    the given tag(s)
    // E:
    // R:
    // P: List<String> filterTagsExclude - category tags that the games should not have
    //    Set<String> matchingGames - games to search through
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void filterByDoesntContainTag(List<String> filterTagsExclude,
                                                Set<String> matchingGames,
                                                Map<String, Set<String>> gameTags) {
        for (String filterTag : filterTagsExclude) {
            Iterator<String> itr = matchingGames.iterator();
            while (itr.hasNext()) {
                String game = itr.next();
                if (gameTags.get(game).contains(filterTag)) {
                    itr.remove();
                }
            }
        }
    }


    // B: inserts a list into another list
    // E:
    // R:
    // P: List<List<String>> allFilterTags - list to insert the list into
    //    List<String> filterTags - list being inserted
    public static void insertListInList(List<List<String>> allFilterTags,
                                        List<String> filterTags) {
        int size = allFilterTags.size();
        allFilterTags.add(new ArrayList<>());
        for (String tag : filterTags) {
            allFilterTags.get(size).add(tag);
        }
    }


    // B: prints all the filters being used and the resulting games and their associated data
    //    after a round of filtering
    // E:
    // R:
    // P: List<String> keyList - currently available filter commands for the filtering menu
    //    int filterNumPlayers - number of players looking to play
    //    int filterDuration - time (in minutes) user is free to play a game
    //    String filterNovelty - whether it is filtering for a game that has or has not
    //                           been played before
    //    double filterSocRating - minimum society rating filtered for
    //    double filterInternetRating - minimum internet rating filtered for
    //    List<List<String>> allFilterTags - tags that were filtered with to find games
    //                                       with these tags
    //    List<List<String>> allFilterTagsExclude - tags that were filtered with to find
    //                                       games without these tags
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    //    Set<String> matchingGames - games that match all the filters
    public static void printResults (List<String> keyList, int filterNumPlayers,
                                     int filterDuration, String filterNovelty,
                                     double filterSocRating, double filterInternetRating,
                                     List<List<String>> allFilterTags,
                                     List<List<String>> allFilterTagsExclude,
                                     Map<String, List<String>> gameData,
                                     Map<String, Set<String>> gameTags,
                                     Set<String> matchingGames) {
        System.out.println();
        System.out.println("== Currently Filtering By ==");
        if (!keyList.contains("\t- [1] Number of players")) {
            System.out.println("Number of players: " + String.valueOf(filterNumPlayers));
        }
        if (!keyList.contains("\t- [2] Duration (in minutes)")) {
            System.out.println("Duration: " + filterDuration);
        }
        if (!keyList.contains("\t- [3] Novelty of game")) {
            System.out.println("New game: " + filterNovelty);
        }
        if (!keyList.contains("\t- [4] Society rating")) {
            System.out.println("Minimum society rating: " + filterSocRating);
        }
        if (!keyList.contains("\t- [5] Internet rating")) {
            System.out.println("Minimum internet rating: " + filterInternetRating);
        }
        if (!allFilterTags.isEmpty()) {
            System.out.println("Tags: " + allFilterTags.get(0));
            if (allFilterTags.size() > 1) {
                for (int i = 1; i < allFilterTags.size(); i++) {
                    System.out.println("Further filtered by tags: "
                                       + allFilterTags.get(i));
                }
            }
        }
        if (!allFilterTagsExclude.isEmpty()) {
            System.out.println("Excluded tags: " + allFilterTagsExclude.get(0));
            if (allFilterTagsExclude.size() > 1) {
                for (int i = 1; i < allFilterTagsExclude.size(); i++) {
                    System.out.println("Further filtered by excluded tags: "
                                       + allFilterTagsExclude.get(i));
                }
            }
        }
        System.out.println();

        // Print results from filter:
        System.out.println("== Filter Results ==");
        printGames(gameData, gameTags, matchingGames);
        System.out.println();
    }


    // B: prints and runs the menu for editing options, allowing the user to edit
    //    a game's data in the catalogue
    // E: throws IllegalArgumentException if input game name is not in the game catalogue
    // R:
    // P: String editGame - name of the game being edited
    //    Scanner console - scanner to read user input
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void editInterface(String editGame, Scanner console,
                                     Map<String, List<String>> gameData,
                                     Map<String, Set<String>> gameTags) {
        if (!gameData.containsKey(editGame)) {
            throw new IllegalArgumentException("Board game is not in the catalogue.");
        }
        printEditKey(editGame);
        String editChoice = console.nextLine();
        while (!editChoice.equalsIgnoreCase("R")) {
            if (editChoice.equals("0") || editChoice.equals("1")
                || editChoice.equals("2") || editChoice.equals("3")
                || editChoice.equals("4") || editChoice.equals("5")) {
                int columnIndex = Integer.parseInt(editChoice);
                System.out.println("Current entry: "
                                   + gameData.get(editGame).get(columnIndex));
                System.out.print("Please enter the new entry "
                                 + "(matching the format of the current entry): ");
                String newEntry = console.nextLine();
                gameData.get(editGame).set(columnIndex, newEntry);
                System.out.println("Successfully changed the entry to: "
                                   + gameData.get(editGame).get(columnIndex));
            
            } else if (editChoice.equals("6")) {
                System.out.println("Current category tags: "
                                   + gameTags.get(editGame).toString());
                System.out.print("Please enter the new category tags "
                                 + "(separate each tag by a space): ");
                Set<String> editTags = gameTags.get(editGame);
                editTags.clear();
                Collections.addAll(editTags, console.nextLine().split(" "));
                System.out.println("Successfully changed the entry to: "
                                   + gameTags.get(editGame).toString());
                
            } else {
                System.out.println("Please enter a valid option from "
                                   + "the menu of editable properties.");
            }
            System.out.println();
            printEditKey(editGame);
            editChoice = console.nextLine();
        }
        System.out.println();
    }


    // B: prints out the key of possible edit commands for the editing menu
    // E:
    // R:
    // P: String editGame - name of the board game being edited
    public static void printEditKey(String editGame) {
        System.out.println("Below are the editable properties of the game.");
        System.out.println("\t- [0] Number of players");
        System.out.println("\t- [1] Ideal number of players");
        System.out.println("\t- [2] Duration");
        System.out.println("\t- [3] Number of times played");
        System.out.println("\t- [4] Society rating");
        System.out.println("\t- [5] Internet rating");
        System.out.println("\t- [6] Category tags");
        System.out.println("\t- [R] Return to main menu");
        System.out.println("Which property of '" + editGame.replaceAll("_", " ")
                           + "' would you like to edit?");
        System.out.print("Enter your choice: ");
    }


    // B: runs and prints the menu to allow a user to mark a game as played and rate it.
    //    Also adds the game to the catalogue if it is new.
    // E:
    // R:
    // P: Scanner console - scanner to read user input
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void markGamePlayedInterface(Scanner console,
                                               Map<String, List<String>> gameData,
                                               Map<String, Set<String>> gameTags) {
        System.out.print("Which board game have you played? " +
                         "Enter the name (please capitalise each word): ");
        String markGame = console.nextLine().replaceAll(" ", "_");
        
        // Adding new entry to map if it's a new game:
        if (!gameData.containsKey(markGame)) {
            System.out.println();
            System.out.println("*** This is a game you have never played before. "
                               + "To add this game to the catalogue please enter "
                               + "the following information. ***");
            addNewGame(markGame, console, gameData, gameTags);
        }

        // Increase playCount (no. times played) by 1:
        Integer playCount = Integer.parseInt(gameData.get(markGame).get(3));
        playCount++;
        gameData.get(markGame).set(3, String.valueOf(playCount));

        // Print confirmation messages & adds new rating if chosen:
        System.out.println();
        System.out.print("Would you like to rate this game? "
                           + "Enter your choice (Yes or No): ");
        String choice = console.nextLine();
        System.out.println();

        if (choice.equalsIgnoreCase("Yes")) {
            rateGame(markGame, console, gameData);
            System.out.printf("Successfully marked '" + markGame.replaceAll("_", " ")
                              + "' as played. It has an updated society rating of "
                              + "%.1f/10 stars.",
                              Double.parseDouble(gameData.get(markGame).get(4)));
            System.out.println();
        } else {
            System.out.println("Successfully marked '" + markGame.replaceAll("_", " ")
                               + "' as played.");
        }
        System.out.println();
    }


    // B: adds a new game to the catalogue
    // E:
    // R:
    // P: String newGame - name of the new board game, capitalised properly and
    //                     formatted to have any spaces be represented as underscores
    //    Scanner console - scanner to read user input
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    public static void addNewGame(String newGame, Scanner console,
                                  Map<String, List<String>> gameData,
                                  Map<String, Set<String>> gameTags) {
        // Initialising new entries for newGame in both maps:
        gameData.put(newGame, new ArrayList<String>());
        List<String> entry = gameData.get(newGame);
        gameTags.put(newGame, new HashSet<String>());
        Set<String> entryTags = gameTags.get(newGame);

        // Populating data from user input:
        System.out.println("How many players is this game for? "
                           + "To enter a range please separate numbers by a hyphen.");
        System.out.print("Enter number of players: ");
        String data = console.nextLine();
        entry.add(data);
        System.out.println();
        System.out.println("How many players are IDEAL for this game? "
                           + "To enter a range please separate numbers by a hyphen.");
        System.out.print("Enter ideal number of players: ");
        data = console.nextLine();
        entry.add(data);
        System.out.println();
        System.out.println("How long does it take to play this game in minutes? "
                           + "Please only enter one number. ");
        System.out.print("Enter duration of game: ");
        data = console.nextLine();
        entry.add(data);
        entry.add("0"); // Initialising playCount (assumes new games have never been played)
        entry.add("0"); // Initialising socRating
        entry.add("0"); // Initialising numRating
        System.out.println();
        System.out.println("What is the rating given to this game from BoardGameGeek?");
        System.out.print("Enter the internet rating: ");
        data = console.nextLine();
        entry.add(data);
        System.out.println();
        System.out.println("What categories does this game fall under? "
                           + "Each category can only be one word long, "
                           + "and they should be separated by a space.");
        System.out.print("Enter categories: ");
        data = console.nextLine();
        Collections.addAll(entryTags, data.split(" "));
        System.out.println();
        System.out.println("Successfully added '" + newGame.replaceAll("_", " ")
                           + "' to board game catalogue.");
    }


    // B: allows the user to rate a given game and calculates the resulting
    //    new society rating for the game
    // E:
    // R:
    // P: String markGame - name of the game to rate
    //    Scanner console - scanner to read user input
    //    Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    public static void rateGame(String markGame, Scanner console,
                                Map<String, List<String>> gameData) {
        System.out.println("How would you rate the game out of 10, "
                           + "with 10 being fantabulous and "
                           + "1 being a game to never be played again.");
        System.out.print("Enter your rating: ");
        double userRating = Double.parseDouble(console.nextLine());
        double gameRating = Double.parseDouble(gameData.get(markGame).get(4));
        int gameNumRatings = Integer.parseInt(gameData.get(markGame).get(5));

        gameNumRatings++;
        gameRating = (userRating + gameRating) / gameNumRatings;

        gameData.get(markGame).set(4, String.valueOf(gameRating));
        gameData.get(markGame).set(5, String.valueOf(gameNumRatings));
    }


    // B: prints the top games ranked by their value in a given column in the catalogue
    // E: throws IllegalArgumentException if given column index is greater than number
    //    of columns
    // R:
    // P: Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    int columnIndex - index of the column in the catalogue that is being used as the
    //                      ranking information
    public static void printTopGames(Map<String, List<String>> gameData, int columnIndex) {
        if (columnIndex > 6) {
            throw new IllegalArgumentException("Column index cannot be greater "
                                               + "than number of columns");
        }
        // Maps count to game names:
        Map<Double, Set<String>> reverseOrderedCounts = new TreeMap<>();
        for (String game : gameData.keySet()) {
            double count = Double.parseDouble(gameData.get(game).get(columnIndex));
            if (!reverseOrderedCounts.containsKey(count)) {
                reverseOrderedCounts.put(count, new TreeSet<String>());
            }
            reverseOrderedCounts.get(count).add(game);
        }

        // Getting counts in descending order:
        Stack<Double> orderedCounts = new Stack<>();
        for (double count : reverseOrderedCounts.keySet()) {
            orderedCounts.push(count);
        }

        // Printing top 10 games for given count:
        int numPrint = 1;
        while (!orderedCounts.isEmpty()) {
            double count = orderedCounts.pop();
            for (String game : reverseOrderedCounts.get(count)) {
                if (numPrint <= 10) {
                    System.out.println(numPrint + ". " + game.replaceAll("_", " "));
                    numPrint++;
                }
            }
        }
        System.out.println();
    }


    // B: prints and outputs the current board game catalogue to a file
    // E: throws FileNotFoundException when file does not exist
    // R:
    // P: Map<String, List<String>> gameData - map containing game names and their associated
    //                                         information (excluding category tags)
    //    Map<String, Set<String>> gameTags - map containing game names and their associated
    //                                        category tags
    //    String path - path to the original games catalogue file
    public static void updateBoardGameCatalogue(Map<String, List<String>> gameData,
                                                Map<String, Set<String>> gameTags,
                                                String path) throws FileNotFoundException {
        File outputFile = new File("updated_" + path);
            // Named differently to original file so can see output
        PrintStream outputStream = new PrintStream(outputFile);

        outputStream.println(gameData.keySet().size());
        outputStream.println("game" + "\t" + "num_players" + "\t" + "best_players" + "\t"
                             + "duration_mins" + "\t" +	"play_count" + "\t"
                             + "soc_rating"	+ "\t" + "num_ratings" + "\t"
                             + "internet_rating" + "\t" + "category");
        for (String game : gameData.keySet()) {
            outputStream.print(game + "\t");
            for (String data : gameData.get(game)) {
                outputStream.print(data + "\t");
            }
            for (String tag : gameTags.get(game)) {
                outputStream.print(tag + " ");
            }
            outputStream.println();
        }
    }
}
