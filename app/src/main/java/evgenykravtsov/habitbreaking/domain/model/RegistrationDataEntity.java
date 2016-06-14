package evgenykravtsov.habitbreaking.domain.model;

public class RegistrationDataEntity {

    private final String name;
    private final int gender;
    private final long dateOfBirth; // Unix Timestamp
    private final long registrationDate;

    //// CONSTRUCTORS

    public RegistrationDataEntity(String name, int gender, long dateOfBirth, long registrationDate) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
    }

    //// PUBLIC METHODS

    public String getEmail() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }


    //// OBJECT INTERFACE

    @Override
    public String toString() {
        return "Registration Data: Name - " + name + " | Gender - " + gender
                + " | Date Of Birth - " + dateOfBirth + " | RegistrationDate - " + registrationDate;
    }
}
