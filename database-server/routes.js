const express = require("express");
const router = express.Router();
const multer = require("multer");
const csv = require("csv-parser");
const fs = require("fs");
const connection = require("./db-connection");

// Set up multer for file upload
const upload = multer({ dest: "uploads/" }); // Files are stored in ./uploads/

// Utility: parse a CSV file and return the data as an array of objects
function parseCsv(filePath) {
  return new Promise((resolve, reject) => {
    const results = [];
    fs.createReadStream(filePath)
      .pipe(csv())
      .on("data", (data) => results.push(data))
      .on("end", () => resolve(results))
      .on("error", (err) => reject(err));
  });
}

// Insert one heart rate data entry into the database
function insertHeartRate(data, sessionId) {
  return new Promise((resolve, reject) => {
    const entry = {
      session_id: sessionId,
      avg_rate: data.avg_rate,
      max_rate: data.max_rate,
      min_rate: data.min_rate,
      time_started: data.time_started,
      session_length: data.session_length,
    };

    connection.query("INSERT INTO heart_data SET ?", entry, (err, results) => {
      if (err) return reject(err);
      resolve(results);
    });
  });
}

// Insert one mantis shot entry into the database
function insertMantisShot(data, sessionId) {
  return new Promise((resolve, reject) => {
    const entry = {
      session_id: sessionId,
      split: data.split,
      score: data.score,
    };

    connection.query("INSERT INTO mantis_data_shots SET ?", entry, (err, results) => {
      if (err) return reject(err);
      resolve(results);
    });
  });
}

// =======================
// ROUTES
// =======================

// GET all users
router.get("/users", (req, res) => {
  connection.query("SELECT * FROM users", (err, results) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.json(results);
  });
});

// POST a new user
router.post("/users", (req, res) => {
  const user = req.body;
  connection.query("INSERT INTO users SET ?", user, (err, results) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.status(201).send(results);
  });
});

// POST route to upload two CSV files (heart rate + mantis shots)
router.post("/upload", upload.fields([
  { name: "heartRateFile" },
  { name: "mantisFile" }
]), async (req, res) => {
  const heartRateFile = req.files?.heartRateFile?.[0];
  const mantisFile = req.files?.mantisFile?.[0];

  if (!heartRateFile || !mantisFile) {
    return res.status(400).json({ error: "Both files are required." });
  }

  try {
    const heartData = await parseCsv(heartRateFile.path);
    const mantisData = await parseCsv(mantisFile.path);

    // Create a new session for this upload
    const newSession = {
      user_id: 1, // TEMP: Use actual logged-in user later
      total_shots: mantisData.length,
      avg_score: mantisData.reduce((acc, d) => acc + parseFloat(d.score || 0), 0) / mantisData.length,
      time_started: heartData[0]?.time_started || new Date(),
      session_length: heartData[0]?.session_length || new Date(),
    };

    // Insert new session
    connection.query("INSERT INTO mantis_data_sessions SET ?", newSession, async (err, result) => {
      if (err) {
        console.error(err);
        return res.status(500).json({ error: "Failed to save session." });
      }

      const sessionId = result.insertId;

      // Insert parsed heart rate data
      for (const row of heartData) {
        await insertHeartRate(row, sessionId);
      }

      // Insert parsed mantis shot data
      for (const row of mantisData) {
        await insertMantisShot(row, sessionId);
      }

      // Cleaning up uploaded files
      fs.unlink(heartRateFile.path, (err) => {
        if (err) console.error("Failed to delete heart rate file:", err);
      });

      fs.unlink(mantisFile.path, (err) => {
        if (err) console.error("Failed to delete mantis file:", err);
      });

      res.json({ message: "Upload complete and data saved.", sessionId });
    });

  } catch (err) {
    console.error("File upload error:", err);
    res.status(500).json({ error: "Server failed to process the files." });
  }
});

module.exports = router;
