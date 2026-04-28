package com.sergitxin.flexilearn.dto;

public class LoginRequestDto {
    private String email;
    private String password;

    public LoginRequestDto(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
    
    public LoginRequestDto() {
		super();
		this.email = "";
		this.password = "";
	}
    
	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
