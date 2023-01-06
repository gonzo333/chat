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
cd C:\Sages\workspace\chat\src\main\java
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
Należy z linni komend przejść do katalogu w którym znajdują się klasy aplikacji, na przykład:
```
cd C:\Sages\workspace\chat\src\main\java
```
Następnnie skompilować kod klienta czat:
```
javac ChatClient.java
```
Oraz uruchomić klienta:
```
java ChatClient
```
### Funkcjonalności czata
Aby zmienić nick należy wykonać następującą komendę
```
/nick nowy_nick
```
Aby opuścić czat
```
/quit
```
Każda inna treść zostanie potraktowana jako wiadomość i zostanie wysłana do wszystkich userów podpiętych do pokoju czatowego.