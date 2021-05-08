const { request } = require('express')
const fetch = require('node-fetch')

const express = require('express')
const app = express()
const port = 3000

app.get('/:currency', async (req, res) => {

	const getURI = (exchange) => {
		return `https://free.currconv.com/api/v7/convert?q=${exchange}&compact=ultra&apiKey=50fc9de4a729557262c5`
	}
	const currency = req.params.currency

	const response = {};

	try {

		switch (currency) {
			case "USD": 

				const rtu = await fetch(getURI("TRY_USD"));
				const jtu = await rtu.json()
				response.TRY_USD = jtu.TRY_USD

				const reu = await fetch(getURI("EUR_USD"));
				const jeu = await reu.json()
				response.EUR_USD = jeu.EUR_USD

				const rgu = await fetch(getURI("GBP_USD"));
				const jgu = await rgu.json()
				response.GBP_USD = jgu.GBP_USD
				break;
			

			case "TRY": 

				const rut = await fetch(getURI("USD_TRY"));
				const jut = await rut.json()
				response.USD_TRY = jut.USD_TRY

				const ret = await fetch(getURI("EUR_TRY"));
				const jet = await ret.json()
				response.EUR_TRY = jet.EUR_TRY

				const rgt = await fetch(getURI("GBP_TRY"));
				const jgt = await rgt.json()
				response.GBP_TRY = jgt.GBP_TRY
				break;
			
			
			case "EUR": 

				const rue = await fetch(getURI("USD_EUR"));
				const jue = await rue.json()
				response.USD_EUR = jue.USD_EUR

				const rte = await fetch(getURI("TRY_EUR"));
				const jte = await rte.json()
				response.TRY_EUR = jte.TRY_EUR

				const rge = await fetch(getURI("GBP_EUR"));
				const jge = await rge.json()
				response.GBP_EUR = jge.GBP_EUR
				break;


			case "GBP": 

				const rug = await fetch(getURI("USD_GBP"));
				const jug = await rug.json()
				response.USD_GBP = jug.USD_GBP

				const rtg = await fetch(getURI("TRY_GBP"));
				const jtg = await rtg.json()
				response.TRY_GBP = jtg.TRY_GBP

				const reg = await fetch(getURI("EUR_GBP"));
				const jeg = await reg.json()
				response.EUR_GBP = jeg.EUR_GBP
				break;
	
		}
		
		console.log(response)
		res.send(response)

	  } catch (err) {
		console.log(err)
		//res.send({"TRY_USD":0.121402,"EUR_USD":1.216325,"GBP_USD":1.39745})
	  }

})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})