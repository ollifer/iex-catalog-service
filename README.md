### Iex Catalog Service
This is spring-boot demo application that queries IEX Developer Platform for company data
 and store in mongodb locally.    
 
 ### Run using docker
`docker-compose up`
 
 ### Rest API
 `curl -X GET \
    'http://localhost:8080/catalog/company/price?symbols=aapl&symbols=fb&timeFrame=20&chronoUnit=SECONDS'`