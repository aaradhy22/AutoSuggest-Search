package AutoSuggestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord;
}

class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert words into Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
    }

    // Get suggestions for given prefix
    public List<String> autoSuggest(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>(); // No suggestions
            }
            node = node.children.get(c);
        }
        List<String> suggestions = new ArrayList<>();
        findAllWords(node, prefix, suggestions);
        return suggestions;
    }

    // DFS to collect words from Trie
    private void findAllWords(TrieNode node, String prefix, List<String> suggestions) {
        if (node.isEndOfWord) {
            suggestions.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findAllWords(entry.getValue(), prefix + entry.getKey(), suggestions);
        }
    }
}

public class AutoSuggestApp extends JFrame {
    private Trie trie;
    private JTextField searchField;
    private JPopupMenu suggestionMenu;

    public AutoSuggestApp() {
        trie = new Trie();
        insertSampleWords();

        setTitle("Auto-Suggest Search Bar");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        searchField = new JTextField(20);
        suggestionMenu = new JPopupMenu();

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                showSuggestions(searchField.getText());
            }
        });

        add(searchField);
        setVisible(true);
    }

    // Insert some sample words
    private void insertSampleWords() {
        String[] words = {"apple", "app", "apricot", "banana", "bat", "ball", "cat", "car", "cart"};
        for (String word : words) {
            trie.insert(word);
        }
    }

    // Show suggestions as user types
    private void showSuggestions(String prefix) {
        suggestionMenu.removeAll();
        if (prefix.isEmpty()) {
            suggestionMenu.setVisible(false);
            return;
        }

        List<String> suggestions = trie.autoSuggest(prefix);
        if (suggestions.isEmpty()) {
            suggestionMenu.setVisible(false);
            return;
        }

        for (String suggestion : suggestions) {
            JMenuItem item = new JMenuItem(suggestion);
            item.addActionListener(e -> {
                searchField.setText(suggestion);
                suggestionMenu.setVisible(false);
            });
            suggestionMenu.add(item);
        }

        suggestionMenu.show(searchField, 0, searchField.getHeight());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutoSuggestApp::new);
    }
}

