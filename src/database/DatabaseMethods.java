/*
 * Group members: Irina Ignatenok, Livio Reinoso
 * Instructions: For Project 2, implement all methods in this class, and test to confirm they behave as expected when the program is run.
 */

package database;

import java.sql.*;
import java.util.*;

import dataClasses.*;
import dataClasses.Driver;

public class DatabaseMethods {
  private Connection conn;

  public DatabaseMethods(Connection conn) {
    this.conn = conn;
  }

  /*
   * Accepts: Nothing
   * Behaviour: Retrieves information about all accounts
   * Returns: List of account objects
   */
  public ArrayList<Account> getAllAccounts() throws SQLException {
    ArrayList<Account> accounts = new ArrayList<Account>();

    // IMPLEMENTATION
    String accountQuery = "SELECT  a.FIRST_NAME, a.LAST_NAME, a.BIRTHDATE, a.PHONE_NUMBER, a.EMAIL, ad.STREET, ad.CITY, ad.PROVINCE, ad.POSTAL_CODE, d.ID AS 'Driver account', p.ID AS 'Passenger Account' FROM accounts a LEFT Join addresses ad ON a.ADDRESS_ID = ad.ID LEFT JOIN drivers d ON a.ID = d.ID LEFT JOIN passengers p ON a.ID = p.ID";
    try (Statement stmt = conn.createStatement();) {
      try (ResultSet accountResults = stmt.executeQuery(accountQuery);) {

        while (accountResults.next()) {
          String firstName = accountResults.getString("FIRST_NAME");
          String lastName = accountResults.getString("LAST_NAME");
          String birthDate = accountResults.getString("BIRTHDATE");
          String phoneNumber = accountResults.getString("PHONE_NUMBER");
          String email = accountResults.getString("EMAIL");
          String street = accountResults.getString("STREET");
          String city = accountResults.getString("CITY");
          String province = accountResults.getString("PROVINCE");
          String postalCode = accountResults.getString("POSTAL_CODE");
          boolean isDriver = accountResults.getInt("Driver account") == 0 ? false : true;
          boolean isPassenger = accountResults.getInt("Passenger Account") == 0 ? false : true;

          Account acc = new Account(firstName, lastName, street, city, province, postalCode, phoneNumber, email,
              birthDate, isPassenger, isDriver);
          accounts.add(acc);
        }
      }
    }
    return accounts;
  }

  /*
   * Accepts: Email address of driver
   * Behaviour: Calculates the average rating over all rides performed by the
   * driver specified by the email address
   * Returns: The average rating value
   */
  public double getAverageRatingForDriver(String driverEmail) throws SQLException {
    double averageRating = 0.0;

    // IMPLEMENTATION
    String query = "SELECT AVG(rides.RATING_FROM_PASSENGER) FROM accounts INNER JOIN rides ON rides.DRIVER_ID = accounts.ID WHERE accounts.EMAIL = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, driverEmail);
      try (ResultSet result = stmt.executeQuery()) {
        while (result.next()) {
          averageRating = result.getDouble(1);
        }
      }
    }
    return averageRating;
  }

  /*
   * Accepts: Account details, and passenger and driver specific details.
   * Passenger or driver details could be
   * null if account is only intended for one type of use.
   * Behaviour:
   * - Insert new account using information provided in Account object
   * - For non-null passenger/driver details, insert the associated data into the
   * relevant tables
   * Returns: Nothing
   */
  public void createAccount(Account account, Passenger passenger, Driver driver) throws SQLException {
    // TODO: Implement
    // Hint: Use the available insertAccount, insertPassenger, and insertDriver
    // methods
  }

  /*
   * Accepts: Account details (which includes address information)
   * Behaviour: Inserts the new account, as well as the account's address if it
   * doesn't already exist. The new/existing address should
   * be linked to the account
   * Returns: Id of the new account
   */
  public int insertAccount(Account account) throws SQLException {
    int accountId = -1;

    // TODO: Implement
    // Hint: Use the insertAddressIfNotExists method

    return accountId;
  }

  /*
   * Accepts: Passenger details (should not be null), and account id for the
   * passenger
   * Behaviour: Inserts the new passenger record, correctly linked to the account
   * id
   * Returns: Id of the new passenger
   */
  public int insertPassenger(Passenger passenger, int accountId) throws SQLException {
    // TODO: Implement
    String query = "INSERT INTO passenger VALUES (?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setInt(1, accountId);
      stmt.setString(2, passenger.getCreditCardNumber());
      stmt.executeUpdate();
    }
    return accountId;
  }

  /*
   * Accepts: Driver details (should not be null), and account id for the driver
   * Behaviour: Inserts the new driver and driver's license record, correctly
   * linked to the account id
   * Returns: Id of the new driver
   */
  public int insertDriver(Driver driver, int accountId) throws SQLException {
    // TODO: TESTING
    // Hint: Use the insertLicense method
    int licenseId = insertLicense(driver.getLicenseNumber(), driver.getLicenseExpiryDate());
    String query = "INSERT INTO drivers values (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setInt(1, accountId);
      stmt.setInt(2, licenseId);
      stmt.executeUpdate();
      // Since we're not changing the accountId value when inserting it into the
      // drivers table, we don't need to retrieve the inserted keys
    }
    return accountId;
  }

  /*
   * Accepts: Driver's license number and license expiry
   * Behaviour: Inserts the new driver's license record
   * Returns: Id of the new driver's license
   */
  public int insertLicense(String licenseNumber, String licenseExpiry) throws SQLException {
    int licenseId = -1;
    // TODO: Implement
    String insertLicense = "INSERT INTO licenses (NUMBER,EXPIRY_DATE) VALUES (?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(insertLicense, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, licenseNumber);
      stmt.setString(2, licenseExpiry);
      stmt.executeUpdate();
      try (ResultSet keys = stmt.getGeneratedKeys()) {
        keys.next();
        licenseId = keys.getInt(1);
      }
      ;
    }
    return licenseId;
  }

  /*
   * Accepts: Address details
   * Behaviour:
   * - Checks if an address with these properties already exists.
   * - If it does, gets the id of the existing address.
   * - If it does not exist, creates the address in the database, and gets the id
   * of the new address
   * Returns: Id of the address
   */
  public int insertAddressIfNotExists(Address address) throws SQLException {
    int addressId = -1;

    // TODO: Implement

    return addressId;
  }

  /*
   * Accepts: Name of new favourite destination, email address of the passenger,
   * and the id of the address being favourited
   * Behaviour: Finds the id of the passenger with the email address, then inserts
   * the new favourite destination record
   * Returns: Nothing
   */
  public void insertFavouriteDestination(String favouriteName, String passengerEmail, int addressId)
      throws SQLException {
    // TODO: TEST IMPLEMENTATION

    // 1. Get passenger ID from email (Q: do we need to validate if it is a
    // passenger?)
    int passengerId = 0;
    String query = "SELECT accounts.ID FROM accounts WHERE accounts.EMAIL = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, passengerEmail);
      try (ResultSet result = stmt.executeQuery()) {
        while (result.next()) {
          passengerId = result.getInt(1);
        }
      }
    }
    // 2. Use ID and args to insert into the favourite_locations table
    query = "INSERT INTO favourite_locations ('PASSENGER_ID', 'LOCATION_ID', 'NAME') VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setInt(1, passengerId);
      stmt.setInt(2, addressId);
      stmt.setString(3, favouriteName);
      stmt.executeUpdate(); // Q: do we need to handle the result of the update?
    }
  }

  /*
   * Accepts: Email address
   * Behaviour: Determines if a driver exists with the provided email address
   * Returns: True if exists, false if not
   */
  public boolean checkDriverExists(String email) throws SQLException {
    // IMPLEMENTATION
    String query = "SELECT * FROM drivers INNER JOIN accounts ON drivers.ID = accounts.ID WHERE accounts.EMAIL = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, email);
      try (ResultSet result = stmt.executeQuery()) {
        if (result.next()) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  /*
   * Accepts: Email address
   * Behaviour: Determines if a passenger exists with the provided email address
   * Returns: True if exists, false if not
   */
  public boolean checkPassengerExists(String email) throws SQLException {
    // IMPLEMENTATION
    String query = "SELECT * FROM passengers INNER JOIN accounts ON passengers.ID = accounts.ID WHERE accounts.EMAIL = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, email);
      try (ResultSet result = stmt.executeQuery()) {
        if (result.next()) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  /*
   * Accepts: Email address of passenger making request, id of dropoff address,
   * requested date/time of ride, and number of passengers
   * Behaviour: Inserts a new ride request, using the provided properties
   * Returns: Nothing
   */
  public void insertRideRequest(String passengerEmail, int dropoffLocationId, String date, String time,
      int numberOfPassengers) throws SQLException {
    int passengerId = this.getPassengerIdFromEmail(passengerEmail);
    int pickupAddressId = this.getAccountAddressIdFromEmail(passengerEmail);

    // TODO: Implement
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets id of passenger with specified email (assumes passenger
   * exists)
   * Returns: Id
   */
  public int getPassengerIdFromEmail(String passengerEmail) throws SQLException {
    int passengerId = -1;
    // TODO: Implement

    return passengerId;
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets id of driver with specified email (assumes driver exists)
   * Returns: Id
   */
  public int getDriverIdFromEmail(String driverEmail) throws SQLException {
    int driverId = -1;
    // TODO: Implement

    return driverId;
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets the id of the address tied to the account with the provided
   * email address
   * Returns: Address id
   */
  public int getAccountAddressIdFromEmail(String email) throws SQLException {
    int addressId = -1;
    // TODO: Implement

    return addressId;
  }

  /*
   * Accepts: Email address of passenger
   * Behaviour: Gets a list of all the specified passenger's favourite
   * destinations
   * Returns: List of favourite destinations
   */
  public ArrayList<FavouriteDestination> getFavouriteDestinationsForPassenger(String passengerEmail)
      throws SQLException {
    ArrayList<FavouriteDestination> favouriteDestinations = new ArrayList<FavouriteDestination>();

    // TODO: Implement

    return favouriteDestinations;
  }

  /*
   * Accepts: Nothing
   * Behaviour: Gets a list of all uncompleted ride requests (i.e. requests
   * without an associated ride record)
   * Returns: List of all uncompleted rides
   */
  public ArrayList<RideRequest> getUncompletedRideRequests() throws SQLException {
    ArrayList<RideRequest> uncompletedRideRequests = new ArrayList<RideRequest>();

    // TODO: Implement

    return uncompletedRideRequests;
  }

  /*
   * Accepts: Ride details
   * Behaviour: Inserts a new ride record
   * Returns: Nothing
   */
  public void insertRide(Ride ride) throws SQLException {
    // TODO: Implement
    // Hint: Use getDriverIdFromEmail
  }

}
