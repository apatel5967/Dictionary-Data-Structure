# Dictionary-Data-Structure

A traditional Trie structure takes takes words and splits them into characters, which are then inputted into the data structure. This code does exactly that, but at the end, it compresses the characters together again to form words. The most common suffix for these words are made the head node, and the children are the remainders of the suffix. 

This code is meant to be efficient and concise. The time complexity it takes to search, insert, and delete are all O(N), where N is the length of the string. The space complexity is the same. 

The following picture is an example of that 
![image](https://github.com/user-attachments/assets/5e63f324-8745-4fa4-81d8-22cfd6c91988)
