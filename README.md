Some background on akka and the concurrency system if not familiar with it.
    - Maybe a bit over engineered for this problem, but this application has the framework for being a highly scalable
      application with a non blocking nature.

    - This server will spawn actors (lightweight threads in a way) to process messages in a non blocking concurrent way
      Right now the application doesn't spawn more than one actor, but it can be easily configured to spawn any number of them to process messages
      That's the basics, but I would love to talk more about this!

This application is an http server that can be hit at this url localhost:8080/search?filename={fileName}
    - The url takes a file name as a parameter.  The file should be accessible to the server somewhere so it will either need
    the path to the file or if you put the file in the top directory just the file name.

    - The return value is a list of the campsites that can be reserved at the date specified in the file taking into account a
      1 day gap rule

    - This url will work after server start up http://localhost:8080/search?filename=test-case.json

    - The main problem of finding which campsites are able to be reserved taking into account the "gap rule" is solved by checking whether
    there is an overlap in the start and end dates and then applying a gap factor to the start and end dates.  If one of the
    new dates is equal to the already reserved dates then it will violate the gap rule.

    - I left in place the framework to expand the gap rule to being a dynamic parameter also.  The parameter is just defaulted to 1 for now

    - The main algorithm, in the DateSearch file, the finding of valid reservations operates in O(N) time, N being the number of reservations in the file

STARTING THE SERVER:
    - Use CMD or whatever terminal you prefer to run a .bat file
      Navigate to the application directory data-range-validator and run sbt.bat
      Once dependencies are all downloaded execute the command reStart and the server will start at port 8080

    - Alternatively you can run through intellij with the scala plugin and sbt plugin
      If you run the sbt shell you can do the same command either reStart or run works

    - run tests with the test command

IMPROVEMENTS:
    - Test data creation should be put into more json files to cover a larger range of scenarios
    - Logging: Depending on if you need visibility into requests or the comparisons more logging could be added
    - Spawning more actors through a Router with round robin load balancing
    - Separate files into more packages most are just 1 off files right now so I didn't yet








