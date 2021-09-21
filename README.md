# Hmrc Camel API

A Springboot app, which can easily be deployed to Heroku.

## Start Project with Docker
--------
  Clone project & Enter the project folder using the terminal run the following commands in the given order.       
  
1) git clone https://github.com/tolgagokmen/hmrc-camel-api.git

2) cd hmrc-camel-api
3) mvn clean install    
4) docker build -t hmrc-camel-api .
5) docker run -p 8083:8083 hmrc-camel-api

#####Your app should now be running on [localhost:8383](http://localhost:8383/).
#####You can find swagger to import postman on http://localhost:8383/api/api-doc



## Heroku Running Locally

Make sure you have Java and Maven installed.  Also, install the [Heroku CLI](https://cli.heroku.com/).

```sh
$ git clone https://github.com/tolgagokmen/hmrc-camel-api.git
$ cd hmrc-camel-api
$ mvn install
$ heroku local:start
```

#####Your app should now be running on [localhost:5000](http://localhost:5000/).
#####You can find swagger to import postman on http://localhost:5000/api/api-doc

```


