const express = require('express');
const bodyParser = require('body-parser')
const MongoClient = require('mongodb').MongoClient
const app = express();
const connectionString = 'mongodb://admin:password@127.0.0.1:27017/admin'
// const connectionString = 'mongodb+srv://admin:password@127.0.0.1/starwars?retryWrites=true&w=majority&tls=false'

MongoClient.connect(connectionString, {useUnifiedTopology:true},(err,client)=>{
    if(err) return console.error(err)
    console.log('connect to Database')
    const db = client.db('star-wars-quotes')
})
app.listen(3000, function(){
    console.log('listening on 3000')
})

app.use(bodyParser.urlencoded({extended: true}))

app.get('/',(req, res)=>{
    res.sendFile(__dirname + '/index.html')
})

app.post('/quotes', (req, res)=>{
    console.log(req.body)
})