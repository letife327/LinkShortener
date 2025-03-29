package az.texnoera.link_shortener.repository;

import az.texnoera.link_shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {
    public Url findByShortCode(String shortUrl);
    @Query("SELECT l FROM Url l JOIN FETCH l.user")
    List<Url> findAllLinksWithUsers();
    List<Url> findByLastVisitTimeBefore(LocalDateTime dateTime);
}
