package com.zerobase.reservation.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    private String storeName;
    private String location;
    private String description;

    @OneToOne
    @JoinColumn(name = "partner_user_id")
    private User partner;
}
