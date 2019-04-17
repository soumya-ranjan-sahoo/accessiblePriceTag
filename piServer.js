// Variable for express library
const express = require('express'),
// Variable for http server library
http = require('http'),

app = express(),
// Creating a http server
server = http.createServer(app),

 // Variable for listening socket 
io = require('socket.io').listen(server);

app.get('/', (req, res) => {

res.send('Pi Server is running on port 3000')
})
// wemos: variable for getting which wemos is sending touch input first or second
// t : variable for getting touch sensor value from wemos
//pt : variable used to get the socket id of phone which has made the socket connection
var t,wemos, pt;

server.listen(3000,()=>{

console.log('Accessible Price Tag Server is running on port 3000')

})
// Socket Connection started
io.on('connection', (socket) => {
 // Socket Connection is made with phone
console.log('made socket connection',socket.id)


socket.on('beacon', (messageContent) => {
console.log(messageContent)


// Getting the touch sensor values from wemos 
app.use('/sensor',function(req,res,next)
{
 t = req.param('value');
console.log("value  = " + t);
wemos = req.param('id');
console.log("id = " + wemos);

// For first handshake =>
 //Server sending a scan value to all  phones which are  near to beacon
let message = {"scan":"touched"}
socket.broadcast.emit('scan',message)
console.log("1st handshake completed : Server -----> Phone ");


})
})
// For second handshake =>
 // Phone will be sending beacon-id back to server (if it is near to the beacon  )
socket.on('beacon-id', (messageContent1) => {
console.log("2nd handshake completed : Phone -----> Server");
console.log(messageContent1) // messageContent1 : beacon-id
pt = socket.id;
console.log("socket id = ",pt);


// After completion of two handshakes, now price is sent in third handshake
//for one beacon system = > if the wemos1 is sending touch input and near to Jon beacon(beetroot)
 // Then only emit the price of the product 
if( wemos.trim() == "wemos1".trim() && messageContent1.trim() === "Jon".trim())
{

let price_tag1 = {"price":"You touched bread and the price is  1,29 euros"}
io.to(socket.id).emit('price',price_tag1);

}


//for one beacon system = > if the wemos2 is sending touch input and near to Me beacon(candy floss)
 // Then only emit the price of the product
 if( wemos.trim() == "wemos2".trim() && messageContent1.trim() === "Me".trim())
{

let price_tag1 = {"price":"You touched musli and the price is  3,29 euros"}
io.to(socket.id).emit('price',price_tag1);

}
})

})

