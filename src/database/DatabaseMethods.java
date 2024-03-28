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
    // TODO: TEST IMPLEMENTATION
    // Hint: Use the available insertAccount, insertPassenger, and insertDriver
    // methods

    // Inserting a new account into the accounts table. I retrieve its account ID
    // after that
    int accountId = insertAccount(account);

    // If the account is for a passenger, I insert it into the passengers table as
    // well, providing the credit card info through the instance of the passenger
    // class and the accountId that I've received from the insertAccount method
    if (account.isPassenger()) {
      insertPassenger(passenger, accountId);
    }
    // Same here for a new driver's account
    if (account.isDriver()) {
      insertDriver(driver, accountId);
    }
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

    // TODO: TEST IMPLEMENTATION
    // Hint: Use the insertAddressIfNotExists method
    String query = "INSERT INTO accounts ('FIRST_NAME','LAST_NAME', 'BIRTHDAY', 'ADDRESS_ID','PHONE_NUMBER', 'EMAIL') values (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, account.getFirstName());
      stmt.setString(2, account.getLastName());
      stmt.setString(3, account.getBirthdate());
      stmt.setInt(4, insertAddressIfNotExists(account.getAddress()));
      stmt.setString(5, account.getPhoneNumber());
      stmt.setString(6, account.getEmail());
      stmt.executeUpdate();
      try (ResultSet keys = stmt.getGeneratedKeys()) {
        while (keys.next()) {
          accountId = keys.getInt(1);
        }
      }
    }
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
    // TODO: TEST IMPLEMENTATION
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
    // TODO: TEST IMPLEMENTATION
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
    // TODO: TEST IMPLEMENTATION
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

    // TODO: TEST IMPLEMENTATION

    // 1. Get addresses and check if the Address received as an argument exists in
    // the list
    // NOTE: the received address will not have an ID assigned

    // Q: I'm going through the whole list so I can make sure that I don't miss and
    // address due to casing. Is this verification required or can we just SELECT
    // a.ID FROM addresses a WHERE 1 && 2 && 3 && 4... ?
    String query = "SELECT * FROM addresses";
    try (Statement stmt = conn.createStatement()) {
      try (ResultSet result = stmt.executeQuery(query)) {
        while (result.next()) {
          Address receivedAddress = new Address(
              result.getInt("ID"),
              result.getString("STREET"),
              result.getString("CITY"),
              result.getString("PROVINCE"),
              result.getString("POSTAL_CODE"));
          if ((receivedAddress.getStreet().toLowerCase().equals(address.getStreet().toLowerCase()))
              && (receivedAddress.getCity().toLowerCase().equals(address.getCity().toLowerCase()))
              && (receivedAddress.getProvince().toLowerCase().equals(address.getProvince().toLowerCase()))
              && (receivedAddress.getPostalCode().toLowerCase().equals(address.getPostalCode().toLowerCase()))) {
            // 1.b If if does, return the id
            return receivedAddress.getId();
          }
        }
      }
    }
    // If I reach this line of code means that the address was not previously
    // entered into the addresses table
    // 2. If the Address does not exists
    // 2.a Create the address in the table
    query = "INSERT INTO addresses ('STREET', 'CITY', 'PROVINCE', 'POSTAL_CODE') values (?, ?, ?, ?)";
    // Adding a second parameter to the prepareStatement method call to get the
    // generated keys on the PreparedStatement object after executing the update
    try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, address.getStreet());
      stmt.setString(2, address.getCity());
      stmt.setString(3, address.getProvince());
      stmt.setString(4, address.getPostalCode());
      stmt.executeUpdate();
      // 2.b Return the AddressID
      try (ResultSet addedId = stmt.getGeneratedKeys()) {
        while (addedId.next()) {
          return addedId.getInt(1);
        }
      }
    }
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
    int passengerId = -1;
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
    // TODO: TEST IMPLEMENTATION
    String query = "SELECT ID from accounts WHERE EMAIL = ?";
    try(PreparedStatement stmt = conn.prepareStatement(query)){
      stmt.setString(1, passengerEmail);
      try(ResultSet result = stmt.executeQuery()){
        while(result.next()){
          passengerId = result.getInt(1);
        }
      }
    }
    return passengerId;
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets id of driver with specified email (assumes driver exists)
   * Returns: Id
   */
  public int getDriverIdFromEmail(String driverEmail) throws SQLException {
    int driverId = -1;
    // TODO: TEST IMPLEMENTATION
    String query = "SELECT ID from accounts WHERE EMAIL = ?";
    try(PreparedStatement stmt = conn.prepareStatement(query)){
      stmt.setString(1, driverEmail);
      try(ResultSet result = stmt.executeQuery()){
        while(result.next()){
          driverId = result.getInt(1);
        }
      }
    }
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
    // TODO: TEST IMPLEMENTATION
    String query = "SELECT ADDRESS_ID from accounts WHERE EMAIL = ?";
    try(PreparedStatement stmt = conn.prepareStatement(query)){
      stmt.setString(1, email);
      try(ResultSet result = stmt.executeQuery()){
        while(result.next()){
          addressId = result.getInt(1);
        }
      }
    }
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

    // TODO: TEST IMPLEMENTATION
    String query = "SELECT favourite_locations.NAME, addresses.ID, addresses.STREET, addresses.CITY, addresses.PROVINCE, addresses.POSTAL_CODE FROM passengers INNER JOIN accounts ON accounts.ID = passengers.ID INNER JOIN favourite_locations ON passengers.ID = favourite_locations.PASSENGER_ID INNER JOIN addresses ON favourite_locations.LOCATION_ID = addresses.ID WHERE accounts.EMAIL = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, passengerEmail);
      try (ResultSet result = stmt.executeQuery()) {
        while (result.next()) {
          FavouriteDestination favDest = new FavouriteDestination(result.getString("NAME"), result.getInt("ID"),
              result.getString("STREET"), result.getString("CITY"), result.getString("PROVINCE"),
              result.getString("POSTAL_CODE"));
          favouriteDestinations.add(favDest);
        }
      }
    }
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
    String query = "SELECT ride_requests.*, accounts.*, pickup.*, dropoff.* FROM ride_requests INNER JOIN accounts ON accounts.ID = ride_requests.PASSENGER_ID INNER JOIN addresses pickup ON pickup.ID = ride_requests.PICKUP_LOCATION_ID INNER JOIN addresses dropoff ON dropoff.ID = ride_requests.DROPOFF_LOCATION_ID LEFT JOIN rides ON rides.REQUEST_ID = ride_requests.ID  WHERE rides.ID IS NULL";
    try(Statement stmt = conn.createStatement()){
      try(ResultSet result = stmt.executeQuery(query)){
        while(result.next()){
          RideRequest req = new RideRequest(result.getInt(1),
                                            result.getString("FIRST_NAME") , 
                                            result.getString("LAST_NAME"), 
                                            result.getString(16), 
                                            result.getString(17), 
                                            result.getString(21), 
                                            result.getString(22), 
                                            result.getString("PICKUP_DATE"), 
                                            result.getString("PICKUP_TIME"));
        }
      }
    }

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
