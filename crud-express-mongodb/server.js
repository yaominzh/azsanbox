const express = require('express');
const bodyParser = require('body-parser')
const MongoClient = require('mongodb').MongoClient
const app = express();
const connectionString = 'mongodb://admin:password@127.0.0.1:27017/admin'
// const connectionString = 'mongodb+srv://admin:password@127.0.0.1/starwars?retryWrites=true&w=majority&tls=false'

MongoClient.connect(connectionString, {useUnifiedTopology:true}).then(client=>{
    console.log('connect to Database')
    const db = client.db('star-wars-quotes')
    const quotesCollection = db.collection('quotes')

    app.use(bodyParser.urlencoded({extended: true}))

    app.get('/',(req, res)=>{
        res.sendFile(__dirname + '/index.html')
    })

    app.post('/quotes', (req, res)=>{
        quotesCollection.insertOne(req.body).then(result =>{
            res.redirect('/')
            console.log('insert successfully')
        })
            .catch(error => console.error(error))
    })
    app.listen(3000, function(){
        console.log('listening on 3000')
    })
}).catch(console.error)

