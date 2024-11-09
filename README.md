# fram-collector
FarmCollector is a Spring Boot application designed to collect and manage data from farmers for different fields and seasons. The application allows farmers to record planting and harvesting data, and provides reports for expected vs. actual product yields grouped by farms or crops.

Features
Planted Data Recording: Record the area, crop type, and expected yield for each planting activity.
Harvest Data Recording: Update the actual harvested product yield for each crop.
Seasonal Reports: View expected vs. actual yield data, grouped by farm or crop.
Technologies
Spring Boot - Java framework for building REST APIs.
H2 Database - In-memory database for local development.
JUnit & Mockito - Testing frameworks for unit and integration tests.
Prerequisites
Java 17 or higher
Maven (optional, as Maven Wrapper is included)
Getting Started
Clone the Repository

bash
Copy code
git clone https://github.com/your-username/farm-collector.git
cd farm-collector
Build the Project

bash
Copy code
./mvnw clean install
Run the Application

bash
Copy code
./mvnw spring-boot:run
The application will start on http://localhost:8080.

API Endpoints
1. Record Planted Data
URL: /api/production/planted
Method: POST
Request Body:
json
Copy code
{
  "farm": "MyFarm",
  "crop": "Potato",
  "season": "Spring",
  "plantingArea": 50.5,
  "expectedProduct": 100
}
Response:
json
Copy code
{
  "id": 1,
  "farm": { "id": 1, "name": "MyFarm" },
  "crop": { "id": 1, "name": "Potato" },
  "season": "spring",
  "plantingArea": 50.5,
  "expectedProduct": 100,
  "actualHarvestedProduct": 0
}
2. Record Harvested Data
URL: /api/production/harvested
Method: POST
Request Body:
json
Copy code
{
  "farm": "MyFarm",
  "crop": "Potato",
  "season": "Spring",
  "actualHarvestedProduct": 99
}
Response (Success):
json
Copy code
{
  "id": 1,
  "farm": { "id": 1, "name": "MyFarm" },
  "crop": { "id": 1, "name": "Potato" },
  "season": "spring",
  "plantingArea": 50.5,
  "expectedProduct": 100,
  "actualHarvestedProduct": 99
}
Response (Failure - Farm or Crop Not Found):
json
Copy code
{
  "error": "Farm does not exist: MyFarm"
}
3. Get Report
URL: /api/production/reports
Method: GET
Query Parameters:
season (required): The season for which the report is requested.
groupBy (optional): Specifies how to group the data. Options:
farm - Groups the report by farm.
crop - Groups the report by crop type.
Example Request:
bash
Copy code
GET /api/production/reports?season=spring&groupBy=farm
Response (Grouped by Farm):
json
Copy code
{
  "MyFarm": [
    {
      "id": 1,
      "farm": { "id": 1, "name": "MyFarm" },
      "crop": { "id": 1, "name": "Potato" },
      "season": "spring",
      "plantingArea": 50.5,
      "expectedProduct": 100,
      "actualHarvestedProduct": 99
    }
  ]
}
Testing the Application
Run Unit and Integration Tests

bash
Copy code
./mvnw test
Test API Endpoints Using Postman or Curl

You can test the endpoints listed above by sending HTTP requests using Postman, Curl, or similar tools.

Example Curl Commands
Record Planted Data:

bash
Copy code
curl -X POST http://localhost:8080/api/production/planted \
  -H "Content-Type: application/json" \
  -d '{
        "farm": "MyFarm",
        "crop": "Potato",
        "season": "Spring",
        "plantingArea": 50.5,
        "expectedProduct": 100
      }'
Record Harvested Data:

bash
Copy code
curl -X POST http://localhost:8080/api/production/harvested \
  -H "Content-Type: application/json" \
  -d '{
        "farm": "MyFarm",
        "crop": "Potato",
        "season": "Spring",
        "actualHarvestedProduct": 99
      }'
Get Report:

bash
Copy code
curl -X GET "http://localhost:8080/api/production/reports?season=spring&groupBy=farm"
Database
This application uses an in-memory H2 database for testing and development purposes. The database will reset each time the application restarts.

To view the database:

Go to http://localhost:8080/h2-console in your browser.
Use the following credentials:
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank)
