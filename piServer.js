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

var sensor_input,wemos_id, pt;

server.listen(3000,()=>{

console.log('Accessible Price Tag Server is running on port 3000')

})
io.on('connection', (socket) => {
console.log('made socket connection',socket.id)


socket.on('beacon', (messageContent) => {
console.log(messageContent)



app.use('/sensor',function(req,res,next)
{
 t = req.param('value');
console.log("value  = " + t);
wemos = req.param('id');
console.log("id = " + wemos);

let message = {"scan":"touched"}
socket.broadcast.emit('scan',message)
console.log("1st handshake completed : Server -----> Phone ");


})
})

socket.on('beacon-id', (messageContent1) => {
console.log("2nd handshake completed : Phone -----> Server");
console.log(messageContent1)
pt = socket.id;
console.log("socket id = ",pt);



//for one beacon system = > if( wemos.trim() == "wemos1".trim() && (  messageContent1.trim() === "Jon".trim() || messageContent1.trim() === "Me".trim() ))
if( wemos.trim() == "wemos1".trim() && messageContent1.trim() === "Jon".trim())
{

let price_tag1 = {"price":"You touched bread and the price is  1,29 euros"}
//socket.emit('price',price_tag1);
io.to(socket.id).emit('price',price_tag1);

}

//for one beacon system => if( wemos.trim() == "wemos2".trim() && (messageContent1.trim() === "Me".trim() ||  messageContent1.trim() === "Jon".trim()))
if( wemos.trim() == "wemos2".trim() && messageContent1.trim() === "Me".trim())
{

let price_tag1 = {"price":"You touched musli and the price is  3,29 euros"}
//socket.emit('price',price_tag1);
io.to(socket.id).emit('price',price_tag1);

}
})

})

