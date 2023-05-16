import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Soccer {
    void menu(Connection con) throws SQLException {
        System.out.println("Soccer Main Menu");
        System.out.println("        1. List information of matches of a country");
        System.out.println("        2. Insert initial player information for a match");
        System.out.println("        3. View or Insert goal");
        System.out.println("        4. Exit application");
        Scanner scanner = new Scanner(System.in); // Create a Scanner object
        System.out.println("Please Enter Your Option:");
        // scanner.nextLine();
        int option = scanner.nextInt();
        if (option == 4) {
            con.close();
        }
        if (option == 1) {
            info(con);
        }
        if (option == 2) {
            insert(con);
        }
        if (option == 3) {
            insertGoal(con);
        }
        scanner.nextLine();

    }

    void info(Connection con) throws SQLException {
        System.out.println("Selected option: List information of matches of a country");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter A Country: ");
        String country = scanner.nextLine();

        con.createStatement();

        String sql = "SELECT matchId, fCountry, sCountry, mDate, mRound, fGoal, sGoal " +
                "FROM Match " +
                "WHERE fCountry = ? OR sCountry = ?";
        PreparedStatement stmt2 = con.prepareStatement(sql);
        stmt2.setString(1, country);
        stmt2.setString(2, country);
        java.sql.ResultSet result = stmt2.executeQuery();

        while (result.next()) {
            String fCountry = result.getString("fCountry");
            String sCountry = result.getString("sCountry");
            String mDate = result.getString("mDate");
            String round = result.getString("mRound");
            Integer fGoal = result.getInt("fGoal");
            Integer sGoal = result.getInt("sGoal");
            int matchId = result.getInt("matchId");

            String sql2 = "SELECT COUNT(*) AS numTickets " +
                    "FROM Ticket " +
                    "WHERE matchId = ?";
            PreparedStatement stmt3 = con.prepareStatement(sql2);
            stmt3.setInt(1, matchId);

            java.sql.ResultSet result2 = stmt3.executeQuery();
            result2.next();
            int numTickets = result2.getInt("numTickets");
            // if date is in the future, goals = null
            LocalDate date = LocalDate.parse(mDate);
            // get the current date
            LocalDate today = LocalDate.now();
            if (date.isAfter(today) || date.isEqual(today)) {
                System.out.println(fCountry + " " + sCountry + " " + mDate + " " + round + " " + null + " " + null + " "
                        + numTickets);
            } else {
                System.out.println(fCountry + " " + sCountry + " " + mDate + " " + round + " " + fGoal + " " + sGoal
                        + " " + numTickets);

            }
        }
        System.out.println("Enter [A] to find matches of another country, [P] to go to the previous menu:");
        String answer = scanner.nextLine();
        if (Objects.equals(answer, "A")) {
            info(con);
        } else {
            menu(con);
        }

    }

    void insert(Connection con) throws SQLException {
        System.out.println("Selected option: Insert initial player information for a match");
        // get matches of the next 3 days
        LocalDate currentDate = LocalDate.now();
        String sql = "SELECT matchId, fCountry, sCountry, mDate, mRound " +
                "FROM Match " +
                "WHERE mDate BETWEEN ? AND ADD_DAYS(?, 3)";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setDate(1, java.sql.Date.valueOf(currentDate));
        statement.setDate(2, java.sql.Date.valueOf(currentDate));
        ResultSet result = statement.executeQuery();
        System.out.println("Matches:");

        while (result.next()) {
            int matchId = result.getInt("matchId");
            String fCountry = result.getString("fCountry");
            String sCountry = result.getString("sCountry");
            LocalDate mDate = result.getDate("mDate").toLocalDate();
            String mRound = result.getString("mRound");
            if (fCountry != null && sCountry != null) {
                System.out.println(matchId + " " + fCountry + " " + sCountry + " " + mDate + " " + mRound);
            }

        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please Enter A Match Identifier or enter [P] to cancel and go the menu.");

        String request = scanner.nextLine();
        if (Objects.equals(request, "P")) {

            menu(con);
        }

        int matchId = Integer.parseInt(request);

        System.out.println("Please Enter A Country: ");
        String countryOfInterest = scanner.nextLine();

        System.out.println(
                "The following players from " + countryOfInterest + " are already entered for match " + matchId + ": ");

        // find players that are playing
        // get tId, positionPlayed, timeIn, timeOut, YellowCards, receiveRed from
        // PlaysMatch
        String sql1 = "SELECT tm.tName, p.tId, p.positionPlayed, p.timeIn, p.timeOut, p.YellowCards, p.receiveRed " +
                "FROM PlaysMatch p " +
                "JOIN TeamMember tm ON p.tId = tm.tId " +
                "WHERE p.matchId = ? AND tm.country = ?";
        PreparedStatement statement1 = con.prepareStatement(sql1);
        statement1.setInt(1, matchId);
        statement1.setString(2, countryOfInterest);
        ResultSet resultSet1 = statement1.executeQuery();

        int playersSelected = 0;
        while (resultSet1.next()) {
            playersSelected++;

            String tName = resultSet1.getString("tName");
            int tId = resultSet1.getInt("tId");
            String positionPlayed = resultSet1.getString("positionPlayed");
            int timeIn = resultSet1.getInt("timeIn");

            String timeOut = String.valueOf(resultSet1.getInt("timeOut"));
            timeOut = resultSet1.wasNull() ? "NULL" : timeOut;
            int YellowCards = resultSet1.getInt("YellowCards");
            int receiveRed = resultSet1.getInt("receiveRed");

            System.out.println(tName + " " + tId + " " + positionPlayed + " from minute " + timeIn + " to minute "
                    + timeOut + " yellow: " + YellowCards + " red: " + receiveRed);

        }

        // find players to choose
        System.out.println("Possible players from " + countryOfInterest + " not yet selected: ");

        String sql3 = "(SELECT tm.tName, tm.tId, p.position " +
                "FROM TeamMember tm " +
                "JOIN Player p ON tm.tId = p.tId " +
                "FULL OUTER JOIN PlaysMatch pm ON tm.tId = pm.tId " +
                "WHERE tm.country = ? AND (pm.matchId <> ? OR pm.matchId IS NULL)) EXCEPT (SELECT tm.tName, tm.tId, p.position " + 
                "FROM TeamMember tm JOIN Player p ON tm.tId = p.tId JOIN PlaysMatch pm ON tm.tId = pm.tId WHERE tm.country = ? "+
                "AND pm.matchId = ?)"
                ;

        PreparedStatement statement3 = con.prepareStatement(sql3);
        statement3.setString(1, countryOfInterest);
        statement3.setInt(2, matchId);
        statement3.setString(3, countryOfInterest);
        statement3.setInt(4, matchId);
        ResultSet resultSet3 = statement3.executeQuery();

        int i = 1;
        // array to store players available
        ArrayList<Integer> available = new ArrayList<Integer>();

        while (resultSet3.next()) {
            String tName = resultSet3.getString("tName");
            int tId = resultSet3.getInt("tId");
            String position = resultSet3.getString("position");

            available.add(tId);
            System.out.println(i + ". " + tName + " " + tId + " " + position);
            i += 1;
        }

        if (!(playersSelected < 11)) {
            System.out.println("Only 11 players can play in a match, can't insert more players.");

            menu(con);
            return;
        }

        System.out.println("Enter the number of the player you want to insert or [P] to go to the previous menu.");
        String toAdd = scanner.nextLine();
        if (Objects.equals(toAdd, "P")) {

            menu(con);
        } else {
            int playerId = Integer.parseInt(toAdd);

            if (available.contains(playerId)) {
                System.out.println("Enter the position for this player to play in this match: ");
                String position = scanner.nextLine();
                String sql_insert_player = "INSERT INTO PlaysMatch VALUES(?, ?, 0, NULL, ?, 0, 0)";

                try {
                    PreparedStatement insert_player = con.prepareStatement(sql_insert_player);
                    insert_player.setInt(1, matchId);
                    insert_player.setInt(2, playerId);
                    insert_player.setString(3, position);
                    insert_player.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e);
                    System.out.println("Could not add player");

                    menu(con);
                }

                System.out.println(
                        "The following players from " + countryOfInterest + " are already entered for match " + matchId
                                + ": ");

                ResultSet afterInsertRS = statement1.executeQuery();
                while (afterInsertRS.next()) {

                    String tName = afterInsertRS.getString("tName");
                    int tId = afterInsertRS.getInt("tId");
                    String positionPlayed = afterInsertRS.getString("positionPlayed");
                    int timeIn = afterInsertRS.getInt("timeIn");

                    String timeOut = String.valueOf(afterInsertRS.getInt("timeOut"));
                    timeOut = afterInsertRS.wasNull() ? "NULL" : timeOut;

                    int YellowCards = afterInsertRS.getInt("YellowCards");
                    int receiveRed = afterInsertRS.getInt("receiveRed");

                    System.out
                            .println(tName + " " + tId + " " + positionPlayed + " from minute " + timeIn + " to minute "
                                    + timeOut + " yellow: " + YellowCards + " red: " + receiveRed);

                }
                menu(con);

            }

            else {
                System.out.println("Player ID " + playerId + " could not be inserted. ");

                menu(con);
            }
        }

    }
    void viewGoal(Connection con, int id) throws SQLException {
        String sql2 = "SELECT Goal.matchId, Goal.occurence, TeamMember.tName, Goal.minute, Goal.duringPen " +
                "FROM Goal " +
                "JOIN TeamMember " +
                "ON Goal.tId = TeamMember.tId " +
                "WHERE Goal.matchId = ?";
        PreparedStatement statement2 = con.prepareStatement(sql2);
        statement2.setInt(1, id);
        ResultSet resultSet2 = statement2.executeQuery();

        while (resultSet2.next()) {
            int matchId = resultSet2.getInt("matchId");
            int occurrence = resultSet2.getInt("occurence");
            String tName = resultSet2.getString("tName");
            int minute = resultSet2.getInt("minute");
            boolean duringPen = resultSet2.getBoolean("duringPen");
            System.out.println(matchId + " " + occurrence + " " + tName + " " + minute + " " + duringPen);
        }
    }

    //view or insert goal
    void insertGoal(Connection con) throws SQLException {
        System.out.println("Selected option: View or Insert goal");
        System.out.println("Here is the list of all matches: ");

        String sql = "SELECT matchId, fCountry, sCountry, mDate, mRound " +
                "FROM Match";
        PreparedStatement statement = con.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        while (result.next()) {
            int matchId = result.getInt("matchId");
            String fCountry = result.getString("fCountry");
            String sCountry = result.getString("sCountry");
            String mDate = result.getString("mDate");
            String mRound = result.getString("mRound");
            System.out.println(matchId + " " + fCountry + " " + sCountry + " " + mDate + " " + mRound);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select the match identification");
        int id = scanner.nextInt();
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("if you would like to view the match's goal, type [V] or if you would like to insert a goal, type [I]");
        String option = scanner2.nextLine();

        if (Objects.equals(option, "V")) {
            viewGoal(con, id);
        }
        else{
            System.out.println("Please indicate the occurrence");
            int oc = scanner.nextInt();
            System.out.println("Please indicate the player's id");
            int pId = scanner.nextInt();
            System.out.println("Please indicate the minute");
            int min = scanner.nextInt();

	    Scanner scanner4 = new Scanner(System.in);
            System.out.println("Please indicate if the goal was a penalty goal [y/n]");
            String pen = scanner4.nextLine();

            
	    Boolean penalty = false;
            if (Objects.equals(pen, "y")){
                penalty = true;
            }
            else{
                penalty = false;
            }
            String insert = "INSERT INTO Goal VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement3 = con.prepareStatement(insert);
            statement3.setInt(1, id);
            statement3.setInt(2, oc);
            statement3.setInt(3, pId);
            statement3.setInt(4, min);
            statement3.setBoolean(5, penalty);
	    statement3.executeUpdate();

            System.out.println("Finished inserting. Here is the goals for match identification: " + id);
            viewGoal(con, id);
        }

        menu(con);
    }


    public static void main(String[] args) throws SQLException {
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }
        String url = "jdbc:db2://winter2023-comp421.cs.mcgill.ca:50000/cs421";
        // REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = "cs421g133";
        String your_password = "YaninHelena133!";

        try {
            Connection con = DriverManager.getConnection(url, your_userid, your_password);
            Soccer c = new Soccer();
            c.menu(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Loop through the result set and print the data

        // Unique table names. Either the user supplies a unique identifier as a command
        // line argument, or the program makes one up.
        // String tableName = "";
        // int sqlCode = 0; // Variable to hold SQLCODE
        // String sqlState = "00000"; // Variable to hold SQLSTATE

        // if (args.length > 0)
        // tableName += args[0];
        // else
        // tableName += "exampletbl";

        // Register the driver. You must register the driver before you can use it.

        // AS AN ALTERNATIVE, you can just set your password in the shell environment in
        // the Unix (as shown below) and read it from there.
        // $ export SOCSPASSWD=yoursocspasswd
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");

        }
    }
}
