# nand2tetris_java
This is my take on the programming projects of the two wonderful courses:

[Nand2Tetris Part I](https://www.coursera.org/learn/build-a-computer) (projects 1-6) & 
[Nand2Tetris Part II](https://www.coursera.org/learn/nand2tetris2/) (projects 7-12)

which I audited during my free time while at uni, over the span of a little less than a year. 
All of the projects in this repository are implemented in Java. But there is more!

## Overview

### 'Naked' projects
The courses have 4 major software projects: 
 - Project 6 - The Assembler ---> "assembler" folder
 - Project 7&8 - The VM Translator ---> "vmtranslator" folder
 - Project 10&11 - The Compiler ---> "compiler" folder 
 - Project 12 - The Operating System ---> "os" folder

### Divergence from proposed implementation
The projects generally follow the proposed implementation presented in the courses, however there are some internal and external structural differences.
I do my best to comment the HACK (pun intended) out of my code, but here is a brief list of the major differences:
 - None of the above projects is standalone, they CANNOT be run from terminal. They are managed by a graphical application, whose functional part is held in 
 nand2tetris.java, in the parent directory, and the graphical part by the gui.java in "gui" folder. The application is very intuitive and needs no usage explaination, but remember: 
 >:small_red_triangle:**main() for the whole project is in nand2tetris.java**:small_red_triangle:
 - "utilities" folder contains utility static methods that all the projects use.
 - "references" folder contains a handful of handwritten PDF Hack Computer Cheat Sheets I heavily used while writing code.
 - "hackComputer" contains a re-implementation of the Hack Computer in Java, following projects 1-5. As of september '20, it almost works. ALMOST. Currently working on a JUnit test for CPU. Ah, and I still haven't re-implemented the screen.
 
 ### Left to do
  - Project 12
  - Cleanup and more thorough comment of project 10&11
  - Extend utilities to Project 6, 7, 8.
  - Fix broken CPU and add working SCREEN (only then see ways to make it all more efficient)
  - Make VM translator more efficient (bring it to under 25000 lines for the whole os, right now it is 40000+ and doesn't even fit in the Hack Computer)
  - Make "if" compiling more efficient
