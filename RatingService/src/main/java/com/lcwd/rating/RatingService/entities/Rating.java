package com.lcwd.rating.RatingService.entities;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("user_ratings")
public class Rating {


    @Id
    private String ratingId;
    @NotBlank
    @Size(max = 100)
    private String userId;
    @NotBlank
    @Size(max = 100)
    private String hotelId;
    @Min(1)
    @Max(10)
    private int rating;
    @Size(max = 2000)
    private String feedback;

}
