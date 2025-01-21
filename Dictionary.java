import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary class that stores words and associates them with their definitions
 */
public class Dictionary {
    private static class TrieNode {
        char value;
        List<TrieNode> children;
        boolean isEndOfWord;
        String definition;
        String compressedEdge;

        public TrieNode(char value) {
            this.value = value;
            this.children = new ArrayList<>();
            this.isEndOfWord = false;
            this.definition = null;
            this.compressedEdge = null;
        }

        public TrieNode getChild(char ch) {
            for(TrieNode child : children) {
                if (child.value == ch) return child;
            }
            return null;
        }
    }
    
    private TrieNode root;
    private boolean isCompressed;

    /**
     * Constructor to initialize the Dictionary
     */
    public Dictionary() {
        this.root = new TrieNode('/'); 
        this.isCompressed = false;
    }

    /**
     * A method to add a new word to the dictionary
     * If the word is already in the dictionary, this method will change its
     * definition
     *
     * @param word       The word we want to add to our dictionary
     * @param definition The definition we want to associate with the word
     */
    public void add(String word, String definition) {
        if(isCompressed) {
            throw new IllegalStateException("Cannot add after compression.");
        }
        TrieNode current = root; 
        for(char ch : word.toCharArray()) {
            TrieNode child = current.getChild(ch);
            if(child == null) {
                child = new TrieNode(ch);
                current.children.add(child);
            }
            current = child;
        }

        current.isEndOfWord = true;
        current.definition = definition;
    }

    /**
     * A method to remove a word from the dictionary
     *
     * @param word The word we want to remove from our dictionary
     */
    public void remove(String word) {
        if(isCompressed) {
            throw new IllegalStateException("Cannot remove after compression.");
        }
        removeHelper(root, word, 0);
    }

    private boolean removeHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.isEndOfWord){
                return false;
            }  
            node.isEndOfWord = false;
            node.definition = null;
            return node.children.isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode child = node.getChild(ch);
        if (child == null) return false;

        boolean shouldDelete = removeHelper(child, word, index + 1);
        if (shouldDelete) {
            node.children.remove(child);
            return node.children.isEmpty() && !node.isEndOfWord;
        }
        return false;
    }

    /**
     * A method to get the definition associated with a word from the dictionary
     * Returns null if the word is not in the dictionary
     *
     * @param word The word we want to get the definition for
     * @return The definition of the word, or null if not found
     */
    public String getDefinition(String word) {
        TrieNode current = root;
        int index = 0;

        while (index < word.length()) {
            char ch = word.charAt(index);
            TrieNode child = current.getChild(ch);

            if (child == null) {
                return null; 
            }

            if (child.compressedEdge != null) {
                String edge = child.compressedEdge;
                int edgeIndex = 0;

                while (edgeIndex < edge.length() && index + 1 < word.length()
                        && edge.charAt(edgeIndex) == word.charAt(index + 1)) {
                    edgeIndex++;
                    index++;
                }

                if (edgeIndex < edge.length()) {
                    return null; 
                }
            }

            current = child;
            index++;
        }

        return current.isEndOfWord ? current.definition : null;
    }

    /**
     * A method to get a string representation of the sequence of nodes which would
     * store the word
     * in a compressed trie consisting of all words in the dictionary
     * Returns null if the word is not in the dictionary
     *
     * @param word The word we want the sequence for
     * @return The sequence representation, or null if word not found
     */
    public String getSequence(String word) {
        if (!isCompressed) {
            throw new IllegalStateException("Compression must be called before getting sequences.");
        }
    
        StringBuilder sequence = new StringBuilder();
        TrieNode current = root;
        int index = 0;
    
        while (index < word.length()) {
            char ch = word.charAt(index);
            TrieNode child = current.getChild(ch);
    
            if (child == null) return null; 
    
            if (sequence.length() > 0) {
                sequence.append("-");
            }
    
            // sequence.append(ch);
            sequence.append(ch);

            if (child.compressedEdge != null) {
                String edge = child.compressedEdge;
                int edgeIndex = 0;
    
                while (edgeIndex < edge.length() && index + 1 < word.length()
                        && edge.charAt(edgeIndex) == word.charAt(index + 1)) {
                    sequence.append(edge.charAt(edgeIndex));
                    edgeIndex++;
                    index++;
                }
                

                if (edgeIndex < edge.length()) {
                    return null; 
                }
            }

    
            current = child;
            index++;
        }
        if(!current.isEndOfWord) return null;
        return sequence.toString();
    }

    private int countWords(TrieNode node) {
        int count = node.isEndOfWord ? 1 : 0;
        for (TrieNode child : node.children) {
            count += countWords(child);
        }
        return count;
    }
    /**
     * Gives the number of words in the dictionary with the given prefix
     *
     * @param prefix The prefix we want to count words for
     * @return The number of words that start with the prefix
     */
    public int countPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return 0;
        }
    
        TrieNode current = root;
        int index = 0;
    
        while (index < prefix.length()) {
            char ch = prefix.charAt(index);
            TrieNode child = current.getChild(ch);
    
            if (child == null) {
                return 0; 
            }
    
            if (child.compressedEdge != null) {
                String edge = child.compressedEdge;
                int edgeIndex = 0;
    
                while (edgeIndex < edge.length() && index + 1 < prefix.length()
                        && edge.charAt(edgeIndex) == prefix.charAt(index + 1)) {
                    edgeIndex++;
                    index++;
                }
    
                if(index + 1 < prefix.length() && edgeIndex < edge.length()) {
                    return 0; 
                }
            }
    
            current = child;
            index++;
        }
    
        return countWords(current);
    }

    /**
     * Compresses the trie by combining nodes with single children
     * This operation should not change the behavior of any other methods
     */
    public void compress() {
        if (isCompressed){
            return;
        }
        compressHelper(root);
        isCompressed = true; 
    }

    private void compressHelper(TrieNode node) {
        boolean bum = false;
        for(TrieNode child : new ArrayList<>(node.children)) {
            while(child.children.size() == 1 && !child.isEndOfWord) {
                TrieNode next = child.children.get(0);
                child.compressedEdge = (child.compressedEdge == null ? "" : child.compressedEdge) + next.value;
                child.children = next.children;
                child.isEndOfWord = next.isEndOfWord;
                child.definition = next.definition;
                bum=true;
            }
            if(!bum || child.children.size() > 0){
                compressHelper(child);
            }
        }
    }
}