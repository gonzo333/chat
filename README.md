## Czat tekstowy client-server oparty na Java Sockets

## Informacje ogólne
Aplikacja umożliwia przeprowadzenie rozmowy czat w utworzonym pokoju czatowym.

## Technologie
* JDK: 20
* Java Sockets

## Uruchomienie projektu
### Uruchomienie servera
Należy z linni komend przejść do katalogu w którym znajdują się klasy aplikacji, na przykład:
```
C:\Sages\workspace\chat\chat-server\src\main\java
```
Następnnie skompilować kod serwera czat:
```
javac .\ChatServer.java
```
Oraz uruchomić serwer:
```
java ChatServer
```

### Uruchomienie klientów
Należy z linni komend przejść do katalogu w którym znajdują się klasy klienta chat, na przykład:
```
C:\Sages\workspace\chat\chat-client\src\main\java
```
Następnnie skompilować kod klienta czat:
```
javac ChatClient.java
```
Oraz uruchomić klienta:
```
java ChatClient
```

### Funkcjonalności czat
Czat umożliwia użytkownikowi:
1. dołączenie do pokoju ogólnego
2. stworzenie własnego pokoju
3. dołączenie do pokoju innego usera
4. czatowanie w każdym z tych pokojów