# Hotel Reservation System

This is a hotel reservation program that we made using Java. You can use it to book rooms and manage the hotel!

## What can you do

- **If your a Client** - you can register and make a booking
- **If your a Receptionist** - you can login and see the clients and bookings. also you can check in guests
- **If your a Manager** - you can login and make receptionist accounts and cancel bookings

## How the files are organized

there are these files:
- Main.java - this is like the main menu
- Client.java - the client stuff
- Receptionist.java - receptionist things
- Manager.java - manager things  
- FileHandler.java - saves everything to files
- SAVES folder - all the data is here

## How to run it

just do this:

```
javac *.java
java Main
```

the program saves everything in the SAVES folder

## Default Login Info

these are the usernames and passwords if u dont have any yet:

Receptionist:
- user: receptionist
- pass: receptionist123

Manager:
- user: manager
- pass: manager123

## How Data is Saved

everything is saved in text files

### CLIENTS.txt

like this:
```
ClientID|Name|Address|Number|Email
```

example:
```
C-1234|Juan Dela Cruz|Laguna, PH|09123456789|juan@gmail.com
```

### RESERVE.txt

like this:
```
ResID|ClientID|Date|RoomType|NumRooms|NumGuests|Meals|AmountPaid|Balance|Status
```

example:
```
R-5678|C-1234|2026-05-12|2|1|2|4|2500.0|500.0|PaidWithBalance
```

### `CHECKED-IN.txt` / `CANCELLED.txt`

These files store reservation lines moved from `RESERVE.txt`.

### `RECEPTIONIST.txt` / `MANAGER.txt` (comma `,` delimited)

Format:

```
username,password
```

Example:

```
receptionist,receptionist123
manager,manager123
```

## Notes / Common Issues

- Do **not** type the `|` character in fields like name/address/email because it is the record delimiter.
- If `SAVES/` doesn’t exist, the program will create it automatically.
- If you run the program from a different folder, it will create/use a different `SAVES/` directory in that working folder.

## OOP Overview

- `Main` is the entry point and routes to the role menus.
- `Client`, `Receptionist`, and `Manager` contain role-specific workflows.
- `FileHandler` provides reusable file operations (append, search, move/update, read-all) used by the other classes.

