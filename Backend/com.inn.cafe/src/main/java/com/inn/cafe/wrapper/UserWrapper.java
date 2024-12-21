package com.inn.cafe.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWrapper {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("password")
    private String password;

    @JsonProperty("status")
    private String status;

    public UserWrapper(Integer id, String name, String email, String contactNumber, String password, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.password = password;
        this.status=status;
    }



}
