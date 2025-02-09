require("dotenv").config(); // Load credential variables from .env file
const mysql = require("mysql2");    // Import mysql2 package

const connection = mysql.createConnection({ // Creating connection to the database
  host: process.env.DB_HOST,    // Database location
  user: process.env.DB_USER,    // Database username
  password: process.env.DB_PASSWORD, // Database (user) password
  database: process.env.DB_NAME,    // Database name
});

connection.connect((err) => {   // Error handling in case database fails to connect
  if (err) {
    console.error("Database connection failed:", err);
    throw err;
  }
});

module.exports = connection;    // Export the connection for use in routes.js and index.js