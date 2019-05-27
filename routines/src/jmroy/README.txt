This is Jessica Roy's Assignment 1 for Advanced Programming Techniques
Boston University MET CS 622, online, Summer 1 2019

Running this application:

I was able to run this application in one of two ways:

1. From IntelliJ, using the Run function on the RoutinesApp class
2. From the command line, in the routines\routines\bin folder:
   java jmroy.RoutinesApp

Interacting with the application:

1. You will be prompted for your "username". Whatever you enter will be
   converted to all lowercase, alphanumerics only. If you enter a name
   that has been used before, it will load the data; otherwise it will
   create a new user.
2. You will be prompted for a name. If you don't enter one, the username
   will be reused.
3. The List, Edit, and Run options will display "No routines found" until
   you enter at least one routine, so you may want to start with option 1.
4. Enter an arbitrary string for the name of your routine.
5. You can have a routine with no tasks, but that isn't very interesting,
   so go ahead and enter a few tasks.
6. When you're done entering tasks, just hit enter when it prompts you
   for the name of the next task.
7. Once you have a routine entered, you can view all the routines,
   edit routines, or "run" a specific routine:
   a) "view" will list all routines and their tasks
   b) "edit" allows you to rename a routine, add tasks, edit tasks, or
      delete tasks
   c) "run" is a dummy for now
8. Choose anything other than 1-4 at the main menu to exit.
