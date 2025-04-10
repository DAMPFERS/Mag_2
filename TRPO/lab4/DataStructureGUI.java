import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    public DataStructureGUI() {
        initComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setTitle("Data Structure GUI");
    }

    private void initComponents() {
        // Type selector
        typeSelector = new JComboBox<>(factory.getTypeNameList().toArray(new String[0]));
        typeSelector.addActionListener(this::typeSelected);
        
        // Input components
        inputField = new JTextField(20);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(this::addItem);
        
        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> sortItems());
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeItem());

        // Output area
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        
        // List model
        listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
    }

    private void setupLayout() {
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Select Type:"));
        controlPanel.add(typeSelector);
        controlPanel.add(new JLabel("Input:"));
        controlPanel.add(inputField);
        controlPanel.add(new JButton("Add") {{
            addActionListener(DataStructureGUI.this::addItem);
        }});
        controlPanel.add(new JButton("Sort") {{
            addActionListener(e -> sortItems());
        }});
        controlPanel.add(new JButton("Remove") {{
            addActionListener(e -> removeItem());
        }});

        JScrollPane scrollPane = new JScrollPane(new JList<>(listModel));
        
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);
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

    private void removeItem() {
        int index = listModel.getSize() > 0 ? 0 : -1; // Simplified for example
        if (index >= 0) {
            dataStructure.remove(index);
            updateDisplay();
        }
    }

    private void sortItems() {
        dataStructure.sort();
        updateDisplay();
    }

    private void updateDisplay() {
        listModel.clear();
        ArrayList<String> items = new ArrayList<>();
        dataStructure.forEach(item -> items.add(item.toString()));
        items.forEach(listModel::addElement);
        outputArea.setText("Current size: " + dataStructure.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStructureGUI().setVisible(true);
        });
    }
}