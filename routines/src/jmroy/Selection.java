package jmroy;

import java.util.ArrayList;
import java.util.Scanner;

public class Selection<Element extends Selectable> {

    /**
     * Displays a list of items for the user and prompts for a selection.
     * @param input Scanner for user input
     * @param items ArrayList of items the user can choose from
     * @param message A message to prompt for the choice (e.g. "Choose a routine")
     * @return The index of the selected item, if one is selected
     * @throws InvalidSelectionException if no items are found or invalid selection is made
     */
     int selectItem(
             Scanner input,
             ArrayList<Element> items,
             String message,
             String singular,
             String plural) throws InvalidSelectionException {
         String error;
        // If there aren't any items, exit
        if (items.size() == 0) {
            error = "No ".concat(plural).concat(" found.");
            throw (new InvalidSelectionException(error));
        }
        // Otherwise, list the items and prompt the user to choose an item
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, items.get(i).getName());
        }
        System.out.println(message);
        int selection;
        try {
            selection = Integer.parseInt(input.nextLine());
            if (selection > 0 && selection <= items.size()) {
                return selection - 1;
            } else {
                // Valid integer, but not on the list
                error = "Not a valid ".concat(singular).concat(".");
                throw (new InvalidSelectionException(error));
            }
        } catch (NumberFormatException e) {
            error = "Not a valid ".concat(singular).concat(".");
            throw (new InvalidSelectionException(error));
        }
    }
}