package evgenykravtsov.habitbreaking.domain.model;

public class RegistrationDataEntity {

    private final String name;
    private final int gender;
    private final long dateOfBirth; // Unix Timestamp
    private final long registrationDate; // Unix Timestamp
    private final String secretQuestion;
    private final String secretQuestionAnswer;

    //// CONSTRUCTORS

    public RegistrationDataEntity(String name, int gender, long dateOfBirth, long registrationDate,
                                  String secretQuestion, String secretQuestionAnswer) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
        this.secretQuestion = secretQuestion;
        this.secretQuestionAnswer = secretQuestionAnswer;
    }

    //// PUBLIC METHODS

    public String getName() {
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

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public String getSecretQuestionAnswer() {
        return secretQuestionAnswer;
    }

    //// OBJECT INTERFACE

    @Override
    public String toString() {
        return "Registration Data: Name - " + name + " | Gender - " + gender
                + " | Date Of Birth - " + dateOfBirth + " | RegistrationDate - " + registrationDate
                + " | Secret Question - " + secretQuestion
                + " | Secret Question Answer - " + secretQuestionAnswer;
    }
}
