# Hotel Reservation System

A Java console application for managing hotel reservations. The program supports client registration and booking, receptionist login and check-in workflows, and manager account administration and reservation cancellation.

## Features

- Client registration with unique client ID generation.
- Reservation creation with room, guest, meal, and payment options.
- Receptionist login, client lookup, reservation viewing, and guest check-in.
- Manager login, receptionist account creation, check-in record viewing, and reservation cancellation.
- File-based persistence using plain text files inside a local `SAVES/` folder.

## Project Structure

- [Main.java](Main.java) - application entry point and main menu.
- [Client.java](Client.java) - client registration and reservation workflow.
- [Receptionist.java](Receptionist.java) - receptionist login and operations.
- [Manager.java](Manager.java) - manager login and administrative actions.
- [FileHandler.java](FileHandler.java) - shared file utilities for saving, searching, moving, and reading records.
- `SAVES/` - generated data directory used by the application.

## Requirements

- Java Development Kit (JDK) installed and available on your PATH.
- A terminal or command prompt in the project folder.

## Run the Program

Compile and start the app from the project root:

```bash
javac *.java
java Main
```

The app creates and uses `SAVES/` in the current working directory. If you run the program from a different folder, it will use that folder's own `SAVES/` directory.

## Staff Login Accounts

- Manager login uses the built-in credentials `admin` / `password123`.
- Receptionist accounts are still stored in `SAVES/RECEPTIONIST.txt`.

## Data Storage

The system stores records as text lines in files under `SAVES/`.

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

These files receive reservation lines moved out of `RESERVE.txt` when a guest is checked in or a reservation is cancelled.

### `RECEPTIONIST.txt`

Receptionist accounts are stored as comma-separated credentials:

```text
username,password
```

Example:

```text
receptionist,receptionist123
```

## Usage Notes

- Do not enter the `|` character in client fields, because it is the record delimiter.
- Client contact numbers must be 11 digits and unique.
- Client emails must end in `@gmail.com` and be unique.
- A client password is saved during registration and must be entered again before creating a reservation.
- Reservation dates must be in `YYYY-MM-DD` format and cannot be in the past.
- Reservation records are stored with numeric room and meal choices, which are translated in the menus when displayed.

## How the Code Is Organized

- `Main` shows the main menu and routes users to the role-specific screens.
- `Client` handles client registration and reservation creation.
- `Receptionist` handles login, record lookup, reservation filtering, and check-in.
- `Manager` handles login, receptionist account creation, check-in record review, and cancellation.
- `FileHandler` centralizes the file operations used by the rest of the program.

## Common Issues

- If you see no data, check that you are running the app from the same folder where the records were created.
- If `SAVES/` is empty, that is normal on a fresh install.
- If login fails, confirm the username and password exactly, including lowercase characters.

