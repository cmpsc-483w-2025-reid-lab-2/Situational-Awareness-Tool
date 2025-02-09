const express = require("express"); // Import express package needed for server (handles HTTP requests and API calls)
const bodyParser = require("body-parser");  // Needed to parse JSON data
const cors = require("cors");   // Needed for Cross-Origin Resource Sharing (CORS)
const routes = require("./routes"); // Import the routes from routes.js 

const app = express();
const port = 3001;  // Port number for the server (should be different than the frontend port)

app.use(bodyParser.json()); // Middleware to parse JSON
app.use(cors()); // Enable Cross-Origin Resource Sharing (CORS)

// Use the routes defined in routes.js
app.use("/api", routes);

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
