# Hotel Reservation System

This is DLSL Hotel Reservation Console Program. It lets a user register as a client, make a booking, log in as a receptionist, or log in as a manager.

## What It Can Do

- register a new client
- make a reservation
- let a receptionist look up clients and check guests in
- let a manager create receptionist accounts and cancel reservations
- save all data in text files inside `SAVES/`

## Main Files

- [Main.java](Main.java) - starts the program and shows the main menu
- [Client.java](Client.java) - client signup and reservation flow
- [Receptionist.java](Receptionist.java) - receptionist login and staff tasks
- [Manager.java](Manager.java) - manager login and admin tasks
- [FileHandler.java](FileHandler.java) - file reading and saving helper

## How To Run

Open a terminal in the project folder and run:

```bash
javac *.java
java Main
```

The program will create a `SAVES/` folder in the folder where you run it.

## Logins

- manager login is `admin` / `password123`
- receptionist accounts are saved in `SAVES/RECEPTIONIST.txt`

## Saved Data Files

The program stores data as plain text files inside `SAVES/`.

### `CLIENTS.txt`

Format:

```text
ClientID|Password|Name|Address|ContactNumber|Email
```

Example:

```text
C-1234|mypassword|Juan Dela Cruz|Laguna, PH|09123456789|juan@gmail.com
```

### `RESERVE.txt`

Format:

```text
ResID|ClientID|Date|RoomChoice|NumRooms|NumGuests|MealChoice|AmountPaid|Balance|Status
```

Example:

```text
R-5678|C-1234|2026-05-12|2|1|2|4|2500.0|500.0|PaidWithBalance
```

### `CHECKED-IN.txt` and `CANCELLED.txt`

- `CHECKED-IN.txt` gets reservations that were checked in.
- `CANCELLED.txt` gets reservations that were cancelled.

### `RECEPTIONIST.txt`

Format:

```text
username,password
```

Example:

```text
receptionist,receptionist123
```

## NOTES

- do not type the `|` symbol in client fields
- contact numbers must be 11 digits
- emails must end with `@gmail.com`
- reservation dates must use `YYYY-MM-DD`
- reservation dates cannot be in the past

## GUIDES

- `Main` opens the program and sends you to the right menu
- `Client` is for registration and booking
- `Receptionist` is for lookup, viewing, and check-in
- `Manager` is for admin login, receptionist accounts, and cancellation
- `FileHandler` handles save, search, move, and read file work
