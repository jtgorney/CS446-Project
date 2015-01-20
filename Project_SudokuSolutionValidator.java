// ******************************************************
// CS446 Project - Sudoku Solution Validator
// Jacob Gorney, Spencer Kokaly, Ethan Rotz
// CS446, Winter 2014
// Dr. Farid Hallouche
// April 15th 2014
// Program description: Program uses three threads to check
// the following items of the puzzle: Column, Row, and Box. If
// ALL checks pass validation, the puzzle is considered valid.
// Sudoku puzzles are read by the program using System.in. This
// solution validator uses multiple threads efficiently.
// ******************************************************

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Main program class that controls data and the puzzle.
 * 
 * @author Jacob Gorney, Spencer Kokaly, Ethan Rotz
 */
public class Project_SudokuSolutionValidator {
   
   // Constants for box and puzzle size.
   // Constraints are provided by the book.
   final int PUZZLE_SIZE = 9;
   final int BOX_SIZE = 3;
   
   // The variable that will contain the puzzle to solve.
   int[][] puzzle = new int[PUZZLE_SIZE][PUZZLE_SIZE];
   
   // Boolean switches for thread control and puzzle validation.
   // Each boolean corresponds to a thread and a validation operation.
   boolean isValidRows = true;
   boolean isValidColumns = true;
   boolean isValidBoxes = true;
   
   // Thread boolean values
   boolean rowCheckerDone;
   boolean columnCheckerDone;
   boolean boxCheckerDone;
   
   /**
    * Main java thread that begins execution.
    * @param args Command line arguments.
    */
   public static void main(String[] args) {
      // Run the program by creating a new object.
      new Project_SudokuSolutionValidator(args);
   }
   
   /**
    * Project class constructor that gets input, prints the puzzle,
    * and begins thread execution.
    */
   public Project_SudokuSolutionValidator(String[] args) {
      // Start the validator
      // Input and print
      if (args.length == 1)
         inputPuzzle(args[0]);
      else
         inputPuzzle(null);
      
      printPuzzle();
      // Spawn the threads
      new SudokuSolutionValidator();
   }
   
   /**
    * Input the Sudoku puzzle using System.in. Checks are in place
    * to catch errors.
    */
   private void inputPuzzle(String fileLocation) {
      // Input via file or command line
      if (fileLocation != null) {
         // Input via file
         FileReader reader = null;
         // Open the file and check for errors.
         try {
            reader = new FileReader(fileLocation);
         } catch (FileNotFoundException ex) {
            // Catch the error
            System.out.println("Error: File does not exist or is invalid.");
            System.exit(0);
         }
         // Read the file
         Scanner scFile = new Scanner(reader);
         // Loop and set the array
         for (int i = 0; i < PUZZLE_SIZE; i++) {
            int j = 0;
            // Set the value of the puzzle
            while (scFile.hasNextInt() && j != PUZZLE_SIZE)
               puzzle[i][j++] = scFile.nextInt();
         }
         
      } else {
         // Create a scanner to get input
         Scanner sc = new Scanner(System.in);
         System.out.println();
         // Call the scanner nine times to get the rows
         for (int i = 0; i < PUZZLE_SIZE; i++) {
            System.out.print("Enter Sudoku Row: ");
            // Get the input
            String[] input = sc.nextLine().split("\\s+");
            // Check for errors
            if (input.length != PUZZLE_SIZE) {
               System.out.println("Error: Number of elements is not "
                       + PUZZLE_SIZE + " Number of elements is " + input.length);
               System.exit(0);
            }
            // Add the row
            for (int j = 0; j < PUZZLE_SIZE; j++) {
               // Check for characters
               try {
                  puzzle[i][j] = Integer.parseInt(input[j]);
               } catch (NumberFormatException ex) {
                  System.out.println("Error: Only add numbers.");
                  System.exit(0);
               }

               // Check for invalid number   
               if (puzzle[i][j] > 9 || puzzle[i][j] < 1) {
                  System.out.println("Error: Only add valid numbers.");
                  System.exit(0);
               }
            }
         }
         System.out.println();
      }
   }
   
   /**
    * Print the puzzle after accepting input.
    */
   private void printPuzzle() {
      // Print the title
      System.out.println("Puzzle Being Checked: ");
      System.out.println();
      // Main loop to print row-column.
      for (int row = 0; row < PUZZLE_SIZE; row++) {
         String line = "";
         for (int column = 0; column < PUZZLE_SIZE; column++) {
            line += " " + puzzle[row][column];
         }
         // Print the line
         System.out.println(line);
      }
      System.out.println();
   }
   
   /**
    * Main class that contains the Runnables for checking the puzzle.
    */
   private class SudokuSolutionValidator {
      
      /**
       * Thread creation constructor. The checker threads are
       * spawned here.
       */
      public SudokuSolutionValidator() {
         new Thread(new ColumnChecker()).start();
         new Thread(new RowChecker()).start();
         new Thread(new BoxChecker()).start();
      }
      
      /**
       * Output the validation results ONLY if all other threads have
       * finished their validation routines.
       */
      public void tryOutputResults() {
         // Check if all threads are done
         if (rowCheckerDone && columnCheckerDone && boxCheckerDone) {
            // Perform the output because all threads are done
            if (isValidRows && isValidColumns && isValidBoxes)
               System.out.println("Validation Result: Puzzle Is Valid");
            else
               System.out.println("Validation Result: Puzzle Is Not Valid");
         }
      }
      
      /**
       * RowChecker checks each row of the puzzle for unique 1 - 9.
       */
      private class RowChecker implements Runnable {
         @Override
         public void run() {
             // Store in hashset for validation
            HashSet<Integer> set = new HashSet<>();
            // Check the rows of puzzle
            for (int row = 0; row < PUZZLE_SIZE; row++) {
               // Loop through columns
               for (int column = 0; column < PUZZLE_SIZE; column++) {
                  // Check for HashSet add
                  if (!set.add(puzzle[row][column])) {
                     isValidRows = false;
                     break;
                  }
               }
               // Clear the HashSet if valid.
               if (!isValidRows)
                  break;
               else
                  set.clear();
               }
            // Set column flag to finished
            rowCheckerDone = true;
            tryOutputResults(); // Try to output the results
         }
      }
      
      /**
       * ColumnChecker checks each column of the puzzle for unique 1 - 9.
       */
      private class ColumnChecker implements Runnable {
         @Override
         public void run() {
            // Store in hashset for validation
            HashSet<Integer> set = new HashSet<>();
            // Check the columns of puzzle
            for (int row = 0; row < PUZZLE_SIZE; row++) {
               // Loop through columns
               for (int column = 0; column < PUZZLE_SIZE; column++) {
                  // Add to HashSet and check
                  if (!set.add(puzzle[column][row])) {
                     isValidColumns = false;
                     break;
                  }
               }
               // Clear the HashSet if valid and continue.
               if (!isValidColumns)
                  break;
               else
                  set.clear();
               }
            // Set column flag to finished
            columnCheckerDone = true;
            tryOutputResults(); // Try to output the results
         }
      }
      
      /**
       * BoxChecker checks each box of the puzzle for unique 1 - 9.
       */
      private class BoxChecker implements Runnable {
         @Override
         public void run() {
            
            // Store in hashset for validation. This can be thought of
            // as using rows of 9 x 9 boxes. Each map corresponds to a column
            // in that row of boxes.
            HashSet<Integer> box1 = new HashSet<>();
            HashSet<Integer> box2 = new HashSet<>();
            HashSet<Integer> box3 = new HashSet<>();
            
            // Set the starting row count
            int rowCount = 0;
            
            for (int row = 0; row < PUZZLE_SIZE; row++) {
               // Iterate through each row
               
               for (int col = 0; col < PUZZLE_SIZE; col++) {
                  // Iterate through each column
                  // Determine which column, or box it is in.
                  if (col >= 0 && col < BOX_SIZE) {
                     // Add to Box 1
                     box1.add(puzzle[row][col]);
                  } else if (col >= BOX_SIZE && col < BOX_SIZE * 2) {
                     // Add to Box 2
                     box2.add(puzzle[row][col]);
                  } else {
                     // Add to Box 3
                     box3.add(puzzle[row][col]);
                  }
               }
               
               // Check if we have a row count of at least
               // BOX_SIZE
               if (rowCount == BOX_SIZE - 1) {
                  // We have hit the limit, check each
                  // box count for a total of 3x3:
                  if (!(box1.size() == Math.pow(BOX_SIZE, 2) &&
                          box2.size() == Math.pow(BOX_SIZE, 2) && 
                          box3.size() == Math.pow(BOX_SIZE, 2))) {
                     isValidBoxes = false;
                     break;
                  } else
                     rowCount = 0;
               } else
                  rowCount++;
            }
            // Output the result
            boxCheckerDone = true;
            tryOutputResults();
         }
      }
   }
}
