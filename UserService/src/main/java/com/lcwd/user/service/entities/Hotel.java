package com.lcwd.user.service.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {



    private  String hotelId;
    private String name;
    private String location;
    private  String about;
}