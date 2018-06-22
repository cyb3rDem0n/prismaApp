# StarNotifier  ![](https://github.com/dwyl/repo-badges/blob/master/svg/job%20satisfaction-100%25-brightgreen.svg) ![](https://github.com/dwyl/repo-badges/blob/master/svg/FunTimes-Guaranteed-brightgreen.svg)
## Welcome to the jungle


This mobile application, for now, should only notify a new event, where an EVENT is related to the observation and detection of a passing meteor.
Each event is new tuple containing some information like eventDate, stationInfo and more...

-------------------------------

### Remember

Commit only working code

-------------------------------

### The Team

 `Giuseppe D'Agostino` `Giovanni Pedà` `Pasquale Labate`
 
 Teamwork - Communication - human synergy is the way 4 the force....
--------------------------------

### Further info and Contact

Having trouble with some stuff in our project? ...your life is not good enough! 

### ROM DB
DaoAccess, DatabAse and UserData are the classes that implements the use of Rom DataBase.
You can invoche a insert / read function in this way:
```java
 private static final String DATABASE_NAME = "userdata_db";
 private DataBase dataBase;

// WRITE
 new Thread(new Runnable() {
    @Override
    public void run() {
        UserData userData = new UserData();
        userData.setUserId(99);
        userData.setUsername("username");
        userData.setPassword("password");
        dataBase.daoAccess () . insertOnlySingleUserData (userData);
            }}) .start();

//Read
 new Thread(new Runnable() {
    @Override
    public void run() {
        UserData userData_ = dataBase.daoAccess () . fetchOneUserDatabyUserId (99);
        System.out.print("----->>> "+ userData_.getUsername());
            }}) .start();
```

### Todos

 - Write MORE Tests
 - Add Night Mode

 
License
----

**Free Software, Hell Yeah!**
