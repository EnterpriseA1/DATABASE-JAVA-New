package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.bson.Document;

public class Database extends JFrame {
    private JTextField idField, moneyField, interestRateField, firstNameField, lastNameField, ageField;
    private JComboBox<String> dayOpenCombo, monthOpenCombo, yearOpenCombo;
    private JComboBox<String> dayBirthCombo, monthBirthCombo, yearBirthCombo;
    private JButton saveButton, showButton;
    
    // MongoDB fields
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public Database() {
        setTitle("Show Detail of Account");
        setSize(390, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Initialize MongoDB connection
        try {
            database = DatabaseConnection.getDatabase();
            collection = database.getCollection("accounts");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to MongoDB: " + e.getMessage());
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("ACCOUNT MONEY", SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Enter Data Account Money"
        );
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        // ID and Money fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(10);
        formPanel.add(idField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("MONEY:"), gbc);
        gbc.gridx = 3;
        moneyField = new JTextField(10);
        formPanel.add(moneyField, gbc);
        gbc.gridx = 4;
        formPanel.add(new JLabel("BATH:"), gbc);

        // Interest Rate field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("ANNUAL INTEREST RATE:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 3;
        interestRateField = new JTextField(20);
        formPanel.add(interestRateField, gbc);

        // Day Open Account
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("DAY OPEN ACCOUNT:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        dayOpenCombo = new JComboBox<>(new String[]{"day"});
        formPanel.add(dayOpenCombo, gbc);
        gbc.gridx = 3;
        monthOpenCombo = new JComboBox<>(new String[]{"month"});
        formPanel.add(monthOpenCombo, gbc);

        // Year and First Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        yearOpenCombo = new JComboBox<>(new String[]{"year"});
        formPanel.add(yearOpenCombo, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("FIRST NAME:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 3;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("LAST NAME:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 3;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);

        // Birth Day
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("BIRTH DAY:"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        dayBirthCombo = new JComboBox<>(new String[]{"day"});
        formPanel.add(dayBirthCombo, gbc);
        gbc.gridx = 3;
        monthBirthCombo = new JComboBox<>(new String[]{"month"});
        formPanel.add(monthBirthCombo, gbc);
        gbc.gridx = 4;
        yearBirthCombo = new JComboBox<>(new String[]{"year"});
        formPanel.add(yearBirthCombo, gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("AGE:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(5);
        formPanel.add(ageField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("YEAR"), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("SAVE");
        showButton = new JButton("SHOW");
        buttonPanel.add(saveButton);
        buttonPanel.add(showButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        initializeComboBoxes();

        saveButton.addActionListener(e -> saveData());
        showButton.addActionListener(e -> showData());

        // Add window listener to close MongoDB connection
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });
    }

    private void initializeComboBoxes() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.format("%02d", i);
        }
        dayOpenCombo.setModel(new DefaultComboBoxModel<>(days));
        dayBirthCombo.setModel(new DefaultComboBoxModel<>(days));

        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        monthOpenCombo.setModel(new DefaultComboBoxModel<>(months));
        monthBirthCombo.setModel(new DefaultComboBoxModel<>(months));

        String[] years = new String[100];
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        yearOpenCombo.setModel(new DefaultComboBoxModel<>(years));
        yearBirthCombo.setModel(new DefaultComboBoxModel<>(years));
    }

    private void saveData() {
        try {
            Document document = new Document()
                .append("_id", idField.getText())
                .append("money", moneyField.getText())
                .append("interestRate", Double.parseDouble(interestRateField.getText()))
                .append("openDate", String.format("%s-%s-%s",
                    yearOpenCombo.getSelectedItem(),
                    monthOpenCombo.getSelectedItem(),
                    dayOpenCombo.getSelectedItem()))
                .append("firstName", firstNameField.getText())
                .append("lastName", lastNameField.getText())
                .append("birthDate", String.format("%s-%s-%s",
                    yearBirthCombo.getSelectedItem(),
                    monthBirthCombo.getSelectedItem(),
                    dayBirthCombo.getSelectedItem()))
                .append("age", Integer.parseInt(ageField.getText()));

            collection.insertOne(document);
            JOptionPane.showMessageDialog(this, "Data saved successfully to MongoDB!");
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    private void showData() {
        try {
            FindIterable<Document> documents = collection.find();
            List<String> data = new ArrayList<>();

            for (Document doc : documents) {
                StringBuilder record = new StringBuilder();
                record.append("ID: ").append(doc.getString("_id")).append("\n");
                record.append("Name: ").append(doc.getString("firstName"))
                      .append(" ").append(doc.getString("lastName")).append("\n");
                record.append("Money: ").append(doc.getString("money")).append(" BATH\n");
                record.append("Interest Rate: ").append(doc.getDouble("interestRate")).append("%\n");
                record.append("Open Account Date: ").append(doc.getString("openDate")).append("\n");
                record.append("Birth Date: ").append(doc.getString("birthDate")).append("\n");
                record.append("Age: ").append(doc.getInteger("age")).append(" years");

                data.add(record.toString());
            }

            if (!data.isEmpty()) {
                for (int i = 0; i < data.size(); i += 4) {
                    StringBuilder message = new StringBuilder();
                    for (int j = i; j < Math.min(i + 4, data.size()); j++) {
                        message.append(data.get(j)).append("\n\n");
                    }
                    JOptionPane.showMessageDialog(this, message.toString(), "Account Data", JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No data found in MongoDB.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading data: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        moneyField.setText("");
        interestRateField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        ageField.setText("");
        
        dayOpenCombo.setSelectedIndex(0);
        monthOpenCombo.setSelectedIndex(0);
        yearOpenCombo.setSelectedIndex(0);
        dayBirthCombo.setSelectedIndex(0);
        monthBirthCombo.setSelectedIndex(0);
        yearBirthCombo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Database().setVisible(true);
        });
    }
}