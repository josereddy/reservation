package com.example.ReservationTimings.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Interceptor_Data_DB {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer Id;
  private String Apiname;
  private String servicename;
  private String url;
  private Date date;
  private Long timemillisec;
}
