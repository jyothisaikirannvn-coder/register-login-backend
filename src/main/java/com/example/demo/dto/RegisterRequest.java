package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterRequest {

	@Valid
	@NotNull
	private PersonalInfo personalInfo;

	@Valid
	@NotNull
	private AccountInfo account;

	@Valid
	@NotNull
	private InvestmentProfile investmentProfile;


    // ---------- Getters & Setters ----------

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }
    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public AccountInfo getAccount() {
        return account;
    }
    public void setAccount(AccountInfo account) {
        this.account = account;
    }

    public InvestmentProfile getInvestmentProfile() {
        return investmentProfile;
    }
    public void setInvestmentProfile(InvestmentProfile investmentProfile) {
        this.investmentProfile = investmentProfile;
    }

    // ---------------- INNER CLASSES ----------------

    public static class PersonalInfo {

        @NotBlank
        private String firstName;

        @NotBlank
        private String lastName;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String phone;

        @NotBlank
        private String dateOfBirth;

        // Getters & Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    }

    public static class AccountInfo {

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        // Getters & Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class InvestmentProfile {

        @NotBlank
        private String riskAppetite;

        @NotBlank
        private String experience;

        @NotBlank
        private String investmentGoal;

        // Getters & Setters
        public String getRiskAppetite() { return riskAppetite; }
        public void setRiskAppetite(String riskAppetite) { this.riskAppetite = riskAppetite; }

        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }

        public String getInvestmentGoal() { return investmentGoal; }
        public void setInvestmentGoal(String investmentGoal) { this.investmentGoal = investmentGoal; }
    }
}
