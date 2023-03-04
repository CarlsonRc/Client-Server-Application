import com.mysql.cj.jdbc.MysqlDataSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;


public class FillTable extends JFrame
{
    private static Connection key;
    public static JComboBox<String> propFilesCmbo = new JComboBox<>();
    public static JPasswordField passFld = new JPasswordField();
    public static JTextField userFld = new JTextField();
    public static JTextField URL = new JTextField("Waiting For Connection...");
    public JTextArea queryArea = new JTextArea( 4, 100);
    private JTable resultsTable;


    public FillTable()
    {
        createComponents();
        setSize(800,600);
        setTitle("SQL Client App - Project 3");
    }

    public static void main(String[] args)
    {
        JFrame frame = new FillTable();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(false);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
    }



    class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (key != null) {
                try {
                    String driver = "com.mysql.cj.jdbc.Driver";
                    Class.forName(driver);
                    Connection connection = key;
                    String sql = queryArea.getText();
                    Statement statement = connection.prepareStatement(sql);

                    if (statement.execute(sql)){
                        try {
                            ResultSet rs = statement.executeQuery(sql);//Execute query
                            operationsLog(true);
                            resultsTable.setModel(updateModel(rs));
                        }
                        catch (SQLException sqlException) {
                            JOptionPane.showMessageDialog( null,
                                    sqlException.getMessage(), "Database error",
                                    JOptionPane.ERROR_MESSAGE );
                            System.out.println("query fail");
                        }
                    }
                    else {
                        operationsLog(false);
                    }


                } catch (SQLException sqlException) {
                    JOptionPane.showMessageDialog( null,
                            sqlException.getMessage(), "Database error",
                            JOptionPane.ERROR_MESSAGE );
                    System.out.println("no permissions / syntax fail");
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Not Connected to Database!", "Submit Query error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public DefaultTableModel updateModel(ResultSet rs) throws SQLException
    {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<>();

        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    private void createComponents()
    {
        ActionListener buttonListener = new ButtonListener();

        queryArea.setWrapStyleWord( true );
        queryArea.setLineWrap( true );

        JScrollPane scrollPane = new JScrollPane( queryArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

        // set up JButton for submitting queries
        JButton submitButton = new JButton( "Submit Query" );
        submitButton.addActionListener(buttonListener);
        submitButton.setBackground(Color.BLUE);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorderPainted(false);
        submitButton.setOpaque(true);
        JButton clearButton = new JButton("Clear Query");
        clearButton.setBackground(Color.RED);
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorderPainted(false);

        //Panel for username and password
        JPanel userPass = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        JLabel propFile = new JLabel("Properties File");
        JLabel userName = new JLabel("Username");
        JLabel password = new JLabel("Password");
        propFilesCmbo.addItem("root.properties");
        propFilesCmbo.addItem("client.properties");
        userFld.setColumns(20);
        passFld.setColumns(20);

        gc.weightx = 0.5;
        gc.weighty = 0.5;

        gc.gridx = 0;
        gc.gridy = 0;
        userPass.add(propFile, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        userPass.add(userName, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        userPass.add(password, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        userPass.add(propFilesCmbo, gc);

        gc.gridx = 1;
        gc.gridy = 1;
        userPass.add(userFld, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        userPass.add(passFld, gc);


        //Panel for Queries
        JPanel query = new JPanel(new BorderLayout());
        query.add( scrollPane, BorderLayout.PAGE_START);
        query.add( submitButton, BorderLayout.LINE_START );
        query.add(clearButton, BorderLayout.LINE_END);


        //Panel to organize top half
        JPanel queryAndUserPass = new JPanel(new GridLayout(1,2));
        queryAndUserPass.add(userPass, LEFT_ALIGNMENT);
        queryAndUserPass.add(query, RIGHT_ALIGNMENT);

        //Panel for connection
        JPanel connection = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton connectButton = new JButton("Connect to Database");
        URL.setColumns(50);
        connection.add(connectButton);
        connection.add(URL);

        //Panel for results
        JPanel results = new JPanel(new BorderLayout());
        resultsTable = new JTable();
        JButton clearTable = new JButton("Clear Results");
        results.add(new JScrollPane(resultsTable), BorderLayout.NORTH);
        results.add(clearTable, BorderLayout.LINE_START);


        // place GUI components on content pane
        add( queryAndUserPass, BorderLayout.NORTH );// Query pane
        add(connection, BorderLayout.CENTER);//Connection pane
        add(results, BorderLayout.SOUTH);//Results pane

        //Clear query area
        clearButton.addActionListener(e -> queryArea.setText(""));

        //Run connect method to access database
        connectButton.addActionListener(e -> {
            try {
                connectToDataBase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //Replace results with blank table
        clearTable.addActionListener(e -> resultsTable.setModel(new DefaultTableModel()));
    }

    public void operationsLog(boolean isQuery) throws SQLException {
        Properties properties = new Properties();
        FileInputStream filein;
        MysqlDataSource dataSource;
        Connection con = null;
        Statement statement = null;
        String query = "update operationscount set num_queries = num_queries + 1;";
        String update = "update operationscount set num_updates = num_updates + 1;";

        System.out.println("isQuery: " + isQuery);
        //read properties file
        try {
            filein = new FileInputStream("operationslog.properties");
            properties.load(filein);
            dataSource = new MysqlDataSource();
            dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
            con = dataSource.getConnection();
            statement = con.createStatement();
            if (isQuery) {
                //+1 to query count
                statement.executeUpdate(query);
                System.out.println("opLog + 1 added to Queries");
            }
            else{
                //+1 to update count
                statement.executeUpdate(update);
                System.out.println("opLog + 1 added to Updates");

            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        assert statement != null;
        statement.close();
        con.close();
    }

    public static void connectToDataBase() throws SQLException {

        Properties properties = new Properties();
        FileInputStream filein;
        MysqlDataSource dataSource;
        //read properties file
        try {
            filein = new FileInputStream((String) Objects.requireNonNull(propFilesCmbo.getSelectedItem()));
            properties.load(filein);
            dataSource = new MysqlDataSource();
            dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));

            if (Objects.equals(dataSource.getUser(), userFld.getText()) && Objects.equals(dataSource.getPassword(), String.valueOf(passFld.getPassword()))) {
                System.out.println("MATCH");
                System.out.println("logged in as: " + dataSource.getUser());
                URL.setText("Connected to " + dataSource.getUrl());
                key = dataSource.getConnection();
            }
            else if (Objects.equals(userFld.getText(), "") || Objects.equals(String.valueOf(passFld.getPassword()), "")) {
                JOptionPane.showMessageDialog(null, "Please enter Username and Password", "Connect error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "Username or Password is incorrect!", "Connect error", JOptionPane.ERROR_MESSAGE);
                System.out.println("MATCH FAILED");
            }

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }

    }
}