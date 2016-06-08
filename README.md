# devops

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).


## Building 

To build the devops client, run:

    ./mvn -Pdev clean package


To ensure everything worked, run:

    java -jar target/devops-0.0.1-SNAPSHOT.war --spring.profiles.active=dev

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.


## Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `devops`
* Source Code Management
    * Git Repository: `https://innersource.accenture.com/academy-application/devops-sample-application.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pdev clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
