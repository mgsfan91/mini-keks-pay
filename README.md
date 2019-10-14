# mini-keks-pay

The app serves to settle the score in a group of people (clients).
Clients can borrow money to each other, pay on behalf of everyone, receive money for some etc.

Transactions (costs or gains) can be submitted and the saldos of the targeted clients will be updated.
The sum of all the saldos is always zero.


## Run

Run on localhost:8090 with the wrapper
```
./mvnw spring-boot:run
```

Swagger interface is accessible on ```localhost:8090/swagger-ui.html```.

For database debugging, H2 is configured by default and its GUI is accessible on ```localhost:8090/h2-console```.
URL is ```jdbc:h2:mem:testdb``` and username ```sa```.


## Usage example

Add clients - John and Julie.
```
curl -i -X POST localhost:8090/clients -H "Content-Type: application/json" -d "{ \"name\": \"John\", \"saldo\": 0}"
curl -i -X POST localhost:8090/clients -H "Content-Type: application/json" -d "{ \"name\": \"Julie\", \"saldo\": 0}"
```

Check them.
```
curl -i localhost:8090/clients
```

John(id==1) borrows Julie(id==2) a 50. (it's a cost for him)
```
curl -i -X POST localhost:8090/transactions/cost -H "Content-Type: application/json" -d "{ \"amount\": 50, \"sources\": [1], \"destinations\": [2]}"
```

Check them now - John has a positive saldo, and Julie owes.
```
curl -i localhost:8090/clients
```

John got a 100 as a gift, but on behalf of both of them.
```
curl -i -X POST localhost:8090/transactions/gain -H "Content-Type: application/json" -d "{ \"amount\": 100, \"sources\": [1], \"destinations\": [1,2]}"
```

They are even now.
```
curl -i localhost:8090/clients
```

