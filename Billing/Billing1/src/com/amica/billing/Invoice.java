package com.amica.billing;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
public class Invoice {
    private String firstName;
    private String lastName;
    private int number;
    private double amount;
    private LocalDate invoiceDate;
    private Optional<LocalDate> paidDate;
    private Customer customer;

}
