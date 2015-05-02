To launch the test, modify and run the following command line (replace the ${...} with your own values, and remove proxy args if you don't need them)

mvn test -Dtest=ContactsAPITest -DargLine="-DrefreshToken=${your refresh token} -DclientId=${your client id} -DclientSecret=${your client secret} -Dhttps.proxyHost=localhost -Dhttps.proxyPort=8118 -Dhttp.proxyHost=localhost -Dhttp.proxyPort=8118"
