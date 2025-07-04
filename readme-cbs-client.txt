Compiling the searchclient:
    $ javac searchclient/*.java

We have implemented a new search client called NewSearchClient.java, which is a CBS client.
So run the new searchclient:
    $ java searchclient.NewSearchClient 25 # ma-cbs, number of 25 means merge after 25 conflicts

The whole command for running:
    $ java -jar ../server.jar -l ../complevels/ -c "java searchclient.NewSearchClient 25" -t 180 -o GHandDirt.zip --encrypt ../server.public