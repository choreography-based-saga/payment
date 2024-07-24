package com.saga.payment.infra.model;

import com.saga.payment.infra.model.enums.MerchantAddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "MerchantAddress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantAddressEntity {

    @Id
    @GeneratedValue
    Integer id;
    @Enumerated(EnumType.STRING)
    MerchantAddressType type;
    String city;
    String zip;
    String streetName;
    String streetNumber;
}
