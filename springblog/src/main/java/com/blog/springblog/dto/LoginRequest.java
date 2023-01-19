package com.blog.springblog.dto;

import lombok.Data;

@Data
public class LoginRequest {
    public String username;
    public String password;
}
