package com.lcwd.hotel.HotelService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {


    @Id
    private  String hotelId;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Size(max = 255)
    private String location;
    @Size(max = 255)
    private String about;
}
