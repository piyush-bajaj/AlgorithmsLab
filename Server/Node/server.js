const express = require("express");
const cors = require("cors");
const { response } = require("express");
const app = express();
const axios = require('axios');

app.use(cors());

// serve your css as static
app.use(express.static(__dirname));

app.listen(8080, () => {
  console.log("Application started and Listening on port 8080");
});

app.get("/", (req, res) => {
    res.sendFile(__dirname + "/index.html");
});

// app.get("/navigation/init", (req, res, next) => {
// 	axios.get(`http://backend:8080${req.originalUrl}`)
//   .then(response => res.json(response.data))
//   .catch(err => next(err));
// });

app.get("/navigation/*", async (req, res) => {
  let url = `http://backend:8080${req.originalUrl}`;
  // url = `http://ip172-18-0-26-c2t1lgvqf8u000abljbg-80.direct.labs.play-with-docker.com${req.originalUrl}`;
	axios.get(url)
  .then(response => res.json(response.data))
  .catch(err => next(err))
});

