// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.util.ArrayList;

// public class DataStructureGUI extends JFrame {
//     private final UserFactory factory = new UserFactory();
//     private UserType currentType;
//     private SinglyLinkedList dataStructure;
//     private DefaultListModel<String> listModel;
    
//     // GUI Components
//     private JComboBox<String> typeSelector;
//     private JTextArea outputArea;
//     private JTextField inputField;

//     public DataStructureGUI() {
//         initComponents();
//         setupLayout();
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(600, 400);
//         setTitle("Data Structure GUI");
//     }

//     private void initComponents() {
//         // Type selector
//         typeSelector = new JComboBox<>(factory.getTypeNameList().toArray(new String[0]));
//         typeSelector.addActionListener(this::typeSelected);
        
//         // Input components
//         inputField = new JTextField(20);
//         JButton addButton = new JButton("Add");
//         addButton.addActionListener(this::addItem);
        
//         JButton sortButton = new JButton("Sort");
//         sortButton.addActionListener(e -> sortItems());
        
//         JButton removeButton = new JButton("Remove Selected");
//         removeButton.addActionListener(e -> removeItem());

//         // Output area
//         outputArea = new JTextArea(10, 40);
//         outputArea.setEditable(false);
        
//         // List model
//         listModel = new DefaultListModel<>();
//         JList<String> itemList = new JList<>(listModel);
//     }

//     private void setupLayout() {
//         JPanel controlPanel = new JPanel();
//         controlPanel.add(new JLabel("Select Type:"));
//         controlPanel.add(typeSelector);
//         controlPanel.add(new JLabel("Input:"));
//         controlPanel.add(inputField);
//         controlPanel.add(new JButton("Add") {{
//             addActionListener(DataStructureGUI.this::addItem);
//         }});
//         controlPanel.add(new JButton("Sort") {{
//             addActionListener(e -> sortItems());
//         }});
//         controlPanel.add(new JButton("Remove") {{
//             addActionListener(e -> removeItem());
//         }});

//         JScrollPane scrollPane = new JScrollPane(new JList<>(listModel));
        
//         add(controlPanel, BorderLayout.NORTH);
//         add(scrollPane, BorderLayout.CENTER);
//         add(new JScrollPane(outputArea), BorderLayout.SOUTH);
//     }

//     // Event handlers
//     private void typeSelected(ActionEvent e) {
//         String typeName = (String) typeSelector.getSelectedItem();
//         currentType = factory.getBuilderByName(typeName);
//         dataStructure = new SinglyLinkedList(currentType);
//         updateDisplay();
//     }

//     private void addItem(ActionEvent e) {
//         try {
//             Object value = currentType.parseValue(inputField.getText());
//             dataStructure.add(value);
//             inputField.setText("");
//             updateDisplay();
//         } catch (Exception ex) {
//             JOptionPane.showMessageDialog(this, "Error parsing input: " + ex.getMessage());
//         }
//     }

//     private void removeItem() {
//         int index = listModel.getSize() > 0 ? 0 : -1; // Simplified for example
//         if (index >= 0) {
//             dataStructure.remove(index);
//             updateDisplay();
//         }
//     }

//     private void sortItems() {
//         dataStructure.sort();
//         updateDisplay();
//     }

//     private void updateDisplay() {
//         listModel.clear();
//         ArrayList<String> items = new ArrayList<>();
//         dataStructure.forEach(item -> items.add(item.toString()));
//         items.forEach(listModel::addElement);
//         outputArea.setText("Current size: " + dataStructure.size());
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             new DataStructureGUI().setVisible(true);
//         });
//     }
// }







import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

public class DataStructureGUI extends JFrame {
    private final UserFactory factory = new UserFactory();
    private UserType currentType;
    private SinglyLinkedList dataStructure;
    private DefaultListModel<String> listModel;
    
    // GUI Components
    private JComboBox<String> typeSelector;
    private JTextArea outputArea;
    private JTextField inputField;
    private JTextField indexField;
    private JTextField removeIndexField;
    private JButton addButton, getButton, removeByIndexButton, sortButton, saveButton, loadButton;

    public DataStructureGUI() {
        initComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setTitle("Data Structure GUI");
    }

    private void initComponents() {
        // Type selector
        typeSelector = new JComboBox<>(factory.getTypeNameList().toArray(new String[0]));
        typeSelector.addActionListener(this::typeSelected);
        
        // Input components
        inputField = new JTextField(20);
        indexField = new JTextField(5);
        removeIndexField = new JTextField(5);

        // Buttons
        addButton = new JButton("Add");
        addButton.addActionListener(this::addItem);
        
        getButton = new JButton("Get by Index");
        getButton.addActionListener(e -> getElementByIndex());
        
        removeByIndexButton = new JButton("Remove by Index");
        removeByIndexButton.addActionListener(e -> removeByIndex());
        
        sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> sortItems());
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveToFile());
        
        loadButton = new JButton("Load");
        loadButton.addActionListener(e -> loadFromFile());

        // Output area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        
        // List model
        listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
    }

    private void setupLayout() {
        JPanel controlPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        
        // First row
        controlPanel.add(new JLabel("Type:"));
        controlPanel.add(typeSelector);
        controlPanel.add(new JLabel("Input:"));
        controlPanel.add(inputField);
        controlPanel.add(addButton);
        
        // Second row
        controlPanel.add(new JLabel("Get Index:"));
        controlPanel.add(indexField);
        controlPanel.add(getButton);
        controlPanel.add(new JLabel("Remove Index:"));
        controlPanel.add(removeIndexField);
        controlPanel.add(removeByIndexButton);
        controlPanel.add(sortButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);

        JScrollPane listScroll = new JScrollPane(new JList<>(listModel));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        
        setLayout(new BorderLayout(10, 10));
        add(controlPanel, BorderLayout.NORTH);
        add(listScroll, BorderLayout.CENTER);
        add(outputScroll, BorderLayout.SOUTH);
    }

    
    // Event handlers
    private void typeSelected(ActionEvent e) {
        String typeName = (String) typeSelector.getSelectedItem();
        currentType = factory.getBuilderByName(typeName);
        dataStructure = new SinglyLinkedList(currentType);
        updateDisplay();
    }

    private void addItem(ActionEvent e) {
        try {
            Object value = currentType.parseValue(inputField.getText());
            dataStructure.add(value);
            inputField.setText("");
            updateDisplay();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error parsing input: " + ex.getMessage());
        }
    }

    private void getElementByIndex() {
        try {
            int index = Integer.parseInt(indexField.getText());
            Object value = dataStructure.get(index);
            outputArea.append("\nElement at index " + index + ": " + value);
            indexField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid index format");
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Index out of bounds");
        }
    }

    private void removeByIndex() {
        try {
            int index = Integer.parseInt(removeIndexField.getText());
            dataStructure.remove(index);
            removeIndexField.setText("");
            updateDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid index format");
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Index out of bounds");
        }
    }

    private void sortItems() {
        dataStructure.sort();
        updateDisplay();
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(fileChooser.getSelectedFile()))) {
                oos.writeObject(dataStructure.getItems());
                JOptionPane.showMessageDialog(this, "Saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fileChooser.getSelectedFile()))) {
                ArrayList<Object> items = (ArrayList<Object>) ois.readObject();
                dataStructure = new SinglyLinkedList(currentType);
                for (Object item : items) {
                    dataStructure.add(item);
                }
                updateDisplay();
                JOptionPane.showMessageDialog(this, "Loaded successfully!");
            } catch (IOException | ClassNotFoundException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        }
    }

    private void updateDisplay() {
        listModel.clear();
        dataStructure.forEach(item -> listModel.addElement(item.toString()));
        outputArea.setText("Current size: " + dataStructure.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStructureGUI().setVisible(true);
        });
    }
}