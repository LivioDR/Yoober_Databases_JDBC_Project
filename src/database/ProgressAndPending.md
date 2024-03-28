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
| createAccount | X | X |  |
| insertAccount | X | X |  |
| insertPassenger | X | X |  |
| insertDriver | X | X |  |
| insertLicense | X | X |  |
| insertAddressIfNotExists | X | X |  |
| insertFavouriteDestination | X | X |  |
| checkDriverExists | X | X | X |
| checkPassengerExists | X | X | X |
| insertRideRequest |  |  |  |
| getPassengerIdFromEmail |  |  |  |
| getDriverIdFromEmail |  |  |  |
| getAccountAddressIdFromEmail |  |  |  |
| getFavouriteDestinationsForPassenger | X | X |  |
| getUncompletedRideRequests |  |  |  |
| insertRide |  |  |  |
---
## Questions about implementation
### insertAddressIfNotExists
- Q: I'm going through the whole list so I can make sure that I don't miss and address due to casing. Is this verification required or can we just SELECT a.ID FROM addresses a WHERE 1 && 2 && 3 && 4... ?
### insertFavouriteDestination
- Q: do we need to validate if the received ID is a passenger?
- Q: when we insert a new destination, do we need to handle the result of the insert query?