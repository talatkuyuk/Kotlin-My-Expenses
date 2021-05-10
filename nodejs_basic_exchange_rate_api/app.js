const fetch = require('node-fetch')
const express = require('express')

const app = express()
const port = 3000


app.get('/:currency', async (req, res) => {

	const getURI = (exchange) => {
		return `https://free.currconv.com/api/v7/convert?q=${exchange}&compact=ultra&apiKey=50fc9de4a729557262c5`
	}
	const currency = req.params.currency

	let response = {};

	try {

		switch (currency) {
			case "USD": 

				const rtu = await fetch(getURI("TRY_USD"));
				const jtu = await rtu.json()
				response.TRY_USD = jtu.TRY_USD ?? 0.0

				const reu = await fetch(getURI("EUR_USD"));
				const jeu = await reu.json()
				response.EUR_USD = jeu.EUR_USD ?? 0.0

				const rgu = await fetch(getURI("GBP_USD"));
				const jgu = await rgu.json()
				response.GBP_USD = jgu.GBP_USD ?? 0.0
				break;
			

			case "TRY": 

				const rut = await fetch(getURI("USD_TRY"));
				const jut = await rut.json()
				response.USD_TRY = jut.USD_TRY ?? 0.0

				const ret = await fetch(getURI("EUR_TRY"));
				const jet = await ret.json()
				response.EUR_TRY = jet.EUR_TRY ?? 0.0

				const rgt = await fetch(getURI("GBP_TRY"));
				const jgt = await rgt.json()
				response.GBP_TRY = jgt.GBP_TRY ?? 0.0
				break;
			
			
			case "EUR": 

				const rue = await fetch(getURI("USD_EUR"));
				const jue = await rue.json()
				response.USD_EUR = jue.USD_EUR ?? 0.0

				const rte = await fetch(getURI("TRY_EUR"));
				const jte = await rte.json()
				response.TRY_EUR = jte.TRY_EUR ?? 0.0

				const rge = await fetch(getURI("GBP_EUR"));
				const jge = await rge.json()
				response.GBP_EUR = jge.GBP_EUR ?? 0.0
				break;


			case "GBP": 

				const rug = await fetch(getURI("USD_GBP"));
				const jug = await rug.json()
				response.USD_GBP = jug.USD_GBP ?? 0.0

				const rtg = await fetch(getURI("TRY_GBP"));
				const jtg = await rtg.json()
				response.TRY_GBP = jtg.TRY_GBP ?? 0.0

				const reg = await fetch(getURI("EUR_GBP"));
				const jeg = await reg.json()
				response.EUR_GBP = jeg.EUR_GBP ?? 0.0
				break;
	
		}
		
		console.log(response)
		res.send(response)

	  } catch (err) {
		console.log(err)

		const num = 0
		
		switch (currency) {
			case "USD": 
				response = { TRY_USD: 0.0, EUR_USD: 0.0, GBP_USD: 0.0 }
				break;
			
			case "TRY": 
				response =  { USD_TRY: 0.0, EUR_TRY: 0.0, GBP_TRY: 0.0 }
				break;
			
			case "EUR": 
				response =  { USD_EUR: 0.0, TRY_EUR: 0.0, GBP_EUR: 0.0 }
				break;

			case "GBP": 
				response =  { USD_GBP: 0.00, TRY_GBP: 0.0, EUR_GBP: 0.0 }
				break;
		}
		console.log(response)
		res.json(response)
	  }
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})