const express = require("express");
const router = express.Router();
const connection = require("./db-connection"); // Import the database connection

// GET route to fetch all users from the database
router.get("/users", (req, res) => {
  connection.query("SELECT * FROM users", (err, results) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.json(results);
  });
});

// POST route to add a new user to the database
router.post("/users", (req, res) => {
  const user = req.body;
  connection.query("INSERT INTO users SET ?", user, (err, results) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.status(201).send(results);
  });
});

module.exports = router; // Export routes for use in index.js
