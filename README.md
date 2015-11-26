### Map the worlds BSSIDs!

Inspired by google's wifi BSSID and SSID database, I thought it would be cool if everyone revealed the SSID and BSSID of the access points near them, and then using the local IP address map the BSSIDs to location.

I have spent my evening making a Java app that gets the nearby relevant data and posts it to a database. The information can be used to plot a map of the world and where every BSSID and SSID is.

Its in java so that it can run on as many OSes as possible with little code tweaking - although only tested on Mac.

##### Testing

Its still in testing, so to try it, first you need to setup a server to receive the data. In the "node" directory, you need to run `npm install` and the `node server.js`

This will listen on port 3000 - that is where the Java app will send the data. You will see it in the console. 

Depending on your version of java (I'm running Java 6.x) you should be able to double click on the app (`connect.jar`). However you won't see much. You can also run it from the command line with 

```
java -jar connect.jar
```

It will thank you for helping out once complete.

### Development

If you want to help develop, you should be able to import it to Eclipse

