# Hotel Reservation System

This is a DLSL Hotel Reservation console program. A user can register as a client, create a reservation, log in as a receptionist, or log in as a manager.

## What It Does

- Registers new clients with a generated client ID.
- Creates reservations with room, guest, meal, and payment choices.
- Lets receptionists search clients, view reservations, and check guests in.
- Lets managers create receptionist accounts, view check-in records, and cancel reservations.
- Saves all data in text files inside the `SAVES/` folder.

## Main Files

- [Main.java](Main.java) - starts the program and shows the main menu.
- [Client.java](Client.java) - handles client registration and reservation creation.
- [Receptionist.java](Receptionist.java) - handles receptionist login and daily staff tasks.
- [Manager.java](Manager.java) - handles manager login and admin actions.
- [FileHandler.java](FileHandler.java) - handles saving, searching, moving, and reading records.

## How To Run

Open a terminal in the project folder and run:

```bash
javac *.java
java Main
```

The program creates the `SAVES/` folder in the same folder where you run it.

## Login Information

- Manager login uses the built-in credentials `admin` / `password123`.
- Receptionist accounts are stored in `SAVES/RECEPTIONIST.txt`.

## File Handling

The program stores information as plain text files inside `SAVES/`.

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

- `CHECKED-IN.txt` stores reservations that have already been checked in.
- `CANCELLED.txt` stores reservations that were cancelled.

### `RECEPTIONIST.txt`

Format:

```text
username,password
```

Example:

```text
admin,password123
```

## Notes

- Do not type the `|` symbol in client fields because it is used as the record separator.
- Contact numbers must be 11 digits and must be unique.
- Emails must end with `@gmail.com` and must be unique.
- A client password is saved during registration and must be entered again before making a reservation.
- Reservation dates must use the `YYYY-MM-DD` format and cannot be in the past.

## Guide

- `Main` opens the program and sends you to the correct menu.
- `Client` is used for registration and booking.
- `Receptionist` is used for lookup, viewing, and check-in.
- `Manager` is used for admin login, receptionist accounts, and reservation cancellation.
- `FileHandler` handles the file work used by the whole program.
