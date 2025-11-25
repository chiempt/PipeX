package com.example.pipex.crm.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
