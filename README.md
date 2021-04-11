# Project 3: Spell Checker
For this project, you will implement a Dictionary ADT with extra functionality of being able to find the closest entries 
in the dictionary to the given invalid word. You will implement such a dictionary using a compact prefix tree. Please refer to the pdf description of the project for details.



For the Suggest():

 1. When the word is in the dictionary, just return one word.

 2. When the word is not in the dictionary, just return the words with the word as the prefix.

 3. When there are not words with the word as prefix , just return the words with the word without the last character. If also not, delete the last character and return the words with this prefix. 

    ps: In character order, after the last character.

    