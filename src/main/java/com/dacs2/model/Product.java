package com.dacs2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.text.NumberFormat;
import java.util.Locale;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String ten;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String mota;

    @ManyToOne
    private Category danhmuc;

    @ManyToOne
    private Brand brand;

    private Double gia;

    private int soluong;

    private int soluongDaBan;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String anh;

    public String getGiaFormatted() {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(gia);
    }

    public String[] getArrayAnh() {
        if (anh == null || anh.trim().isEmpty()) {
            return new String[0];
        }
        return anh.split(", ");
    }

    private Boolean trangthai;

    private int sale;

    private Double giasale;

    public String getGiaSaleFormatted() {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(giasale);
    }

    private int soluongDanhgia;

    private int tongsoSao;

    public Double getRating() {
        return tongsoSao * 1.0 / soluongDanhgia;
    }

}
