package evgenykravtsov.habitbreaking.domain.model;

public class RegistrationDataEntity {

    private final String email;
    private final int gender;
    private final int age;
    private final long registrationDate;

    //// CONSTRUCTORS

    public RegistrationDataEntity(String email, int gender, int age, long registrationDate) {
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.registrationDate = registrationDate;
    }

    //// PUBLIC METHODS

    public String getEmail() {
        return email;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }


    //// OBJECT INTERFACE

    @Override
    public String toString() {
        return "Registration Data: Email - " + email + " | Gender - " + gender
                + " | Age - " + age + " | RegistrationDate - " + registrationDate;
    }
}
