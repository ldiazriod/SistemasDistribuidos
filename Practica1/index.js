
const app = require('express')();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const port = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', (socket) => {
  socket.on('chat message', (msg, color) => {
    io.emit('chat message', msg, color);
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
