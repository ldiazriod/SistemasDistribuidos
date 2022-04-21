const app = require('express')();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const cors = require('cors');
const port = process.env.PORT || 3000;

app.use(cors());
app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});
app.get("/defaultImg", (req, res) => {
  res.sendFile(__dirname + "/assets/DefaultImg.jpg")
})

io.on('connection', (socket) => {
  socket.on('chat message', (img, msg, color) => {
    io.emit('chat message', img, msg, color);
  });
  socket.on('writing message', msg => {
    io.emit('writing message', msg);
  })
  socket.on('send image', (file, user) => {
    io.emit('send image', file, user);
  })
});

http.listen(port, () => {
  console.log(`Socket.IO server running at http://localhost:${port}/`);
});
