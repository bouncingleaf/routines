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
2. If you are creating a new user, you will be prompted for a name.
   If you don't enter one, the username will be reused as your name.
3. The home screen presents the options:
     Add a routine
     Manage routines
     Preferences
   ...followed by the list of routines if any.
   If you click on a routine, it will "run".
4. Running a routine:
   For now this is a simulation.
   Choose Exit to return to the main menu.
5. Add a routine:
   Enter an arbitrary string for the name of your routine.
   You can have a routine with no tasks, but that isn't very interesting,
   so go ahead and enter a few tasks.
   When you're done entering tasks, Save the routine.
6. Manage routines:
   On this screen you can:
     Rename a routine
     Add tasks
     Delete tasks (select one and choose Delete)
     Edit tasks (select one, edit the info, choose Save)
   You can save the routine, or discard changes
7. Preferences:
   The only preference currently available is the program color theme.
   You can choose Light or Dark and it will save automatically for the user.
   Sign out, sign back in - the theme should be retained.
   Light is the default theme.



