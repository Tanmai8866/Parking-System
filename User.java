import java.time.LocalDateTime;

public class User extends Person {
    public String vehicleType;
    public String vehicleSymbol;
    public String registrationNumber;
    public int reservedSlot;
    public char reservedFloor;
    public String reservationCode;
    public LocalDateTime desiredEntryTime;
    public LocalDateTime desiredExitTime;

    public User(String n, String p) {
        super(n, p);
        reservedSlot = -1;
    }

    public String toFileString() {
        return name + "," + password + "," + vehicleType + "," +
                vehicleSymbol + "," + registrationNumber + "," +
                reservedSlot + "," + reservedFloor + "," +
                reservationCode + "," +
                desiredEntryTime + "," + desiredExitTime;
    }

    public static User fromFileString(String line) {
        String[] arr = line.split(",");

        User u = new User(arr[0], arr[1]);
        u.vehicleType = arr[2];
        u.vehicleSymbol = arr[3];
        u.registrationNumber = arr[4];
        u.reservedSlot = Integer.parseInt(arr[5]);
        u.reservedFloor = arr[6].charAt(0);
        u.reservationCode = arr[7];

        u.desiredEntryTime = LocalDateTime.parse(arr[8]);
        u.desiredExitTime = LocalDateTime.parse(arr[9]);

        return u;
    }

    @Override
    public void displayInfo() {
        System.out.println("User Name: " + name +
                ", Reservation Code: " + reservationCode +
                ", Vehicle Type: " + vehicleType +
                ", Registration Number: " + registrationNumber +
                ", Reserved Slot: " + reservedSlot + "-" + reservedFloor);
    }
}
