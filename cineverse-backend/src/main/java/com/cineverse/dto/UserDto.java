package com.cineverse.dto;

import com.cineverse.model.Role;
import com.cineverse.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UserDto {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private String address;
    private String dob;
    private String gender;
    private String avatar;
    private String joinedOn;
    private Role role;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.phone = user.getPhone();
        dto.city = user.getCity();
        dto.address = user.getAddress();
        dto.dob = user.getDob() != null ? user.getDob().toString() : "";
        dto.gender = user.getGender();
        dto.avatar = user.getAvatar();
        dto.joinedOn = user.getJoinedOn() != null ? user.getJoinedOn().toString() : "";
        dto.role = user.getRole();
        return dto;
    }

    public static String avatarFromName(String name) {
        return Arrays.stream(name.trim().split("\\s+"))
                .map(w -> w.substring(0, 1))
                .collect(Collectors.joining())
                .toUpperCase()
                .substring(0, Math.min(2, name.trim().split("\\s+").length >= 2 ? 2 : 1));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getJoinedOn() { return joinedOn; }
    public void setJoinedOn(String joinedOn) { this.joinedOn = joinedOn; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
