# Project Details:

## Project Name: Server

### Project Description: 
Build an API with local file parsing, loading and searching
and broadband api requests. 

### Team Members (cslogin) and Contributions:
Jakobi Haskell (jhaskell)
Frank Chiu (schiu4)

Jakobi worked on the Broadband API while Frank worked o CSV Parsing and Searching. The work was split evenly and largely 
worked on together.

### Time Spent: 17 hours

### Design:
We used a simple design for our server. We have a Server class that handles the routing and a Handler class for each 
endpoint that handles the logic for that endpoint. We also have a CSVHandler class that handles the CSV parsing and
searching. In addition, caching is implemented for the CSV Parsing, which means that
the CSV file is only parsed once and then stored in memory.

### Errors/Bugs:
No bugs. 

### Testing:
Our testing framework is comprised of both unit and integration tests. 
For integration testing, we've segmented it into various categories covering `loadcsv`, `viewcsv`, `searchcsv`, 
as well as features related to broadband and caching. 
These tests engage directly with the server to verify the accuracy of the returned messages and values.
They test for both errors and valid responses, including both when the county is specified and when it is not specified. 
These also test that the datetime is within the correct range. 

### Build:
To run the program run the Server class, and open browser with "localhost:3000/" followed by an
endpoint and parameters. For example, "localhost:3000/loadcsv?file=<file>" would be a valid request. Any additional parameters
outside of the first can be queried using the "&" character.

To use the broadband API, request is formed as: "localhost:3000/broadband?state=<State>&county=<County>". 
Use no county or an asterisk to get all counties in a state. If the county is not specified, 
the response will be a list of all counties in the state. 