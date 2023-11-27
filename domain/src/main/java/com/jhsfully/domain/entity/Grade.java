package com.jhsfully.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grade")
public class Grade {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String gradeName;
  private int gradePosition;
  private long price;
  private int apiMaxCount;
  private int fieldMaxCount;
  private int queryMaxCount;
  private int recordMaxCount;
  private int dbMaxSize;
  private int accessorMaxCount;
  private int historyStorageDays;
}
