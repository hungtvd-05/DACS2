package com.dacs2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.Locale;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

}
