package az.texnoera.link_shortener.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counter;
    private String url;
    private String shortCode;
    @Timestamp
    private LocalDateTime createTime;
    @Timestamp
    private LocalDateTime expireTime;
    @ManyToOne
    private User user;
}
