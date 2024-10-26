package com.piche.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_operation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransferOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deposit", nullable = false)
    private Double deposit;
}
