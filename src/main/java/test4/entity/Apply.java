package test4.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상세 분석 신청서 엔티티.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String author; // 작성자

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시
    
    private Long uid;
    private char completedYn; // 분석 완료 여부 ('Y': 완료, 'N': 미완료)

    private String link;
    private String location;
    private String type;
    
    @ElementCollection
    @CollectionTable(name = "apply_files", joinColumns = @JoinColumn(name = "apply_id"))
    private List<FileDetail> files = new ArrayList<>(); // 첨부 파일 리스트

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
