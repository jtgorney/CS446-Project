// ******************************************************
// CS446 Project - The Sleeping Teaching Assistant
// Jacob Gorney, Spencer Kokaly, Ethan Rotz
// CS446, Winter 2014
// Dr. Farid Hallouche
// April 15th 2014
// Program description: Program simulates a teaching assistant
// student interraction scenario where the TA office only has 4
// available spots including the chair in the office. If the TA
// does not have any students asking for help, the TA will sleep.
// If a student comes for help, they will ask the TA to wake up, 
// or take a seat in line to get help.
// ******************************************************

import java.util.concurrent.Semaphore;

/**
 * SleepingTeacherAssistant program class for simulation of
 * student to TA interaction.
 * 
 * @author Jacob Gorney
 */
public class SleepingTeacherAssistant {
   // Only allow one resource to be used at a time, since the
   // TA can only help 1 student in their office.
   // Classes are built around semaphore which provides mutual exclusion
   // and thread wait.
   Semaphore TAHelpSemaphore = new Semaphore(1);
   
   /**
    * Main java thread that begins execution.
    * @param args Command line arguments.
    */
   public static void main(String[] args) {
      try {
         if (args.length >= 1)
            new SleepingTeacherAssistant(Integer.parseInt(args[0]));
         else {
            System.out.println("Please enter number of students as argument.");
            System.exit(0);
         }
      } catch (Exception e) {
         System.out.println("Error parsing number of students.");
            System.exit(0);
      }
   }
   
   /**
    * SleepingTeacherAssistant class constructor that creates TA and
    * student threads based on the command line argument.
    * 
    * @param numStudents Number of students
    */
   public SleepingTeacherAssistant(int numStudents) {
      // Spawn the TA thread.
      new Thread(new TeachingAssistant()).start();
      
      // Spawn the student threads.
      for (int i = 0; i < numStudents; i++)
         new Thread(new Student()).start();
   }
   
   /**
    * Teaching assistant thread that helps a student.
    */
   private class TeachingAssistant implements Runnable {
      @Override
      public void run() {
         try {
            while (true) {
               // Wake up the TA
               if (!TAHelpSemaphore.hasQueuedThreads())
                  System.out.println("The Teaching Assistant was sleeping. Waking up.");
               // Get resources from semaphore
               TAHelpSemaphore.release();
               // Print the message and help the student
               System.out.println("Teaching Assistant is now helping a student.");
               // Add one to count for the one being accepted
               System.out.println("Current Students Waiting/Being Helped: " 
                       + (TAHelpSemaphore.getQueueLength() + 1));
               Thread.sleep((int)(Math.random() * 30000));
            }
         } catch (InterruptedException e) {
            // Thread exception
            System.out.println("Student made TA mad or he just got a nasty phone call from the dean.");
         }
      }
   }
   
   /**
    * Student thread that requests help from the TA.
    */
   private class Student implements Runnable {
      @Override
      public void run() {
         try {
            // Student will always be studying/learning
            while (true) {
               // Sleep the thread for a random time to simulate studying
               Thread.sleep((int)(Math.random() * 30000));
               // If we already have 3 students waiting, come back later.
               if (TAHelpSemaphore.getQueueLength() >= 3) {
                  System.out.println("Student Says: Looks like the Teaching Assistant " + 
                          "is busy. I'll come back later.");
               } else
                  TAHelpSemaphore.acquire(); // Take a seat.
            }
         } catch (InterruptedException e) {
            // Catch student thread error.
            System.out.println("Student is mad at the TA and has left.");
         }
      }
   }
}