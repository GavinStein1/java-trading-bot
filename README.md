Java Trading Bot
---

FTX BRANCH: Implementing into the FTX exchange...
---
This repo uses binance's java implementation of their api to connect to their markets and pull data to then use in a custom strategy. The strategy that will be implemented is the same one covered in another repo called polygon arbitrage.

This project currently lacks trade execution functionality. Implementing a successful arbitrage strategy requires extremely low latency for the REST API calls (~25 ms) which is usually unachievable with regular connections. Trade execution methods are being investigated...
