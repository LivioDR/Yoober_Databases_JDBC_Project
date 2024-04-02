# Databases Course Project Part 2
## Current status
---
>This file will showcase the current status of every method that is required to complete the second part of the Databases Course Project
---
## Current progress on Methods
---
| Name | Started | Testing | Passed OK |
| ----------- | ----------- | ----------- | ----------- |
| getAllAccounts | X | X | X |
| getAverageRatingForDriver | X | X | X |
| createAccount | X | X | X |
| insertAccount | X | X | X |
| insertPassenger | X | X | X |
| insertDriver | X | X | X |
| insertLicense | X | X | X |
| insertAddressIfNotExists | X | X | X |
| insertFavouriteDestination | X | X | X |
| checkDriverExists | X | X | X |
| checkPassengerExists | X | X | X |
| insertRideRequest | X | X | X |
| getPassengerIdFromEmail | X | X | X |
| getDriverIdFromEmail | X | X | X |
| getAccountAddressIdFromEmail | X | X | X |
| getFavouriteDestinationsForPassenger | X | X | X |
| getUncompletedRideRequests | X | X | X |
| insertRide | X | X | X |
---
## Questions about implementation
### insertAddressIfNotExists
- Q: We go through the whole list so we can make sure that we don't miss and address due to casing. Is this verification required or can we just SELECT a.ID FROM addresses a WHERE 1 && 2 && 3 && 4... ?
### insertFavouriteDestination
- Q: do we need to validate if the received ID is a passenger?
- Q: when we insert a new destination, do we need to handle the result of the insert query?
### insertRideRequest
- Q: should the pick-up address be customizable in some way, instead of selecting always the passenger address by default?